package com.salesapp.dto.request;

import com.salesapp.entity.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateRequest {

    private String categoryName;

}
