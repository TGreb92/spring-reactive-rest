package com.tgreb.skeleton.springreactiverestmongo.controller.errorhandling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
@Order(-2)
@Slf4j
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final List<Class> BAD_REQUEST_ERRORS = List.of(IllegalArgumentException.class, IllegalStateException.class);
    private static final List<Class> NOT_FOUND_ERRORS = List.of(NoSuchElementException.class);

    public GlobalErrorWebExceptionHandler(final GlobalErrorAttributes globalErrorAttributes,
                                          final ApplicationContext applicationContext,
                                          final ServerCodecConfigurer serverCodecConfigurer) {
        super(globalErrorAttributes, new ResourceProperties(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(final ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        var errorPropertiesMap = getErrorAttributes(request, false);
        var errorKey = DefaultErrorAttributes.class.getCanonicalName() + ".ERROR";
        var attribute = request.attribute(errorKey);
        var throwable = (Throwable) attribute.get();
        var error = getError(throwable);

        errorPropertiesMap.put("errorhandling", throwable.getClass().getSimpleName());
        errorPropertiesMap.put("status", error.getStatus().value());

        return ServerResponse.status(error.getStatus())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(errorPropertiesMap));
    }

    private Error getError(final Throwable exception) {
        log.error("Something went wrong with the Http request: ", exception);
        return handleBadRequest.apply(exception)
                .or(() -> handleNotFound.apply(exception))
                .orElse(handleInternalServerError.apply(exception));
    }

    private Optional<Error> getHttpError(final List<Class> throwables, final Throwable exception, final HttpStatus status) {
        return throwables.stream()
                .filter(c -> c.isInstance(exception))
                .map(c -> mapToError.apply(exception, status))
                .findFirst();
    }

    private static BiFunction<Throwable, HttpStatus, Error> mapToError =
            (exception, status) -> Error.of(exception.getMessage(), status);

    private Function<Throwable, Optional<Error>> handleBadRequest =
            ex -> getHttpError(BAD_REQUEST_ERRORS, ex, HttpStatus.BAD_REQUEST);

    private Function<Throwable, Optional<Error>> handleNotFound =
            ex -> getHttpError(NOT_FOUND_ERRORS, ex, HttpStatus.NOT_FOUND);

    private Function<Throwable, Error> handleInternalServerError =
            ex -> mapToError.apply(ex, HttpStatus.INTERNAL_SERVER_ERROR);
}
