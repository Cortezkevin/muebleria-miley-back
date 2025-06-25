package com.furniture.miley.commons.helpers;

import com.furniture.miley.security.model.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.springframework.stereotype.Component;

@Component
public class StripeHelpers {
    public static String createStripeClient(User user) throws StripeException {
        Stripe.apiKey = "sk_test_51LDHfGCjrtAyA6AHlTaXE88uQjaFPSq0EHYWGbsCIiELO6Jt1n1v8PGBPtl4PRlZrOSpl5gK8XC3xTsiusbZqP8D00sPgDAJA2";
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(user.getPersonalInformation().getFullName())
                        .setEmail(user.getEmail())
                        .addPreferredLocale("ES")
                        .build();

        return Customer.create(params).getId();
    }
}