package com.lms.www.campus.Transport;
import lombok.Data;
@Data
public class QRAttendanceRequest {

    private Long studentId;
    private Long vehicleId;
    private String session;
}
