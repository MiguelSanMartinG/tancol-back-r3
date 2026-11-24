package com.main.tancol.common.exceptions.exeption;

/** La fecha de inicio es posterior a la de fin. Se responde con 422. */
public class RangoFechasInvalidoException extends RuntimeException {
    public RangoFechasInvalidoException(String message) {
        super(message);
    }
}
