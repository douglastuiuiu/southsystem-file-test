package com.douglasog87.exceptions;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MissingEnvironmentVariableException extends Exception {
    public MissingEnvironmentVariableException(String message) {
        super(message);
    }
}
