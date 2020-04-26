package com.tgreb.skeleton.springreactiverestmongo.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

@UtilityClass
public class ApiHelper {
    private final static Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private final static String ID_PATH_VARIABLE_NAME = "id";

    public void validate(final Object object) {
        var violations = VALIDATOR.validate(object);
        if (!violations.isEmpty()) {
            var message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .findFirst();
            throw new IllegalArgumentException(message.get());
        }
    }

    public Mono<String> getIdFromRequest(ServerRequest serverRequest){
        return BlockingWrapper.execute(() -> serverRequest.pathVariable(ID_PATH_VARIABLE_NAME));
    }

    public <T> Mono<ServerResponse> makeJsonResponse(HttpStatus httpStatus, T body){
        return ServerResponse
                .status(httpStatus)
                .bodyValue(body);
    }

    public Mono<ServerResponse> makeResponseWithoutBody(HttpStatus httpStatus){
        return ServerResponse
                .status(httpStatus)
                .build();
    }


}
