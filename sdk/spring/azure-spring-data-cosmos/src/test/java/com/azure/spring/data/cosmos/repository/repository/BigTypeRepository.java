// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.spring.data.cosmos.repository.repository;

import com.azure.spring.data.cosmos.domain.BigType;
import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BigTypeRepository extends CosmosRepository<BigType, String> {

}
