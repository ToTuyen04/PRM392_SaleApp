package com.salesapp.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {
     Integer id;
     String productName;
     String briefDescription;
     String fullDescription;
     String technicalSpecifications;
     BigDecimal price;
     String imageURL;
     Integer categoryID;
     String categoryName;
}
