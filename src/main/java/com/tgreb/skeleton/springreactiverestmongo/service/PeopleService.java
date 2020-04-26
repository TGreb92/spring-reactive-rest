package com.tgreb.skeleton.springreactiverestmongo.service;

import com.tgreb.skeleton.springreactiverestmongo.model.People;
import com.tgreb.skeleton.springreactiverestmongo.repository.PeopleRepository;
import com.tgreb.skeleton.springreactiverestmongo.util.ServiceHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
@Slf4j
public class PeopleService {

    private final PeopleRepository peopleRepository;

    public Mono<List<People>> findAll(){
        return peopleRepository.findAll()
                .collectList();
    }

    public Mono<People> findById(String id){
        return peopleRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("People not found with id: ".concat(id))));
    }

    public Mono<People> create(People people){
        return peopleRepository.insert(people)
                .doOnNext(entity -> log.info("Saved new people: {}", entity));
    }

    public Mono<People> update(String id, People people){
        return findById(id)
                .map(foundEntity -> ServiceHelper.merge(people, foundEntity))
                .doOnNext(entity -> log.info("Updating people: {}", entity))
                .flatMap(peopleRepository::save);
    }

    public Mono<Void> delete(String id){
        return peopleRepository.deleteById(id);
    }
}
