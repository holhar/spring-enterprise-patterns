package de.holhar.spring.patterns.mysql;

import de.holhar.spring.patterns.mysql.domain.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie, Long> {
}
