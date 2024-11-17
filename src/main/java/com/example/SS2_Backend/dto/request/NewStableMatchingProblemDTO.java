package com.example.SS2_Backend.dto.request;
import com.example.SS2_Backend.constants.MessageConst.ErrMessage;
import com.example.SS2_Backend.constants.MessageConst.ErrCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.ValidationResult;
import org.jfree.util.Log;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class NewStableMatchingProblemDTO {

    //TODO: validate String length <= 255, thêm message
    private String problemName;

    @Min(value = 2, message = ErrMessage.MES_001)
    private int numberOfSets;

    @Min(value = 3, message = ErrMessage.MES_002)
    private int numberOfIndividuals;

    @Min(value = 1, message = ErrMessage.MES_003)
    private int numberOfProperty;

    //TODO: làm gì có cái array nào length < 0 mà min 0
    @Size(min = 0, message = ErrMessage.MES_004)
    private int[] individualSetIndices;

    @Size(min = 1, message = ErrMessage.MES_004)
    private int[] individualCapacities;

    @Size(min = 3, message = ErrMessage.MES_002)
    private List<List<String>> individualRequirements;

    @Size(min = 3, message = ErrMessage.MES_002)
    private List<List<Double>> individualWeights;

    @Size(min = 3, message = ErrMessage.MES_002)
    private List<List<Double>> individualProperties;

    //TODO: validate size >=2, validate not empty for each
    private String[] evaluateFunction;

    //TODO: validate not blank
    @NotBlank
    private String fitnessFunction;

    //TODO: validate <= 1000
    private int populationSize;

    //TODO: validate <= 100
    private int generation;

    private int maxTime;

    //TODO: add message
    @NotEmpty
    private String algorithm;

    private String distributedCores;

    public void isEvaluateFunctionValid(BindingResult bindingResult) {
        ArrayList<Boolean> validEvalFunc = new ArrayList<>();
        for (String evaluateFunction: this.getEvaluateFunction()) {
            ExpressionBuilder e = new ExpressionBuilder(evaluateFunction);
            // TODO: không log bừa bãi, chỉ log những bước:
            //  bắt đầu validate,
            //  validate fail,
            //  bắt đầu xử lý service nếu validate thành công,
            //  xử lý xong request,
            //  gặp lỗi (log cả e để có trace, không dùng e.getMessage())
            Log.debug("[Evaluate Function] Validating " + evaluateFunction);
            for (int i = 1; i <= this.getNumberOfProperty(); i++) {
                e.variable(String.format("P%d", i)).variable(String.format("W%d", i));
            }

            Expression expressionValidator = e.build();
            ValidationResult res = expressionValidator.validate();
            validEvalFunc.add(res.isValid());
            Log.debug("[Evaluate Function] " + evaluateFunction + "validation result: " + res.isValid());
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
            bindingResult.rejectValue("individualRequirements", ErrCode.INVALID_LENGTH, "Invalid individualRequirements, doesn't match the number of individuals");
        }

        //TODO: thêm error code
        if (!bindingResult.hasFieldErrors("individualWeights")
                && individualWeights.size() != numberOfIndividuals) {
            bindingResult.rejectValue("individualWeights", "Invalid individualWeights, doesn't match the number of individuals");
        }

        //TODO: thêm error code
        if (!bindingResult.hasFieldErrors("individualProperties")
                && individualProperties.size() != numberOfIndividuals) {
            bindingResult.rejectValue("individualProperties", "Invalid individualProperties, doesn't match the number of individuals");
        }

        //TODO: thêm error code
        if (!bindingResult.hasFieldErrors("individualSetIndices")
                && individualSetIndices.length != numberOfIndividuals) {
            bindingResult.rejectValue("individualSetIndices", "Invalid individualSetIndices, doesn't match the number of individuals");
        }

        //TODO: thêm error code
        if (!bindingResult.hasFieldErrors("individualCapacities")
                && individualCapacities.length != numberOfIndividuals) {
            bindingResult.rejectValue("individualCapacities", "Invalid individualCapacities, doesn't match the number of individuals");
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
        String[][] individual2DReq = fromListToStringArray(individualRequirements);
        double[][] individual2DWeight = fromListToDoubleArray(individualWeights);
        double[][] individual2DValue = fromListToDoubleArray(individualProperties);

        //TODO: tại sao không loop qua list mà phải map về array?
        //TODO: loop qua 3 List<List<T>>, bỏ 2 hàm static đi NẾU không cần thiết phải map về array
        for (int i = 0; isValid && i < individual2DValue.length; ++i) {
            isValid = (individual2DValue[i].length == individual2DWeight[i].length)
                    && (individual2DValue[i].length == individual2DReq[i].length);
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
