package com.example.SS2_Backend.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class CustomDouble2DArrayDeserializer extends JsonDeserializer<double[][]> {

    @Override
    public double[][] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        int rows = jsonNode.size();
        int cols = jsonNode.get(0).size();

        double[][] array = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            JsonNode row = jsonNode.get(i);
            for (int j = 0; j < cols; j++) {
                array[i][j] = row.get(j).asInt();
            }
        }

        return array;
    }
}