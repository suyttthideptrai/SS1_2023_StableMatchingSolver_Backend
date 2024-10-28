package com.example.SS2_Backend.dto.request;
import com.example.SS2_Backend.dto.response.EvaluateFunctionConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class NewStableMatchingProblemDTO {

    private String problemName;

    private int numberOfSets;
    @Min(value = 3, message = "The number of individuals should be at least 3")
    private int numberOfIndividuals;

    @NotEmpty
    private String[] allPropertyNames;

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

    @Size(min = 0)
    private int[] individualSetIndices;
    @Size(min = 0)
    private int[] individualCapacities;

    @Size(min = 3)
    private List<List<String>> individualRequirements;
    @Size(min = 3)
    private List<List<Double>> individualWeights;
    @Size(min = 3)
    private List<List<Double>> individualProperties;

    @NotEmpty
    @EvaluateFunctionConstraint
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
                ", AllPropertyName = " + Arrays.toString(allPropertyNames) +
                ", fitnessFunction = '" + fitnessFunction + "\n" +
                ", PopulationSize = " + populationSize + "\n" +
                ", Generation = " +generation + "\n" +
                ", individualRequirements: " + Arrays.deepToString(individualRequirements.toArray()) + "\n" +
                ", individualWeights: " + Arrays.deepToString(individualWeights.toArray()) + "\n" +
                ", individualProperties: " + Arrays.deepToString(individualProperties.toArray()) + "\n" +
                "}";
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
