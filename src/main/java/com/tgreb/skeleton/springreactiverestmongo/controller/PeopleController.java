package com.tgreb.skeleton.springreactiverestmongo.controller;

import com.tgreb.skeleton.springreactiverestmongo.model.People;
import com.tgreb.skeleton.springreactiverestmongo.service.PeopleService;
import com.tgreb.skeleton.springreactiverestmongo.util.ApiHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@AllArgsConstructor
public class PeopleController {
    private final PeopleService peopleService;

    public Mono<ServerResponse> findAll(ServerRequest serverRequest){
        return peopleService.findAll()
                .flatMap(response -> ApiHelper.makeJsonResponse(HttpStatus.OK, response));
    }

    public Mono<ServerResponse> findById(ServerRequest serverRequest){
        return ApiHelper.getIdFromRequest(serverRequest)
                .flatMap(peopleService::findById)
                .flatMap(response -> ApiHelper.makeJsonResponse(HttpStatus.OK, response));
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest){
        return serverRequest.bodyToMono(People.class)
                .flatMap(peopleService::create)
                .flatMap(response -> ApiHelper.makeJsonResponse(HttpStatus.CREATED, response));
    }

    public Mono<ServerResponse> update(ServerRequest serverRequest){
        return ApiHelper.getIdFromRequest(serverRequest)
                .flatMap(id ->
                    serverRequest.bodyToMono(People.class)
                            .flatMap(people -> peopleService.update(id, people)))
                .flatMap(response -> ApiHelper.makeJsonResponse(HttpStatus.ACCEPTED, response));
    }

    public Mono<ServerResponse> deleteById(ServerRequest serverRequest){
        return ApiHelper.getIdFromRequest(serverRequest)
                .flatMap(peopleService::delete)
                .flatMap(response -> ApiHelper.makeResponseWithoutBody(HttpStatus.OK));
    }
}
