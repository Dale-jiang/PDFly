package com.tb.pdfly.page.fragments

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.databinding.FragmentFileListBinding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.adapter.FileListAdapter
import com.tb.pdfly.page.base.BaseFragment
import com.tb.pdfly.page.read.DocReadActivity
import com.tb.pdfly.page.read.PDFReadActivity
import com.tb.pdfly.page.vm.GlobalVM
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.FileInfo
import com.tb.pdfly.parameter.FileType
import com.tb.pdfly.parameter.TabType
import com.tb.pdfly.parameter.changeNameLiveData
import com.tb.pdfly.parameter.database
import com.tb.pdfly.parameter.fileDeleteLiveData
import com.tb.pdfly.parameter.showFileDetailsDialog
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.report.ReportCenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")
class FileListFragment : BaseFragment<FragmentFileListBinding>(FragmentFileListBinding::inflate) {

    companion object {
        private const val ARG_TYPE_STR_1 = "TAB_TYPE"
        private const val ARG_TYPE_STR_2 = "FILE_TYPE"
        fun newInstance(tabType: TabType, fileType: FileType): FileListFragment {
            return FileListFragment().also {
                it.arguments = Bundle().apply {
                    this.putSerializable(ARG_TYPE_STR_1, tabType)
                    this.putSerializable(ARG_TYPE_STR_2, fileType)
                }
            }
        }
    }

    private val mTabType by lazy { arguments?.getSerializable(ARG_TYPE_STR_1) as? TabType ?: TabType.HOME }
    private val mFileType by lazy { arguments?.getSerializable(ARG_TYPE_STR_2) as? FileType }

    private val viewModel by activityViewModels<GlobalVM>()
    private lateinit var mAdapter: FileListAdapter

    override fun initView() {
        initAdapter()

        when (mTabType) {
            TabType.HOME -> {
                viewModel.onScanResultLiveData.observe(this) {
                    formatData(it)
                }
            }

            TabType.HISTORY -> {
                viewModel.onHistoryLiveData.observe(this) {
                    formatData(it)
                }
            }

            TabType.COLLECTION -> {
                viewModel.onCollectionLiveData.observe(this) {
                    formatData(it)
                }
            }
        }

        changeNameLiveData.observe(this) { pair ->
            val newList = mAdapter.currentList.map {
                if (it.path == pair.first) {
                    it.copy(path = pair.second, displayName = File(pair.second).name)
                } else it
            }
            mAdapter.submitList(null)
            mAdapter.submitList(newList)
        }

        fileDeleteLiveData.observe(this) { path ->
            val newList = mAdapter.currentList.filter { it.path != path }
            mAdapter.submitList(newList)
        }
    }

    private fun initAdapter() {
        mAdapter = FileListAdapter(requireContext(), mTabType, itemClick = {
            showViewAd {
                if (it.getFileType() != FileType.PDF) {

                    requireActivity().toActivity<DocReadActivity> {
                        putExtra(DocReadActivity.FILE_INFO, it)
                    }
                } else {
                    requireActivity().toActivity<PDFReadActivity> {
                        putExtra(PDFReadActivity.FILE_INFO, it)
                    }
                }
            }
        }, moreClick = {
            if (mTabType == TabType.COLLECTION) {
                lifecycleScope.launch(Dispatchers.IO + SupervisorJob()) {
                    it.isCollection = false
                    database.fileInfoDao().upsert(it)
                }
            } else {
                (requireActivity() as MainActivity).showFileDetailsDialog(it) {
                    showViewAd {
                        if (it.getFileType() != FileType.PDF) {
                            requireActivity().toActivity<DocReadActivity> {
                                putExtra(DocReadActivity.FILE_INFO, it)
                            }
                        } else {
                            requireActivity().toActivity<PDFReadActivity> {
                                putExtra(PDFReadActivity.FILE_INFO, it)
                            }
                        }
                    }
                }
            }
        })
        binding?.recyclerView?.itemAnimator = null
        binding?.recyclerView?.adapter = mAdapter
    }

    private fun formatData(data: List<FileInfo>) {
        if (null == mFileType || mFileType == FileType.ALL) {
            binding?.viewEmpty?.isVisible = data.isEmpty()
            binding?.viewEmpty?.text = getEmptyTips()
            mAdapter.submitList(data) {
                binding?.recyclerView?.scrollToPosition(0)
            }
        } else {
            val result = data.filter { item -> item.getFileType() == mFileType }
            binding?.viewEmpty?.isVisible = result.isEmpty()
            binding?.viewEmpty?.text = getEmptyTips()
            mAdapter.submitList(result) {
                binding?.recyclerView?.scrollToPosition(0)
            }
        }
    }

    private fun getEmptyTips(): String {
        return when (mTabType) {
            TabType.HISTORY -> {
                getString(R.string.no_history_yet)
            }

            TabType.COLLECTION -> {
                getString(R.string.no_bookmarks_yet)
            }

            TabType.HOME -> {
                getString(R.string.no_files_tips)
            }

        }
    }

    private fun showViewAd(callBack: CallBack) {
        ReportCenter.reportManager.report("pdfly_ad_chance", mapOf("ad_pos_id" to "pdfly_scan_int"))
        lifecycleScope.launch {
            while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
            val ad = AdCenter.pdflyScanInt
            val ac = requireActivity() as MainActivity
            if (ad.canShow(ac)) {
                ad.showFullAd(ac, "pdfly_scan_int", showLoading = true) { callBack() }
            } else {
                ad.loadAd(ac)
                callBack()
            }
        }
    }

}