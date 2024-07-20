package org.example;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.Map;

/**
 * Plugin of Notification HTTP for Rundeck.
 * Allows you to make custom HTTP requests as notifications.
 */
@Plugin(service = "Notification", name = "HttpNotificationPlugin")
@PluginDescription(title = "HTTP Notification Plugin", description = "A plugin that sends notifications via HTTP")
public class HttpNotificationPlugin implements NotificationPlugin {

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
        String url = (String) config.get("url");
        String method = (String) config.get("method");
        String body = (String) config.get("body");
        String contentType = (String) config.get("contentType");

        // create the client http
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Create the HTTP request based on the parameters
            HttpUriRequest request = createRequest(method, url, body, contentType);
            // Execute the request
            client.execute(request);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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

        switch (method.toUpperCase()) {
            case "POST":
                HttpPost postRequest = new HttpPost(url);
                postRequest.setEntity(new StringEntity(body));
                postRequest.setHeader("Content-Type", contentType);
                request = postRequest;
                break;
            case "PUT":
                HttpPut putRequest = new HttpPut(url);
                putRequest.setEntity(new StringEntity(body));
                putRequest.setHeader("Content-Type", contentType);
                request = putRequest;
                break;
            case "DELETE":
                HttpDelete deleteRequest = new HttpDelete(url);
                request = deleteRequest;
                break;
            case "GET":
            default:
                request = new HttpGet(url);
                break;
        }

        return request;
    }
}
