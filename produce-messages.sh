docker run \
-i \
--net=host \
--rm \
confluentinc/cp-kafka \
kafka-console-producer \
--request-required-acks 1 --broker-list localhost:9092 --property "parse.key=true" --property "key.separator=:" --topic "word"