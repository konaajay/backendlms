package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.Item;
import com.lms.www.management.repository.ItemRepository;
import com.lms.www.management.service.ItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Item createItem(Item item) {

        item.setCreatedAt(LocalDateTime.now());
        item.setStatus("ACTIVE");

        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public Item updateItem(Long id, Item item) {

        Item existing = getItemById(id);

        if (item.getItemName() != null) {
            existing.setItemName(item.getItemName());
        }

        if (item.getCategory() != null) {
            existing.setCategory(item.getCategory());
        }

        if (item.getDescription() != null) {
            existing.setDescription(item.getDescription());
        }

        if (item.getUnit() != null) {
            existing.setUnit(item.getUnit());
        }

        if (item.getLocation() != null) {
            existing.setLocation(item.getLocation());
        }

        if (item.getMinStockLevel() != null) {
            existing.setMinStockLevel(item.getMinStockLevel());
        }

        if (item.getOpeningStock() != null) {
            existing.setOpeningStock(item.getOpeningStock());
        }

        if (item.getPrice() != null) {
            existing.setPrice(item.getPrice());
        }

        if (item.getTaxPercentage() != null) {
            existing.setTaxPercentage(item.getTaxPercentage());
        }

        if (item.getIsRefundable() != null) {
            existing.setIsRefundable(item.getIsRefundable());
        }

        if (item.getIsTrackable() != null) {
            existing.setIsTrackable(item.getIsTrackable());
        }

        if (item.getIsConsumable() != null) {
            existing.setIsConsumable(item.getIsConsumable());
        }

        if (item.getLinkedCourseId() != null) {
            existing.setLinkedCourseId(item.getLinkedCourseId());
        }

        if (item.getStatus() != null) {
            existing.setStatus(item.getStatus());
        }

        return itemRepository.save(existing);
    }
    @Override
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}