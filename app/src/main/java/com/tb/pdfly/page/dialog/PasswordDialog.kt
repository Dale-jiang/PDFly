package com.tb.pdfly.page.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.tb.pdfly.R
import com.tb.pdfly.databinding.DialogPasswordBinding
import com.tb.pdfly.utils.CommonUtils.isPdfPasswordCorrect

class PasswordDialog(private val path: String, private val result: (String) -> Unit) : DialogFragment() {

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
                if (isPdfPasswordCorrect(requireActivity(), path, pwd)) {
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


    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }


}