package com.team9.api_gateway.common.exception;


public class NotFound extends ClientError {
    public NotFound(String errorMessage) {
        this.errorCode = "NotFound";
        this.errorMessage = errorMessage;
    }
}
