package com.lms.www.campus.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.Transport.dto.VehicleGpsDTO;
import com.lms.www.campus.Transport.ConductorDetails;
import com.lms.www.campus.Transport.DriverDetails;
import com.lms.www.campus.Transport.FuelLog;
import com.lms.www.campus.Transport.RouteWay;
import com.lms.www.campus.Transport.StudentTransportAssignment;
import com.lms.www.campus.Transport.TransportAttendance;
import com.lms.www.campus.Transport.TransportFeeStructure;
import com.lms.www.campus.Transport.TransportPayments;
import com.lms.www.campus.Transport.TransportSetting;
import com.lms.www.campus.Transport.Vehicle;
import com.lms.www.campus.Transport.VehicleGPS;
import com.lms.www.campus.Transport.VehicleMaintenance;
import com.lms.www.campus.repository.Transport.ConductorDetailsRepository;
import com.lms.www.campus.repository.Transport.DriverDetailsRepository;
import com.lms.www.campus.repository.Transport.FuelLogRepository;
import com.lms.www.campus.repository.Transport.RouteWayRepository;
import com.lms.www.campus.repository.Transport.StudentTransportAssignmentRepository;
import com.lms.www.campus.repository.Transport.TransportAttendanceRepository;
import com.lms.www.campus.repository.Transport.TransportFeeStructureRepository;
import com.lms.www.campus.repository.Transport.TransportPaymentRepository;
import com.lms.www.campus.repository.Transport.TransportSettingRepository;
import com.lms.www.campus.repository.Transport.VehicleGPSRepository;
import com.lms.www.campus.repository.Transport.VehicleMaintenanceRepository;
import com.lms.www.campus.repository.Transport.VehicleRepository;

@Service
@Transactional
public class TransportService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RouteWayRepository routeWayRepository;

    @Autowired
    private DriverDetailsRepository driverDetailsRepository;

    @Autowired
    private ConductorDetailsRepository conductorRepository;

    @Autowired
    private VehicleGPSRepository gpsRepository;

    @Autowired
    private TransportAttendanceRepository attendanceRepository;

    @Autowired
    private StudentTransportAssignmentRepository studentTransportAssignRepo;

    @Autowired
    private FuelLogRepository fuelLogRepository;

    @Autowired
    private VehicleMaintenanceRepository maintenanceRepository;

