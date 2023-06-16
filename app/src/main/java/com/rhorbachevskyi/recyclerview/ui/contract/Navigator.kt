package com.rhorbachevskyi.recyclerview.ui.contract

import androidx.fragment.app.Fragment
import com.rhorbachevskyi.recyclerview.domain.model.User

fun Fragment.navigator(): Navigator {
    return requireActivity() as Navigator
}

interface Navigator {
    fun showContactsScreen(user: User)
}
