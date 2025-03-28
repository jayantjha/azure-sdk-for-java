// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.fluidrelay.generated;

import com.azure.core.util.BinaryData;
import com.azure.resourcemanager.fluidrelay.fluent.models.OperationResultInner;
import com.azure.resourcemanager.fluidrelay.models.OperationDisplay;
import com.azure.resourcemanager.fluidrelay.models.OperationListResult;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;

public final class OperationListResultTests {
    @org.junit.jupiter.api.Test
    public void testDeserialize() throws Exception {
        OperationListResult model = BinaryData.fromString(
            "{\"value\":[{\"name\":\"quvgjxpybczme\",\"display\":{\"provider\":\"zopbsphrupidgs\",\"resource\":\"bejhphoycmsxa\",\"operation\":\"hdxbmtqio\",\"description\":\"zehtbmu\"},\"isDataAction\":false},{\"name\":\"noi\",\"display\":{\"provider\":\"lrxybqsoq\",\"resource\":\"gkdmb\",\"operation\":\"zlobcufpd\",\"description\":\"rbt\"},\"isDataAction\":true},{\"name\":\"nq\",\"display\":{\"provider\":\"qgn\",\"resource\":\"ooojywifsqe\",\"operation\":\"agdfmglzlh\",\"description\":\"rifkwm\"},\"isDataAction\":true}],\"nextLink\":\"siznto\"}")
            .toObject(OperationListResult.class);
        Assertions.assertEquals("quvgjxpybczme", model.value().get(0).name());
        Assertions.assertEquals("zopbsphrupidgs", model.value().get(0).display().provider());
        Assertions.assertEquals("bejhphoycmsxa", model.value().get(0).display().resource());
        Assertions.assertEquals("hdxbmtqio", model.value().get(0).display().operation());
        Assertions.assertEquals("zehtbmu", model.value().get(0).display().description());
        Assertions.assertEquals(false, model.value().get(0).isDataAction());
        Assertions.assertEquals("siznto", model.nextLink());
    }

    @org.junit.jupiter.api.Test
    public void testSerialize() throws Exception {
        OperationListResult model = new OperationListResult().withValue(Arrays.asList(
            new OperationResultInner().withName("quvgjxpybczme")
                .withDisplay(new OperationDisplay().withProvider("zopbsphrupidgs")
                    .withResource("bejhphoycmsxa")
                    .withOperation("hdxbmtqio")
                    .withDescription("zehtbmu"))
                .withIsDataAction(false),
            new OperationResultInner().withName("noi")
                .withDisplay(new OperationDisplay().withProvider("lrxybqsoq")
                    .withResource("gkdmb")
                    .withOperation("zlobcufpd")
                    .withDescription("rbt"))
                .withIsDataAction(true),
            new OperationResultInner().withName("nq")
                .withDisplay(new OperationDisplay().withProvider("qgn")
                    .withResource("ooojywifsqe")
                    .withOperation("agdfmglzlh")
                    .withDescription("rifkwm"))
                .withIsDataAction(true)))
            .withNextLink("siznto");
        model = BinaryData.fromObject(model).toObject(OperationListResult.class);
        Assertions.assertEquals("quvgjxpybczme", model.value().get(0).name());
        Assertions.assertEquals("zopbsphrupidgs", model.value().get(0).display().provider());
        Assertions.assertEquals("bejhphoycmsxa", model.value().get(0).display().resource());
        Assertions.assertEquals("hdxbmtqio", model.value().get(0).display().operation());
        Assertions.assertEquals("zehtbmu", model.value().get(0).display().description());
        Assertions.assertEquals(false, model.value().get(0).isDataAction());
        Assertions.assertEquals("siznto", model.nextLink());
    }
}
