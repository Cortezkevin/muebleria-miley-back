package com.furniture.miley.profile.service;

import com.furniture.miley.commons.helpers.NotificationHelpers;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.profile.dto.notification.NewNotificationDTO;
import com.furniture.miley.profile.dto.notification.NotificationDTO;
import com.furniture.miley.profile.model.Notification;
import com.furniture.miley.profile.repository.NotificationRepository;
import com.furniture.miley.security.model.MainUser;
import com.furniture.miley.security.model.User;
import com.furniture.miley.security.service.UserService;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public void sendNotificationTo(User user, NewNotificationDTO newNotificationDTO) throws StripeException, FirebaseMessagingException {
        if(user == null || newNotificationDTO == null) return;
        Timestamp date = new Timestamp(System.currentTimeMillis());

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .title(newNotificationDTO.title())
                        .body(newNotificationDTO.body())
                        .date(date)
                        .build()
        );

        user.getNotifications().add(notification);
        userService.save(user);

        NotificationHelpers.sendNotification(
                user,
                newNotificationDTO.title(),
                newNotificationDTO.body(),
                Map.of("date",date.toString(), "id", notification.getId()),
                newNotificationDTO.action()
        );
    }

    public void sendNotificationTo(List<User> users, NewNotificationDTO newNotificationDTO) throws FirebaseMessagingException {
        if(users == null || users.isEmpty() || newNotificationDTO == null) return;

        Timestamp date = new Timestamp(System.currentTimeMillis());

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .title(newNotificationDTO.title())
                        .body(newNotificationDTO.body())
                        .date(date)
                        .build()
        );

        List<String> tokens = new ArrayList<>();

        List<User> usersUpdated = users.stream().map( user -> {
            user.getNotifications().add(notification);
            if(user.getNotificationMobileToken() != null ) tokens.add(user.getNotificationMobileToken());
            if(user.getNotificationWebToken() != null ) tokens.add(user.getNotificationWebToken());
            return user;
        }).toList();

        userService.saveAll(usersUpdated);

        NotificationHelpers.sendNotification(
               tokens,
                newNotificationDTO.title(),
                newNotificationDTO.body(),
                Map.of("date",date.toString(), "id", notification.getId()),
                newNotificationDTO.action()
        );
    }

    public List<NotificationDTO> getAllFromSession() throws ResourceNotFoundException {
        MainUser mainUser = (MainUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(mainUser.getEmail());
        return user.getNotifications().stream().map(NotificationDTO::toDTO).toList();
    }
}
