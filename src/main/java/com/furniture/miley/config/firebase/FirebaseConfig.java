package com.furniture.miley.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void initFirebase() throws IOException {
        Resource keyResource = resourceLoader.getResource("classpath:miley-notifications-firebase-adminsdk-fbsvc-f77ecb0036.json");
        InputStream serviceAccount  = keyResource.getInputStream();
        FirebaseOptions options = FirebaseOptions.builder()
                .setProjectId("miley-notifications")
                .setCredentials(
                        GoogleCredentials.fromStream(serviceAccount)
                )
                .build();
        FirebaseApp.initializeApp(options);
    }

}
