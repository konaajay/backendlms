package com.lms.www.campus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.lms.www.campus.service.impl.TransportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transport")
@RequiredArgsConstructor
public class TransportController {

    @Autowired
    private TransportService transportService;

    /*
     * =====================================================
     * VEHICLE
     * =====================================================
     */

    // @PreAuthorize("hasAuthority('VEHICLE_ADD')")
    @PostMapping("/vehicles")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(transportService.addVehicle(vehicle));
    }

    // @PreAuthorize("hasAuthority('VEHICLE_VIEW')")
    @GetMapping("/vehicles")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(transportService.getAllVehicles());
    }

    // @PreAuthorize("hasAuthority('VEHICLE_VIEW')")
    @GetMapping("/vehicles/{vehicleNumber}")
    public ResponseEntity<Vehicle> getVehicleByNumber(@PathVariable String vehicleNumber) {
        return ResponseEntity.ok(transportService.getVehicleByNumber(vehicleNumber));
    }

    // @PreAuthorize("hasAuthority('VEHICLE_UPDATE')")
    @PutMapping("/vehicles/{vehicleNumber}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable String vehicleNumber,
            @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(transportService.updateVehicle(vehicleNumber, vehicle));
    }

    // @PreAuthorize("hasAuthority('VEHICLE_UPDATE')")
    @PatchMapping("/vehicles/{vehicleNumber}")
    public ResponseEntity<Vehicle> patchVehicle(
            @PathVariable String vehicleNumber,
            @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(transportService.patchVehicle(vehicleNumber, vehicle));
    }

    // @PreAuthorize("hasAuthority('VEHICLE_DELETE')")
    @DeleteMapping("/vehicles/{vehicleNumber}")
    public ResponseEntity<String> deleteVehicle(@PathVariable String vehicleNumber) {
        transportService.deleteVehicle(vehicleNumber);
        return ResponseEntity.ok("Vehicle deleted successfully");
    }

    /*
     * =====================================================
     * ROUTEWAY
     * =====================================================
     */

    @PreAuthorize("hasAuthority('ROUTE_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/routes")
    public ResponseEntity<RouteWay> addRoute(@RequestBody RouteWay routeWay) {
        return ResponseEntity.ok(transportService.addRoute(routeWay));
    }

    @PreAuthorize("hasAuthority('ROUTE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/routes")
    public ResponseEntity<List<RouteWay>> getAllRoutes() {
        return ResponseEntity.ok(transportService.getAllRoutes());
    }

    @PreAuthorize("hasAuthority('ROUTE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/routes/active")
    public ResponseEntity<List<RouteWay>> getActiveRoutes() {
        return ResponseEntity.ok(transportService.getActiveRoutes());
    }

    @PreAuthorize("hasAuthority('ROUTE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/routes/{routeCode}")
    public ResponseEntity<RouteWay> getRouteByCode(@PathVariable Long routeCode) {
        return ResponseEntity.ok(transportService.getRouteByCode(routeCode));
    }

    @PreAuthorize("hasAuthority('ROUTE_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PutMapping("/routes/{routeCode}")
    public ResponseEntity<RouteWay> updateRoute(
            @PathVariable Long routeCode,
            @RequestBody RouteWay routeWay) {
        return ResponseEntity.ok(transportService.updateRoute(routeCode, routeWay));
    }

    @PreAuthorize("hasAuthority('ROUTE_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PatchMapping("/routes/{routeCode}")
    public ResponseEntity<RouteWay> patchRoute(
            @PathVariable Long routeCode,
            @RequestBody RouteWay routeWay) {
        return ResponseEntity.ok(transportService.patchRoute(routeCode, routeWay));
    }

    @PreAuthorize("hasAuthority('ROUTE_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    @DeleteMapping("/routes/{routeCode}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long routeCode) {
        transportService.deleteRoute(routeCode);
        return ResponseEntity.ok("Route deleted successfully");
    }

    /*
     * =====================================================
     * DRIVER DETAILS
     * =====================================================
     */

    @PreAuthorize("hasAuthority('DRIVER_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/drivers")
    public ResponseEntity<DriverDetails> addDriver(@RequestBody DriverDetails driver) {
        return ResponseEntity.ok(transportService.addDriver(driver));
    }

    @PreAuthorize("hasAuthority('DRIVER_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/drivers")
    public ResponseEntity<List<DriverDetails>> getAllDrivers() {
        return ResponseEntity.ok(transportService.getAllDrivers());
    }

    @PreAuthorize("hasAuthority('DRIVER_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/drivers/{driverId}")
    public ResponseEntity<DriverDetails> getDriverById(@PathVariable Long driverId) {
        return ResponseEntity.ok(transportService.getDriverById(driverId));
    }

    @PreAuthorize("hasAuthority('DRIVER_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PutMapping("/drivers/{driverId}")
    public ResponseEntity<DriverDetails> updateDriver(
            @PathVariable Long driverId,
            @RequestBody DriverDetails driver) {
        return ResponseEntity.ok(transportService.updateDriver(driverId, driver));
    }

    @PreAuthorize("hasAuthority('DRIVER_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PatchMapping("/drivers/{driverId}")
    public ResponseEntity<DriverDetails> patchDriver(
            @PathVariable Long driverId,
            @RequestBody DriverDetails driver) {

        return ResponseEntity.ok(
                transportService.patchDriver(driverId, driver));
    }

    @PreAuthorize("hasAuthority('DRIVER_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> deleteDriver(@PathVariable Long driverId) {
        transportService.deleteDriver(driverId);
        return ResponseEntity.ok("Driver deleted successfully");
    }
    /*
     * ===================================================== *
     * CONDUCTOR DETAILS
     * =====================================================
     */

    @PreAuthorize("hasAuthority('CONDUCTOR_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/conductors")
    public ResponseEntity<ConductorDetails> addConductor(
            @RequestBody ConductorDetails conductor) {
        return ResponseEntity.ok(
                transportService.addConductor(conductor));
    }

    @PreAuthorize("hasAuthority('CONDUCTOR_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/conductors")
    public ResponseEntity<List<ConductorDetails>> getAllConductors() {
        return ResponseEntity.ok(
                transportService.getAllConductors());
    }

    @PreAuthorize("hasAuthority('CONDUCTOR_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/conductors/active")
    public ResponseEntity<List<ConductorDetails>> getActiveConductors() {

        return ResponseEntity.ok(
                transportService.getActiveConductors());
    }

    @PreAuthorize("hasAuthority('CONDUCTOR_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/conductors/{conductorId}")
    public ResponseEntity<ConductorDetails> getConductorById(
            @PathVariable Long conductorId) {
        return ResponseEntity.ok(
                transportService.getConductorById(conductorId));
    }

    @PreAuthorize("hasAuthority('CONDUCTOR_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PutMapping("/conductors/{conductorId}")
    public ResponseEntity<ConductorDetails> updateConductor(
            @PathVariable Long conductorId,
            @RequestBody ConductorDetails conductor) {
        return ResponseEntity.ok(
                transportService.updateConductor(conductorId, conductor));
    }

    @PreAuthorize("hasAuthority('CONDUCTOR_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PatchMapping("/conductors/{conductorId}")
    public ResponseEntity<ConductorDetails> patchConductor(
            @PathVariable Long conductorId,
            @RequestBody ConductorDetails conductor) {
        return ResponseEntity.ok(
                transportService.patchConductor(conductorId, conductor));
    }

    @PreAuthorize("hasAuthority('CONDUCTOR_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    @DeleteMapping("/conductors/{conductorId}")
    public ResponseEntity<String> deleteConductor(
            @PathVariable Long conductorId) {
        transportService.deleteConductor(conductorId);
        return ResponseEntity.ok("Conductor deleted successfully");
    }
    /*
     * =====================================================
     * VEHICLE GPS
     * =====================================================
     */

    @PreAuthorize("hasAuthority('GPS_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/gps")
    public ResponseEntity<String> sendGpsData(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double speed) {

        Long defaultVehicleId = 1L;

        transportService.sendGpsToKafka(
                defaultVehicleId,
                latitude,
                longitude,
                speed);

        return ResponseEntity.ok("GPS Data Sent to Kafka Successfully");
    }

    @PreAuthorize("hasAuthority('GPS_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/gps/latest/{vehicleId}")
    public ResponseEntity<VehicleGPS> getLatestLocation(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(
                transportService.getLatestLocation(vehicleId));
    }

    @PreAuthorize("hasAuthority('GPS_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/gps/history/{vehicleId}")
    public ResponseEntity<List<VehicleGPS>> getVehicleHistory(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(
                transportService.getVehicleHistory(vehicleId));
    }

    /*
     * =====================================================
     * TRANSPORT ATTENDANCE
     * =====================================================
     */

    /*
     * //Generate QR Code
     * 
     * @PreAuthorize("hasAuthority('TRANSPORT_ATTENDANCE_ADD')")
     * 
     * @GetMapping(value = "/qr/{vehicleId}", produces = "image/png") public byte[]
     * generateQR(
     * 
     * @PathVariable Long vehicleId,
     * 
     * @RequestParam String type) {
     * 
     * return transportService.generateVehicleQR(vehicleId, type); } // Scan QR
     * (Student Side)
     * 
     * @PreAuthorize("hasRole('STUDENT')")
     * 
     * @PostMapping("/scan") public ResponseEntity<TransportAttendance> scanQR(
     * 
     * @PathVariable Long studentId,
     * 
     * @RequestBody String qrText) {
     * 
     * return ResponseEntity.ok( transportService.markAttendance(studentId, qrText)
     * ); }
     */
    // =====================Manual
    // Attendance===========================================//

    @PreAuthorize("hasAuthority('TRANSPORT_ATTENDANCE_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/attendance")
    public ResponseEntity<TransportAttendance> markAttendance(
            @RequestBody TransportAttendance attendance) {

        return ResponseEntity.ok(
                transportService.createOrUpdateAttendance(attendance));
    }

    // Get
    @PreAuthorize("hasAuthority('TRANSPORT_ATTENDANCE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/attendances")
    public ResponseEntity<List<TransportAttendance>> getAllAttendance() {

        return ResponseEntity.ok(
                transportService.getAllAttendance());
    }

    // Get attendance by vehicle and date
    @PreAuthorize("hasAuthority('TRANSPORT_ATTENDANCE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/attendance/vehicle/{vehicleId}")
    public ResponseEntity<List<TransportAttendance>> getAttendanceByVehicleAndDate(
            @PathVariable Long vehicleId,
            @RequestParam(required = false) String date) {

        java.time.LocalDate attendanceDate = (date != null)
                ? java.time.LocalDate.parse(date)
                : java.time.LocalDate.now();

        return ResponseEntity.ok(
                transportService.getAttendanceByVehicleAndDate(vehicleId, attendanceDate));
    }

    // Update
    @PreAuthorize("hasAuthority('TRANSPORT_ATTENDANCE_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PutMapping("/attendance/{id}")
    public ResponseEntity<TransportAttendance> updateAttendance(
            @PathVariable Long id,
            @RequestBody TransportAttendance attendance) {

        return ResponseEntity.ok(
                transportService.updateAttendance(id, attendance));
    }

    // Delete
    @PreAuthorize("hasAuthority('TRANSPORT_ATTENDANCE_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    @DeleteMapping("/attendance/{id}")
    public ResponseEntity<String> deleteAttendance(
            @PathVariable Long id) {

        transportService.deleteAttendance(id);
        return ResponseEntity.ok("Transport attendance deleted successfully");
    }

    // =======================================StudentVehicleAssign========================//

    // CREATE
    @PreAuthorize("hasAnyAuthority('ASSIGNMENT_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/assignments")
    public ResponseEntity<StudentTransportAssignment> addAssignment(
            @RequestBody StudentTransportAssignment assignment) {

        return ResponseEntity.ok(
                transportService.addStudentTransportAssignment(assignment));
    }

    // READ – ALL (Admin, Instructor only)
    @PreAuthorize("hasAnyAuthority('ASSIGNMENT_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/assignments")
    public ResponseEntity<List<StudentTransportAssignment>> getAllAssignments() {

        return ResponseEntity.ok(
                transportService.getAllStudentTransportAssignments());
    }

    // READ – BY STUDENT (Admin, Instructor, Parent, Student)
    @PreAuthorize("hasAnyAuthority('ASSIGNMENT_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/assignments/student/{studentId}")
    public ResponseEntity<List<StudentTransportAssignment>> getAssignmentsByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                transportService.getStudentAssignments(studentId));
    }

    // UPDATE
    @PreAuthorize("hasAnyAuthority('ASSIGNMENT_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PutMapping("/assignments/{id}")
    public ResponseEntity<StudentTransportAssignment> updateAssignment(
            @PathVariable Long id,
            @RequestBody StudentTransportAssignment assignment) {

        return ResponseEntity.ok(
                transportService.updateStudentTransportAssignment(id, assignment));
    }

    // PATCH
    @PreAuthorize("hasAnyAuthority('ASSIGNMENT_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PatchMapping("/assignments/{id}")
    public ResponseEntity<StudentTransportAssignment> patchAssignment(
            @PathVariable Long id,
            @RequestBody StudentTransportAssignment assignment) {

        return ResponseEntity.ok(
                transportService.patchStudentTransportAssignment(id, assignment));
    }

    // DELETE (ADMIN ONLY)
    @PreAuthorize("hasAuthority('ASSIGNMENT_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    @DeleteMapping("/assignments/{id}")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long id) {

        transportService.deleteStudentTransportAssignment(id);
        return ResponseEntity.ok("Assignment deleted successfully");
    }

    // READ – BY VEHICLE
    @PreAuthorize("hasAnyAuthority('ASSIGNMENT_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/assignments/vehicle/{vehicleId}")
    public ResponseEntity<List<StudentTransportAssignment>> getAssignmentsByVehicle(
            @PathVariable Long vehicleId) {

        return ResponseEntity.ok(
                transportService.getVehicleAssignments(vehicleId));
    }

    /* ===================== FUEL LOGS ===================== */

    // CREATE
    @PreAuthorize("hasAuthority('FUEL_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/fuel")
    public ResponseEntity<FuelLog> addFuel(@RequestBody FuelLog fuelLog) {

        return ResponseEntity.ok(
                transportService.addFuelLog(fuelLog));
    }

    // READ – ALL
    @PreAuthorize("hasAuthority('FUEL_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/fuel")
    public ResponseEntity<List<FuelLog>> getAllFuelLogs() {

        return ResponseEntity.ok(
                transportService.getAllFuelLogs());
    }

    // READ – BY VEHICLE
    @PreAuthorize("hasAuthority('FUEL_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/fuel/vehicle/{vehicleId}")
    public ResponseEntity<List<FuelLog>> getFuelByVehicle(
            @PathVariable String vehicleId) {

        return ResponseEntity.ok(
                transportService.getFuelLogsByVehicle(vehicleId));
    }
    // UPDATE
    /*
     * @PreAuthorize("hasAuthority('FUEL_UPDATE')")
     * 
     * @PutMapping("/fuel/{id}") public ResponseEntity<@Nullable Object> updateFuel(
     * 
     * @PathVariable Long id,
     * 
     * @RequestBody FuelLog fuelLog) {
     * 
     * return ResponseEntity.ok( transportService.updateFuelLog(id, fuelLog)); }
     */

    // DELETE (ADMIN ONLY)
    @PreAuthorize("hasAuthority('FUEL_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    @DeleteMapping("/fuel/{id}")
    public ResponseEntity<String> deleteFuel(@PathVariable Long id) {

        transportService.deleteFuelLog(id);
        return ResponseEntity.ok("Fuel log deleted successfully");
    }

    /* ===================== TRANSPORT MAINTENANCE ===================== */

    // CREATE
    @PreAuthorize("hasAuthority('MAINTENANCE_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/maintenance")
    public ResponseEntity<VehicleMaintenance> addMaintenance(
            @RequestBody VehicleMaintenance maintenance) {

        return ResponseEntity.ok(
                transportService.addMaintenance(maintenance));
    }

    // READ – ALL
    @PreAuthorize("hasAuthority('MAINTENANCE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/maintenance")
    public ResponseEntity<List<VehicleMaintenance>> getAllMaintenance() {

        return ResponseEntity.ok(
                transportService.getAllMaintenance());
    }

    // READ – BY VEHICLE
    @PreAuthorize("hasAuthority('MAINTENANCE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/maintenance/vehicle/{vehicleId}")
    public ResponseEntity<List<VehicleMaintenance>> getMaintenanceByVehicle(
            @PathVariable String vehicleId) {

        return ResponseEntity.ok(
                transportService.getMaintenanceByVehicle(vehicleId));
    }

    // UPDATE
    @PreAuthorize("hasAuthority('MAINTENANCE_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PutMapping("/maintenance/{id}")
    public ResponseEntity<VehicleMaintenance> updateMaintenance(
            @PathVariable Long id,
            @RequestBody VehicleMaintenance maintenance) {

        return ResponseEntity.ok(
                transportService.updateMaintenance(id, maintenance));
    }

    // PATCH
    @PreAuthorize("hasAuthority('MAINTENANCE_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PatchMapping("/maintenance/{id}")
    public ResponseEntity<VehicleMaintenance> patchMaintenance(
            @PathVariable Long id,
            @RequestBody VehicleMaintenance maintenance) {

        return ResponseEntity.ok(
                transportService.patchMaintenance(id, maintenance));
    }

    // DELETE
    @PreAuthorize("hasAuthority('MAINTENANCE_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    @DeleteMapping("/maintenance/{id}")
    public ResponseEntity<String> deleteMaintenance(@PathVariable Long id) {

        transportService.deleteMaintenance(id);
        return ResponseEntity.ok("Maintenance record deleted successfully");
    }

    // ================================Transport Fee
    // Structure=================================//

    // Create Fee Structure
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/fee-structure")
    public ResponseEntity<TransportFeeStructure> createFeeStructure(
            @RequestBody TransportFeeStructure structure) {

        return ResponseEntity.ok(transportService.save(structure));
    }

    // View All Fee Structures (Admin)
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/fee-structure")
    public ResponseEntity<List<TransportFeeStructure>> getAllFeeStructures() {

        return ResponseEntity.ok(transportService.getAll());
    }

    // Student View Own Route Fee
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_VIEW_SELF') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/fee-structure/my")
    public ResponseEntity<TransportFeeStructure> getMyRouteFee() {

        return ResponseEntity.ok(transportService.getMyRouteFee());
    }

    // Update Fee Structure
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PutMapping("/fee-structure/{id}")
    public ResponseEntity<TransportFeeStructure> updateFeeStructure(
            @PathVariable Long id,
            @RequestBody TransportFeeStructure structure) {

        return ResponseEntity.ok(transportService.update(id, structure));
    }

    // Delete Fee Structure
    @PreAuthorize("hasAuthority('FEE_STRUCTURE_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    @DeleteMapping("/fee-structure/{id}")
    public ResponseEntity<String> deleteFeeStructure(@PathVariable Long id) {

        transportService.delete(id);
        return ResponseEntity.ok("Fee structure deleted successfully");
    }
    // ==============================Transport Payments
    // Controller=================//

    // Admin Record Payment
    @PreAuthorize("hasAuthority('PAYMENT_ADD') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/payments")
    public ResponseEntity<TransportPayments> addPayment(
            @RequestBody TransportPayments payment) {

        return ResponseEntity.ok(transportService.save(payment));
    }

    // Admin View All Payments
    @PreAuthorize("hasAuthority('PAYMENT_VIEW_ALL') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/payments")
    public ResponseEntity<List<TransportPayments>> getAllPayments() {

        return ResponseEntity.ok(transportService.getAllPayments());
    }

    // Student View Own Payments
    @PreAuthorize("hasAuthority('PAYMENT_VIEW_SELF') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/payments/my")
    public ResponseEntity<List<TransportPayments>> getMyPayments() {

        return ResponseEntity.ok(transportService.getMyPayments());
    }

    // Parent View Child Payments
    @PreAuthorize("hasAuthority('PAYMENT_VIEW_CHILD') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/payments/child/{studentId}")
    public ResponseEntity<List<TransportPayments>> getChildPayments(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(transportService.getByStudent(studentId));
    }

    // Admin Delete Payment
    @PreAuthorize("hasAuthority('PAYMENT_DELETE') or hasAuthority('ALL_PERMISSIONS')")
    @DeleteMapping("/payments/{paymentId}")
    public ResponseEntity<String> deletePayment(@PathVariable Long paymentId) {
        transportService.delete(paymentId);
        return ResponseEntity.ok("Payment deleted successfully");
    }

    // =========================Transport Settings Controller=================//

    @PreAuthorize("hasAuthority('TRANSPORT_SETTING_UPDATE') or hasAuthority('ALL_PERMISSIONS')")
    @PostMapping("/settings")
    public ResponseEntity<TransportSetting> saveSetting(
            @RequestBody TransportSetting setting) {

        return ResponseEntity.ok(transportService.save(setting));
    }

    @PreAuthorize("hasAuthority('TRANSPORT_SETTING_VIEW') or hasAuthority('ALL_PERMISSIONS')")
    @GetMapping("/settings")
    public ResponseEntity<List<TransportSetting>> getAllSettings() {

        return ResponseEntity.ok(transportService.getAllSettings());
    }
}
