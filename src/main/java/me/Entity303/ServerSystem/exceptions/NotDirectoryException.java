package me.Entity303.ServerSystem.exceptions;

import java.io.IOException;

public class NotDirectoryException extends IOException {

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
