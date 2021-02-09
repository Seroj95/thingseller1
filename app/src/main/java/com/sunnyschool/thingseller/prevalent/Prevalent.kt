package com.sunnyschool.thingseller.prevalent

import com.sunnyschool.thingseller.models.Users

object Prevalent {
    var currentOnlineUser: Users? = null
    const val UserEmailKey = "UserEmail"
    const val UserPasswordKey = "UserPassword"
}