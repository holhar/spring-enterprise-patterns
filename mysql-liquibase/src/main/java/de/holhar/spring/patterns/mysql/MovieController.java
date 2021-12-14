package de.holhar.spring.patterns.mysql;

import de.holhar.spring.patterns.mysql.domain.Movie;
import de.holhar.spring.patterns.mysql.exception.MovieNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository repository;

    public MovieController(MovieRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Movie getMovie(@PathVariable("id") long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException("Movie with id '" + id + "' does not exist"));
    }

    /*
     * curl -X POST "http://localhost:8884/movies" -H"content-type:application/json" --data '{"releaseDate":"1999-11-04T13:12:13.206580Z","title":"Fight Club","director":"David Fincher"}'
     */
    @PostMapping
    public ResponseEntity<Void> postMovie(@RequestBody Movie movie) {
        repository.save(movie);
        return ResponseEntity.ok(null);
    }
}
