package net.codinux.log.loki.quarkus.config;

import io.quarkus.runtime.annotations.ConfigGroup;

import java.util.Optional;

@ConfigGroup
public interface QuarkusAuthenticationConfig {

    /**
     * Basic auth username in case Loki is protected with basic auth.
     */
    Optional<String> username();

    /**
     * Basic auth password in case Loki is protected with basic auth.
     */
    Optional<String> password();

}