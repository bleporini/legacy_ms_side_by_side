package myretailer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import myretailer.model.TblCustomer;
import myretailer.model.TblProduct;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Monitor implements ToRuntimeException{
    private final HttpServer server;

    public Monitor(
            final Topology topology,
            final ReadOnlyKeyValueStore<Integer, TblProduct> productStore,
            final ReadOnlyKeyValueStore<Integer, TblCustomer> customerStore
            ) {
        RequestProcessor<TblCustomer> customerProcessor = new RequestProcessor<>(
                "/customers/(\\d++)",
                customerStore);
        RequestProcessor<TblProduct> productProcessor = new RequestProcessor<>(
                "/products/(\\d++)",
                productStore);


        server = toRuntime(() ->
                HttpServer.create(new InetSocketAddress(8000), 0)
        );

        server.createContext("/products", productProcessor::processByIdRequest);

        server.createContext("/products/count", productProcessor::processCountRequest);

        server.createContext("/customers", customerProcessor::processByIdRequest);

        server.createContext(
                "/topology/describe",
                exchange -> {
                    byte[] bytes = topology.describe().toString().getBytes();
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                });

        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static final class RequestProcessor<V>{
        private final Pattern pattern;
        private final ReadOnlyKeyValueStore<Integer, V> store;
        private final ObjectMapper mapper = new ObjectMapper();

        private RequestProcessor(String patternAsString, ReadOnlyKeyValueStore<Integer, V> store) {
            pattern = Pattern.compile(patternAsString);

            this.store = store;
        }


        void processByIdRequest(HttpExchange exchange) throws IOException {
            Matcher matcher = pattern.matcher(exchange.getRequestURI().getPath());
            if (!matcher.matches()) {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            }

            String idAsString = matcher.group(1);
            Integer id = Integer.decode(idAsString);

            V jsonNode = store.get(id);
            sendResponse(exchange, jsonNode);
        }

        void processCountRequest(HttpExchange exchange) throws IOException {
            long count = store.approximateNumEntries();
            System.out.println("count = " + count);
            sendResponse(exchange, count);
        }

        void sendResponse(final HttpExchange exchange, Object payload) throws IOException {
            byte[] bytes = mapper.writeValueAsBytes(payload);
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }

    }

    public static HttpServer startMonitor(
            final Topology topology,
            final ReadOnlyKeyValueStore<Integer, TblProduct> productStore,
            final ReadOnlyKeyValueStore<Integer, TblCustomer> customerStore) {
        Monitor monitor = new Monitor(topology, productStore, customerStore);
        return monitor.server;
    }
}
