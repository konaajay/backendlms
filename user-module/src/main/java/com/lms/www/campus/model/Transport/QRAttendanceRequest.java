package com.lms.www.campus.model.Transport;
import lombok.Data;
@Data
public class QRAttendanceRequest {

    private Long studentId;
    private Long vehicleId;
    private String session;
}