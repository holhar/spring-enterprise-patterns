package de.holhar.spring.patterns.redis.sessioncache;

import de.holhar.spring.patterns.redis.sessioncache.service.GreetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class WebController {

    private final GreetingService greetingService;

    public WebController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    @GetMapping("/user/hello")
    public String userHello(Principal principal) {
        return greetingService.getGreeting(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Should not happen"))
                .getValue();
    }
}
