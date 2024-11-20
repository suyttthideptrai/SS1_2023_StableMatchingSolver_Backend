package com.example.SS2_Backend.dto.request;
import com.example.SS2_Backend.constants.MessageConst.ErrMessage;
import com.example.SS2_Backend.constants.MessageConst.ErrCode;
import jakarta.validation.constraints.*;
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

public class NewStableMatchingProblemDTO {

    // TODO: validate String length <= 255, thêm message
    @Max(value = 255, message = ErrMessage.PROBLEM_NAME)
    private String problemName;

    @Min(value = 2, message = ErrMessage.MES_001)
    private int numberOfSets;

    @Min(value = 3, message = ErrMessage.MES_002)
    private int numberOfIndividuals;

    @Min(value = 1, message = ErrMessage.MES_003)
    private int numberOfProperty;

    // TODO: làm gì có cái array nào length < 0 mà min 0
    @Size(min = 1, message = ErrMessage.MES_004)
    private int[] individualSetIndices;

    @Size(min = 1, message = ErrMessage.MES_004)
    private int[] individualCapacities;

    @Size(min = 3, message = ErrMessage.MES_002)
    private List<List<String>> individualRequirements;

    @Size(min = 3, message = ErrMessage.MES_002)
    private List<List<Double>> individualWeights;

    @Size(min = 3, message = ErrMessage.MES_002)
    private List<List<Double>> individualProperties;

    // TODO: validate size >=2, validate not empty for each
    @Size(min = 2, message = ErrMessage.EVAL_FN_NUM)
    private String[] evaluateFunction;

    //TODO: validate not blank
    @NotBlank
    private String fitnessFunction;

    //TODO: validate <= 1000
    @Max(value = 1000, message = ErrMessage.POPULATION_SIZE)
    private int populationSize;

    //TODO: validate <= 100
    @Max(value = 100, message = ErrMessage.GENERATION)
    private int generation;

    private int maxTime;

    //TODO: add message
    @NotEmpty(message = ErrMessage.ALGORITHM)
    private String algorithm;

    private String distributedCores;

    public void isEvaluateFunctionValid(BindingResult bindingResult) {
        ArrayList<Boolean> validEvalFunc = new ArrayList<>();
        for (String evaluateFunction: this.getEvaluateFunction()) {
            if (evaluateFunction.isEmpty()) {
                bindingResult.rejectValue("evaluateFunction", "", "Empty evaluateFunction(s)");
                return;
            }

            ExpressionBuilder e = new ExpressionBuilder(evaluateFunction);
            // TODO: không log bừa bãi, chỉ log những bước:
            //  bắt đầu validate,
            //  validate fail,
            //  bắt đầu xử lý service nếu validate thành công,
            //  xử lý xong request,
            //  gặp lỗi (log cả e để có trace, không dùng e.getMessage())
            // Log.debug("[Evaluate Function] Validating " + evaluateFunction);
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

    public void is2DArrayValid(BindingResult bindingResult) {

        //TODO: tách message & errCode ra thành constant, trường hợp này dùng chung hết một message và error code
        if (!bindingResult.hasFieldErrors("individualRequirements")
                && individualRequirements.size() != numberOfIndividuals) {
            bindingResult.rejectValue("individualRequirements", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
        }

        //TODO: thêm error code
        if (!bindingResult.hasFieldErrors("individualWeights")
                && individualWeights.size() != numberOfIndividuals) {
            bindingResult.rejectValue("individualWeights", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
        }

        //TODO: thêm error code
        if (!bindingResult.hasFieldErrors("individualProperties")
                && individualProperties.size() != numberOfIndividuals) {
            bindingResult.rejectValue("individualProperties", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
        }

        //TODO: thêm error code
        if (!bindingResult.hasFieldErrors("individualSetIndices")
                && individualSetIndices.length != numberOfIndividuals) {
            bindingResult.rejectValue("individualSetIndices", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
        }

        //TODO: thêm error code
        if (!bindingResult.hasFieldErrors("individualCapacities")
                && individualCapacities.length != numberOfIndividuals) {
            bindingResult.rejectValue("individualCapacities", ErrCode.INVALID_LENGTH, ErrMessage.INVALID_ARR_SIZE);
        }

    }

    public void valid2dArraysDimension(BindingResult bindingResult) {
        // không check thêm nếu có lỗi trước đó
        if (!bindingResult.hasFieldErrors("individualRequirements")
                || !bindingResult.hasFieldErrors("individualWeights")
                || !bindingResult.hasFieldErrors("individualProperties")) {
            return;
        }

        boolean isValid = true;

        //TODO: tại sao không loop qua list mà phải map về array?
        //TODO: loop qua 3 List<List<T>>, bỏ 2 hàm static đi NẾU không cần thiết phải map về array
        // REPLY: Em cần phải chuyển nó về 2D Array để xử lý trong bài toán
        // (Có thể em sẽ thử đưa lại về dạng List<List<T>> xem nếu em xong phần Test)
        for (int i = 0; isValid && i < individualProperties.size(); ++i) {
            isValid = (individualProperties.get(i).size() == individualWeights.get(i).size())
                    && (individualProperties.get(i).size() == individualRequirements.get(i).size());
        }

        //TODO: Làm tương tự: thêm err code và message như trên
        if (!isValid) {
            bindingResult.rejectValue("individualRequirements", "", "");
            bindingResult.rejectValue("individualWeights", "", "");
            bindingResult.rejectValue("individualProperties", "", "");
        }
    }

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

    public String toString() {
        return "Matching_Theory_Problem {" + "\n" +
                " ProblemName = " + problemName + "\n" +
                ", NumberOfSets = " + numberOfSets + "\n" +
                ", NumberOfIndividuals = " + numberOfIndividuals + "\n" +
                ", IndividualSetIndices = " + Arrays.toString(individualSetIndices) + "\n" +
                ", IndividualCapacities = " + Arrays.toString(individualCapacities) + "\n" +
                ", fitnessFunction = '" + fitnessFunction + "\n" +
                ", PopulationSize = " + populationSize + "\n" +
                ", Generation = " +generation + "\n" +
                ", individualRequirements: " + Arrays.deepToString(individualRequirements.toArray()) + "\n" +
                ", individualWeights: " + Arrays.deepToString(individualWeights.toArray()) + "\n" +
                ", individualProperties: " + Arrays.deepToString(individualProperties.toArray()) + "\n" +
                "}";
    }
}