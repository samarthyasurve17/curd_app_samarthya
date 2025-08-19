package com.crudapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import com.crudapp.service.OTPService;

@RestController
@RequestMapping("/api/auth")
public class OAuthController {

    @Autowired
    private OTPService otpService;

    @GetMapping("/user")
    public OAuth2User user(@AuthenticationPrincipal OAuth2User principal) {
        // This endpoint will be called after successful OAuth login
        // Generate and send OTP to the user's email
        String email = principal.getAttribute("email");
        String otp = otpService.generateOTP(email);
        
        // In a real app, you would send the OTP to the user's email
        System.out.println("OTP for " + email + ": " + otp);
        
        return principal;
    }
    
    @PostMapping("/verify-otp")
    public String verifyOTP(@RequestParam String email, @RequestParam String otp) {
        if (otpService.validateOTP(email, otp)) {
            // OTP is valid, log the user in
            return "OTP verified successfully";
        } else {
            return "Invalid OTP";
        }
    }
    
    @GetMapping("/login")
    public String login() {
        return "redirect:http://localhost:3000/login";
    }
}
