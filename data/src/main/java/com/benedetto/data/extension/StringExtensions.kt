package com.benedetto.data.extension

fun String.isValidZipCode(): Boolean {
    return this.matches(Regex("^[0-9]{5}$"))
}