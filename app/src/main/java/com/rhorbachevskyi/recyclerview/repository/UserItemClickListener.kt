package com.rhorbachevskyi.recyclerview.repository

import com.rhorbachevskyi.recyclerview.domain.model.User

interface UserItemClickListener {
    fun onUserDelete(user: User, position: Int)
}
