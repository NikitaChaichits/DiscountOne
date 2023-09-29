package com.digeltech.discountone.ui.categoryandshop

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.utils.MDUtil.ifNotZero
import com.digeltech.discountone.R
import com.digeltech.discountone.databinding.BottomFragmentPriceBinding
import com.digeltech.discountone.util.view.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetPriceFragment(private val priceFrom: Int, private val priceTo: Int) : BottomSheetDialogFragment() {

    private val binding by viewBinding(BottomFragmentPriceBinding::bind)

    interface BottomSheetListener {
        fun onSubmitClicked(input1: Int, input2: Int)
    }

    private var listener: BottomSheetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetFragmentTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_fragment_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            ivClose.setOnClickListener {
                dismiss()
            }

            priceFrom.ifNotZero {
                etPriceFrom.setText(it.toString())
            }
            priceTo.ifNotZero {
                etPriceTo.setText(it.toString())
            }

            btnSubmit.setOnClickListener {
                var input1 = etPriceFrom.text.toString().trim()
                var input2 = etPriceTo.text.toString().trim()

                if (input1.isNotEmpty() && input2.isNotEmpty() && input1.toInt() > input2.toInt()) {
                    it.context.toast("Value From must be less then value To")
                    etPriceFrom.text = null
                    etPriceTo.text = null
                    return@setOnClickListener
                }

                if (input1.isEmpty()) input1 = "0"
                if (input2.isEmpty()) input2 = "0"

                listener?.onSubmitClicked(input1.toInt(), input2.toInt())
                dismiss()
            }

            tvTrowOff.setOnClickListener {
                etPriceFrom.text = null
                etPriceTo.text = null
                listener?.onSubmitClicked(0, 0)
                dismiss()
            }
        }
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        this.listener = listener
    }
}
