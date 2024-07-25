package org.example;

import java.util.*;
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

        Locale.setDefault(new Locale("en", "US"));

        // Load the resource bundle for internationalization
        ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.getDefault());

        // Create a new instance of the HttpNotificationPlugin
        HttpNotificationPlugin plugin = new HttpNotificationPlugin();

        // Generate a random message
        String randomBody = getRandomMessage();

        // Configure the notification
        Map<String, String> config = new HashMap<>();
        config.put("url", "http://httpbin.org/post");
        config.put("method", "POST");
        config.put("body", "{\"message\":\"" + randomBody + "\"}");
        config.put("contentType", "application/json");

        // Send the notification
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

    /**
     * Get a random message from a predefined list.
     *
     * @return A random message.
     */
    private static String getRandomMessage() {
        List<String> messages = Arrays.asList(
                "System update completed successfully.",
                "New user registered: Samuel Arandia",
                "Database backup was successful.",
                "Server rebooted without issues.",
                "New comment posted on the blog.",
                "User login detected from a new device.",
                "Password change request received.",
                "Scheduled maintenance completed.",
                "Payment received for order ID: 12345.",
                "New order placed with ID: 12345."
        );

        Random random = new Random();
        return messages.get(random.nextInt(messages.size()));
    }
}
