package com.geekbeast.retrofit;


import java.nio.charset.StandardCharsets;
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

    // Non-ASCII on purpose: decoding with any charset other than UTF-8 changes this string.
    private static final String NON_ASCII_TEXT = "Ñandú café 研究 – ✓";

    @Test
    public void testCreation() {
        Api api = apiReturning( 200, "application/json", new byte[0] );
        Assert.assertNull( api.post( M ) );
    }

    @Test
    public void plainTextBodyIsDecodedAsUtf8() {
        Api api = apiReturning( 200, "text/plain; charset=utf-8", NON_ASCII_TEXT.getBytes( StandardCharsets.UTF_8 ) );
        Assert.assertEquals( NON_ASCII_TEXT, api.text() );
    }

    @Test
    public void errorBodyIsDecodedAsUtf8() {
        Api api = apiReturning( 400, "text/plain; charset=utf-8", NON_ASCII_TEXT.getBytes( StandardCharsets.UTF_8 ) );
        try {
            api.text();
            Assert.fail( "a 400 response must throw" );
        } catch ( RhizomeRetrofitCallException e ) {
            Assert.assertEquals( 400, e.getCode() );
            Assert.assertEquals( NON_ASCII_TEXT, e.getBody() );
        }
    }

    private static Api apiReturning( int code, String contentType, byte[] body ) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor( chain -> new Response.Builder()
                        .request( chain.request() )
                        .protocol( Protocol.HTTP_1_1 )
                        .code( code )
                        .message( code < 400 ? "OK" : "Bad Request" )
                        .body( ResponseBody.create( body, MediaType.get( contentType ) ) )
                        .build() )
                .build();
        final String baseURL = "http://localhost:8081/";
        Retrofit adapter = new Retrofit.Builder().baseUrl( baseURL ).client( httpClient )
                .addConverterFactory( new RhizomeByteConverterFactory() )
                .addConverterFactory( new RhizomeJacksonConverterFactory() )
                .addCallAdapterFactory( new RhizomeCallAdapterFactory() ).build();
        return adapter.create( Api.class );
    }
}
