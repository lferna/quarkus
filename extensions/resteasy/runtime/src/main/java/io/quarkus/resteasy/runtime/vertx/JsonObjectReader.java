package io.quarkus.resteasy.runtime.vertx;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

/**
 * A body reader that allows to get a Vert.x {@link JsonObject} as JAX-RS request content.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonObjectReader implements MessageBodyReader<JsonObject> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == JsonObject.class;
    }

    @Override
    public JsonObject readFrom(Class<JsonObject> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        byte[] bytes = getBytes(entityStream);
        if (bytes.length == 0) {
            throw new NoContentException("Cannot create JsonObject");
        }
        return new JsonObject(Buffer.buffer(bytes));
    }

    private static byte[] getBytes(InputStream entityStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = entityStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        return baos.toByteArray();
    }
}
