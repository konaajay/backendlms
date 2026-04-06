package com.lms.www.campus.Library;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "fine_slabs")
public class FineSlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "library_settings_id", nullable = false)
    private LibrarySettings librarySettings;

    @Column(name = "member_role")
    private String memberRole;

    @Column(name = "from_day", nullable = false)
    private Integer fromDay;

    @Column(name = "to_day", nullable = false)
    private Integer toDay;

    @Column(name = "fine_per_day", nullable = false)
    private Double finePerDay;

    @Column(name = "slab_order")
    private Integer slabOrder;

    // Manual Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LibrarySettings getLibrarySettings() { return librarySettings; }
    public void setLibrarySettings(LibrarySettings librarySettings) { this.librarySettings = librarySettings; }

    public String getMemberRole() { return memberRole; }
    public void setMemberRole(String memberRole) { this.memberRole = memberRole; }

    public Integer getFromDay() { return fromDay; }
    public void setFromDay(Integer fromDay) { this.fromDay = fromDay; }

    public Integer getToDay() { return toDay; }
    public void setToDay(Integer toDay) { this.toDay = toDay; }

    public Double getFinePerDay() { return finePerDay; }
    public void setFinePerDay(Double finePerDay) { this.finePerDay = finePerDay; }

    public Integer getSlabOrder() { return slabOrder; }
    public void setSlabOrder(Integer slabOrder) { this.slabOrder = slabOrder; }
}
