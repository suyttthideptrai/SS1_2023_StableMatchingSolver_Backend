package com.example.SS2_Backend.model.gameTheory;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Strategy {
    private String name;
    private List<Double> properties = new ArrayList<>();
    private double payoff;

    public List<Double> getProperties() {

        return properties;
    }

    public double getPayoff() {
        return payoff;
    }

    public void setPayoff(double payoff) {
            this.payoff = payoff;
    }


    private double evaluateStringExpression(String expression) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");
        try {
            Object result = engine.eval(expression);
            return Double.parseDouble(result.toString());
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    public void addProperty(double property) {
        properties.add(property);
    }

    @Override
    public String toString() {
        StringBuilder props = new StringBuilder("[ ");
        for (double prop : properties) {
            props.append(prop).append(", ");
        }
        return props.substring(0, props.length() - 2) + " ]";
    }
}