// @Autowired
// private KafkaTemplate<String, VehicleGpsDTO> kafkaTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private TransportFeeStructureRepository feeRepository;

    @Autowired
    private TransportPaymentRepository paymentRepository;

    @Autowired
    private TransportSettingRepository settingRepository;

    /* ================= VEHICLE ===================== */

    public Vehicle addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle must not be null");
        }

        if (vehicle.getRoute() != null && vehicle.getRoute().getRouteCode() != null) {
            RouteWay route = routeWayRepository
                    .findByRouteCode(vehicle.getRoute().getRouteCode())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            vehicle.setRoute(route);
        } else {
            vehicle.setRoute(null);
        }
        if (vehicle.getOccupiedSeats() == null) {
            vehicle.setOccupiedSeats(0);
        }

        vehicle.setVehicleStatus(
                vehicle.getVehicleStatus() == null
                        ? Vehicle.VehicleStatus.INACTIVE
                        : vehicle.getVehicleStatus());

        if (vehicle.getGpsEnabled() == null) {
            vehicle.setGpsEnabled(false);
        }

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleByNumber(String vehicleNumber) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicle updateVehicle(String vehicleNumber, Vehicle vehicle) {
        Vehicle existing = getVehicleByNumber(vehicleNumber);

        existing.setVehicletype(vehicle.getVehicletype());
        existing.setCapacity(vehicle.getCapacity());
        existing.setOccupiedSeats(vehicle.getOccupiedSeats());
        existing.setVehicleStatus(vehicle.getVehicleStatus());
        existing.setGpsEnabled(vehicle.getGpsEnabled());

        if (vehicle.getRoute() != null) {
            RouteWay route = routeWayRepository
                    .findByRouteCode(vehicle.getRoute().getRouteCode())
                    .orElseThrow(() -> new RuntimeException("Route not found"));

            existing.setRoute(route);
        }

        return vehicleRepository.save(existing);
    }

    public Vehicle patchVehicle(String vehicleNumber, Vehicle vehicle) {
        Vehicle existing = getVehicleByNumber(vehicleNumber);

        if (vehicle.getVehicletype() != null)
            existing.setVehicletype(vehicle.getVehicletype());
        if (vehicle.getCapacity() != null)
            existing.setCapacity(vehicle.getCapacity());
        if (vehicle.getOccupiedSeats() != null)
            existing.setOccupiedSeats(vehicle.getOccupiedSeats());
        if (vehicle.getVehicleStatus() != null)
            existing.setVehicleStatus(vehicle.getVehicleStatus());
        if (vehicle.getGpsEnabled() != null)
            existing.setGpsEnabled(vehicle.getGpsEnabled());

        return vehicleRepository.save(existing);
    }

    public void deleteVehicle(String vehicleNumber) {
        vehicleRepository.delete(getVehicleByNumber(vehicleNumber));
    }

    // ================================ ROUTEWAY ======================================== //

    public RouteWay addRoute(RouteWay routeWay) {
        if (routeWay.getActive() == null) {
            routeWay.setActive(true);
        }
        return routeWayRepository.save(routeWay);
    }

    @Transactional(readOnly = true)
    public List<RouteWay> getAllRoutes() {
        return routeWayRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<RouteWay> getActiveRoutes() {
        return routeWayRepository.findByActiveTrue();
    }

    public RouteWay getRouteByCode(Long routeCode) {
        return routeWayRepository.findByRouteCode(routeCode)
                .orElseThrow(() -> new RuntimeException("Route not found"));
    }

    public RouteWay updateRoute(Long routeCode, RouteWay routeWay) {
        RouteWay existing = getRouteByCode(routeCode);

        existing.setRouteName(routeWay.getRouteName());
        existing.setPickupPoints(routeWay.getPickupPoints());
        existing.setDropPoints(routeWay.getDropPoints());
        existing.setDistanceKm(routeWay.getDistanceKm());
        existing.setEstimatedTimeMinutes(routeWay.getEstimatedTimeMinutes());
        existing.setActive(routeWay.getActive());

        return routeWayRepository.save(existing);
    }

    public RouteWay patchRoute(Long routeCode, RouteWay routeWay) {
        RouteWay existing = getRouteByCode(routeCode);

        if (routeWay.getRouteName() != null)
            existing.setRouteName(routeWay.getRouteName());
        if (routeWay.getActive() != null)
            existing.setActive(routeWay.getActive());

        return routeWayRepository.save(existing);
    }

    public void deleteRoute(Long routeCode) {
        routeWayRepository.delete(getRouteByCode(routeCode));
    }

    /* ============================== DRIVER DETAILS ========================================== */

    public DriverDetails addDriver(DriverDetails driver) {
        driver.setActive(true);
        return driverDetailsRepository.save(driver);
    }

    public List<DriverDetails> getAllDrivers() {
        return driverDetailsRepository.findAll();
    }

    public List<DriverDetails> getActiveDrivers() {
        return driverDetailsRepository.findByActiveTrue();
    }

    public DriverDetails getDriverById(Long driverId) {
        return driverDetailsRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    public DriverDetails updateDriver(Long driverId, DriverDetails driver) {
        DriverDetails existing = getDriverById(driverId);
        existing.setFullName(driver.getFullName());
        existing.setContactNumber(driver.getContactNumber());
        return driverDetailsRepository.save(existing);
    }

    public DriverDetails patchDriver(Long driverId, DriverDetails updates) {
        DriverDetails existing = driverDetailsRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (updates.getFullName() != null)
            existing.setFullName(updates.getFullName());

        if (updates.getContactNumber() != null)
            existing.setContactNumber(updates.getContactNumber());

        if (updates.getLicenseNumber() != null)
            existing.setLicenseNumber(updates.getLicenseNumber());

        if (updates.getLicenseExpiryDate() != null)
            existing.setLicenseExpiryDate(updates.getLicenseExpiryDate());

        if (updates.getRole() != null)
            existing.setRole(updates.getRole());

        if (updates.getExperienceCategory() != null)
            existing.setExperienceCategory(updates.getExperienceCategory());

        if (updates.getExperienceYears() != null)
            existing.setExperienceYears(updates.getExperienceYears());

        if (updates.getShift() != null)
            existing.setShift(updates.getShift());

        if (updates.getBackgroundVerified() != null)
            existing.setBackgroundVerified(updates.getBackgroundVerified());

        if (updates.getLicenseValidityStatus() != null)
            existing.setLicenseValidityStatus(updates.getLicenseValidityStatus());

        if (updates.getVerificationStatus() != null)
            existing.setVerificationStatus(updates.getVerificationStatus());

        if (updates.getActive() != null)
            existing.setActive(updates.getActive());

        if (updates.getVehicle() != null && updates.getVehicle().getId() != null)
            existing.setVehicle(
                    vehicleRepository.findById(updates.getVehicle().getId())
                            .orElseThrow(() -> new RuntimeException("Vehicle not found")));

        if (updates.getRoute() != null && updates.getRoute().getId() != null)
            existing.setRoute(
                    routeWayRepository.findById(updates.getRoute().getId())
                            .orElseThrow(() -> new RuntimeException("Route not found")));

        return driverDetailsRepository.save(existing);
    }

    public void deleteDriver(Long driverId) {
        driverDetailsRepository.deleteById(driverId);
    }

    /* ================================== CONDUCTOR DETAILS ======================================== */

    public ConductorDetails addConductor(ConductorDetails conductor) {
        conductor.setActive(true);
        return conductorRepository.save(conductor);
    }

    public List<ConductorDetails> getAllConductors() {
        return conductorRepository.findAll();
    }

    public List<ConductorDetails> getActiveConductors() {
        return conductorRepository.findByActiveTrue();
    }

    public ConductorDetails getConductorById(Long conductorId) {
        return conductorRepository.findById(conductorId)
                .orElseThrow(() -> new RuntimeException("Conductor not found"));
    }

    public ConductorDetails updateConductor(Long conductorId, ConductorDetails conductor) {
        ConductorDetails existing = getConductorById(conductorId);

        existing.setConductorName(conductor.getConductorName());
        existing.setContactNumber(conductor.getContactNumber());
        existing.setExperienceYears(conductor.getExperienceYears());
        existing.setVerificationStatus(conductor.getVerificationStatus());
        existing.setActive(conductor.getActive());

        if (conductor.getRoute() != null && conductor.getRoute().getId() != null) {
            RouteWay route = routeWayRepository.findById(conductor.getRoute().getId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            existing.setRoute(route);
        }

        if (conductor.getVehicle() != null && conductor.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(conductor.getVehicle().getId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            existing.setVehicle(vehicle);
        }

        return conductorRepository.save(existing);
    }

    public ConductorDetails patchConductor(Long conductorId, ConductorDetails conductor) {
        ConductorDetails existing = getConductorById(conductorId);

        if (conductor.getConductorName() != null)
            existing.setConductorName(conductor.getConductorName());

        if (conductor.getContactNumber() != null)
            existing.setContactNumber(conductor.getContactNumber());

        if (conductor.getExperienceYears() != null)
            existing.setExperienceYears(conductor.getExperienceYears());

        if (conductor.getVerificationStatus() != null)
            existing.setVerificationStatus(conductor.getVerificationStatus());

        if (conductor.getActive() != null)
            existing.setActive(conductor.getActive());

        if (conductor.getRoute() != null && conductor.getRoute().getId() != null) {
            RouteWay route = routeWayRepository.findById(conductor.getRoute().getId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            existing.setRoute(route);
        }

        if (conductor.getVehicle() != null && conductor.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(conductor.getVehicle().getId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            existing.setVehicle(vehicle);
        }

        return conductorRepository.save(existing);
    }

    public void deleteConductor(Long conductorId) {
        conductorRepository.deleteById(conductorId);
    }

    // ================================== VEHICLE GPS =================================== */

    private static final double MIN_LAT = 17.3800;
    private static final double MAX_LAT = 17.4000;
    private static final double MIN_LNG = 78.4800;
    private static final double MAX_LNG = 78.5000;

    public void sendGpsToKafka(Long vehicleId, double latitude, double longitude, double speed) {
        /* Kafka is disabled in application.properties
        Long defaultVehicleId = 1L;

        VehicleGpsDTO dto = new VehicleGpsDTO();
        dto.setLatitude(latitude);
        dto.setLongitude(longitude);
        dto.setSpeed(speed);
        dto.setVehicleId(defaultVehicleId);

        kafkaTemplate.send("vehicle-gps-topic", dto);
        System.out.println(" GPS DTO Sent to Kafka Successfully");
        */
    }

    // @KafkaListener(topics = "vehicle-gps-topic", groupId = "gps-group")
    public void consumeGpsData(VehicleGpsDTO dto) {
        System.out.println("Kafka Consumer Disabled");
        /*
        System.out.println("Kafka Consumer Triggered");
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found"));
        VehicleGPS gps = new VehicleGPS();
        gps.setVehicle(vehicle);
        gps.setLatitude(dto.getLatitude());
        gps.setLongitude(dto.getLongitude());
        gps.setSpeed(dto.getSpeed());
        gps.setStatus(determineStatus(dto.getSpeed()));
        if (isOutsideCampus(dto.getLatitude(), dto.getLongitude())) {
            gps.setStatus(VehicleGPS.VehicleGPSStatus.OFFLINE);
        }
        VehicleGPS saved = gpsRepository.save(gps);
        VehicleGpsDTO responseDto = new VehicleGpsDTO(
                saved.getId(), saved.getLatitude(), saved.getLongitude(), saved.getSpeed(),
                saved.getStatus().name(), saved.getTimestamp(), saved.getVehicle().getId());
        messagingTemplate.convertAndSend("/topic/vehicle/" + saved.getVehicle().getId(), responseDto);
        */
    }

    public VehicleGPS getLatestLocation(Long vehicleId) {
        return gpsRepository.findTopByVehicle_IdOrderByTimestampDesc(vehicleId)
                .orElse(null); // Return null instead of throwing exception if no data
    }

    public List<VehicleGPS> getVehicleHistory(Long vehicleId) {
        return gpsRepository.findByVehicle_IdOrderByTimestampDesc(vehicleId);
    }

    @Scheduled(fixedRate = 60000)
    public void checkOfflineVehicles() {
        // List<VehicleGPS> allGps = gpsRepository.findAll();
    }

    private boolean isOutsideCampus(double lat, double lng) {
        return lat < MIN_LAT || lat > MAX_LAT || lng < MIN_LNG || lng > MAX_LNG;
    }

    private VehicleGPS.VehicleGPSStatus determineStatus(double speed) {
        if (speed == 0) {
            return VehicleGPS.VehicleGPSStatus.STOPPED;
        } else if (speed < 5) {
            return VehicleGPS.VehicleGPSStatus.IDLE;
        } else {
            return VehicleGPS.VehicleGPSStatus.ACTIVE;
        }
    }

    /* ============================== TRANSPORT ATTENDANCE ===================================== */

    public TransportAttendance createOrUpdateAttendance(TransportAttendance request) {
        if (request.getStudentId() == null) throw new RuntimeException("Student ID is required");
        if (request.getAttendanceDate() == null) throw new RuntimeException("Attendance date is required");
        if (request.getMarkedBy() == null) throw new RuntimeException("MarkedBy is required");
        if (request.getRoute() == null || request.getRoute().getId() == null) throw new RuntimeException("Route is required");
        if (request.getVehicle() == null || request.getVehicle().getId() == null) throw new RuntimeException("Vehicle is required");

        RouteWay route = routeWayRepository.findById(request.getRoute().getId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicle().getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        TransportAttendance attendance = attendanceRepository
                .findByStudentIdAndAttendanceDate(request.getStudentId(), request.getAttendanceDate())
                .orElse(new TransportAttendance());

        attendance.setStudentId(request.getStudentId());
        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setMarkedBy(request.getMarkedBy());
        attendance.setRoute(route);
        attendance.setVehicle(vehicle);

        if (request.getPickupStatus() != null) {
            attendance.setPickupStatus(request.getPickupStatus());
        } else if (attendance.getPickupStatus() == null) {
            attendance.setPickupStatus(TransportAttendance.PickupStatus.ABSENT);
        }

        if (request.getDropStatus() != null) {
            attendance.setDropStatus(request.getDropStatus());
        } else if (attendance.getDropStatus() == null) {
            attendance.setDropStatus(TransportAttendance.DropStatus.ABSENT);
        }

        return attendanceRepository.save(attendance);
    }

    public List<TransportAttendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public List<TransportAttendance> getAttendanceByVehicleAndDate(Long vehicleId, java.time.LocalDate date) {
        return attendanceRepository.findByVehicle_IdAndAttendanceDate(vehicleId, date);
    }

    public TransportAttendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found"));
    }

    public TransportAttendance updateAttendance(Long id, TransportAttendance attendance) {
        TransportAttendance existing = getAttendanceById(id);
        existing.setPickupStatus(attendance.getPickupStatus());
        existing.setDropStatus(attendance.getDropStatus());
        existing.setMarkedBy(attendance.getMarkedBy());
        existing.setRoute(attendance.getRoute());
        existing.setVehicle(attendance.getVehicle());
        return attendanceRepository.save(existing);
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.delete(getAttendanceById(id));
    }

    /* ========================== STUDENT TRANSPORT ASSIGNMENT ========================== */

    public StudentTransportAssignment addStudentTransportAssignment(StudentTransportAssignment assignment) {
        if (assignment.getShift() == null) {
            assignment.setShift(StudentTransportAssignment.Shift.Morning);
        }
        if (assignment.getVehicle() != null && assignment.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(assignment.getVehicle().getId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            assignment.setVehicle(vehicle);
        }
        if (assignment.getRoute() != null && assignment.getRoute().getId() != null) {
            RouteWay route = routeWayRepository.findById(assignment.getRoute().getId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            assignment.setRoute(route);
        }
        return studentTransportAssignRepo.save(assignment);
    }

    public List<StudentTransportAssignment> getAllStudentTransportAssignments() {
        return studentTransportAssignRepo.findAll();
    }

    public List<StudentTransportAssignment> getStudentAssignments(Long studentId) {
        return studentTransportAssignRepo.findByStudentId(studentId);
    }

    public List<StudentTransportAssignment> getVehicleAssignments(Long vehicleId) {
        return studentTransportAssignRepo.findByVehicle_Id(vehicleId);
    }

    public StudentTransportAssignment getStudentTransportAssignmentById(Long id) {
        return studentTransportAssignRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student transport assignment not found"));
    }

    public StudentTransportAssignment updateStudentTransportAssignment(Long id, StudentTransportAssignment assignment) {
        StudentTransportAssignment existing = getStudentTransportAssignmentById(id);
        existing.setStudentId(assignment.getStudentId());
        existing.setPickupPoint(assignment.getPickupPoint());
        existing.setDropPoint(assignment.getDropPoint());
        existing.setShift(assignment.getShift());

        if (assignment.getVehicle() != null && assignment.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(assignment.getVehicle().getId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            existing.setVehicle(vehicle);
        }

        if (assignment.getRoute() != null && assignment.getRoute().getId() != null) {
            RouteWay route = routeWayRepository.findById(assignment.getRoute().getId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            existing.setRoute(route);
        }

        return studentTransportAssignRepo.save(existing);
    }

    public StudentTransportAssignment patchStudentTransportAssignment(Long id, StudentTransportAssignment assignment) {
        StudentTransportAssignment existing = getStudentTransportAssignmentById(id);
        if (assignment.getStudentId() != null) existing.setStudentId(assignment.getStudentId());
        if (assignment.getPickupPoint() != null) existing.setPickupPoint(assignment.getPickupPoint());
        if (assignment.getDropPoint() != null) existing.setDropPoint(assignment.getDropPoint());
        if (assignment.getShift() != null) existing.setShift(assignment.getShift());

        if (assignment.getVehicle() != null && assignment.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(assignment.getVehicle().getId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            existing.setVehicle(vehicle);
        }

        if (assignment.getRoute() != null && assignment.getRoute().getId() != null) {
            RouteWay route = routeWayRepository.findById(assignment.getRoute().getId())
                    .orElseThrow(() -> new RuntimeException("Route not found"));
            existing.setRoute(route);
        }

        return studentTransportAssignRepo.save(existing);
    }

    public void deleteStudentTransportAssignment(Long id) {
        studentTransportAssignRepo.delete(getStudentTransportAssignmentById(id));
    }

    /* ========================== FUEL LOG ========================== */

    public FuelLog addFuelLog(FuelLog fuelLog) {
        if (fuelLog.getDate() == null) fuelLog.setDate(LocalDate.now());
        if (fuelLog.getQuantity() <= 0) throw new RuntimeException("Fuel quantity must be greater than zero");
        if (fuelLog.getCost() <= 0) throw new RuntimeException("Fuel cost must be greater than zero");
        return fuelLogRepository.save(fuelLog);
    }

    public List<FuelLog> getAllFuelLogs() {
        return fuelLogRepository.findAll();
    }

    public List<FuelLog> getFuelLogsByVehicle(String vehicleId) {
        return fuelLogRepository.findByVehicleId(vehicleId);
    }

    public List<FuelLog> getFuelLogsByDate(LocalDate date) {
        return fuelLogRepository.findByDate(date);
    }

    public FuelLog getFuelLogById(Long id) {
        return fuelLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fuel log not found"));
    }

    public void deleteFuelLog(Long id) {
        fuelLogRepository.delete(getFuelLogById(id));
    }

    /* ========================== VEHICLE MAINTENANCE ========================== */

    public VehicleMaintenance addMaintenance(VehicleMaintenance maintenance) {
        if (maintenance.getDate() == null) maintenance.setDate(LocalDate.now());
        if (maintenance.getStatus() == null) maintenance.setStatus(VehicleMaintenance.MaintenanceStatus.Pending);
        return maintenanceRepository.save(maintenance);
    }

    public List<VehicleMaintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    public VehicleMaintenance getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found"));
    }

    public List<VehicleMaintenance> getMaintenanceByVehicle(String vehicleId) {
        return maintenanceRepository.findByVehicleId(vehicleId);
    }

    public VehicleMaintenance updateMaintenance(Long id, VehicleMaintenance maintenance) {
        VehicleMaintenance existing = getMaintenanceById(id);
        existing.setVehicleId(maintenance.getVehicleId());
        existing.setType(maintenance.getType());
        existing.setDate(maintenance.getDate());
        existing.setCost(maintenance.getCost());
        existing.setDescription(maintenance.getDescription());
        existing.setStatus(maintenance.getStatus());
        existing.setNextDue(maintenance.getNextDue());
        return maintenanceRepository.save(existing);
    }

    public VehicleMaintenance patchMaintenance(Long id, VehicleMaintenance maintenance) {
        VehicleMaintenance existing = getMaintenanceById(id);
        if (maintenance.getType() != null) existing.setType(maintenance.getType());
        if (maintenance.getDate() != null) existing.setDate(maintenance.getDate());
        if (maintenance.getCost() != null) existing.setCost(maintenance.getCost());
        if (maintenance.getDescription() != null) existing.setDescription(maintenance.getDescription());
        if (maintenance.getStatus() != null) existing.setStatus(maintenance.getStatus());
        if (maintenance.getNextDue() != null) existing.setNextDue(maintenance.getNextDue());
        return maintenanceRepository.save(existing);
    }

    public void deleteMaintenance(Long id) {
        maintenanceRepository.delete(getMaintenanceById(id));
    }

    // =========================FEE STRUCTURE ====================//

    public TransportFeeStructure save(TransportFeeStructure structure) {
        return feeRepository.save(structure);
    }

    public List<TransportFeeStructure> getAll() {
        return feeRepository.findAll();
    }

    public TransportFeeStructure getMyRouteFee() {
        Long routeId = getLoggedInStudentRouteId();
        return feeRepository.findByRouteId(routeId)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));
    }

    public TransportFeeStructure update(Long id, TransportFeeStructure updated) {
        TransportFeeStructure existing = feeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee structure not found"));
        existing.setAnnualFee(updated.getAnnualFee());
        existing.setAcademicYear(updated.getAcademicYear());
        existing.setRouteId(updated.getRouteId());
        return feeRepository.save(existing);
    }

    public void delete(Long id) {
        feeRepository.deleteById(id);
    }

    // =============Payments=======================//

    public TransportPayments save(TransportPayments payment) {
        if (payment.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            Optional<TransportSetting> setting = settingRepository.findByKeyName("refundAllowed");
            if (setting.isPresent() && "false".equalsIgnoreCase(setting.get().getValue())) {
                throw new RuntimeException("Refunds are not allowed");
            }
        }
        payment.setId("txn_" + System.currentTimeMillis());
        return paymentRepository.save(payment);
    }

    public List<TransportPayments> getAllPayments() {
        return paymentRepository.findAll();
    }

    public List<TransportPayments> getMyPayments() {
        Long studentId = getLoggedInStudentId();
        return paymentRepository.findByStudentId(studentId);
    }

    public List<TransportPayments> getByStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    public void delete(String paymentId) {
        paymentRepository.deleteById(paymentId);
    }
    // ============================================ Payment Settings =========================================//

    public TransportSetting save(TransportSetting request) {
        Optional<TransportSetting> existing = settingRepository.findByKeyName(request.getKeyName());
        if (existing.isPresent()) {
            TransportSetting setting = existing.get();
            setting.setValue(request.getValue());
            return settingRepository.save(setting);
        }
        return settingRepository.save(request);
    }

    public List<TransportSetting> getAllSettings() {
        return settingRepository.findAll();
    }

    private Long getLoggedInStudentId() { return 1L; }
    private Long getLoggedInStudentRouteId() { return 1L; }
}
