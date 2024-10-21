package com.example.SS2_Backend.model.onetomany;

import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Data Structure for result of StableMatchingExtra algorithm
 * OneToManyMatches = {Provider -> {Consumer1, Consumer2, ...}, Provider2 -> {...}, ...}
 * Each provider can have multiple consumers, but each consumer can only be matched to one provider.
 */
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class Matches implements Serializable {
    // check excluded pairs with the output map
    @Serial
    private static final long serialVersionUID = 1L;
    // The matches for each provider (provider -> set of consumers)
    Map<Integer, Set<Integer>> providerToConsumers;
    // To track consumers and which provider they are assigned to
    Map<Integer, Integer> consumerToProvider;
    // Consumers that were not matched to any provider
    Set<Integer> leftOverConsumers = new HashSet<>();

    // Constructor initializes with a specified number of providers
    public Matches(int numProviders) {
        this.providerToConsumers = new HashMap<>(numProviders);
        this.consumerToProvider = new HashMap<>();
    }

    // Get the set of consumers matched to a specific provider
    public Set<Integer> getConsumersOfProvider(int providerIndex) {
        return providerToConsumers.getOrDefault(providerIndex, new HashSet<>());
    }

    // Add a match between a provider and a consumer
    public void addMatch(int provider, int consumer) {
        providerToConsumers
                .computeIfAbsent(provider, k -> new HashSet<>()) // Ensure the provider has a set of consumers
                .add(consumer);
        consumerToProvider.put(consumer, provider); // Track the provider that the consumer is matched to
    }

    // Remove a match between a provider and a consumer
    public void removeMatch(int provider, int consumer) {
        Set<Integer> consumers = providerToConsumers.get(provider);
        if (consumers != null) {
            consumers.remove(consumer);
            consumerToProvider.remove(consumer); // Remove from the consumerToProvider mapping as well
        }
    }

    // Check if a specific consumer is already matched to a provider
    public boolean isConsumerMatched(int consumer) {
        return consumerToProvider.containsKey(consumer);
    }

    // Get the provider that a specific consumer is matched to
    public Integer getProviderOfConsumer(int consumer) {
        return consumerToProvider.get(consumer);
    }

    // Check if a provider has reached their maximum capacity of consumers
    public boolean isProviderFull(int provider, int maxCapacity) {
        return getConsumersOfProvider(provider).size() >= maxCapacity;
    }

    // Add a consumer to the list of left-over consumers
    public void addLeftOverConsumer(int consumer) {
        leftOverConsumers.add(consumer);
    }

    // Get an array of consumers for a specific provider
    public Integer[] getConsumerMatchesForProvider(int provider) {
        return getConsumersOfProvider(provider).toArray(new Integer[0]);
    }

    // Get the total number of matches
    public int totalMatches() {
        return consumerToProvider.size();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("Matches: \n");
        for (Map.Entry<Integer, Set<Integer>> entry : providerToConsumers.entrySet()) {
            result.append("Provider ").append(entry.getKey()).append(" -> Consumers: ")
                    .append(entry.getValue()).append("\n");
        }
        result.append("Left-over Consumers: ").append(leftOverConsumers).append("\n");
        return result.toString();
    }
}
