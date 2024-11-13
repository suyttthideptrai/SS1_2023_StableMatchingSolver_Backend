package com.example.SS2_Backend.dto.request;
import com.example.SS2_Backend.constants.MessageConst;
import jakarta.validation.constraints.Min;
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
    private String problemName;
    @Min(value = 2, message = MessageConst.MatchingValidate.MES_001)
    private int numberOfSets;
    @Min(value = 3, message = MessageConst.MatchingValidate.MES_002)
    private int numberOfIndividuals;

    @Min(value = 1, message = MessageConst.MatchingValidate.MES_003)
    private int numberOfProperty;

    /* Các phần Array mới có độ dài bằng nhau được tách ra từ Individual gốc
     * LƯU Ý: Nếu bạn không phải là Maintainer cũ thì không cần đọc Documentation này. Phần này
     * sẽ để giải thích cách hoạt động của phần mới và thực hiện những thay đổi khác
     *
     * Thay vì sử dụng IndividualList và Deserializer Class như StableMatchingProblemDTO
     * thì trong NewStableMatchingProblemDTO sẽ thay bằng
     * Mỗi một Individual bao gồm những phần sẽ được xử lý ở Backend:
     * - SetIndex
     * - Capacity
     *
     * Những thành phần sau đây được chuyển đổi từ List<Property>, mỗi một Property bao gồm:
     * - Requirement -> individualRequirements
     * - Weight -> individualWeights
     * - Value -> individualProperties
     *
     * Ở đây sử dụng 2D Array cho 3 phần này vì:
     * Trong mỗi một Individual sẽ có ít nhất 1 Property trở lên.
     *
     * Ví dụ: individualProperties (Cấu trúc của các 2D Array khác cũng tương tự)
     * [ (3 số trong một Array nhỏ ([0, 0, 0]) ở đây đại diện cho 3 Property cho mỗi Individual, độ dài bằng nhau cho từng Individual)
     *  [0, 0, 0], -> Số các Property Value thuộc Individual 1 gốc
     *  [0, 0, 0], -> Số các Property Value thuộc Individual 2 gốc
     *  [0, 0, 0] -> Số các Property Value thuộc Individual 3 gốc
     * ] -> Danh sách này chứa toàn bộ cho cả IndividualList
     * */

    @Size(min = 0, message = MessageConst.MatchingValidate.MES_GREATER_THAN_ZERO)
    private int[] individualSetIndices;
    @Size(min = 0, message = MessageConst.MatchingValidate.MES_GREATER_THAN_ZERO)
    private int[] individualCapacities;

    /*
    * Cần có ít nhất ba để tương ứng với số lượng Individual ở numberOfIndividuals
    * */
    @Size(min = 3, message = MessageConst.MatchingValidate.MES_002)
    private List<List<String>> individualRequirements;
    @Size(min = 3, message = MessageConst.MatchingValidate.MES_002)
    private List<List<Double>> individualWeights;
    @Size(min = 3, message = MessageConst.MatchingValidate.MES_002)
    private List<List<Double>> individualProperties;

    @NotEmpty
    private String[] evaluateFunction;

    @NotEmpty
    private String fitnessFunction;
    private int populationSize;

    private int generation;
    private int maxTime;

    @NotEmpty
    private String algorithm;

    private String distributedCores;

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

    public void isEvaluateFunctionValid(BindingResult bindingResult) {
        ArrayList<Boolean> validEvalFunc = new ArrayList<>();
        for (String evaluateFunction: this.getEvaluateFunction()) {
            ExpressionBuilder e = new ExpressionBuilder(evaluateFunction);
            Log.debug("[Evaluate Function] Validating " + evaluateFunction);
            for (int i = 1; i <= this.getNumberOfProperty(); i++) {
                e.variable(String.format("P%d", i)).variable(String.format("W%d", i));
            }

            Expression expressionValidator = e.build();
            ValidationResult res = expressionValidator.validate();
            validEvalFunc.add(res.isValid());
            Log.debug("[Evaluate Function] " + evaluateFunction + "validation result: " + res.isValid());
        }
        if (validEvalFunc.stream().allMatch(e -> true)) {
            Log.debug("Valida evaluate function(s).");
        } else {
            bindingResult.reject("evaluateFunction", "Invalid evaluation function(s). Rejected.");
        }

    }

    public void is2DArrayValid(BindingResult bindingResult) {
        if (individualRequirements.size() != numberOfIndividuals) {
            bindingResult.reject("individualRequirements", "Invalid individualRequirements, doesn't match the number of individuals");
        } else if (individualWeights.size() != numberOfIndividuals) {
            bindingResult.reject("individualWeights", "Invalid individualWeights, doesn't match the number of individuals");
        } else if (individualProperties.size() != numberOfIndividuals) {
            bindingResult.reject("individualProperties", "Invalid individualProperties, doesn't match the number of individuals");
        } else if (individualSetIndices.length != numberOfIndividuals) {
            bindingResult.reject("individualSetIndices", "Invalid individualSetIndices, doesn't match the number of individuals");
        } else if (individualCapacities.length != numberOfIndividuals) {
            bindingResult.reject("individualCapacities", "Invalid individualCapacities, doesn't match the number of individuals");
        } else {
            Log.debug("Validation completed, all the arrays related to the numberOfIndividuals are all valid!");
        }
    }

    public void valid2dArraysDimension(BindingResult bindingResult) {
        boolean isValid = true;
        String[][] individual2DReq = fromListToStringArray(individualRequirements);
        double[][] individual2DWeight = fromListToDoubleArray(individualWeights);
        double[][] individual2DValue = fromListToDoubleArray(individualProperties);

        for (int i = 0; isValid && i < individual2DValue.length; ++i) {
            isValid = (individual2DValue[i].length == individual2DWeight[i].length)
                    && (individual2DValue[i].length == individual2DReq[i].length);
        }

        if (isValid) {
            Log.debug("Validation completed, all the arrays related to the numberOfIndividuals are all valid!");
        } else {
            bindingResult.reject("Invalid 2D Array's length", "Dimension của các ma trận không đồng đều");
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
}
