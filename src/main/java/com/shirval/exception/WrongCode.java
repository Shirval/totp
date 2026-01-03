package com.shirval.exception;

import org.springframework.http.HttpStatus;

public class WrongCode extends TotpException {

    private final PublicError publicError = new PublicError("wrongCode", "Wrong code");

    @Override
    public PublicError getPublicError() {
        return publicError;
    }

    @Override
    public int geHttpCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
