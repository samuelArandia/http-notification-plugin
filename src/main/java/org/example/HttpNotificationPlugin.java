package org.example;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Plugin of Notification HTTP for Rundeck.
 * Allows you to make custom HTTP requests as notifications.
 */
@Plugin(service = "Notification", name = "HttpNotificationPlugin")
@PluginDescription(title = "HTTP Notification Plugin", description = "A plugin that sends notifications via HTTP")
public class HttpNotificationPlugin implements NotificationPlugin {

    private static final Logger LOGGER = Logger.getLogger(HttpNotificationPlugin.class.getName());
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.getDefault());
    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT_MILLIS = 5000;

    /**
     * Send a notification based on the provided parameters.
     *
     * @param trigger       The trigger of the notification.
     * @param executionData The data of the execution that triggered the notification.
     * @param config        The configuration of the notification.
     * @return {@code true} if the notification was sent successfully, {@code false} otherwise.
     */
    @Override
    public boolean postNotification(String trigger, Map executionData, Map config) {
        LOGGER.info("Sending notification with trigger: " + trigger);

        try {
            validateConfig(config);

            String url = (String) config.get("url");
            String method = (String) config.get("method");
            String body = (String) config.get("body");
            String contentType = (String) config.get("contentType");

            // Create the HTTP client with timeouts
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT_MILLIS)
                    .setConnectTimeout(TIMEOUT_MILLIS)
                    .build();

            try (CloseableHttpClient client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(requestConfig)
                    .build()) {
                // save in the database
                return sendWithRetries(client, method, url, body, contentType);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format(messages.getString("notification.error"), e.getMessage()), e);
            return false;
        }
    }

    private void validateConfig(Map config) {
        Objects.requireNonNull(config.get("url"), messages.getString("error.url.null"));
        Objects.requireNonNull(config.get("method"), messages.getString("error.method.null"));
    }

    private boolean sendWithRetries(CloseableHttpClient client, String method, String url, String body, String contentType) throws Exception {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                HttpUriRequest request = createRequest(method, url, body, contentType);
                HttpResponse response = client.execute(request);

                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                // save in the database
                DatabaseUtils.logRequest(method, url, body, contentType, statusCode, responseBody);
                LOGGER.info(messages.getString("save.success"));

                if (statusCode >= 200 && statusCode < 300) {
                    LOGGER.info(messages.getString("notification.success"));
                    return true;
                } else {
                    LOGGER.warning("Received non-success response: " + statusCode + " - " + responseBody);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Attempt " + (i + 1) + " failed: " + e.getMessage(), e);
                TimeUnit.SECONDS.sleep(2);
            }
        }
        return false;
    }

    private HttpUriRequest createRequest(String method, String url, String body, String contentType) throws Exception {
        HttpUriRequest request;
        LOGGER.info("Creating request with method: " + method + ", url: " + url + ", body: " + body + ", contentType: " + contentType);
        switch (method.toUpperCase()) {
            case "POST":
                request = createPostRequest(url, body, contentType);
                break;
            case "PUT":
                request = createPutRequest(url, body, contentType);
                break;
            case "DELETE":
                request = new HttpDelete(url);
                break;
            case "GET":
            default:
                request = new HttpGet(url);
                break;
        }
        LOGGER.info("Request created successfully");
        return request;
    }

    private HttpPost createPostRequest(String url, String body, String contentType) {
        HttpPost postRequest = new HttpPost(url);
        setRequestBody(postRequest, body, contentType);
        return postRequest;
    }

    private HttpPut createPutRequest(String url, String body, String contentType) {
        HttpPut putRequest = new HttpPut(url);
        setRequestBody(putRequest, body, contentType);
        return putRequest;
    }

    private void setRequestBody(HttpEntityEnclosingRequestBase request, String body, String contentType) {
        if (body != null && !body.isEmpty()) {
            request.setEntity(new StringEntity(body, "UTF-8"));
            request.setHeader("Content-Type", contentType != null ? contentType : "application/json");
        }
    }
}
