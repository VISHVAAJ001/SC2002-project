package com.ntu.fdae.group1.bto.exceptions;

/**
* Constructs a new WeakPasswordException with the specified detail message.
* @param message the detail message.
*/
public class WeakPasswordException extends Exception {
    public WeakPasswordException(String message) {
        super(message);
    }
}
