package com.example.SS2_Backend.model.onetomany;

import com.example.SS2_Backend.model.onetomany.Requirement.Requirement;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static com.example.SS2_Backend.model.StableMatching.Individual.decodeInputRequirement;

@Data
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Individual {
    String name; // Name of the individual
    int belongToSet; // 0 for provider, 1 for consumer
    List<Property> properties; // List of individual properties
    int capacity; // For providers

    @JsonProperty("Properties")
    public void setProperty(double propertyValue, double propertyWeight, String inputRequirement) {
        String[] decodedRequirement = decodeInputRequirement(inputRequirement);
        Property property = new Property(propertyValue, propertyWeight, decodedRequirement);
        this.properties.add(property);
    }

    @JsonProperty("IndividualName")
    public void setName(String name) {
        this.name = name.trim();
    }

    @JsonProperty("IndividualSet")
    public void setBelongToSet(int set) {
        this.belongToSet = set;
    }

    public double getProperty(int index) {
        if (index >= 0 && index < this.properties.size()) {
            return properties
                    .get(index)
                    .getValue();
        } else {
            return 0;
        }
    }

    public double getPropertyWeight(int index) {
        if (index >= 0 && index < this.properties.size()) {
            return properties
                    .get(index)
                    .getWeight();
        } else {
            return 0;
        }
    }
    public Requirement getRequirement(int index) {
        return properties
                .get(index)
                .getRequirement();
    }
}