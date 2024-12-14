package com.example.SS2_Backend.dto.request;

import com.example.SS2_Backend.constants.MessageConst.ErrMessage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewStableMatchingProblemDTO implements ProblemRequestDto {

    @Size(max = 255, message = ErrMessage.PROBLEM_NAME)
    private String problemName;

    @Min(value = 2, message = ErrMessage.MES_001)
    private int numberOfSets;

    @Min(value = 3, message = ErrMessage.MES_002)
    private int numberOfIndividuals;

    @Min(value = 1, message = ErrMessage.MES_003)
    private int numberOfProperty;

    @Size(min = 1, message = ErrMessage.MES_004)
    private int[] individualSetIndices;

    @Size(min = 1, message = ErrMessage.MES_004)
    private int[] individualCapacities;

    @Size(min = 3, message = ErrMessage.MES_002)
    private String[][] individualRequirements;

    @Size(min = 3, message = ErrMessage.MES_002)
    private double[][] individualWeights;

    @Size(min = 3, message = ErrMessage.MES_002)
    private double[][] individualProperties;

    private String[] evaluateFunctions;

    private String fitnessFunction;

    private int [][] excludedPairs;

    @Max(value = 2000, message = ErrMessage.POPULATION_SIZE)
    private int populationSize;

    @Max(value = 100, message = ErrMessage.GENERATION)
    private int generation;

    private int maxTime;

    @NotEmpty(message = ErrMessage.NOT_BLANK)
    private String algorithm;

    private String distributedCores;

    public void isEvaluateFunctionValid(BindingResult bindingResult) {
        ArrayList<Boolean> validEvalFunc = new ArrayList<>();
        for (String evaluateFunction: this.getEvaluateFunctions()) {
            if (evaluateFunction.isEmpty()) {
                bindingResult.rejectValue("evaluateFunction", "", "Empty evaluateFunction(s)");
                return;
            }

            ExpressionBuilder e = new ExpressionBuilder(evaluateFunction);
            for (int i = 1; i <= this.getNumberOfProperty(); i++) {
                e.variable(String.format("P%d", i)).variable(String.format("W%d", i));
            }

            Expression expressionValidator = e.build();
            ValidationResult res = expressionValidator.validate();
            validEvalFunc.add(res.isValid());
            // Log.debug("[Evaluate Function] " + evaluateFunction + "validation result: " + res.isValid());
        }
        if (!validEvalFunc.stream().allMatch(e -> true)) {
            bindingResult.rejectValue("evaluateFunction", "",
                    "Invalid evaluation function(s). Rejected.");
        }

    }

//    public void is2DArrayValid(BindingResult bindingResult) {
//
//        if (!bindingResult.hasFieldErrors("individualRequirements")
//                && individualRequirements.size() != numberOfIndividuals) {
//            bindingResult.rejectValue("individualRequirements", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
//        }
//
//        if (!bindingResult.hasFieldErrors("individualWeights")
//                && individualWeights.size() != numberOfIndividuals) {
//            bindingResult.rejectValue("individualWeights", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
//        }
//
//        if (!bindingResult.hasFieldErrors("individualProperties")
//                && individualProperties.size() != numberOfIndividuals) {
//            bindingResult.rejectValue("individualProperties", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
//        }
//
//        if (!bindingResult.hasFieldErrors("individualSetIndices")
//                && individualSetIndices.length != numberOfIndividuals) {
//            bindingResult.rejectValue("individualSetIndices", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
//        }
//
//        if (!bindingResult.hasFieldErrors("individualCapacities")
//                && individualCapacities.length != numberOfIndividuals) {
//            bindingResult.rejectValue("individualCapacities", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
//        }
//
//    }

//    public void valid2dArraysDimension(BindingResult bindingResult) {
//        // không check thêm nếu có lỗi trước đó
//        if (!bindingResult.hasFieldErrors("individualRequirements")
//                || !bindingResult.hasFieldErrors("individualWeights")
//                || !bindingResult.hasFieldErrors("individualProperties")) {
//            return;
//        }
//
//        boolean isValid = true;
//
//        //TODO: tại sao không loop qua list mà phải map về array?
//        //TODO: loop qua 3 List<List<T>>, bỏ 2 hàm static đi NẾU không cần thiết phải map về array
//        // REPLY: Em cần phải chuyển nó về 2D Array để xử lý trong bài toán
//        // (Có thể em sẽ thử đưa lại về dạng List<List<T>> xem nếu em xong phần Test)
//        for (int i = 0; isValid && i < individualProperties.size(); ++i) {
//            isValid = (individualProperties.get(i).size() == individualWeights.get(i).size())
//                    && (individualProperties.get(i).size() == individualRequirements.get(i).size());
//        }
//
//        if (!isValid) {
//            bindingResult.rejectValue("individualRequirements", "", "");
//            bindingResult.rejectValue("individualWeights", "", "");
//            bindingResult.rejectValue("individualProperties", "", "");
//        }
//    }

    public static String[][] fromListToStringArray(List<List<String>> list) {
        String[][] array = new String[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            List<String> currentArr = list.get(i);
            array[i] = currentArr.toArray(new String[0]);
        }
        return array;
    }

    public static double[][] fromListToDoubleArray(List<List<Double>> list) {
        double[][] array = new double[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).stream().mapToDouble(Double::doubleValue).toArray();
        }
        return array;
    }

    @Override
    public String toString() {
        return "StableMatchingProblemDTO{" + "problemName='" + problemName + '\'' +
                ", numberOfSets=" + numberOfSets + ", numberOfIndividuals=" + numberOfIndividuals +
                ", numberOfProperty=" + numberOfProperty + ", individualSetIndices=" +
                Arrays.toString(individualSetIndices) + ", individualCapacities=" +
                Arrays.toString(individualCapacities) + ", individualRequirements=" +
                Arrays.toString(individualRequirements) + ", individualWeights=" +
                Arrays.toString(individualWeights) + ", individualProperties=" +
                Arrays.toString(individualProperties) + ", evaluateFunctions=" +
                Arrays.toString(evaluateFunctions) + ", fitnessFunction='" + fitnessFunction +
                '\'' + ", excludedPairs=" + Arrays.toString(excludedPairs) + ", populationSize=" +
                populationSize + ", generation=" + generation + ", maxTime=" + maxTime +
                ", algorithm='" + algorithm + '\'' + ", distributedCores='" + distributedCores +
                '\'' + '}';
    }
}