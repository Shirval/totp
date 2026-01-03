package com.shirval.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends TotpException {

    private final PublicError publicError = new PublicError("notFound", "Requested entity not found");

    public NotFoundException() {
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    @Override
    public int geHttpCode() {
        return HttpStatus.NOT_FOUND.value();
    }

    @Override
    public PublicError getPublicError() {
        return publicError;
    }
}
