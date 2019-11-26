package myretailer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Map;

public class MyDeserializationExceptionHandler implements DeserializationExceptionHandler {
    @Override
    public DeserializationHandlerResponse handle(
            ProcessorContext context, ConsumerRecord<byte[], byte[]> record, Exception exception) {

        String key = new String(record.key());
        String value = new String(record.value());
        System.err.println("key = " + key);
        System.err.println("value = " + value);
        exception.printStackTrace();
        return DeserializationHandlerResponse.FAIL;
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
