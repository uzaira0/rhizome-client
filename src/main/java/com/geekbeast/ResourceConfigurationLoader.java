package com.geekbeast;

import com.geekbeast.mappers.mappers.ObjectMappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.geekbeast.rhizome.configuration.configuration.annotation.ReloadableConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Created by mtamayo on 7/3/17.
 */
public class ResourceConfigurationLoader {
    private static final Logger       logger = LoggerFactory.getLogger( ResourceConfigurationLoader.class );
    private static final ObjectMapper mapper = ObjectMappers.getYamlMapper();

    @Nullable
    public static ObjectMapper getYamlMapper() {
        return mapper;
    }

    public static <T> T loadConfiguration( Class<T> clazz ) {
        String uri = getReloadableConfigurationUri( clazz );
        return loadConfigurationFromResource( uri, clazz );
    }

    public static String getReloadableConfigurationUri( Class<?> clazz ) {
        ReloadableConfiguration config = clazz.getAnnotation( ReloadableConfiguration.class );
        if ( config != null ) {
            if ( StringUtils.isBlank( config.uri() ) ) {
                return clazz.getCanonicalName();
            } else {
                return config.uri();
            }
        } else {
            return null;
        }
    }

    public static <T> T loadConfigurationFromFile( String path, Class<T> clazz ) {
        String uri = getReloadableConfigurationUri( clazz );
        if ( uri == null ) {
            logger.warn( "No @ReloadableConfiguration URI found for class {}", clazz.getCanonicalName() );
            return null;
        }
        try {
            Path p = Path.of( path, uri );
            File f = p.toFile();
            if ( !f.exists() ) {
                logger.warn( "Configuration file does not exist: {}", p );
                return null;
            }
            return mapper.readValue( f, clazz );
        } catch ( IOException e ) {
            logger.warn( "Failed to load configuration from {} for {}: {}", path, clazz, e.getMessage(), e );
            return null;
        }
    }

    public static <T> T loadConfigurationFromResource( String uri, Class<T> clazz ) {
        if ( uri == null ) {
            logger.warn( "Cannot load configuration from null URI for class {}", clazz.getCanonicalName() );
            return null;
        }
        String yamlString = null;
        try {
            URL resource = Resources.getResource( uri );
            yamlString = Resources.toString( resource, StandardCharsets.UTF_8 );
            if ( StringUtils.isBlank( yamlString ) ) {
                logger.warn( "Configuration resource {} is blank", uri );
                return null;
            }
        } catch ( IOException | IllegalArgumentException e ) {
            logger.warn( "Failed to load resource from {}: {}", uri, e.getMessage(), e );
            return null;
        }
        try {
            return mapper.readValue( yamlString, clazz );
        } catch ( IOException e ) {
            logger.error( "Failed to deserialize configuration for {}: {}", uri, e.getMessage(), e );
            return null;
        }
    }
}
