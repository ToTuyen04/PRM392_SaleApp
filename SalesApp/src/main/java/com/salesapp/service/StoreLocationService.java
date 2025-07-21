package com.salesapp.service;

import com.salesapp.dto.request.StoreLocationRequest;
import com.salesapp.dto.response.StoreLocationResponse;
import com.salesapp.entity.StoreLocation;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import com.salesapp.repository.StoreLocationRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreLocationService {

    private final StoreLocationRepository storeLocationRepository;

    public List<StoreLocationResponse> getAllStoreLocations() {
        List<StoreLocation> locations = storeLocationRepository.findAll();
        return locations.stream().map(loc -> new StoreLocationResponse(
                loc.getId(),
                loc.getLatitude().doubleValue(),
                loc.getLongitude().doubleValue(),
                loc.getStoreName(),
                loc.getAddress()
        )).collect(Collectors.toList());
    }

    public StoreLocation update(Integer id, StoreLocationRequest request) {
        StoreLocation store = storeLocationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STORE_LOCATION_NOT_FOUND));

        store.setLatitude(BigDecimal.valueOf(request.getLatitude()));
        store.setLongitude(BigDecimal.valueOf(request.getLongitude()));
        store.setAddress(request.getAddress());
        store.setStoreName(request.getStoreName());

        return storeLocationRepository.save(store);

    }

    public void delete(Integer id) {
        if (!storeLocationRepository.existsById(id)) {
            throw new AppException(ErrorCode.STORE_LOCATION_NOT_FOUND);
        }
        storeLocationRepository.deleteById(id);
        throw new RuntimeException("Store location deleted");

    }

    public StoreLocation save(StoreLocation storeLocation) {
        return storeLocationRepository.save(storeLocation);
    }

}