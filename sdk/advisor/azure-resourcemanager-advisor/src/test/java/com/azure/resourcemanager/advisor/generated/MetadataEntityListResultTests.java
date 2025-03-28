// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.advisor.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.advisor.fluent.models.MetadataEntityInner;
import com.azure.resourcemanager.advisor.models.MetadataEntityListResult;
import com.azure.resourcemanager.advisor.models.MetadataSupportedValueDetail;
import com.azure.resourcemanager.advisor.models.Scenario;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class MetadataEntityListResultTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        MetadataEntityListResult model = BinaryData.fromString(
            "{\"value\":[{\"id\":\"fdkfogk\",\"type\":\"gjofjd\",\"name\":\"qs\",\"properties\":{\"displayName\":\"upewnwreitjzy\",\"dependsOn\":[\"sarhmofc\",\"hs\",\"yurkdtmlxhekuksj\"],\"applicableScenarios\":[\"Alerts\",\"Alerts\",\"Alerts\",\"Alerts\"],\"supportedValues\":[{\"id\":\"cryuan\",\"displayName\":\"uxzdxtay\"},{\"id\":\"hmwhfpmrqo\",\"displayName\":\"tu\"},{\"id\":\"nryrtihf\",\"displayName\":\"ijbpzvgnwzsymgl\"},{\"id\":\"fcyzkohdbihanufh\",\"displayName\":\"bj\"}]}}],\"nextLink\":\"a\"}")
            .toObject(MetadataEntityListResult.class);
        Assertions.assertEquals("fdkfogk", model.value().get(0).id());
        Assertions.assertEquals("gjofjd", model.value().get(0).type());
        Assertions.assertEquals("qs", model.value().get(0).name());
        Assertions.assertEquals("upewnwreitjzy", model.value().get(0).displayName());
        Assertions.assertEquals("sarhmofc", model.value().get(0).dependsOn().get(0));
        Assertions.assertEquals(Scenario.ALERTS, model.value().get(0).applicableScenarios().get(0));
        Assertions.assertEquals("cryuan", model.value().get(0).supportedValues().get(0).id());
        Assertions.assertEquals("uxzdxtay", model.value().get(0).supportedValues().get(0).displayName());
        Assertions.assertEquals("a", model.nextLink());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        MetadataEntityListResult model = new MetadataEntityListResult()
            .withValue(Arrays.asList(new MetadataEntityInner().withId("fdkfogk")
                .withType("gjofjd")
                .withName("qs")
                .withDisplayName("upewnwreitjzy")
                .withDependsOn(Arrays.asList("sarhmofc", "hs", "yurkdtmlxhekuksj"))
                .withApplicableScenarios(
                    Arrays.asList(Scenario.ALERTS, Scenario.ALERTS, Scenario.ALERTS, Scenario.ALERTS))
                .withSupportedValues(
                    Arrays.asList(new MetadataSupportedValueDetail().withId("cryuan").withDisplayName("uxzdxtay"),
                        new MetadataSupportedValueDetail().withId("hmwhfpmrqo").withDisplayName("tu"),
                        new MetadataSupportedValueDetail().withId("nryrtihf").withDisplayName("ijbpzvgnwzsymgl"),
                        new MetadataSupportedValueDetail().withId("fcyzkohdbihanufh").withDisplayName("bj")))))
            .withNextLink("a");
        model = BinaryData.fromObject(model).toObject(MetadataEntityListResult.class);
        Assertions.assertEquals("fdkfogk", model.value().get(0).id());
        Assertions.assertEquals("gjofjd", model.value().get(0).type());
        Assertions.assertEquals("qs", model.value().get(0).name());
        Assertions.assertEquals("upewnwreitjzy", model.value().get(0).displayName());
        Assertions.assertEquals("sarhmofc", model.value().get(0).dependsOn().get(0));
        Assertions.assertEquals(Scenario.ALERTS, model.value().get(0).applicableScenarios().get(0));
        Assertions.assertEquals("cryuan", model.value().get(0).supportedValues().get(0).id());
        Assertions.assertEquals("uxzdxtay", model.value().get(0).supportedValues().get(0).displayName());
        Assertions.assertEquals("a", model.nextLink());
    }
}
