package com.salesapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreLocationResponse {
    private Integer id;
    private double latitude;
    private double longitude;
    private String storeName;
    private String address;
}
