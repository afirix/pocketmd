package org.coursera.android.capstone.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessAttemptException extends RuntimeException {

	public UnauthorizedAccessAttemptException() {
		super();
	}

	public UnauthorizedAccessAttemptException(final String message) {
		super(message);
	}

	public UnauthorizedAccessAttemptException(final Throwable cause) {
		super(cause);
	}

	public UnauthorizedAccessAttemptException(final String message,
			final Throwable cause) {
		super(message, cause);
	}
}
