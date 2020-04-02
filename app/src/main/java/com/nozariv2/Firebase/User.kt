package com.nozariv2.Firebase

import java.io.Serializable

class User (
    var email: String? = null,
    var fullName: String? = null,
    var languageSelection: String? = null,
    var phoneNumber: String? = null,
    var tokens: Int? = null
) : Serializable