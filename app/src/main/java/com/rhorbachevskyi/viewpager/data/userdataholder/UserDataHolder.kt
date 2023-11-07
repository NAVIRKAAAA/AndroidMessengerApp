package com.rhorbachevskyi.viewpager.data.userdataholder

import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.domain.states.ApiState

object UserDataHolder {
    var states: ArrayList<Pair<Long, ApiState>> = ArrayList()

    var serverUsers: List<Contact> = listOf()

    var serverContacts: List<Contact> = listOf()

    lateinit var userData: UserResponse.Data

}
