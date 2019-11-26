package myretailer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.List;
import java.util.function.Function;

public abstract class JsonListSerde<T> implements Serde<List<T>>, ToRuntimeException{

    private static final ObjectMapper mapper = new ObjectMapper();

    static <T> Serde<List<T>> build(Class<T> clazz){
        return new JsonListSerde<T>() {};
    }


    @Override
    public Serializer<List<T>> serializer() {
        return (topic, data) -> toRuntime(() -> mapper.writeValueAsBytes(data));
    }

    @Override
    public Deserializer<List<T>> deserializer() {
        return (topic, data) -> data !=null ?
                toRuntime(() -> mapper.readValue(data, List.class)) :
                null;
    }

}
