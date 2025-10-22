package com.tb.pdfly.page

import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.tb.pdfly.R
import com.tb.pdfly.databinding.ActivityCompleteBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.FileInfo
import com.tb.pdfly.parameter.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class CompleteActivity : BaseActivity<ActivityCompleteBinding>(ActivityCompleteBinding::inflate) {

    companion object {
        const val FILE_INFO = "FILE_INFO"
        const val RESULT_DES = "RESULT_DES"
    }

    private val fileInfo by lazy { intent?.getParcelableExtra<FileInfo>(FILE_INFO) }
    private val resultDesc by lazy { intent?.getStringExtra(RESULT_DES) }


    override fun initView() {

        binding.apply {

            onBackPressedDispatcher.addCallback {
                finish()
            }
            ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }


            textDes.text = resultDesc ?: getString(R.string.completed)
            dialogName.text = fileInfo?.displayName ?: ""
            dialogDesc.text = fileInfo?.path ?: ""

            btnOpen.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    runCatching {
                        val findItem = database.fileInfoDao().getFileByPath(fileInfo?.path ?: "") ?: fileInfo
                        findItem?.recentViewTime = System.currentTimeMillis()
                        findItem?.let { database.fileInfoDao().upsert(findItem) }
                    }
                }

                // TODO:  open

                finish()
            }


        }
    }


}