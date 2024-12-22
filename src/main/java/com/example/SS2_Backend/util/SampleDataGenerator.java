package com.example.SS2_Backend.util;

import com.example.SS2_Backend.constants.MatchingConst.ReqTypes;
import com.example.SS2_Backend.dto.mapper.StableMatchingProblemMapper;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.model.stableMatching.Matches.Matches;
import com.example.SS2_Backend.model.stableMatching.StableMatchingRBOProblem;
import com.example.SS2_Backend.ss.smt.MatchingProblem;
import com.example.SS2_Backend.ss.smt.requirement.Requirement;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

enum MATCHING_PROBLEM_TYPE {
  MTM, OTM, OTO
}

/**
 * Stable Matching Problem Testing Space.
 */
@Data
@Slf4j
public class SampleDataGenerator {

  private static final Random RANDOM = new Random(); // Random generator
  // private int set1Cap = 10; // Default capacity for set 1
  // private int set2Cap = 10; // Default capacity for set 2
  Map<Integer, Integer> setCapacities;
  // capRandomize: Căn cứ với set Capacities để generate capacity cho matching data
  boolean[] capRandomize = {false, false};
  // Configuration parameters
  private MATCHING_PROBLEM_TYPE matchingProblemType = MATCHING_PROBLEM_TYPE.MTM;
  // problemSize
  private int individualNum;
  private int numberOfProperties;
  // max capacity tiêu chuẩn cho mỗi set dạng map<int, int>, vd: với MTM: {0: 2, 1: 10}, 3Set: {0: 1, 1: 10, 2: 12}
  private int[] numberForeachSet;
  private String[] evaluateFunctions = {"none", "none"};
  private String fnf = "none"; // Fitness function


  /**
   * Constructor for configuring generator with required sets.
   * @param numberOfSet1 Number of individuals in set 1
   * @param numberOfSet2 Number of individuals in set 2
   */
  public SampleDataGenerator(int numberOfSet1, int numberOfSet2, int numberOfProperties) {
    this.numberForeachSet = new int[2];
    this.numberForeachSet[0] = numberOfSet1;
    this.numberForeachSet[1] = numberOfSet2;
    this.setCapacities.put(numberOfSet1, 10);
    this.setCapacities.put(numberOfSet2, 10);
    this.numberOfProperties = numberOfProperties;

  }

  /**
   * Main method to demonstrate usage.
   */
  public static void main(String[] args) {
    int numberOfProperties = 5;
    SampleDataGenerator generator = new SampleDataGenerator(20, 2000, numberOfProperties);
    generator.setCapacities.put(20, 1);
    generator.setCapacities.put(2000, 100);
    generator.setCapRandomize(new boolean[]{false, false});
    generator.setEvaluateFunctions(new String[]{"none", "none"});

    generator.setFnf("none");
    // Generate the NewStableMatchingProblemDTO instance
    NewStableMatchingProblemDTO request = generator.generateDto();
    String algo = "IBEA";
    // Mapping DTO to MatchingProblem
    MatchingProblem problem = StableMatchingProblemMapper.toMTM(request);
    // Run the algorithm
    long startTime = System.currentTimeMillis();
    NondominatedPopulation result = new Executor()
            .withProblem(problem)
            .withAlgorithm(algo)
            .withMaxEvaluations(100)
            .withProperty("populationSize", 1000)
            .distributeOnAllCores()
            .run();
    long endTime = System.currentTimeMillis();
    double runtime = ((double) (endTime - startTime) / 1000);
    runtime = Math.round(runtime * 100.0) / 100.0;

    // Process and print the results
    for (Solution solution : result) {
      Matches matches = (Matches) solution.getAttribute("matches");
//            System.out.println("Output Matches (by Gale Shapley):\n" + matches.toString());
//            System.out.println("Randomized Individuals Input Order (by MOEA): " + solution.getVariable(0).toString());
      System.out.println("Fitness Score: " + -solution.getObjective(0));
//            Testing tester = new Testing(matches,
//                    problem.getIndividuals().getNumberOfIndividual(),
//                    problem.getIndividuals().getCapacities());
//            System.out.println("Solution has duplicate individual? : " + tester.hasDuplicate());
    }
    System.out.println("\nExecution time: " + runtime + " Second(s) with Algorithm: " + algo);

  }

