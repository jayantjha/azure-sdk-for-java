package com.azure.ai.projects;

import com.azure.ai.projects.generated.AIProjectClientTestBase;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashMap;
import java.util.Map;

public class EvaluationsClientTest extends AIProjectClientTestBase {

    @BeforeEach
    void setUp() {
        this.beforeTest();
    }

//    @Test
//    void createEvaluation() {
//        Evaluation evaluation = new Evaluation()
//            .setDisplayName("Remote Evaluation")
//            .setDescription("Evaluation of dataset")
//            .setData(new InputData())
//            .setEvaluators(
//                mapOf(
//                    "f1_score", new EvaluatorConfiguration().setId()
//
//                )
//            )
//    }

    // Use "Map.of" if available
    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> mapOf(Object... inputs) {
        Map<String, T> map = new HashMap<>();
        for (int i = 0; i < inputs.length; i += 2) {
            String key = (String) inputs[i];
            T value = (T) inputs[i + 1];
            map.put(key, value);
        }
        return map;
    }
}
