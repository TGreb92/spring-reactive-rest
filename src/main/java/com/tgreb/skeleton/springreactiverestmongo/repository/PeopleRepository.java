package com.tgreb.skeleton.springreactiverestmongo.repository;

import com.tgreb.skeleton.springreactiverestmongo.model.People;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeopleRepository extends ReactiveMongoRepository<People, String> {
}
