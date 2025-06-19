package com.salesapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID", nullable = false)
    private Integer id;

    @Column(name = "ProductName", nullable = false, length = 100)
    private String productName;

    @Column(name = "BriefDescription")
    private String briefDescription;

    @Lob
    @Column(name = "FullDescription")
    private String fullDescription;

    @Lob
    @Column(name = "TechnicalSpecifications")
    private String technicalSpecifications;

    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "ImageURL")
    private String imageURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryID", nullable = false)
    private Category categoryID;

    @OneToMany(mappedBy = "productID")
    private Set<CartItem> cartItems = new LinkedHashSet<>();

}