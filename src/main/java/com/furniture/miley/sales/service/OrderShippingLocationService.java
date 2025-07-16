package com.furniture.miley.sales.service;

import com.furniture.miley.delivery.model.OrderShippingLocation;
import com.furniture.miley.sales.repository.order.OrderShippingLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderShippingLocationService {
    private final OrderShippingLocationRepository mRepository;

    public OrderShippingLocation save(OrderShippingLocation o){
        return mRepository.save(o);
    }
}
