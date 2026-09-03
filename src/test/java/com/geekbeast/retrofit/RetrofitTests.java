package com.geekbeast.retrofit;


import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class RetrofitTests {
    public static final Map<String, String> M = ImmutableMap.of( "a", "b" );

    @Test
    public void testCreation() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor( chain -> new Response.Builder()
                        .request( chain.request() )
                        .protocol( Protocol.HTTP_1_1 )
                        .code( 200 )
                        .message( "OK" )
                        .body( ResponseBody.create( new byte[0], MediaType.get( "application/json" ) ) )
                        .build() )
                .build();
        final String baseURL = "http://localhost:8081/";
        Retrofit adapter = new Retrofit.Builder().baseUrl( baseURL ).client( httpClient )
                .addConverterFactory( new RhizomeByteConverterFactory() )
                .addConverterFactory( new RhizomeJacksonConverterFactory() )
                .addCallAdapterFactory( new RhizomeCallAdapterFactory() ).build();
        Api api = adapter.create( Api.class );
        Assert.assertNull( api.post( M ) );
    }
}
