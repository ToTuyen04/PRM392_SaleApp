package com.salesapp.mapper;

import com.salesapp.dto.request.StoreLocationRequest;
import com.salesapp.dto.response.StoreLocationResponse;
import com.salesapp.entity.StoreLocation;

import java.math.BigDecimal;

public class StoreLocationMapper {

    public static StoreLocationResponse toResponse(StoreLocation entity) {
        return new StoreLocationResponse(
                entity.getId(),
                entity.getLatitude().doubleValue(),
                entity.getLongitude().doubleValue(),
                entity.getAddress(),
                "Cửa hàng #" + entity.getId()
        );
    }

    public static StoreLocation toEntity(StoreLocationRequest req) {
        StoreLocation entity = new StoreLocation();
        entity.setLatitude(BigDecimal.valueOf(req.getLatitude()));
        entity.setLongitude(BigDecimal.valueOf(req.getLongitude()));
        entity.setAddress(req.getAddress());
        return entity;
    }
}
