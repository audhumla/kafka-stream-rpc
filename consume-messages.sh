docker run \
-i \
--net=host \
--rm \
confluentinc/cp-kafka \
kafka-console-consumer \
--bootstrap-server localhost:9092 --from-beginning --topic "word-count"