package org.example;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
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
        String url = (String) config.get("url");
        String method = (String) config.get("method");
        String body = (String) config.get("body");
        String contentType = (String) config.get("contentType");

        // create the client http
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpUriRequest request = createRequest(method, url, body, contentType);
            client.execute(request);
            LOGGER.info(messages.getString("notification.success"));
            return true;
        } catch (Exception e) {
            LOGGER.severe(String.format(messages.getString("notification.error"), e.getMessage()));
            return false;
        }
    }


    /**
     * Create an HTTP request based on the method, URL, body, and content type.
     *
     * @param method      The HTTP method (GET, POST, PUT, DELETE).
     * @param url         The URL to which the request is sent.
     * @param body        Body of the request, if applicable.
     * @param contentType Type of content of the request body.
     * @return A configured HTTP request.
     * @throws Exception If the request cannot be created.
     */
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
