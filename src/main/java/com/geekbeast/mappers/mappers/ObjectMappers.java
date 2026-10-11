package com.geekbeast.mappers.mappers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Maps;

public final class ObjectMappers {
    public enum Mapper {
        YAML,
        SMILE,
        JSON
    }

    // TODO: Add options that for configuring serialization types supported.

    private static final Map<Mapper, ObjectMapper> mappers = Maps.newEnumMap( Mapper.class );

    static {
        mappers.put( Mapper.YAML, createYamlMapper() );
        mappers.put( Mapper.SMILE, createSmileMapper() );
        mappers.put( Mapper.JSON, createJsonMapper() );
    }

    private ObjectMappers() {}

    protected static ObjectMapper createYamlMapper() {
        return configureMapper( new ObjectMapper( new YAMLFactory() ) );
    }

    protected static ObjectMapper createSmileMapper() {
        return configureMapper( new ObjectMapper( new SmileFactory() ) );
    }

    protected static ObjectMapper createJsonMapper() {
        return configureMapper( new ObjectMapper() );
    }

    private static ObjectMapper configureMapper( ObjectMapper mapper ) {
        mapper.registerModule( new Jdk8Module() );
        mapper.registerModule( new JavaTimeModule() );
        mapper.registerModule( new GuavaModule() );
        mapper.registerModule( new BlackbirdModule() );
        mapper.registerModule( new KotlinModule.Builder().build() );
        mapper.configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false );
        mapper.disable( DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE );
        mapper.configure( MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, false );
        mapper.registerModule( setOrderModule() );
        return mapper;
    }

    /**
     * Reads every {@code Set} as a {@link LinkedHashSet}, so a value read and written again keeps
     * its stored order. The Jackson default is a {@code HashSet}, and for enums its order changes
     * with every JVM start, so re-saved JSON differs from the stored JSON.
     */
    public static SimpleModule setOrderModule() {
        return new SimpleModule( "PreserveSetOrder" ).addAbstractTypeMapping( Set.class, LinkedHashSet.class );
    }

    public static ObjectMapper getYamlMapper() {
        return ObjectMappers.getMapper( Mapper.YAML );
    }

    public static ObjectMapper getSmileMapper() {
        return ObjectMappers.getMapper( Mapper.SMILE );
    }

    public static ObjectMapper getJsonMapper() {
        return ObjectMappers.getMapper( Mapper.JSON );
    }

    public static ObjectMapper newJsonMapper() {
        return createJsonMapper();
    }

    public static ObjectMapper getMapper( Mapper type ) {
        return mappers.get( type );
    }

    public static void foreach( Consumer<ObjectMapper> object ) {
        mappers.values().forEach( object );
    }
}
