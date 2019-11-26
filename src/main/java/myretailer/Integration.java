package myretailer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import myretailer.model.*;
import myretailer.model.Order.OrderItem;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class Integration implements ToRuntimeException {

    public static void main(String[] args) {
        new Integration().run();
    }

    /**********************************************************************************************************
     *
     *    _____             __ _                       _   _
     *   / ____|           / _(_)                     | | (_)
     *  | |     ___  _ __ | |_ _  __ _ _   _ _ __ __ _| |_ _  ___  _ __
     *  | |    / _ \| '_ \|  _| |/ _` | | | | '__/ _` | __| |/ _ \| '_ \
     *  | |___| (_) | | | | | | | (_| | |_| | | | (_| | |_| | (_) | | | |
     *   \_____\___/|_| |_|_| |_|\__, |\__,_|_|  \__,_|\__|_|\___/|_| |_|
     *                            __/ |
     *                           |___/
     *
     *********************************************************************************************************/
                                                                                                             //
    private Properties loadSettings(){                                                                       //
        String configFilePath = System.getProperty("config-file");                                           //
        log.info("Configuration file: {}", configFilePath);                                                  //
        Properties properties = new Properties();                                                            //
        toRuntime(() -> properties.load(new FileInputStream(configFilePath)));                               //
        return properties;                                                                                   //
    }                                                                                                        //
                                                                                                             //
    private Properties configure(){                                                                          //
        Properties props = loadSettings();                                                                   //
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "legacy-integration");                                //
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass().getName());      //
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());                //
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, "3");                                             //
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");                                      //
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");                                      //
        props.put(                                                                                           //
                StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,                        //
                MyDeserializationExceptionHandler.class.getName());                                          //
        return props;                                                                                        //
    }                                                                                                        //
                                                                                                             //
    /*********************************************************************************************************/


    public void run() {

        final StreamsBuilder builder = new StreamsBuilder();

        RetailTopologyBuilder topologyBuilder = RetailTopologyBuilder.build(builder);
        Topology topology = topologyBuilder.getTopology();

        final KafkaStreams streams = new KafkaStreams(topology, configure());
        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread(
                () -> {
                    streams.close();
                    latch.countDown();
                },
                "streams-legacy-integration")
        );

        try {

            streams.start();
            ReadOnlyKeyValueStore<Integer, TblProduct> productStore =
                    streams.store(topologyBuilder.productsByIdStoreName(), QueryableStoreTypes.keyValueStore());
            ReadOnlyKeyValueStore<Integer, TblCustomer> customerStore =
                    streams.store(topologyBuilder.customersByIdStoreName(), QueryableStoreTypes.keyValueStore());
            Monitor.startMonitor(topology,productStore, customerStore);
            latch.await();
        } catch (final Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);

    }

}
