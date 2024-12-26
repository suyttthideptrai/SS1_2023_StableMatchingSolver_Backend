package com.example.SS2_Backend.util;

import com.example.SS2_Backend.constants.MatchingConst.ReqTypes;
import com.example.SS2_Backend.constants.AppConst;
import com.example.SS2_Backend.dto.mapper.StableMatchingProblemMapper;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.model.stableMatching.Matches.Matches;
import com.example.SS2_Backend.model.stableMatching.StableMatchingRBOProblem;
import com.example.SS2_Backend.ss.smt.MatchingProblem;
import com.example.SS2_Backend.ss.smt.requirement.Requirement;
import com.example.SS2_Backend.ss.smt.requirement.RequirementDecoder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.IBEA;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.example.SS2_Backend.constants.MatchingConst.DEFAULT_EVALUATE_FUNC;
import static com.example.SS2_Backend.constants.MatchingConst.DEFAULT_FITNESS_FUNC;

/**
 * Stable Matching Problem Testing Space.
 */
@Data
@Slf4j
public class SampleDataGenerator {

    private static final Random RANDOM = new Random(); // Random generator
    Map<Integer, Integer> setCapacities = new HashMap<Integer, Integer>();
    // capRandomize: Căn cứ với set Capacities để generate capacity cho matching data
    // Mặc định để xử lý MTM Problem nên sẽ để cả hai đều là `true`
    boolean[] capRandomize = {true, true};
    // Configuration parameters
    private MatchingProblemType matchingProblemType = MatchingProblemType.MTM;
    // problemSize
    private int individualNum;
    private int numberOfProperties;
    // max capacity tiêu chuẩn cho mỗi set dạng map<int, int>, vd: với MTM: {0: 2, 1: 10}, 3Set: {0: 1, 1: 10, 2: 12}
    private int[] numberForeachSet;
    private String[] evaluateFunctions = {DEFAULT_EVALUATE_FUNC, DEFAULT_EVALUATE_FUNC};
    private String fnf = DEFAULT_FITNESS_FUNC; // Fitness function

    public SampleDataGenerator(MatchingProblemType matchingProblemType, int numberOfSet1, int numberOfSet2, int numberOfProperties) {
        if (numberOfSet1 <= 0 || numberOfSet2 <= 0 || numberOfProperties <= 0) {
            throw new IllegalArgumentException("Number of sets and properties must be greater than 0");
        }
        this.matchingProblemType = matchingProblemType;
        this.numberForeachSet = new int[2];
        this.individualNum = numberOfSet1 + numberOfSet2;
        this.numberForeachSet[0] = numberOfSet1;
        this.numberForeachSet[1] = numberOfSet2;
        this.setCapacities.put(numberOfSet1, 10);
        this.setCapacities.put(numberOfSet2, 10);
        this.numberOfProperties = numberOfProperties;
    }

    public SampleDataGenerator(MatchingProblemType matchingProblemType, int[] numberForeachSet, int numberOfProperties) {
        this.matchingProblemType = matchingProblemType;
        this.numberForeachSet = numberForeachSet;
        this.numberOfProperties = numberOfProperties;
    }

    /**
     * Main method to demonstrate usage.
     */
    public static void main(String[] args) {
        int numberOfProperties = 5;
        SampleDataGenerator generator = new SampleDataGenerator(MatchingProblemType.MTM, 20, 2000, numberOfProperties);
        generator.setCapacities.put(20, 1);
        generator.setCapacities.put(2000, 100);
        generator.setCapRandomize(new boolean[]{true, true});
        generator.setEvaluateFunctions(new String[]{DEFAULT_EVALUATE_FUNC, DEFAULT_EVALUATE_FUNC});
        generator.setFnf(DEFAULT_FITNESS_FUNC);

        String algo = "IBEA";
//        MatchingProblem problem = generator.generateProblem();
        MatchingProblem problem = StableMatchingProblemMapper.toMTM(generator.generateDto());
        // Run the algorithm
        long startTime = System.currentTimeMillis();
//        NondominatedPopulation result = new Executor()
//                .withProblem(problem)
//                .withAlgorithm(algo)
//                .withMaxEvaluations(100)
//                .withProperty("populationSize", 1000)
//                .distributeOnAllCores()
//                .run();

        IBEA algorithm = new IBEA(problem);
        algorithm.run(100);
        algorithm.getResult().display();

        long endTime = System.currentTimeMillis();
        double runtime = ((double) (endTime - startTime) / 1000);
        runtime = Math.round(runtime * 100.0) / 100.0;

        // Process and print the results
//        for (Solution solution : result) {
//            Matches matches = (Matches) solution.getAttribute("matches");
//            System.out.println("Fitness Score: " + -solution.getObjective(0));
//        }
        System.out.println("\nExecution time: " + runtime + " Second(s) with Algorithm: " + algo);

    }

