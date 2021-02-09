package com.sunnyschool.design.extension

fun String?.isValidEmail(): Boolean {
    val pattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+")
    return !this.isNullOrEmpty() && this.matches(pattern)
}