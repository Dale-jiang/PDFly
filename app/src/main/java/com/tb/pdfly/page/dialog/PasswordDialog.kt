package com.tb.pdfly.page.dialog

import android.content.Context
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.ahmer.pdfium.PdfiumCore
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tb.pdfly.R
import com.tb.pdfly.databinding.DialogPasswordBinding
import java.io.File

class PasswordDialog(private val path: String, private val result: (String) -> Unit) : BottomSheetDialogFragment() {

    private var viewBinding: DialogPasswordBinding? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.decorView?.background = null
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DialogPasswordBinding.inflate(inflater, container, false)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)

        viewBinding?.apply {

            editInput.requestFocus()
            editInput.isFocusable = true
            editInput.isFocusableInTouchMode = true
            editInput.postDelayed({
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editInput, InputMethodManager.SHOW_IMPLICIT)
            }, 250L)


            btnSave.setOnClickListener {
                if (editInput.text.toString().isBlank()) {
                    Toast.makeText(requireActivity(), getString(R.string.please_enter_password), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val pwd = editInput.text.toString()
                if (isPdfPasswordCorrect(path, pwd)) {
                    result.invoke(pwd)
                    dismiss()

                    runCatching {
                        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editInput.windowToken, 0)
                    }

                } else {
                    Toast.makeText(requireActivity(), getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show()
                }
            }

            btnCancel.setOnClickListener {
                result.invoke("")
                dismiss()
            }

        }


    }

    private fun isPdfPasswordCorrect(filePath: String, password: String): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        val pdfiumCore = PdfiumCore(requireActivity())
        var fd: ParcelFileDescriptor? = null
        return try {
            fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfiumCore.newDocument(fd, password)
            pdfiumCore.close()
            true
        } catch (e: Exception) {
            false
        } finally {
            fd?.close()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }


}