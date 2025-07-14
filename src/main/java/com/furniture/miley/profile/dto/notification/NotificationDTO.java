package com.furniture.miley.profile.dto.notification;

import com.furniture.miley.profile.model.Notification;

import java.sql.Timestamp;

public record NotificationDTO(
        String id,
        String title,
        String body,
        Timestamp date
) {
    public static NotificationDTO toDTO(Notification notification){
        return new NotificationDTO(
                notification.getId(),
                notification.getTitle(),
                notification.getBody(),
                notification.getDate()
        );
    }
}
