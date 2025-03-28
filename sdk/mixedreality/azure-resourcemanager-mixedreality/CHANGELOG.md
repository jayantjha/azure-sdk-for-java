# Release History

## 1.1.0-beta.1 (Unreleased)

### Features Added

### Breaking Changes

### Bugs Fixed

### Other Changes

## 1.0.0 (2024-12-23)

- Azure Resource Manager MixedReality client library for Java. This package contains Microsoft Azure SDK for MixedReality Management SDK. Mixed Reality Client. Package tag package-2021-01. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

### Other Changes

- Release Azure Resource Manager MixedReality client library for Java.

## 1.0.0-beta.3 (2024-10-17)

- Azure Resource Manager MixedReality client library for Java. This package contains Microsoft Azure SDK for MixedReality Management SDK. Mixed Reality Client. Package tag package-2021-01. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

### Features Added

#### `models.Identity` was modified

* `fromJson(com.azure.json.JsonReader)` was added
* `toJson(com.azure.json.JsonWriter)` was added

#### `models.LogSpecification` was modified

* `fromJson(com.azure.json.JsonReader)` was added
* `toJson(com.azure.json.JsonWriter)` was added

#### `models.OperationProperties` was modified

* `toJson(com.azure.json.JsonWriter)` was added
* `fromJson(com.azure.json.JsonReader)` was added

#### `models.MetricSpecification` was modified

* `toJson(com.azure.json.JsonWriter)` was added
* `fromJson(com.azure.json.JsonReader)` was added

#### `models.ServiceSpecification` was modified

* `fromJson(com.azure.json.JsonReader)` was added
* `toJson(com.azure.json.JsonWriter)` was added

#### `models.SpatialAnchorsAccountPage` was modified

* `fromJson(com.azure.json.JsonReader)` was added
* `toJson(com.azure.json.JsonWriter)` was added

#### `models.AccountKeyRegenerateRequest` was modified

* `toJson(com.azure.json.JsonWriter)` was added
* `fromJson(com.azure.json.JsonReader)` was added

#### `models.OperationDisplay` was modified

* `fromJson(com.azure.json.JsonReader)` was added
* `toJson(com.azure.json.JsonWriter)` was added

#### `models.Sku` was modified

* `toJson(com.azure.json.JsonWriter)` was added
* `fromJson(com.azure.json.JsonReader)` was added

#### `models.RemoteRenderingAccountPage` was modified

* `toJson(com.azure.json.JsonWriter)` was added
* `fromJson(com.azure.json.JsonReader)` was added

#### `models.CheckNameAvailabilityRequest` was modified

* `toJson(com.azure.json.JsonWriter)` was added
* `fromJson(com.azure.json.JsonReader)` was added

#### `models.OperationPage` was modified

* `toJson(com.azure.json.JsonWriter)` was added
* `fromJson(com.azure.json.JsonReader)` was added

#### `models.MetricDimension` was modified

* `fromJson(com.azure.json.JsonReader)` was added
* `toJson(com.azure.json.JsonWriter)` was added

## 1.0.0-beta.2 (2023-01-18)

- Azure Resource Manager MixedReality client library for Java. This package contains Microsoft Azure SDK for MixedReality Management SDK. Mixed Reality Client. Package tag package-2021-01. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

### Breaking Changes

#### `models.RemoteRenderingAccounts` was modified

* `deleteWithResponse(java.lang.String,java.lang.String,com.azure.core.util.Context)` was removed

#### `models.SpatialAnchorsAccounts` was modified

* `deleteWithResponse(java.lang.String,java.lang.String,com.azure.core.util.Context)` was removed

### Features Added

#### `models.RemoteRenderingAccounts` was modified

* `deleteByResourceGroupWithResponse(java.lang.String,java.lang.String,com.azure.core.util.Context)` was added

#### `models.SpatialAnchorsAccount` was modified

* `resourceGroupName()` was added

#### `models.MetricSpecification` was modified

* `withFillGapWithZero(java.lang.Boolean)` was added
* `sourceMdmNamespace()` was added
* `withSupportedAggregationTypes(java.util.List)` was added
* `metricFilterPattern()` was added
* `supportedTimeGrainTypes()` was added
* `withMetricFilterPattern(java.lang.String)` was added
* `withSourceMdmAccount(java.lang.String)` was added
* `withCategory(java.lang.String)` was added
* `withEnableRegionalMdmAccount(java.lang.Boolean)` was added
* `withSourceMdmNamespace(java.lang.String)` was added
* `enableRegionalMdmAccount()` was added
* `sourceMdmAccount()` was added
* `withLockedAggregationType(java.lang.String)` was added
* `supportedAggregationTypes()` was added
* `category()` was added
* `fillGapWithZero()` was added
* `withSupportedTimeGrainTypes(java.util.List)` was added
* `lockedAggregationType()` was added

#### `models.RemoteRenderingAccount` was modified

* `resourceGroupName()` was added

#### `MixedRealityManager$Configurable` was modified

* `withRetryOptions(com.azure.core.http.policy.RetryOptions)` was added
* `withScope(java.lang.String)` was added

#### `models.SpatialAnchorsAccounts` was modified

* `deleteByResourceGroupWithResponse(java.lang.String,java.lang.String,com.azure.core.util.Context)` was added

#### `MixedRealityManager` was modified

* `authenticate(com.azure.core.http.HttpPipeline,com.azure.core.management.profile.AzureProfile)` was added

#### `models.MetricDimension` was modified

* `toBeExportedForShoebox()` was added
* `withToBeExportedForShoebox(java.lang.Boolean)` was added

## 1.0.0-beta.1 (2021-04-27)

- Azure Resource Manager MixedReality client library for Java. This package contains Microsoft Azure SDK for MixedReality Management SDK. Mixed Reality Client. Package tag package-2021-01. For documentation on how to use this package, please see [Azure Management Libraries for Java](https://aka.ms/azsdk/java/mgmt).

