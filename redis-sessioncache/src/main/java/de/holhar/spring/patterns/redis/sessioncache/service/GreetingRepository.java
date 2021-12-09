package de.holhar.spring.patterns.redis.sessioncache.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class GreetingRepository {

    private static final Logger logger = LoggerFactory.getLogger(GreetingRepository.class);

    public Greeting getGreeting(String name) {
        logger.info("Should only be executed once!");
        Greeting greeting = new Greeting();
        greeting.setValue("Hello " + name + "!");
        return greeting;
    }
}
