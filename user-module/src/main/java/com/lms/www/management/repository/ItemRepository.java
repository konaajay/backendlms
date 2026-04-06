package com.lms.www.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findBySkuCode(String skuCode);

}