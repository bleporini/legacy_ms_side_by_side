package myretailer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.function.Function;

public class JsonSerde implements Serde<JsonNode>, ToRuntimeException{

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Serializer<JsonNode> serializer() {
        return (topic, data) -> toRuntime(() -> mapper.writeValueAsBytes(data));
    }

    @Override
    public Deserializer<JsonNode> deserializer() {
        return (topic, data) -> toRuntime(() -> mapper.readValue(data, JsonNode.class));
    }

    public Serde<JsonNode> serializeMap(
        Function<JsonNode, JsonNode> serializerMapper
    ){
        JsonSerde delegate = this;
        return new Serde<JsonNode>() {
            @Override
            public Serializer<JsonNode> serializer() {
                return (topic, data) ->
                        delegate.serializer().serialize(topic, serializerMapper.apply(data));
            }

            @Override
            public Deserializer<JsonNode> deserializer() {
                return delegate.deserializer();
            }
        };
    }
}
