package de.holhar.spring.soap.client;

import de.holhar.spring.soap.ws.Movie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    private final WsClient wsClient;

    public WebController(WsClient wsClient) {
        this.wsClient = wsClient;
    }

    @GetMapping("/movies/{id}")
    public Movie getMovie(@PathVariable("id") String id) {
        return wsClient.getMovie(id);
    }
}
