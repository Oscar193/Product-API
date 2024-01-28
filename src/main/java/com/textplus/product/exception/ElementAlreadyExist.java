package com.textplus.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ElementAlreadyExist extends RuntimeException {
    public ElementAlreadyExist(String message) {
        super(message);
    }

    public ElementAlreadyExist(String message, String... params) {
        super(String.format(message, params));
    }

}
