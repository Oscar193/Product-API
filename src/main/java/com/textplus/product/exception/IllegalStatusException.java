package com.textplus.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class IllegalStatusException extends RuntimeException {
    public IllegalStatusException(String s) {
        super(s);
    }
}
