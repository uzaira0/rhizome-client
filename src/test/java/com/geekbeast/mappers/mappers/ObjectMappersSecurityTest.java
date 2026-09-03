package com.geekbeast.mappers.mappers;

import com.fasterxml.jackson.databind.MapperFeature;
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
}
