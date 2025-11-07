package com.tb.pdfly.parameter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.review.ReviewManagerFactory
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.R
import com.tb.pdfly.databinding.DialogFileDetailsBinding
import com.tb.pdfly.databinding.DialogLoadingBinding
import com.tb.pdfly.databinding.DialogRenameBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.page.dialog.RateDialog
import com.tb.pdfly.utils.CommonUtils.isPdfEncrypted
import com.tb.pdfly.utils.CommonUtils.printPdfFile
import com.tb.pdfly.utils.alreadyRatedApp
import com.tb.pdfly.utils.defaultLocalCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

fun String.showLog(tag: String = "----PDFLY----") {
    if (BuildConfig.DEBUG) {
        Log.e(tag, this)
    }
}

fun Context.getScreenWidth(): Float {
    return resources.displayMetrics.widthPixels/ resources.displayMetrics.density
}


fun Context.isGrantedPostNotification(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else NotificationManagerCompat.from(this).areNotificationsEnabled()
}


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

fun buildUri(context: Context, path: String?): Uri? {
    if (path.isNullOrBlank()) return null
    return if (path.startsWith("/")) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        FileProvider.getUriForFile(context, context.packageName + ".pdfly.fileProvider", File(path))
    else File(path).toUri() else path.toUri()
}

fun Context.shareFile(path: String, mimeType: String) {

    runCatching {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType.ifBlank { "*/*" }
            putExtra(Intent.EXTRA_STREAM, buildUri(this@shareFile, path))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }.onFailure {
        if (mimeType.isNotEmpty() && mimeType != "*/*") {
            shareFile(path, "*/*")
        } else {
            Toast.makeText(this, getString(R.string.an_unknown_error_occurred), Toast.LENGTH_LONG).show()
        }
    }
}

fun Context.notifyMediaStoreFileDeleted(file: File) {
    if (!file.exists()) return
    MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), null) { path, uri ->
        runCatching {
            contentResolver.delete(uri, null, null)
        }
    }
    notifySystemToScan(file)
}


fun notifySystemToScan(file: File?) {
    if (file == null || !file.exists()) return
    val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
    intent.setData(("file://" + file.absolutePath).toUri())
    app.sendBroadcast(intent)
}

fun Context.notifyPdfUpdate(file: File) {
    if (!file.exists()) return
    MediaScannerConnection.scanFile(this, arrayOf(file.absolutePath), arrayOf("application/pdf")) { path, uri -> }
    notifySystemToScan(file)
}

fun Activity.showLoading(contentId: Int = R.string.loading): AlertDialog? {
    val binding = DialogLoadingBinding.inflate(LayoutInflater.from(this), window.decorView as ViewGroup, false)
    binding.textGeneral.text = getString(contentId)

    val dialog = MaterialAlertDialogBuilder(this)
        .setView(binding.root)
        .setCancelable(false)
        .show()
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    return dialog
}