  /**
   * Generates a NewStableMatchingProblemDTO instance based on the configured parameters.
   * @return A NewStableMatchingProblemDTO object
   */
  public NewStableMatchingProblemDTO generateDto() {
    // TODO: goi thg generate data
    NewStableMatchingProblemDTO problemDTO = new NewStableMatchingProblemDTO();
    return problemDTO;
  }

  /**
   * Generates a StableMatchingRBOProblem instance based on the configured parameters.
   * @return StableMatchingRBOProblem
   */
  public StableMatchingRBOProblem generateProblem() {
    NewStableMatchingProblemDTO problemDTO = new NewStableMatchingProblemDTO();

    StableMatchingRBOProblem stableMatchingRBOProblem = new StableMatchingRBOProblem();
    stableMatchingRBOProblem.setPopulation(problemDTO);
    return stableMatchingRBOProblem;
  }

  /**
   * Adds properties to an individual.
   */
  private Map<String, Object> generatePWR(String type) {

    Map<String, Object> result = new HashMap<>();

    double[][] individualProperties = new double[this.individualNum][this.numberOfProperties];
    double[][] individualWeights = new double[this.individualNum][this.numberOfProperties];
    String[][] individualRequirements = new String[this.individualNum][this.numberOfProperties];
    Requirement[][] requirements = new Requirement[0][0];

    for (int i = 0; i < this.individualNum; i++) {
      for (int j = 0; j < numberOfProperties; j++) {
        // Example property values
        double propertyValue = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
        double propertyWeight = 1 + (10 - 1) * RANDOM.nextDouble();
        individualProperties[i][j] = propertyValue;
        individualWeights[i][j] = propertyWeight;
      }
    }
    if (GenerateType.DTO.equals(type)) {
      String[] expression = {"", "--", "++"};
      for (int i = 0; i < this.individualNum; i++) {
        for (int j = 0; j < numberOfProperties; j++) {
          int randomType = RANDOM.nextInt(2) + 1;
          double propertyBound = RANDOM.nextDouble() * (70.0 - 20.0) + 20.0;
          String requirement;

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
    }

    if (GenerateType.PROBLEM.equals(type)) {
      requirements = this.generateRequirement();
    }


    result.put(ObjectKeys.PROPERTY, individualProperties);
    result.put(ObjectKeys.WEIGHT, individualWeights);
    result.put(ObjectKeys.REQUIREMENT_STRING, individualRequirements);
    result.put(ObjectKeys.REQUIREMENT, requirements);
    return result;
  }

  private int[] generateSetIndices() {
    // TODO: individualSetIndices -> dựa vào numberForeachSet, label cho từng i với id set cụ thể
    return new int[0];
  }

  private int[] generateCapacities() {
    int[] capacities = new int[this.individualNum];
    int nSet = this.numberForeachSet.length;
    int set = 0;
    int setCurrentCap = this.numberForeachSet[0];
    for (int i = 0; i < this.individualNum; i++) {
      //TODO: generate từng cap cho individual
      //Dựa theo set, capacity của từng set và this.capRandomize
      // Nếu randomize của set hiện tại là true
      // -> Thực hiện random từ [1, capacity của set hiện tại]
      // -> add vào int[] capacities với số thứ tự
      // Ngược lại
      // -> add thẳng vào int[] với giá trí {capacity của từng set}
    }
    return capacities;
  }

  private Requirement[][] generateRequirement() {
    // TODO: viết hàm generate thẳng Requirement object, dùng khi gọi hàm generateProblem()
    return new Requirement[0][];
  }

  private String[][] generateRequirementString() {
      // TODO: tách logic generate req String vào đây
      return new String[0][0];
  }


  private interface ObjectKeys {

    String PROPERTY = "property";
    String WEIGHT = "weight";
    String REQUIREMENT = "requirement";
    String REQUIREMENT_STRING = "req_string";

  }


  private interface GenerateType {

    String DTO = "dto";
    String PROBLEM = "problem";

  }

}
