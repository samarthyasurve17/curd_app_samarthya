package com.crudapp.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {
    
    // Store OTPs with email as key and OTP as value
    private final Map<String, String> otpMap = new ConcurrentHashMap<>();
    private final Map<String, Long> otpExpiryMap = new ConcurrentHashMap<>();
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000; // 5 minutes
    
    // Generate a 6-digit OTP
    public String generateOTP(String email) {
        // Generate random 6-digit OTP
        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(999999));
        
        // Store OTP with current timestamp
        otpMap.put(email, otp);
        otpExpiryMap.put(email, System.currentTimeMillis() + OTP_VALID_DURATION);
        
        // In a real application, send the OTP to the user's email
        // For now, we'll just print it to the console
        System.out.println("OTP for " + email + ": " + otp);
        
        return otp;
    }
    
    // Validate the OTP
    public boolean validateOTP(String email, String otp) {
        String storedOTP = otpMap.get(email);
        Long expiryTime = otpExpiryMap.get(email);
        
        // Check if OTP exists and is not expired
        if (storedOTP == null || expiryTime == null || System.currentTimeMillis() > expiryTime) {
            // Remove expired OTP
            otpMap.remove(email);
            otpExpiryMap.remove(email);
            return false;
        }
        
        // Check if OTP matches
        if (storedOTP.equals(otp)) {
            // OTP is valid, remove it
            otpMap.remove(email);
            otpExpiryMap.remove(email);
            return true;
        }
        
        return false;
    }
    
    // Clean up expired OTPs (can be called periodically)
    public void cleanupExpiredOTPs() {
        long currentTime = System.currentTimeMillis();
        otpExpiryMap.entrySet().removeIf(entry -> {
            if (entry.getValue() < currentTime) {
                otpMap.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
}
