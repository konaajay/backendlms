package com.lms.www.campus.Transport.util;
import java.io.ByteArrayOutputStream;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeUtil {

	 public static byte[] generateQRCode(String text) {
	        try {
	            QRCodeWriter writer = new QRCodeWriter();
	            BitMatrix matrix =
	                    writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);

	            ByteArrayOutputStream os = new ByteArrayOutputStream();
	            MatrixToImageWriter.writeToStream(matrix, "PNG", os);
	            return os.toByteArray();

	        } catch (Exception e) {
	            throw new RuntimeException("QR code generation failed", e);
	        }
	    
	 }
}
