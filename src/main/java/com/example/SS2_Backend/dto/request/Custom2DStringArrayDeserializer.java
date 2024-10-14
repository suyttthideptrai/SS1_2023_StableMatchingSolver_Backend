package com.example.SS2_Backend.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
public class Custom2DStringArrayDeserializer extends JsonDeserializer<String[][]> {

    @Override
    public String[][] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
        int rows = jsonNode.size();
        int cols = jsonNode.get(0).size();

        String[][] array = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            JsonNode row = jsonNode.get(i);
            for (int j = 0; j < cols; j++) {
                array[i][j] = row.get(j).toString();
            }
        }
        return array;
    }
}