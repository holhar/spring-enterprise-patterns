package de.holhar.spring.patterns.jaxbdatabinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class JaxbDataBindingApplication {

    private static final Logger logger = LoggerFactory.getLogger(JaxbDataBindingApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(JaxbDataBindingApplication.class, args);
    }

    @PostConstruct
    private void init() {
        Movie movie = new Movie();
        movie.setTitle("All Is Lost");
        movie.setDirector("J.C. Chandor");
        movie.setScreenwriter("J.C. Chandor");
        CastMember castMember = new CastMember();
        castMember.setActor("Robert Redfort");
        castMember.setCharacter("Our Man");
        movie.getCast().add(castMember);
        logger.info("Created '{}'", movie);
    }
}
