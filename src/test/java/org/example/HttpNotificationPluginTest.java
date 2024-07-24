package org.example;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for HttpNotificationPlugin.
 */
public class HttpNotificationPluginTest {

    /**
     * Test that verifies the successful sending of a POST notification.
     */
    @Test
    public void testPostNotificationSuccess() {
        HttpNotificationPlugin plugin = new HttpNotificationPlugin();
        Map<String, String> config = new HashMap<>();
        config.put("url", "http://httpbin.org/post");
        config.put("method", "POST");
        config.put("body", "{\"message\":\"Hello, World!\"}");
        config.put("contentType", "application/json");

        assertTrue(plugin.postNotification("trigger", new HashMap<>(), config));
    }

    /**
     * Test that verifies the failure in sending a notification due to an invalid URL.
     */
    @Test
    public void testPostNotificationFailure() {
        HttpNotificationPlugin plugin = new HttpNotificationPlugin();
        Map<String, String> config = new HashMap<>();
        config.put("url", "http://invalid-url");
        config.put("method", "POST");
        config.put("body", "{\"message\":\"Hello, World!\"}");
        config.put("contentType", "application/json");

        assertFalse(plugin.postNotification("trigger", new HashMap<>(), config));
    }

}
