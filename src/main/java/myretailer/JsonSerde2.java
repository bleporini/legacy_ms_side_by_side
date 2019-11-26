package myretailer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class JsonSerde2 <T> implements Serde<T>, ToRuntimeException{

    private static final ObjectMapper mapper = new ObjectMapper();
    abstract Class<T> getClazz();

    static <T> Serde<T> build(Class<T> clazz){
        return new JsonSerde2<T>() {
            @Override
            Class<T> getClazz() {
                return clazz;
            }
        };
    }


    @Override
    public Serializer<T> serializer() {
        return (topic, data) -> toRuntime(() -> mapper.writeValueAsBytes(data));
    }

    @Override
    public Deserializer<T> deserializer() {
        return (topic, data) -> data !=null ?
                toRuntime(() -> mapper.readValue(data, getClazz())) :
                null;
    }

}
