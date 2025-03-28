// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.resourcemanager.appplatform;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpPipeline;
import com.azure.core.http.policy.HttpLogOptions;
import com.azure.core.http.policy.HttpPipelinePolicy;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.logging.ClientLogger;
import com.azure.core.util.logging.LogLevel;
import com.azure.resourcemanager.appservice.AppServiceManager;
import com.azure.resourcemanager.dns.DnsZoneManager;
import com.azure.resourcemanager.keyvault.KeyVaultManager;
import com.azure.resourcemanager.resources.fluentcore.utils.HttpPipelineProvider;
import com.azure.resourcemanager.resources.fluentcore.utils.ResourceManagerUtils;
import com.azure.resourcemanager.test.ResourceManagerTestProxyTestBase;
import com.azure.resourcemanager.test.utils.TestDelayProvider;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AppPlatformTest extends ResourceManagerTestProxyTestBase {
    private static final ClientLogger LOGGER = new ClientLogger(AppPlatformTest.class);

    protected AppPlatformManager appPlatformManager;
    protected AppServiceManager appServiceManager;
    protected DnsZoneManager dnsZoneManager;
    protected KeyVaultManager keyVaultManager;
    protected String rgName = "";

    @Override
    protected HttpPipeline buildHttpPipeline(TokenCredential credential, AzureProfile profile,
        HttpLogOptions httpLogOptions, List<HttpPipelinePolicy> policies, HttpClient httpClient) {
        return HttpPipelineProvider.buildHttpPipeline(credential, profile, null, httpLogOptions, null,
            new RetryPolicy("Retry-After", ChronoUnit.SECONDS), policies, httpClient);
    }

    @Override
    protected void initializeClients(HttpPipeline httpPipeline, AzureProfile profile) {
        ResourceManagerUtils.InternalRuntimeContext.setDelayProvider(new TestDelayProvider(!isPlaybackMode()));
        rgName = generateRandomResourceName("rg", 20);
        appPlatformManager = buildManager(AppPlatformManager.class, httpPipeline, profile);
        appServiceManager = buildManager(AppServiceManager.class, httpPipeline, profile);
        dnsZoneManager = buildManager(DnsZoneManager.class, httpPipeline, profile);
        keyVaultManager = buildManager(KeyVaultManager.class, httpPipeline, profile);
    }

    @Override
    protected void cleanUpResources() {
        try {
            appPlatformManager.resourceManager().resourceGroups().beginDeleteByName(rgName);
        } catch (Exception e) {
        }
    }

    protected boolean checkRedirect(String url) throws IOException {
        for (int i = 0; i < 60; ++i) {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setInstanceFollowRedirects(false);
            try {
                connection.connect();
                if (200 <= connection.getResponseCode() && connection.getResponseCode() < 400) {
                    connection.getInputStream().close();
                    if (connection.getResponseCode() / 100 == 3) {
                        return true;
                    }
                    LOGGER.verbose("Do request to {} with response code {}", url, connection.getResponseCode());
                }
            } catch (Exception e) {
                LOGGER.log(LogLevel.VERBOSE, () -> "Do request to " + url + " with error", e);
            } finally {
                connection.disconnect();
            }
            ResourceManagerUtils.sleep(Duration.ofSeconds(5));
        }
        return false;
    }

    protected boolean requestSuccess(String url) throws Exception {
        for (int i = 0; i < 60; ++i) {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            try {
                connection.connect();
                if (connection.getResponseCode() == 200) {
                    LOGGER.log(LogLevel.VERBOSE, () -> "Request to " + url + " succeeded");
                    connection.getInputStream().close();
                    return true;
                }
                LOGGER.verbose("Do request to {} with response code {}", url, connection.getResponseCode());
            } catch (Exception e) {
                LOGGER.log(LogLevel.VERBOSE, () -> "Do request to " + url + " with error", e);
            } finally {
                connection.disconnect();
            }
            ResourceManagerUtils.sleep(Duration.ofSeconds(5));
        }
        return false;
    }

    protected void allowAllSSL() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((urlHostName, session) -> true);
    }
}
