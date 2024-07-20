package org.example;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        // Create a new instance of the HttpNotificationPlugin
        HttpNotificationPlugin plugin = new HttpNotificationPlugin();

        //Config the parameters for the notification
        Map<String, String> config = new HashMap<>();
        config.put("url", "http://httpbin.org/post");
        config.put("method", "POST");
        config.put("body", "{\"message\":\"Hello, World!\"}");
        config.put("contentType", "application/json");

        //send the notification
        boolean result = plugin.postNotification("trigger", new HashMap<>(), config);

        //Print the result
        System.out.println("Notification sent: " + result);
    }
}