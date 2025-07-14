package com.furniture.miley.commons.helpers;

import com.furniture.miley.security.model.User;
import com.google.firebase.messaging.*;
import com.stripe.exception.StripeException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class NotificationHelpers {
    public static String sendNotification(User receptor, String title, String content, Map<String, String> extraData, String intentAction) throws FirebaseMessagingException {
        if(receptor.getNotificationMobileToken() != null && receptor.getNotificationWebToken() != null){
            List<String> tokens = Arrays.asList(receptor.getNotificationMobileToken(), receptor.getNotificationWebToken());
            MulticastMessage multicastMessage = MulticastMessage.builder()
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(content)
                                    .build()
                    )
                    .addAllTokens(tokens)
                    .putAllData(extraData)
                    .setAndroidConfig(
                            AndroidConfig.builder()
                                    .setNotification(AndroidNotification.builder()
                                            .setClickAction(intentAction)
                                            .build())
                                    .build()
                    )
                    .build();

            FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);
            return "Notifications sent";
        }else {
            if(receptor.getNotificationMobileToken() != null || receptor.getNotificationWebToken() != null){
                Message message = Message.builder()
                        .putAllData(extraData)
                        .setNotification(
                                Notification.builder()
                                        .setTitle(title)
                                        .setBody(content)
                                        .build()
                        )
                        .setAndroidConfig(
                                AndroidConfig.builder()
                                        .setNotification(AndroidNotification.builder()
                                                .setClickAction(intentAction)
                                                .build())
                                        .build()
                        )
                        .setToken(
                                receptor.getNotificationMobileToken() != null
                                        ? receptor.getNotificationMobileToken()
                                        : receptor.getNotificationWebToken()
                        )
                        .build();
                return FirebaseMessaging.getInstance().send(message);
            }else {
                return "No tokens";
            }
        }
    }

    public static void sendNotification(List<String> tokens, String title, String body, Map<String, String> extraData, String action) throws FirebaseMessagingException {
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .addAllTokens(tokens)
                .putAllData(extraData)
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setNotification(AndroidNotification.builder()
                                        .setClickAction(action)
                                        .build())
                                .build()
                )
                .build();
        FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);
    }
}
