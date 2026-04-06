package com.lms.www.marketing.repository;

import com.lms.www.marketing.model.LandingPage;

public interface LandingPageRepository
        extends org.springframework.data.jpa.repository.JpaRepository<LandingPage, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"features"})
    @org.springframework.lang.NonNull
    java.util.List<LandingPage> findAll();

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"features"})
    java.util.Optional<LandingPage> findBySlug(String slug);
}
