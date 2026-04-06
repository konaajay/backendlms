package com.lms.www.campus.Transport;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "drivers")
public class DriverDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "fullname", nullable = false)
    private String fullName;
        
    @Column(name = "contact_number", nullable = false, unique = true)
    private String contactNumber;
    
    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(name = "license_expiry_date", nullable = false)
    private LocalDate licenseExpiryDate;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    public enum Role {
        DRIVER, CONDUCTOR ,HELPER
    }
    
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_category")
    private ExperienceCategory experienceCategory;
    
    public enum ExperienceCategory {
        SCHOOLBUS, HEAVYVEHICLE, LIGHTVEHICLE ,BOTHSCHOOLBUSANDHEAVYVEHICLE
    }
   
    @Column(name = "background_verified", nullable = false)
    private Boolean backgroundVerified = false;
    
    @Enumerated(EnumType.STRING)
    private ShiftType shift;
    
    public enum ShiftType {
         MORNING, EVENING ,BOTH 
    }
    
    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "license_validity_status")
    private LicenseValidityStatus licenseValidityStatus;
    
    public enum LicenseValidityStatus {
        VALID,
        EXPIRED,
        EXPIRINGSOON
    }
 
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private DriverStatus verificationStatus;
    
    public enum DriverStatus {
        PENDING,
        VERIFIED,
        SUSPENDED,
        REJECTED
    }
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "route_id") 
    private RouteWay route;

    @ManyToOne
    @JoinColumn(name = "conductor_id") 
    private ConductorDetails conductor;

    public DriverDetails() {}

    public DriverDetails(Long driverId, String fullName, String contactNumber, String licenseNumber, LocalDate licenseExpiryDate, Role role, ExperienceCategory experienceCategory, Boolean backgroundVerified, ShiftType shift, Integer experienceYears, LicenseValidityStatus licenseValidityStatus, DriverStatus verificationStatus, Boolean active, Vehicle vehicle, RouteWay route, ConductorDetails conductor) {
        this.driverId = driverId;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.licenseNumber = licenseNumber;
        this.licenseExpiryDate = licenseExpiryDate;
        this.role = role;
        this.experienceCategory = experienceCategory;
        this.backgroundVerified = backgroundVerified;
        this.shift = shift;
        this.experienceYears = experienceYears;
        this.licenseValidityStatus = licenseValidityStatus;
        this.verificationStatus = verificationStatus;
        this.active = active;
        this.vehicle = vehicle;
        this.route = route;
        this.conductor = conductor;
    }

    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public ExperienceCategory getExperienceCategory() { return experienceCategory; }
    public void setExperienceCategory(ExperienceCategory experienceCategory) { this.experienceCategory = experienceCategory; }

    public Boolean getBackgroundVerified() { return backgroundVerified; }
    public void setBackgroundVerified(Boolean backgroundVerified) { this.backgroundVerified = backgroundVerified; }

    public ShiftType getShift() { return shift; }
    public void setShift(ShiftType shift) { this.shift = shift; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public LicenseValidityStatus getLicenseValidityStatus() { return licenseValidityStatus; }
    public void setLicenseValidityStatus(LicenseValidityStatus licenseValidityStatus) { this.licenseValidityStatus = licenseValidityStatus; }

    public DriverStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(DriverStatus verificationStatus) { this.verificationStatus = verificationStatus; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    public RouteWay getRoute() { return route; }
    public void setRoute(RouteWay route) { this.route = route; }

    public ConductorDetails getConductor() { return conductor; }
    public void setConductor(ConductorDetails conductor) { this.conductor = conductor; }
}
