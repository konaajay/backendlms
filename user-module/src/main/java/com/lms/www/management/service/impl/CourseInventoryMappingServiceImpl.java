package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.CourseInventoryMapping;
import com.lms.www.management.model.Course;
import com.lms.www.management.repository.CourseInventoryMappingRepository;
import com.lms.www.management.repository.CourseRepository;
import com.lms.www.management.service.CourseInventoryMappingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseInventoryMappingServiceImpl implements CourseInventoryMappingService {

    private final CourseInventoryMappingRepository mappingRepository;
    private final CourseRepository courseRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void createBulkMapping(java.util.Map<String, Object> data) {
        @SuppressWarnings("unchecked")
        List<Object> mandatory = (List<Object>) data.get("mandatoryItems");
        @SuppressWarnings("unchecked")
        List<Object> optional = (List<Object>) data.get("optionalItems");

        Long courseId = data.containsKey("courseId") ? Long.valueOf(data.get("courseId").toString()) : 1L;

        if (mandatory != null) {
            for (Object itemId : mandatory) {
                saveSingle(courseId, itemId.toString(), true);
            }
        }

        if (optional != null) {
            for (Object itemId : optional) {
                saveSingle(courseId, itemId.toString(), false);
            }
        }
    }

    private void saveSingle(Long courseId, String itemId, boolean mandatory) {
        CourseInventoryMapping mapping = new CourseInventoryMapping();
        mapping.setCourseId(courseId);
        mapping.setItemId(itemId);
        mapping.setMandatory(mandatory);
        mapping.setQuantityRequired(1);
        mapping.setStatus("ACTIVE");
        mapping.setCreatedAt(LocalDateTime.now());
        mapping.setUpdatedAt(LocalDateTime.now());
        mappingRepository.save(mapping);
    }

    @Override
    public CourseInventoryMapping createMapping(CourseInventoryMapping mapping) {

        if (mapping.getCourseId() == null) {
            throw new RuntimeException("Course ID is required");
        }

        if (mapping.getItemId() == null) {
            throw new RuntimeException("Item ID is required");
        }

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
        List<CourseInventoryMapping> mappings = mappingRepository.findByCourseId(courseId);
        if (mappings.isEmpty()) return mappings;

        String courseName = courseRepository.findById(courseId)
                .map(Course::getCourseName)
                .orElse("Course #" + courseId);

        // For a single course, group all items
        CourseInventoryMapping summary = new CourseInventoryMapping();
        summary.setId(courseId);
        summary.setCourseId(courseId);
        summary.setName(courseName);
        summary.setMandatoryItems(mappings.stream().filter(m -> Boolean.TRUE.equals(m.getMandatory())).map(CourseInventoryMapping::getItemId).collect(Collectors.toList()));
        summary.setOptionalItems(mappings.stream().filter(m -> !Boolean.TRUE.equals(m.getMandatory())).map(CourseInventoryMapping::getItemId).collect(Collectors.toList()));
        
        return java.util.Collections.singletonList(summary);
    }

    @Override
    public List<CourseInventoryMapping> getAllMappings() {
        List<CourseInventoryMapping> all = mappingRepository.findAll();
        
        // Group by courseId for the UI
        return all.stream()
                .collect(Collectors.groupingBy(CourseInventoryMapping::getCourseId))
                .entrySet().stream()
                .map(entry -> {
                    Long cid = entry.getKey();
                    List<CourseInventoryMapping> group = entry.getValue();
                    
                    CourseInventoryMapping m = new CourseInventoryMapping();
                    m.setId(cid);
                    m.setCourseId(cid);
                    m.setName(courseRepository.findById(cid).map(Course::getCourseName).orElse("Course #" + cid));
                    m.setMandatoryItems(group.stream().filter(i -> Boolean.TRUE.equals(i.getMandatory())).map(CourseInventoryMapping::getItemId).collect(Collectors.toList()));
                    m.setOptionalItems(group.stream().filter(i -> !Boolean.TRUE.equals(i.getMandatory())).map(CourseInventoryMapping::getItemId).collect(Collectors.toList()));
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMapping(Long id) {
        mappingRepository.deleteById(id);
    }
}