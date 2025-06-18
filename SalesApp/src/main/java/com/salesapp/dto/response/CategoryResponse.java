package com.salesapp.dto.response;

import com.salesapp.entity.Product;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    private Integer id;
    private String categoryName;

}