    /**
     * Generates a NewStableMatchingProblemDTO instance based on the configured parameters.
     *
     * @return A NewStableMatchingProblemDTO object
     */
    public NewStableMatchingProblemDTO generateDto() {
        NewStableMatchingProblemDTO problemDTO = new NewStableMatchingProblemDTO();
        problemDTO.setIndividualSetIndices(generateSetIndices());
        problemDTO.setIndividualCapacities(generateCapacities());
        problemDTO.setNumberOfSets(numberForeachSet.length);
        problemDTO.setNumberOfProperty(numberOfProperties);
        problemDTO.setNumberOfIndividuals(individualNum);
        problemDTO.setIndividualProperties((double[][]) generatePW().get("property"));
        problemDTO.setIndividualWeights((double[][]) generatePW().get("weight"));
        problemDTO.setIndividualRequirements(generateRequirementString());
        problemDTO.setEvaluateFunctions(evaluateFunctions);
        problemDTO.setFitnessFunction(fnf);
        return problemDTO;
    }

    /**
     * Generates a StableMatchingRBOProblem instance based on the configured parameters.
     *
     * @return StableMatchingRBOProblem
     */
    public MatchingProblem generateProblem() {
        MatchingProblem matchingProblem;
        NewStableMatchingProblemDTO newDto = this.generateDto();

        switch (this.matchingProblemType) {
            case MTM -> {
                this.capRandomize = new boolean[]{true, true};
                matchingProblem = StableMatchingProblemMapper.toMTM(newDto);
            }
            case OTM -> {
                this.capRandomize = new boolean[]{true, false};
                matchingProblem = StableMatchingProblemMapper.toOTM(newDto);
            }
            case OTO -> {
                this.capRandomize = new boolean[]{false};
                matchingProblem = StableMatchingProblemMapper.toOTO(newDto);
            }
            default -> {
                log.info("[ERROR] Match Problem Type hasn't been initialized yet. Terminated...");
                matchingProblem = null;
            }
        }

        return matchingProblem;
    }

    /**
     * Adds properties to an individual.
     */
    private Map<String, Object> generatePW() {
        Map<String, Object> result = new HashMap<>();
        double[][] individualProperties = new double[this.individualNum][this.numberOfProperties];
        double[][] individualWeights = new double[this.individualNum][this.numberOfProperties];
        for (int i = 0; i < this.individualNum; i++) {
            for (int j = 0; j < numberOfProperties; j++) {
                // Example property values
                double propertyValue = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
                double propertyWeight = 1 + (10 - 1) * RANDOM.nextDouble();
                individualProperties[i][j] = propertyValue;
                individualWeights[i][j] = propertyWeight;
            }
        }

        result.put(ObjectKeys.PROPERTY, individualProperties);
        result.put(ObjectKeys.WEIGHT, individualWeights);
        return result;
    }

    private int[] generateSetIndices() {
        int[] setIndices = new int[this.individualNum];
        // Số set hiện tại
        int currentSetIndex = 1;
        // Số lượng tổng các Individual, làm giới hạn cho mỗi lần chuyển sang một Set khác
        int currentPosition = numberForeachSet[currentSetIndex - 1];
        for (int i = 0; i < this.individualNum; i++) {
            if (i > currentPosition) {
                currentSetIndex += 1;
                currentPosition += numberForeachSet[currentSetIndex - 1];
            } else {
                setIndices[i] = currentSetIndex;
            }
        }
        return setIndices;
    }

    private int[] generateCapacities() {
        int[] capacities = new int[this.individualNum];

        int currentSetIndex = 1;
        // Số lượng tổng các Individual, làm giới hạn cho mỗi lần chuyển sang một Set khác
        int currentPosition = numberForeachSet[currentSetIndex - 1];
        int setCurrentCap = this.numberForeachSet[currentSetIndex];

        for (int i = 0; i < this.individualNum; i++) {
            // Nếu số hiện tại lớn hơn số lượng individual của set hiện tại thì +1;
            if (i > currentPosition) {
                currentSetIndex += 1;
                currentPosition += numberForeachSet[currentSetIndex - 1];
            } else {
                if (this.capRandomize[currentSetIndex - 1]) {
                    capacities[i] = RANDOM.nextInt() * setCurrentCap;
                } else {
                    capacities[i] = setCurrentCap;
                }
            }
        }
        return capacities;
    }

    private Requirement[][] generateRequirement() {
        String[][] requirementString = generateRequirementString();
        Requirement[][] individualRequirements = new Requirement[this.individualNum][this.numberOfProperties];
        individualRequirements = RequirementDecoder.decode(requirementString);
        return individualRequirements;
    }

    private String[][] generateRequirementString() {
        String[][] individualRequirements = new String[this.individualNum][this.numberOfProperties];

        String[] expression = {"", "--", "++"};
        for (int i = 0; i < this.individualNum; i++) {
            for (int j = 0; j < numberOfProperties; j++) {
                String requirement;
                int randomType = RANDOM.nextInt(2) + 1;
                double propertyBound = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;

                if (ReqTypes.ONE_BOUND == randomType) {
                    int randomExpression = RANDOM.nextInt(2) + 1;
                    requirement = propertyBound + expression[randomExpression];
                } else if (ReqTypes.TWO_BOUND == randomType) {
                    double propertyBound2 = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
                    requirement = propertyBound + ":" + propertyBound2;
                    //  if (ReqTypes.SCALE_TARGET == randomType)
                } else {
                    int requirementScale = 1 + (10 - 1) * RANDOM.nextInt();
                    requirement = String.valueOf(requirementScale);
                }
                individualRequirements[i][j] = requirement;
            }

        }
        return individualRequirements;
    }


    private interface ObjectKeys {
        String PROPERTY = "property";
        String WEIGHT = "weight";
    }

}
