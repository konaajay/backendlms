package com.lms.www.management.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class VideoMetadataUtil {

	public static int getDurationInSeconds(String filePath) {
	    try {
	        ProcessBuilder builder = new ProcessBuilder(
	                "C:\\ffmpeg\\bin\\ffprobe.exe",
	                "-v", "error",
	                "-show_entries", "format=duration",
	                "-of", "default=noprint_wrappers=1:nokey=1",
	                filePath
	        );

	        builder.redirectErrorStream(true);

	        Process process = builder.start();

	        BufferedReader reader =
	                new BufferedReader(
	                        new InputStreamReader(process.getInputStream()));

	        String output = reader.readLine();
	        process.waitFor();

	        if (output == null || output.isBlank()) {
	            throw new RuntimeException("ffprobe returned empty output");
	        }

	        double duration = Double.parseDouble(output.trim());

	        return (int) Math.round(duration);

	    } catch (Exception e) {
	        System.err.println("FFmpeg not found or failed. Proceeding without duration. Error: " + e.getMessage());
	        return 0;
	    }
	}
	
}