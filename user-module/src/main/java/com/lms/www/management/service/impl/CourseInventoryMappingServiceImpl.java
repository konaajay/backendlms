package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.CourseInventoryMapping;
import com.lms.www.management.model.Item;
import com.lms.www.management.repository.CourseInventoryMappingRepository;
import com.lms.www.management.repository.ItemRepository;
import com.lms.www.management.service.CourseInventoryMappingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseInventoryMappingServiceImpl implements CourseInventoryMappingService {

    private final CourseInventoryMappingRepository mappingRepository;
    private final ItemRepository itemRepository;

    @Override
    public CourseInventoryMapping createMapping(CourseInventoryMapping mapping) {

        // ✅ validation
        if (mapping.getCourseId() == null) {
            throw new RuntimeException("Course ID is required");
        }

        if (mapping.getItem() == null || mapping.getItem().getId() == null) {
            throw new RuntimeException("Item is required");
        }

        // ✅ fetch item
        Item item = itemRepository.findById(mapping.getItem().getId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        mapping.setItem(item);

        // ✅ defaults
        if (mapping.getMandatory() == null) {
            mapping.setMandatory(false);
        }

        if (mapping.getAutoReserve() == null) {
            mapping.setAutoReserve(false);
        }

        if (mapping.getRefundable() == null) {
            mapping.setRefundable(false);
        }

        if (mapping.getPrice() == null) {
            mapping.setPrice(0.0);
        }

        if (mapping.getQuantityRequired() == null) {
            mapping.setQuantityRequired(1);
        }

        if (mapping.getStatus() == null) {
            mapping.setStatus("ACTIVE");
        }

        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setUpdatedAt(LocalDateTime.now());

        return mappingRepository.save(mapping);
    }

    @Override
    public List<CourseInventoryMapping> getMappingsByCourse(Long courseId) {
        return mappingRepository.findByCourseId(courseId);
    }

    @Override
    public List<CourseInventoryMapping> getAllMappings() {
        return mappingRepository.findAll();
    }

    @Override
    public void deleteMapping(Long id) {
        mappingRepository.deleteById(id);
    }
}