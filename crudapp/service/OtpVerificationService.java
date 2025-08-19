package com.crudapp.service;

import com.crudapp.model.OtpVerification;
import com.crudapp.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpVerificationService {

    @Autowired
    private OtpVerificationRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;

    public String generateAndSendOtp(String email, String userName) {
        // Clean up any existing OTPs for this email
        otpRepository.deleteByEmail(email);
        
        // Generate 6-digit OTP
        String otpCode = generateOtpCode();
        
        // Set expiry time
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
        
        // Save OTP to database
        OtpVerification otpVerification = new OtpVerification(email, otpCode, expiresAt);
        otpRepository.save(otpVerification);
        
        // Send OTP via email
        emailService.sendOtpEmail(email, otpCode, userName);
        
        return otpCode; // Return for testing purposes (remove in production)
    }

    public boolean verifyOtp(String email, String otpCode) {
        // Clean up expired OTPs
        cleanupExpiredOtps();
        
        Optional<OtpVerification> otpOpt = otpRepository.findByEmailAndOtpCodeAndUsedFalse(email, otpCode);
        
        if (otpOpt.isPresent()) {
            OtpVerification otp = otpOpt.get();
            
            if (otp.isExpired()) {
                return false;
            }
            
            // Mark OTP as used and verified
            otp.setUsed(true);
            otp.setVerified(true);
            otpRepository.save(otp);
            
            return true;
        }
        
        return false;
    }

    public boolean hasValidOtp(String email) {
        cleanupExpiredOtps();
        Optional<OtpVerification> otpOpt = otpRepository.findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email);
        return otpOpt.isPresent() && !otpOpt.get().isExpired();
    }

    private String generateOtpCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }

    private void cleanupExpiredOtps() {
        otpRepository.deleteExpiredOtps(LocalDateTime.now());
    }

    public void invalidateOtps(String email) {
        otpRepository.deleteByEmail(email);
    }
}
