package com.salesapp.controller.v1;

import com.salesapp.dto.request.StoreLocationRequest;
import com.salesapp.dto.response.StoreLocationResponse;
import com.salesapp.entity.StoreLocation;
import com.salesapp.service.StoreLocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/store")
@RequiredArgsConstructor
public class StoreLocationV1Controller {

    private final StoreLocationService storeLocationService;

    @GetMapping("/list")
    public ResponseEntity<List<StoreLocationResponse>> getAllStores() {
        return ResponseEntity.ok(storeLocationService.getAllStoreLocations());
    }

    @PostMapping("/create")
    public ResponseEntity<StoreLocation> createStore(@RequestBody StoreLocation storeLocation) {
        return ResponseEntity.ok(storeLocationService.save(storeLocation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreLocation> updateStore(@PathVariable Integer id,
                                                     @RequestBody StoreLocationRequest request) {
        return ResponseEntity.ok(storeLocationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Integer id) {
        storeLocationService.delete(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
