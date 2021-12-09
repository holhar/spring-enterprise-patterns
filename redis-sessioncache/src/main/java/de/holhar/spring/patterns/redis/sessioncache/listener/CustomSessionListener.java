package de.holhar.spring.patterns.redis.sessioncache.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * CustomSessionListener might execute special operations for an expired internal Spring session, e.g. invalidate
 * OAuth 2.0 tokens or log out the corresponding user at an SSO/IDP.
 */
public class CustomSessionListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(CustomSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        logger.debug("Session created '{}'", sessionEvent.getSession().getId());
        // Nothing to do
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        logger.debug("Session destroyed '{}'", sessionEvent.getSession().getId());
        var session = sessionEvent.getSession();

        Object attribute = session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        if (attribute instanceof SecurityContextImpl) {
            var authentication = ((SecurityContextImpl) attribute).getAuthentication();
            logger.info("user '{}' logged out", authentication.getName());
        }
    }
}