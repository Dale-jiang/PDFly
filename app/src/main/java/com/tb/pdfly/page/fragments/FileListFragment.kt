package com.tb.pdfly.page.fragments

import androidx.fragment.app.activityViewModels
import com.tb.pdfly.databinding.FragmentFileListBinding
import com.tb.pdfly.page.adapter.FileListAdapter
import com.tb.pdfly.page.base.BaseFragment
import com.tb.pdfly.page.vm.GlobalVM
import com.tb.pdfly.parameter.FileInfo

class FileListFragment : BaseFragment<FragmentFileListBinding>(FragmentFileListBinding::inflate) {


    private val viewModel by activityViewModels<GlobalVM>()
    private lateinit var mAdapter: FileListAdapter

    override fun initView() {
        initAdapter()
        val list = mutableListOf<FileInfo>()
        for (i in 0..10) {
            list.add(FileInfo())
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