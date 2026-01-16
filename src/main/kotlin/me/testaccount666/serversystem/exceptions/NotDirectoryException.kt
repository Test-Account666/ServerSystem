package me.testaccount666.serversystem.exceptions

import java.io.IOException
import java.io.Serial

class NotDirectoryException : IOException {
    constructor(message: String?) : super(message)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(cause: Throwable?) : super(cause)

    constructor()

    companion object {
        @Serial
        private const val serialVersionUID = 2917557613235459376L
    }
}

