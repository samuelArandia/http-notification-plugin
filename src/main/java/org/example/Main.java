package org.example;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class to demonstrate the usage of HttpNotificationPlugin with internationalization.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Main method to run the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Set the default locale (optional, can be set from system properties or configuration)
        Locale.setDefault(new Locale("en", "US"));

        // Load the resource bundle for internationalization
        ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.getDefault());

        // Create a new instance of the HttpNotificationPlugin
        HttpNotificationPlugin plugin = new HttpNotificationPlugin();

        // Configure the parameters for the notification
        Map<String, String> config = new HashMap<>();
        config.put("url", "http://httpbin.org/post");
        config.put("method", "POST");
        config.put("body", "{\"message\":\"Hello, World!\"}");
        config.put("contentType", "application/json");

        // Send the notification and handle the result
        try {
            boolean result = plugin.postNotification("trigger", new HashMap<>(), config);
            if (result) {
                LOGGER.info(messages.getString("notification.success"));
            } else {
                LOGGER.warning(messages.getString("notification.failure"));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, messages.getString("notification.error"), e);
        }
    }
}