FROM confluentinc/cp-kafka-connect

RUN confluent-hub install --no-prompt debezium/debezium-connector-mysql:0.10.0
RUN confluent-hub install --no-prompt  mongodb/kafka-connect-mongodb:0.2
