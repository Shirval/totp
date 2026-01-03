package com.shirval.exception;

import org.springframework.http.HttpStatus;

public class TotpException extends RuntimeException implements HttpCodedException, PublicException {

    public static final PublicError publicError = new PublicError("internalException", "Internal error");

    public TotpException() {
    }

    public TotpException(String message) {
        super(message);
    }

    public TotpException(String message, Throwable cause) {
        super(message, cause);
    }

    public TotpException(Throwable cause) {
        super(cause);
    }

    @Override
    public int geHttpCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @Override
    public PublicError getPublicError() {
        return publicError;
    }
}
