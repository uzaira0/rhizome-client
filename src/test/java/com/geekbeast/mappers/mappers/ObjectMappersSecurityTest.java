package com.geekbeast.mappers.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class ObjectMappersSecurityTest {

    @Test
    public void mappersRejectCaseInsensitiveProperties() {
        Assert.assertFalse(
                ObjectMappers.getJsonMapper().isEnabled( MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES ) );
        Assert.assertFalse(
                ObjectMappers.getYamlMapper().isEnabled( MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES ) );
        Assert.assertFalse(
                ObjectMappers.getSmileMapper().isEnabled( MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES ) );
        Assert.assertFalse(
                ObjectMappers.newJsonMapper().isEnabled( MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES ) );
    }

    @Test
    public void setsKeepTheirJsonOrder() throws Exception {
        // A HashSet iterates "a" before "b"; the stored order must survive a read and write.
        String json = "[\"b\",\"a\"]";
        for ( ObjectMapper mapper : new ObjectMapper[] {
                ObjectMappers.getJsonMapper(), ObjectMappers.newJsonMapper() } ) {
            Set<String> values = mapper.readValue( json, new TypeReference<Set<String>>() {} );
            Assert.assertEquals( json, mapper.writeValueAsString( values ) );
        }
    }
}
