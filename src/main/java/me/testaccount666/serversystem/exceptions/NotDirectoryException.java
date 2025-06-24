package me.testaccount666.serversystem.exceptions;

import java.io.IOException;
import java.io.Serial;

public class NotDirectoryException extends IOException {

    @Serial
    private static final long serialVersionUID = 2917557613235459376L;

    public NotDirectoryException(String message) {
        super(message);
    }

    public NotDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotDirectoryException(Throwable cause) {
        super(cause);
    }

    public NotDirectoryException() {
    }
}

