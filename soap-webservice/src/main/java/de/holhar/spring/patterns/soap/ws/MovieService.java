package de.holhar.spring.patterns.soap.ws;

import org.springframework.stereotype.Service;

@Service
public class MovieService {

    public Movie findService(UID uid) {
        Movie movie = new Movie();
        movie.setId(uid);
        movie.setTitle("All Is Lost");
        movie.setDirector("J.C. Chandor");
        movie.setScreenwriter("J.C. Chandor");
        CastMember castMember = new CastMember();
        castMember.setActor("Robert Redfort");
        castMember.setCharacter("Our Man");
        movie.getCast().add(castMember);
        return movie;
    }
}
