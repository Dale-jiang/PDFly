package com.tb.pdfly.page.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tb.pdfly.databinding.DialogRateBinding
import com.tb.pdfly.databinding.DialogUninstallBinding
import kotlin.compareTo

class RateDialog(private val result: () -> Unit) : DialogFragment() {

    private var viewBinding: DialogRateBinding? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.decorView?.background = null
        dialog?.window?.apply {
            setGravity(Gravity.CENTER)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = DialogRateBinding.inflate(inflater, container, false)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(true)

        viewBinding?.apply {

            ratingBar.setOnRatingBarChangeListener { bar, rating, fromUser ->
                if (fromUser) {
                    if (rating <= 0f) {
                        btnSave.isEnabled = false
                        btnSave.alpha = 0.5f
                    } else {
                        btnSave.isEnabled = true
                        btnSave.alpha = 1f
                    }
                }
            }


            btnSave.setOnClickListener {
                dismiss()
                if (ratingBar.rating >= 4.5) {
                    result()
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

}