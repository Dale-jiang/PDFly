package com.tb.pdfly.parameter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tb.pdfly.R
import com.tb.pdfly.databinding.DialogFileDetailsBinding
import com.tb.pdfly.page.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.jvm.java

fun AppCompatActivity.myEnableEdgeToEdge(parentView: ViewGroup? = null, topPadding: Boolean = true, bottomPadding: Boolean = true) {
    runCatching {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            if (parentView != null) {
                parentView.setPadding(0, if (topPadding) systemBarsInsets.top else 0, 0, if (bottomPadding) systemBarsInsets.bottom else 0)
            } else {
                this@myEnableEdgeToEdge.window.decorView.setPadding(0, if (topPadding) systemBarsInsets.top else 0, 0, if (bottomPadding) systemBarsInsets.bottom else 0)
            }
            insets
        }
    }.onFailure { throwable ->
        throwable.printStackTrace()
    }
}

@Suppress("DEPRECATION")
fun AppCompatActivity.setDensity() {
    resources.displayMetrics.apply {
        density = heightPixels / 765f
        densityDpi = (density * 160).toInt()
        scaledDensity = density
    }
}

inline fun <reified T : AppCompatActivity> Activity.toActivity(finishCurrent: Boolean = false, block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java).apply(block)
    startActivity(intent)
    if (finishCurrent) finish()
}

fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics)
}

fun Context.hasStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Environment.isExternalStorageManager()
    else {
        mutableListOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE).all {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, it)
        }
    }
}

fun Activity.showFileDetailsDialog(fileItem: FileInfo, fromDetails: Boolean = false) {
    val binding = DialogFileDetailsBinding.inflate(LayoutInflater.from(this), window.decorView as ViewGroup, false)
    val dialog = BottomSheetDialog(this).apply {
        setContentView(binding.root)
    }
    dialog.create()
    runCatching {
        val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
    }
    val fileType = fileItem.getFileType()
    if (fileType != FileType.PDF) {
        binding.btnPrint.isVisible = false
    }
    if (fromDetails) {
        binding.btnRename.isVisible = false
        binding.btnDelete.isVisible = false
    }

    binding.dialogImage.setImageResource(fileType?.iconId?: R.drawable.ic_language)
    binding.dialogName.text = fileItem.displayName
    binding.dialogDesc.text = fileItem.path
    binding.btnRename.setOnClickListener {
        dialog.dismiss()
//        showRenameDialog(fileItem)
    }
    binding.btnPrint.setOnClickListener {
        dialog.dismiss()
//        runCatching {
//            val core = Document.openDocument(fileItem.path)
//            if (core.needsPassword()) {
//                core.destroy()
//                Toast.makeText(this, getString(R.string.pwd_protected_tips), Toast.LENGTH_LONG).show()
//                return@setOnClickListener
//            } else {
//                core.destroy()
//            }
//        }
//        (this as? BaseActivity<*>)?.let { act ->
//            AdConfig.mergeIntController.showFullScreenAd(act, eventName = "pdb_print_int", canShowAd = {
//                isTestDevice.not() && UserBlockRepository.isBuyUser()
//            }, dismissed = {
//                act.printContext?.let { ctx ->
//                    printPdfFile(ctx, fileItem)
//                }
//            })
//        }
//        EventPoster.send("print_click")
    }
    binding.btnShare.setOnClickListener {
//        shareFile(fileItem.path, fileItem.mimeType)
    }
    binding.btnCollection.setOnClickListener {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val findItem = app.database.fileItemDao().getFileByPath(fileItem.path)
//            val isFavorite = (findItem?.isFavorite ?: false).not()
//            val item = findItem ?: fileItem
//            item.isFavorite = isFavorite
//            withContext(Dispatchers.Main) {
//                binding.btnCollect.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                    if (fileItem.isFavorite) R.drawable.ic_bookmark_checked else R.drawable.ic_bookmark_no_check,
//                    0,
//                    0,
//                    0
//                )
//            }
//            app.database.fileItemDao().upsert(item)
//        }
//        EventPoster.send("pdf_bookmarks_click")
        dialog.dismiss()
    }
    binding.btnDelete.setOnClickListener {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val file = File(fileItem.path)
//            file.delete().let { success ->
//                if (success) {
//                    val findItem = app.database.fileItemDao().getFileByPath(fileItem.path)
//                    if (null != findItem) app.database.fileItemDao().delete(findItem)
//                    pdfList.remove(fileItem)
//                    app.notifyDeleteItem.postValue(fileItem.path)
//                    notifyMediaStoreFileDeleted(file)
//                }
//            }
//        }
        dialog.dismiss()
    }
//    lifecycleScope.launch(Dispatchers.IO) {
//        val findItem = app.database.fileItemDao().getFileByPath(fileItem.path)
//        if (null != findItem) {
//            withContext(Dispatchers.Main) {
//                binding.btnCollect.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                    if (findItem.isFavorite) R.drawable.ic_bookmark_checked else R.drawable.ic_bookmark_no_check,
//                    0,
//                    0,
//                    0
//                )
//            }
//        }
//    }
    dialog.show()
}