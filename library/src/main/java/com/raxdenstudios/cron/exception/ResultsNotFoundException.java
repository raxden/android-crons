package com.raxdenstudios.cron.exception;

/**
 * Exception throw by the application when a Cron search can't return a valid result.
 */
public class ResultsNotFoundException extends Exception {

    public ResultsNotFoundException() {
        super();
    }

    public ResultsNotFoundException(final String message) {
        super(message);
    }

    public ResultsNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ResultsNotFoundException(final Throwable cause) {
        super(cause);
    }

}