fun AppCompatActivity.showFileDetailsDialog(fileItem: FileInfo, fromDetails: Boolean = false, callBack: CallBack = {}) {
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
        binding.btnOpen.isVisible = false
    }

    binding.dialogImage.setImageResource(fileType?.iconId ?: R.drawable.image_file_other)
    binding.dialogName.text = fileItem.displayName
    binding.dialogDesc.text = fileItem.path

    lifecycleScope.launch(Dispatchers.IO) {
        val findItem = database.fileInfoDao().getFileByPath(fileItem.path)
        if (null != findItem) {
            withContext(Dispatchers.Main) {
                binding.btnCollection.setImageResource(if (findItem.isCollection) R.drawable.ic_item_collection else R.drawable.ic_item_collection_grey)
            }
        }
    }

    binding.btnRename.setOnClickListener {
        dialog.dismiss()
        showRenameDialog(fileItem)
    }
    binding.btnPrint.setOnClickListener {
        dialog.dismiss()
        runCatching {

            if (isPdfEncrypted(this, fileItem.path)) {
                Toast.makeText(this, getString(R.string.pwd_protected_tips), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }
        (this as? BaseActivity<*>)?.let { act ->
            act.printContext?.let { ctx ->
                printPdfFile(ctx, fileItem)
            }
        }
    }

    binding.btnOpen.setOnClickListener {
        callBack.invoke()
        dialog.dismiss()
    }

    binding.btnShare.setOnClickListener {
        shareFile(fileItem.path, fileItem.mimeType)
    }
    binding.btnCollection.setOnClickListener {
        lifecycleScope.launch(Dispatchers.IO + SupervisorJob()) {
            val findItem = database.fileInfoDao().getFileByPath(fileItem.path)
            val isCollected = (findItem?.isCollection ?: false).not()
            val item = findItem ?: fileItem
            item.isCollection = isCollected
            withContext(Dispatchers.Main) {
                binding.btnCollection.setImageResource(if (fileItem.isCollection) R.drawable.ic_item_collection else R.drawable.ic_item_collection_grey)
            }
            database.fileInfoDao().upsert(item)
        }
        dialog.dismiss()
    }
    binding.btnDelete.setOnClickListener {
        lifecycleScope.launch(Dispatchers.IO + SupervisorJob()) {
            val file = File(fileItem.path)
            file.delete().let { success ->
                if (success) {
                    val findItem = database.fileInfoDao().getFileByPath(fileItem.path)
                    if (null != findItem) database.fileInfoDao().delete(findItem)
                    fileDeleteLiveData.postValue(fileItem.path)
                    notifyMediaStoreFileDeleted(file)
                }
            }
        }
        dialog.dismiss()
    }
    dialog.show()
}

fun AppCompatActivity.showRenameDialog(info: FileInfo) {
    val binding = DialogRenameBinding.inflate(LayoutInflater.from(this), window.decorView as ViewGroup, false)
    val dialog = Dialog(this).apply {
        setContentView(binding.root)
    }
    dialog.create()
    runCatching {
        dialog.window?.decorView?.background = null
        dialog.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
    binding.editInput.apply {
        val name = info.displayName.substringBeforeLast(".")
        setText(name)
        setSelection(name.length)
        requestFocus()
        isFocusable = true
        isFocusableInTouchMode = true
        postDelayed({
            val imm = this@showRenameDialog.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }, 200L)
    }
    binding.btnCancel.setOnClickListener { dialog.dismiss() }
    binding.btnSave.setOnClickListener {

        if (binding.editInput.text.toString().isBlank()) {
            Toast.makeText(this, getString(R.string.type_your_pdf_name), Toast.LENGTH_SHORT).show()
            return@setOnClickListener
        }

        lifecycleScope.launch(Dispatchers.IO + SupervisorJob()) {
            val originalFile = File(info.path)
            val afterName = "${binding.editInput.text.toString().replace(Regex("[\\\\/:*?\"<>|\\x00]"), "_")}.${info.displayName.substringAfterLast('.', "")}"
            val afterFile = File(originalFile.parent, afterName)

            if (afterFile.exists()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@showRenameDialog, getString(R.string.file_name_already_exists), Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val success = originalFile.renameTo(afterFile)
            if (success) {
                val findItem = database.fileInfoDao().getFileByPath(info.path)
                if (null != findItem) {
                    findItem.displayName = afterFile.name
                    findItem.path = afterFile.path
                    database.fileInfoDao().upsert(findItem)
                }
                info.apply {
                    path = afterFile.path
                    displayName = afterFile.name
                }
                changeNameLiveData.postValue(Pair(originalFile.path, afterFile.path))
                withContext(Dispatchers.Main) {
                    runCatching {
                        val imm = this@showRenameDialog.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.editInput.windowToken, 0)
                    }

                    dialog.dismiss()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@showRenameDialog, getString(R.string.an_unknown_error_occurred), Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
        }

    }
    dialog.show()
}


@Suppress("DEPRECATION")
fun Context.updateResources(): Context = run {
    val language = defaultLocalCode.ifBlank { Locale.getDefault().language }
    val locale = Locale(language)
    Locale.setDefault(locale)
    val configuration = resources.configuration
    configuration.setLocale(locale)
    resources.updateConfiguration(configuration, resources.displayMetrics)
    return createConfigurationContext(configuration)
}

fun AppCompatActivity.showRatingDialog() {
    if (alreadyRatedApp) return
    alreadyRatedApp = true
    RateDialog {
        runCatching {
            val manager = ReviewManagerFactory.create(this)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener { _ -> }
                }
            }
        }
    }.show(supportFragmentManager, "google_Rate")
}