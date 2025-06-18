package com.salesapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryID", nullable = false)
    private Integer id;

    @Column(name = "CategoryName", nullable = false, length = 100)
    private String categoryName;

    @OneToMany(mappedBy = "categoryID")
    private Set<Product> products = new LinkedHashSet<>();

}