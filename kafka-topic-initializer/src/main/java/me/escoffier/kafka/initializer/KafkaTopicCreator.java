package me.escoffier.kafka.initializer;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.common.annotation.Identifier;
import jakarta.inject.Inject;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@QuarkusMain
public class KafkaTopicCreator implements QuarkusApplication {


    private static final int DEFAULT_ADMIN_CLIENT_TIMEOUT = 5000;

    @Inject
    @Identifier("default-kafka-broker")
    Map<String, Object> config;

    @Inject
    Logger logger;

    @ConfigProperty(name = "kafka.topics")
    String topics;

    private AdminClient client;

    @Override
    public int run(String... args) {
        List<TopicDeclaration> list = parse(topics);

        Map<String, Object> conf = new HashMap<>();
        conf.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, DEFAULT_ADMIN_CLIENT_TIMEOUT);
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            if (AdminClientConfig.configNames().contains(entry.getKey())) {
                conf.put(entry.getKey(), entry.getValue().toString());
            }
        }
        client = AdminClient.create(conf);

        try {
            logger.infof("Retrieving all the topics...");
            List<TopicDeclaration> toBeCreated = new ArrayList<>();
            Set<TopicDeclaration> toBeDescribed = new HashSet<>();
            List<NewTopic> creationRequests = new ArrayList<>();

            var topics = client.listTopics(new ListTopicsOptions().listInternal(false)).names().get();

            for (TopicDeclaration td : list) {
                if (topics.contains(td.name())) {
                    toBeDescribed.add(td);
                } else {
                    toBeCreated.add(td);
                }
            }

            // Analyze existing topics
            if (!toBeDescribed.isEmpty()) {
                logger.infof("Retrieving topic description for %s", toBeDescribed.stream().map(td -> td.name).collect(Collectors.joining(",")));
                var descriptions = client.describeTopics(toBeDescribed.stream().map(td -> td.name).collect(Collectors.toList())).allTopicNames().get();

                for (var declaration : toBeDescribed) {
                    TopicDescription description = descriptions.get(declaration.name);
                    logger.infof("Topic %s already exists with %d partitions", description.name(), description.partitions().size());

                    if (declaration.numberOfPartitions != -1) {
                        if (description.partitions().size() == declaration.numberOfPartitions) {
                            logger.infof("Topic %s has the expected number of partitions (%d)", description.name(), declaration.numberOfPartitions);
                        } else {
                            logger.errorf("The topic %s already exist,  but does not have the expected number of partition (%d != %d)", description.name(), declaration.numberOfPartitions, description.partitions().size());
                            return 1;
                        }
                    }
                }
            }

            // Create non-existing topics
            if (!toBeCreated.isEmpty()) {
                for (TopicDeclaration declaration : toBeCreated) {
                    logger.infof("Topic %s does not exist", declaration);
                    Optional<Integer> numberOfPartitions = declaration.numberOfPartitions == -1 ? Optional.empty() : Optional.of(declaration.numberOfPartitions);
                    Optional<Short> replication = declaration.replication == -1 ? Optional.empty() : Optional.of(declaration.replication);

                    NewTopic newTopic = new NewTopic(declaration.name, numberOfPartitions, replication);
                    logger.infof("Topic %s does not exist, creating new topic %s", declaration.name, newTopic);
                    creationRequests.add(newTopic);
                }
                logger.infof("Creating topics: %s", creationRequests.stream().map(NewTopic::name).collect(Collectors.joining(",")));
                client.createTopics(creationRequests).all().get();
                logger.infof("Topics created.");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Unable to describe or create topics", e);
            return 1;
        } finally {
            client.close();
        }

        return 0;
    }

    private List<TopicDeclaration> parse(String string) {
        return Arrays.stream(string.split(","))
                .map(String::trim)
                .map(this::parseTopicDeclaration)
                .collect(Collectors.toList());
    }

    private TopicDeclaration parseTopicDeclaration(String string) {
        var segments = string.split(":");
        if (segments.length == 0) {
            throw new IllegalArgumentException("Unable to parse topic declaration: `" + string + "`");
        }
        if (segments.length == 1) {
            return new TopicDeclaration(segments[0].trim(), (int) -1, (short) -1);
        }
        if (segments.length == 2) {
            return new TopicDeclaration(segments[0].trim(), Integer.parseInt(segments[1].trim()), (short) -1);
        }
        if (segments.length == 3) {
            return new TopicDeclaration(segments[0].trim(), Integer.parseInt(segments[1].trim()), Short.parseShort(segments[2].trim()));
        }
        throw new IllegalArgumentException("Unable to parse topic declaration: `" + string + "`");
    }

    record TopicDeclaration(String name, int numberOfPartitions, short replication) {

    }


}
