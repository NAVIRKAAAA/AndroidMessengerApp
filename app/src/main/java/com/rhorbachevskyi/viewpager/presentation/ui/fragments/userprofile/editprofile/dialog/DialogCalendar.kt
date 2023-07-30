package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentDialogCalendarBinding
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.interfaces.DialogCalendarListener
import java.util.Calendar

class DialogCalendar : AppCompatDialogFragment() {
    private lateinit var binding: FragmentDialogCalendarBinding

    private var listener: DialogCalendarListener? = null

    fun setListener(listener: DialogCalendarListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_dialog_calendar, null)
        val builder = AlertDialog.Builder(requireContext()).setView(dialogView)
        binding = FragmentDialogCalendarBinding.bind(dialogView)
        setListeners()
        return builder.create()
    }

    private fun setListeners() {
        val calendar = Calendar.getInstance()
        var date = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        with(binding) {
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                date = "$dayOfMonth/${(month + 1)}/$year"
            }
            textViewSave.setOnClickListener {
                listener?.onDateSelected(date)
                dismiss()
            }

            textViewCancel.setOnClickListener {
                dismiss()
            }
        }
    }
}