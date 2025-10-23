package com.tb.pdfly.page.dialog

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.tb.pdfly.R
import com.tb.pdfly.databinding.DialogRenameBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RenameDialog(private val result: (String) -> Unit) : DialogFragment() {

    private var viewBinding: DialogRenameBinding? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.decorView?.background = null
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DialogRenameBinding.inflate(inflater, container, false)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)

        viewBinding?.apply {
            val fileName = "PDF_Create_${SimpleDateFormat("MMddyyyyHHMMss", Locale.getDefault()).format(Date(System.currentTimeMillis()))}"

            editInput.setText(fileName)
            editInput.setSelection(fileName.length)
            editInput.requestFocus()
            editInput.isFocusable = true
            editInput.isFocusableInTouchMode = true
            editInput.postDelayed({
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editInput, InputMethodManager.SHOW_IMPLICIT)
            }, 260L)


            btnSave.setOnClickListener {
                val name = editInput.text.toString()
                if (name.isEmpty()) {
                    Toast.makeText(requireActivity(), getString(R.string.type_your_pdf_name), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (isNameDuplicate("$name.pdf")) {
                    Toast.makeText(requireActivity(), getString(R.string.file_name_already_exists), Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (name.isNotEmpty()) {

                    result.invoke(name)

                    runCatching {
                        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editInput.windowToken, 0)
                    }

                    dismiss()
                }
            }

            btnCancel.setOnClickListener {
                result.invoke("")
                dismiss()
            }

        }


    }

    private fun isNameDuplicate(name: String): Boolean {
        val directory = File(Environment.getExternalStorageDirectory(), "${Environment.DIRECTORY_DOCUMENTS}${File.separator}pdf${File.separator}pdfly")
        if (!directory.exists() || !directory.isDirectory) {
            return false
        }
        return File(directory, name).exists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }


}