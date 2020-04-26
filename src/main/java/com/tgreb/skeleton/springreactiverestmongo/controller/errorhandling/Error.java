package com.tgreb.skeleton.springreactiverestmongo.controller.errorhandling;

import lombok.Value;
import org.springframework.http.HttpStatus;

@Value(staticConstructor = "of")
class Error {
    final String message;
    final HttpStatus status;
}