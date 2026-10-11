package com.geekbeast;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RetiredConfigurationKeys extends DeserializationProblemHandler {
    private static final Logger logger = LoggerFactory.getLogger( RetiredConfigurationKeys.class );
    // Names keep rhizome-client independent of the server-side configuration classes.
    private static final Map<String, Set<String>> RETIRED_KEYS = Map.of(
            "com.geekbeast.rhizome.configuration.RhizomeConfiguration", Set.of(
                    "session-clustering-enabled", "cors-access-control-allow-origin-url",
                    "spark", "hazelcast-session-filter" ),
            "com.geekbeast.rhizome.configuration.hazelcast.HazelcastConfiguration", Set.of(
                    "password", "replication-factor" ),
            "com.geekbeast.rhizome.configuration.graphite.GraphiteConfiguration", Set.of(
                    "host", "port", "console-enabled" ),
            "com.geekbeast.authentication.Auth0Configuration", Set.of( "users" ) );
    private static final Set<String> warnedKeys = ConcurrentHashMap.newKeySet();

    @Override
    public boolean handleUnknownProperty( DeserializationContext context, JsonParser parser,
            JsonDeserializer<?> deserializer, Object beanOrClass, String propertyName ) throws IOException {
        Class<?> type = beanOrClass instanceof Class<?> ? (Class<?>) beanOrClass : beanOrClass.getClass();
        if ( !RETIRED_KEYS.getOrDefault( type.getName(), Set.of() ).contains( propertyName ) ) {
            return false;
        }
        parser.skipChildren();
        if ( warnedKeys.add( type.getName() + "." + propertyName ) ) {
            logger.warn( "Retired configuration key {}.{} is ignored; remove it from your configuration.",
                    type.getSimpleName(), propertyName );
        }
        return true;
    }
}
