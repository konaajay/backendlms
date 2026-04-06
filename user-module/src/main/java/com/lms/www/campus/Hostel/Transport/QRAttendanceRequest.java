package com.lms.www.campus.Hostel.Transport;
import lombok.Data;
@Data
public class QRAttendanceRequest {

    private Long studentId;
    private Long vehicleId;
    private String session;
}