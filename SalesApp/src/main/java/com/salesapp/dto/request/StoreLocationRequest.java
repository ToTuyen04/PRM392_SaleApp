package com.salesapp.dto.request;

import lombok.Data;

@Data
public class StoreLocationRequest {
    private double latitude;
    private double longitude;
    private String address;
    private String storeName;
}
