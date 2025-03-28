// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.migration.assessment.generated;

/**
 * Samples for MachinesOperations ListByAssessmentProject.
 */
public final class MachinesOperationsListByAssessmentProjectSamples {
    /*
     * x-ms-original-file:
     * specification/migrate/resource-manager/Microsoft.Migrate/AssessmentProjects/stable/2023-03-15/examples/
     * MachinesOperations_ListByAssessmentProject_MaximumSet_Gen.json
     */
    /**
     * Sample code: MachinesOperations_ListByAssessmentProject_MaximumSet_Gen.
     * 
     * @param manager Entry point to MigrationAssessmentManager.
     */
    public static void machinesOperationsListByAssessmentProjectMaximumSetGen(
        com.azure.resourcemanager.migration.assessment.MigrationAssessmentManager manager) {
        manager.machinesOperations()
            .listByAssessmentProject("ayagrawrg", "app18700project", null, 1, null, 1,
                com.azure.core.util.Context.NONE);
    }
}
