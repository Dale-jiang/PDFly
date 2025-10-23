package com.tb.pdfly.page.read

import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.seapeak.docviewer.DocViewerFragment
import com.seapeak.docviewer.config.DocConfig
import com.seapeak.docviewer.config.DocType
import com.tb.pdfly.R
import com.tb.pdfly.databinding.ActivityDocReadBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.FileInfo
import com.tb.pdfly.parameter.FileType
import com.tb.pdfly.parameter.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")
class DocReadActivity : BaseActivity<ActivityDocReadBinding>(ActivityDocReadBinding::inflate) {

    companion object {
        const val FILE_INFO = "FILE_INFO"
    }

    private val fileInfo by lazy { intent?.getParcelableExtra<FileInfo>(FILE_INFO) }


    override fun initView() {

        if (fileInfo == null || !File(fileInfo!!.path).exists()) {
            Toast.makeText(this, getString(R.string.an_unknown_error_occurred), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fileInfo?.apply {

            binding.title.text = displayName

            val file = File(path)
            val uri = FileProvider.getUriForFile(
                this@DocReadActivity,
                "${this@DocReadActivity.packageName}.pdfly.fileProvider",
                file
            )
            val type = when (getFileType()) {
                FileType.WORD -> DocType.WORD
                FileType.EXCEL -> DocType.EXCEL
                FileType.PPT -> DocType.PPT
                else -> DocType.PDF
            }

            val config = DocConfig(url = uri.toString(), type = type)
            val fragment = DocViewerFragment.newInstance(config)

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()

            lifecycleScope.launch(Dispatchers.IO) {
                val findItem = database.fileInfoDao().getFileByPath(path) ?: this@apply
                findItem.recentViewTime = System.currentTimeMillis()
                database.fileInfoDao().upsert(findItem)
            }
        }


        onBackPressedDispatcher.addCallback {
            finish()
        }
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}