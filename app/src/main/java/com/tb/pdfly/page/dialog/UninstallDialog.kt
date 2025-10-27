package com.tb.pdfly.page.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tb.pdfly.databinding.DialogUninstallBinding

class UninstallDialog(private val result: (Boolean) -> Unit) : DialogFragment() {

    private var viewBinding: DialogUninstallBinding? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.decorView?.background = null
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DialogUninstallBinding.inflate(inflater, container, false)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)

        viewBinding?.apply {

            btnSave.setOnClickListener {
                result.invoke(false)
                dismiss()

            }

            btnCancel.setOnClickListener {
                result.invoke(true)
                dismiss()
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

}