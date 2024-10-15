package com.badia.examples.vertxdocker;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import io.vertx.core.json.JsonObject;
import java.io.UnsupportedEncodingException;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotificationVerticle extends AbstractVerticle{
    private static final Logger logger = LoggerFactory.getLogger(NotificationVerticle.class);
    @Override
    public void start() {
        vertx.eventBus().consumer("event.notifications", message -> {
            try {
                JsonObject eventData = (JsonObject) message.body();
                logger.debug("Received event data: {}", eventData.encodePrettily());
                sendPushNotification(eventData);
            } catch (Exception e) {
                logger.error("Exception while processing event.notifications: ", e);
                message.fail(500, e.getMessage());
            }
        });
    }
    
    
    private void sendPushNotification(JsonObject eventData) {
        try {
            Message message = Message.builder()
                .putData("title", eventData.getString("name"))
                .putData("body", "Lugar: " + eventData.getString("place"))
                .putData("date", eventData.getString("date"))
                .putData("addToCalendarUrl", generateGoogleCalendarLink(eventData))
                .setToken(eventData.getString("deviceToken")) // El token del dispositivo al que enviarás la notificación
                .build();
    
            System.out.println("Attempting to send notification to device token: " + eventData.getString("deviceToken"));
    
            ApiFuture<String> future = FirebaseMessaging.getInstance().sendAsync(message);
    
            ApiFutures.addCallback(future, new ApiFutureCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    System.out.println("Mensaje enviado exitosamente: " + result);
                }
    
                @Override
                public void onFailure(Throwable t) {
                    System.err.println("Error al enviar el mensaje: " + t);
                }
            }, MoreExecutors.directExecutor());
        } catch (Exception e) {
            System.err.println("Exception occurred while sending notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    private String generateGoogleCalendarLink(JsonObject eventData) {
        String baseUrl = "https://www.google.com/calendar/render?action=TEMPLATE";
        String text = "&text=" + URLEncoder.encode(eventData.getString("name"), StandardCharsets.UTF_8);
        String dates = "&dates=" + eventData.getString("startDate") + "/" + eventData.getString("endDate");
        String details = "&details=" + URLEncoder.encode(eventData.getString("description"), StandardCharsets.UTF_8);
        String location = "&location=" + URLEncoder.encode(eventData.getString("place"), StandardCharsets.UTF_8);
        return baseUrl + text + dates + details + location;
    }
}
