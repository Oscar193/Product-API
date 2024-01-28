package com.textplus.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ElementNotFound extends NoSuchElementException {
    public ElementNotFound(String s) {
        super(s);
    }
}
