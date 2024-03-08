package com.task.ktsimple.exceptions

import com.task.ktsimple.enums.AuthErrors
import com.task.ktsimple.enums.AuthState

/**
 * Created by Admin on 08,March,2024
 */
class AuthException(val authErrors: AuthErrors) : Exception()