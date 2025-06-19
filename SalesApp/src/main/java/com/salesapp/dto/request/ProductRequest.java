package com.salesapp.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
     String productName;
     String briefDescription;
     String fullDescription;
     String technicalSpecifications;
     BigDecimal price;
     String imageURL;
     Integer categoryID;
}
