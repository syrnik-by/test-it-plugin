package ru.testit.management.clients;

import jakarta.ws.rs.client.Client;
import ru.psb.testit.client.api.*;
import ru.psb.testit.client.invoker.ApiClient;
import ru.testit.management.windows.settings.TmsSettingsState;

public class PluginApiClient extends ApiClient {

    @Override
    protected Client buildHttpClient() {
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(PluginApiClient.class.getClassLoader());
            return super.buildHttpClient();
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
}
