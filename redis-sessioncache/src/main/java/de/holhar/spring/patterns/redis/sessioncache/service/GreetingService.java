package de.holhar.spring.patterns.redis.sessioncache.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GreetingService {

    private final GreetingRepository greetingRepository;

    public GreetingService(GreetingRepository greetingRepository) {
        this.greetingRepository = greetingRepository;
    }

    @Cacheable(value = "greetings", key = "#name", unless = "#result == null")
    public Optional<Greeting> getGreeting(String name) {
        return Optional.of(greetingRepository.getGreeting(name));
    }
}
