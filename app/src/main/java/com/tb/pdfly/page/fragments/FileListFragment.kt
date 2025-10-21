package com.tb.pdfly.page.fragments

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.tb.pdfly.databinding.FragmentFileListBinding
import com.tb.pdfly.page.adapter.FileListAdapter
import com.tb.pdfly.page.base.BaseFragment
import com.tb.pdfly.page.vm.GlobalVM
import com.tb.pdfly.parameter.FileInfo
import com.tb.pdfly.parameter.FileType
import com.tb.pdfly.parameter.TabType

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
    private val mFileType by lazy { arguments?.getSerializable(ARG_TYPE_STR_2) as? FileType ?: FileType.PDF }

    private val viewModel by activityViewModels<GlobalVM>()
    private lateinit var mAdapter: FileListAdapter

    override fun initView() {
        initAdapter()
        val list = mutableListOf<FileInfo>()
        for (i in 0..10) {
            if (mTabType == TabType.COLLECTION) {
                list.add(FileInfo(isCollection = true))
            } else {
                list.add(FileInfo())
            }
        }
        mAdapter.submitList(list)
    }


    private fun initAdapter() {
        mAdapter = FileListAdapter(requireContext(), itemClick = {

        }, moreClick = {

        })
        binding?.recyclerView?.itemAnimator = null
        binding?.recyclerView?.adapter = mAdapter
    }

}