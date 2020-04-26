package com.tgreb.skeleton.springreactiverestmongo.util;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Callable;

@UtilityClass
public class BlockingWrapper {

    public <T> Mono<T> execute(Callable<T> callable){
        return Mono.fromCallable(callable)
                .subscribeOn(Schedulers.boundedElastic());
    }

}
