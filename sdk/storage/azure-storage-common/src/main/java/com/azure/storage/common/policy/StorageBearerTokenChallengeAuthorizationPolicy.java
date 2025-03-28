// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.storage.common.policy;

import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.http.HttpHeaderName;
import com.azure.core.http.HttpPipelineCallContext;
import com.azure.core.http.HttpResponse;
import com.azure.core.http.policy.BearerTokenAuthenticationPolicy;
import com.azure.core.util.CoreUtils;
import com.azure.core.util.logging.ClientLogger;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

/**
 * The storage authorization policy which supports challenge.
 */
public class StorageBearerTokenChallengeAuthorizationPolicy extends BearerTokenAuthenticationPolicy {

    private static final ClientLogger LOGGER = new ClientLogger(StorageBearerTokenChallengeAuthorizationPolicy.class);

    private static final String DEFAULT_SCOPE = "/.default";
    static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private String[] scopes;

    /**
     * Creates StorageBearerTokenChallengeAuthorizationPolicy.
     *
     * @param credential the token credential to authenticate the request
     * @param scopes the scopes used in credential, using default scopes when empty
     */
    public StorageBearerTokenChallengeAuthorizationPolicy(TokenCredential credential, String... scopes) {
        super(credential, scopes);
        this.scopes = scopes;
    }

    @Override
    public Mono<Void> authorizeRequest(HttpPipelineCallContext context) {
        String[] scopes = this.scopes;
        scopes = getScopes(context, scopes);
        if (scopes == null) {
            return Mono.empty();
        } else {
            return setAuthorizationHeader(context, new TokenRequestContext().addScopes(scopes));
        }
    }

    @Override
    public void authorizeRequestSync(HttpPipelineCallContext context) {
        String[] scopes = this.scopes;
        scopes = getScopes(context, scopes);

        if (scopes != null) {
            setAuthorizationHeaderSync(context, new TokenRequestContext().addScopes(scopes));
        }
    }

    @Override
    public Mono<Boolean> authorizeRequestOnChallenge(HttpPipelineCallContext context, HttpResponse response) {
        String authHeader = response.getHeaderValue(HttpHeaderName.WWW_AUTHENTICATE);
        Map<String, String> challenges = extractChallengeAttributes(authHeader, BEARER_TOKEN_PREFIX);

        String scope = getScopeFromChallenges(challenges);
        String authorization = getAuthorizationFromChallenges(challenges);

        if (scope != null) {
            scope += DEFAULT_SCOPE;
            scopes = new String[] { scope };
            scopes = getScopes(context, scopes);
        }

        if (authorization != null) {
            String tenantId = extractTenantIdFromUri(authorization);
            TokenRequestContext tokenRequestContext = new TokenRequestContext().addScopes(scopes).setTenantId(tenantId);
            return setAuthorizationHeader(context, tokenRequestContext).thenReturn(true);
        }

        if (scope != null) {
            TokenRequestContext tokenRequestContext = new TokenRequestContext().addScopes(scopes);
            return setAuthorizationHeader(context, tokenRequestContext).thenReturn(true);
        }

        return Mono.just(false);
    }

    String extractTenantIdFromUri(String uri) {
        try {
            String[] segments = new URI(uri).getPath().split("/");
            if (segments.length > 1) {
                return segments[1];
            } else {
                throw LOGGER.logExceptionAsError(new RuntimeException("Invalid authorization URI: tenantId not found"));
            }
        } catch (URISyntaxException e) {
            throw LOGGER.logExceptionAsError(new RuntimeException("Invalid authorization URI", e));
        }
    }

    @Override
    public boolean authorizeRequestOnChallengeSync(HttpPipelineCallContext context, HttpResponse response) {
        String authHeader = response.getHeaderValue(HttpHeaderName.WWW_AUTHENTICATE);
        Map<String, String> challenges = extractChallengeAttributes(authHeader, BEARER_TOKEN_PREFIX);

        String scope = getScopeFromChallenges(challenges);
        String authorization = getAuthorizationFromChallenges(challenges);

        if (scope != null) {
            scope += DEFAULT_SCOPE;
            scopes = new String[] { scope };
            scopes = getScopes(context, scopes);
        }

        if (authorization != null) {
            String tenantId = extractTenantIdFromUri(authorization);
            TokenRequestContext tokenRequestContext = new TokenRequestContext().addScopes(scopes).setTenantId(tenantId);
            setAuthorizationHeaderSync(context, tokenRequestContext);
            return true;
        }

        if (scope != null) {
            TokenRequestContext tokenRequestContext = new TokenRequestContext().addScopes(scopes);
            setAuthorizationHeaderSync(context, tokenRequestContext);
            return true;
        }

        return false;
    }

    String[] getScopes(HttpPipelineCallContext context, String[] scopes) {
        return CoreUtils.clone(scopes);
    }

    Map<String, String> extractChallengeAttributes(String header, String authChallengePrefix) {
        if (!isBearerChallenge(header, authChallengePrefix)) {
            return Collections.emptyMap();
        }

        header = header.toLowerCase(Locale.ROOT).replace(authChallengePrefix.toLowerCase(Locale.ROOT), "");

        String[] attributes = header.split(" ");
        Map<String, String> attributeMap = new HashMap<>();

        for (String pair : attributes) {
            String[] keyValue = pair.split("=");

            attributeMap.put(keyValue[0].replaceAll("\"", ""), keyValue[1].replaceAll("\"", ""));
        }

        return attributeMap;
    }

    static boolean isBearerChallenge(String authenticateHeader, String authChallengePrefix) {
        return (!CoreUtils.isNullOrEmpty(authenticateHeader)
            && authenticateHeader.toLowerCase(Locale.ROOT).startsWith(authChallengePrefix.toLowerCase(Locale.ROOT)));
    }

    String getScopeFromChallenges(Map<String, String> challenges) {
        return challenges.get("resource_id");
    }

    String getAuthorizationFromChallenges(Map<String, String> challenges) {
        return challenges.get("authorization_uri");
    }
}
