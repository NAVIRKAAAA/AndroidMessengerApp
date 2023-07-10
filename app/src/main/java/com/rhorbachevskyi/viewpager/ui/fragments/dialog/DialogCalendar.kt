package com.rhorbachevskyi.viewpager.ui.fragments.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentDialogCalendarBinding
import com.rhorbachevskyi.viewpager.ui.fragments.userprofile.interfaces.DialogCalendarListener

class DialogCalendar : AppCompatDialogFragment() {
    private lateinit var binding: FragmentDialogCalendarBinding

    private var listener: DialogCalendarListener? = null

    fun setListeners(listener: DialogCalendarListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.fragment_dialog_calendar, null)
        var date = ""
        binding = FragmentDialogCalendarBinding.bind(dialogView)
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            date = "$dayOfMonth/${(month + 1)}/$year"
        }
        builder.setView(dialogView).setPositiveButton(R.string.save){ _, _ ->
            listener?.onDateSelected(date)

        }.setNegativeButton(R.string.cancel){_,_ ->
            dismiss()
        }
        return builder.create()
    }
}