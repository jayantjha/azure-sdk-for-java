// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.migration.assessment.generated;

/**
 * Samples for PrivateEndpointConnectionOperations ListByAssessmentProject.
 */
public final class PrivateEndpointConnectionOperationsListByAssessmentProjectSamples {
    /*
     * x-ms-original-file:
     * specification/migrate/resource-manager/Microsoft.Migrate/AssessmentProjects/stable/2023-03-15/examples/
     * PrivateEndpointConnectionOperations_ListByAssessmentProject_MaximumSet_Gen.json
     */
    /**
     * Sample code: PrivateEndpointConnectionOperations_ListByAssessmentProject_MaximumSet_Gen.
     * 
     * @param manager Entry point to MigrationAssessmentManager.
     */
    public static void privateEndpointConnectionOperationsListByAssessmentProjectMaximumSetGen(
        com.azure.resourcemanager.migration.assessment.MigrationAssessmentManager manager) {
        manager.privateEndpointConnectionOperations()
            .listByAssessmentProject("sakanwar", "sakanwar1204project", com.azure.core.util.Context.NONE);
    }
}
