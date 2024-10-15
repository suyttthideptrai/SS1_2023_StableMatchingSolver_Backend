package com.example.SS2_Backend.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class Array2DDeserializer extends JsonDeserializer<String[][]> {
    @Override
    public String[][] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode valuesNode = jsonParser.getCodec().readTree(jsonParser);

        int rows = valuesNode.size();
        int cols = valuesNode.get(0).size();

        String[][] values = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            JsonNode rowNode = valuesNode.get(i);
            for (int j = 0; j < cols; j++) {
                values[i][j] = rowNode.get(j).toString();
            }
        }

        return values;
    }
}