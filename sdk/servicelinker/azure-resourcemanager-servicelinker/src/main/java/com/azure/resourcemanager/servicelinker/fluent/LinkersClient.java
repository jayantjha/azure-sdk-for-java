// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.servicelinker.fluent;

import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.Response;
import com.azure.core.management.polling.PollResult;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import com.azure.resourcemanager.servicelinker.fluent.models.LinkerResourceInner;
import com.azure.resourcemanager.servicelinker.fluent.models.SourceConfigurationResultInner;
import com.azure.resourcemanager.servicelinker.fluent.models.ValidateOperationResultInner;
import com.azure.resourcemanager.servicelinker.models.LinkerPatch;

/**
 * An instance of this class provides access to all the operations defined in LinkersClient.
 */
public interface LinkersClient {
    /**
     * Returns list of Linkers which connects to the resource.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the list of Linker as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<LinkerResourceInner> list(String resourceUri);

    /**
     * Returns list of Linkers which connects to the resource.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the list of Linker as paginated response with {@link PagedIterable}.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    PagedIterable<LinkerResourceInner> list(String resourceUri, Context context);

    /**
     * Returns Linker resource for a given name.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return linker of source and target resource along with {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<LinkerResourceInner> getWithResponse(String resourceUri, String linkerName, Context context);

    /**
     * Returns Linker resource for a given name.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return linker of source and target resource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    LinkerResourceInner get(String resourceUri, String linkerName);

    /**
     * Create or update linker resource.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param parameters Linker details.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of linker of source and target resource.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<LinkerResourceInner>, LinkerResourceInner> beginCreateOrUpdate(String resourceUri,
        String linkerName, LinkerResourceInner parameters);

    /**
     * Create or update linker resource.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param parameters Linker details.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of linker of source and target resource.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<LinkerResourceInner>, LinkerResourceInner> beginCreateOrUpdate(String resourceUri,
        String linkerName, LinkerResourceInner parameters, Context context);

    /**
     * Create or update linker resource.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param parameters Linker details.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return linker of source and target resource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    LinkerResourceInner createOrUpdate(String resourceUri, String linkerName, LinkerResourceInner parameters);

    /**
     * Create or update linker resource.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param parameters Linker details.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return linker of source and target resource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    LinkerResourceInner createOrUpdate(String resourceUri, String linkerName, LinkerResourceInner parameters,
        Context context);

    /**
     * Delete a link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of long-running operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<Void>, Void> beginDelete(String resourceUri, String linkerName);

    /**
     * Delete a link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of long-running operation.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<Void>, Void> beginDelete(String resourceUri, String linkerName, Context context);

    /**
     * Delete a link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    void delete(String resourceUri, String linkerName);

    /**
     * Delete a link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    void delete(String resourceUri, String linkerName, Context context);

    /**
     * Operation to update an existing link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param parameters Linker details.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of linker of source and target resource.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<LinkerResourceInner>, LinkerResourceInner> beginUpdate(String resourceUri, String linkerName,
        LinkerPatch parameters);

    /**
     * Operation to update an existing link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param parameters Linker details.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of linker of source and target resource.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<LinkerResourceInner>, LinkerResourceInner> beginUpdate(String resourceUri, String linkerName,
        LinkerPatch parameters, Context context);

    /**
     * Operation to update an existing link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param parameters Linker details.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return linker of source and target resource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    LinkerResourceInner update(String resourceUri, String linkerName, LinkerPatch parameters);

    /**
     * Operation to update an existing link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param parameters Linker details.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return linker of source and target resource.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    LinkerResourceInner update(String resourceUri, String linkerName, LinkerPatch parameters, Context context);

    /**
     * Validate a link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of the validation operation result for a linker.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<ValidateOperationResultInner>, ValidateOperationResultInner> beginValidate(String resourceUri,
        String linkerName);

    /**
     * Validate a link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the {@link SyncPoller} for polling of the validation operation result for a linker.
     */
    @ServiceMethod(returns = ReturnType.LONG_RUNNING_OPERATION)
    SyncPoller<PollResult<ValidateOperationResultInner>, ValidateOperationResultInner> beginValidate(String resourceUri,
        String linkerName, Context context);

    /**
     * Validate a link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the validation operation result for a linker.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ValidateOperationResultInner validate(String resourceUri, String linkerName);

    /**
     * Validate a link.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return the validation operation result for a linker.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    ValidateOperationResultInner validate(String resourceUri, String linkerName, Context context);

    /**
     * list source configurations for a linker.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @param context The context to associate with this operation.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return configurations for source resource, include appSettings, connectionString and serviceBindings along with
     * {@link Response}.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    Response<SourceConfigurationResultInner> listConfigurationsWithResponse(String resourceUri, String linkerName,
        Context context);

    /**
     * list source configurations for a linker.
     * 
     * @param resourceUri The fully qualified Azure Resource manager identifier of the resource to be connected.
     * @param linkerName The name Linker resource.
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws com.azure.core.management.exception.ManagementException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     * @return configurations for source resource, include appSettings, connectionString and serviceBindings.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    SourceConfigurationResultInner listConfigurations(String resourceUri, String linkerName);
}
