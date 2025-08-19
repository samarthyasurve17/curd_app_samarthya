package com.crudapp.controller;

import com.crudapp.model.User;
import com.crudapp.service.OtpVerificationService;
import com.crudapp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OtpController {

    @Autowired
    private OtpVerificationService otpVerificationService;
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        HttpSession session = httpRequest.getSession();
        
        String email = request.get("email");
        String otpCode = request.get("otpCode");
        
        // Check if there's a pending OAuth login
        Boolean oauthLoginPending = (Boolean) session.getAttribute("oauthLoginPending");
        String pendingEmail = (String) session.getAttribute("pendingOAuthEmail");
        User pendingUser = (User) session.getAttribute("pendingOAuthUser");
        
        if (oauthLoginPending == null || !oauthLoginPending || 
            pendingEmail == null || !pendingEmail.equals(email) || pendingUser == null) {
            response.put("success", false);
            response.put("message", "No pending OAuth login found. Please try logging in again.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Verify the OTP
        boolean isValidOtp = otpVerificationService.verifyOtp(email, otpCode);
        
        if (isValidOtp) {
            // OTP verified successfully - complete the login
            session.setAttribute("user", pendingUser);
            session.setAttribute("authenticated", true);
            
            // Programmatically authenticate the user in Spring Security
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(pendingUser, null, pendingUser.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            // Ensure the context is stored in the session for subsequent requests
            session.setAttribute("SPRING_SECURITY_CONTEXT", context);
            // Also set application-specific session flags used by /api/auth/check
            session.setAttribute("user", pendingUser);
            session.setAttribute("authenticated", true);
            
            // Clean up pending OAuth session attributes
            session.removeAttribute("oauthLoginPending");
            session.removeAttribute("pendingOAuthEmail");
            session.removeAttribute("pendingOAuthUser");
            
            // Send welcome email
            emailService.sendWelcomeEmail(email, pendingUser.getFullName());
            
            // Always return JSON to avoid CORS issues with XHR redirects
            response.put("success", true);
            response.put("message", "OTP verified successfully! Welcome to CRUD App.");
            response.put("redirectUrl", "/dashboard");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid or expired OTP. Please try again.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/verify-redirect")
    public ResponseEntity<?> verifyOtpRedirect(
            @RequestParam String email,
            @RequestParam("otpCode") String otpCode,
            HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession();

        Boolean oauthLoginPending = (Boolean) session.getAttribute("oauthLoginPending");
        String pendingEmail = (String) session.getAttribute("pendingOAuthEmail");
        User pendingUser = (User) session.getAttribute("pendingOAuthUser");

        if (oauthLoginPending == null || !oauthLoginPending ||
                pendingEmail == null || !pendingEmail.equals(email) || pendingUser == null) {
            return ResponseEntity.status(302)
                    .header("Location", "http://localhost:3000/login?error=no_pending_login")
                    .build();
        }

        boolean isValidOtp = otpVerificationService.verifyOtp(email, otpCode);
        if (!isValidOtp) {
            return ResponseEntity.status(302)
                    .header("Location", "http://localhost:3000/verify-otp?email=" + email + "&error=invalid_otp")
                    .build();
        }

        // Authenticate
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(pendingUser, null, pendingUser.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
        session.setAttribute("user", pendingUser);
        session.setAttribute("authenticated", true);

        // Clean up pending attributes
        session.removeAttribute("oauthLoginPending");
        session.removeAttribute("pendingOAuthEmail");
        session.removeAttribute("pendingOAuthUser");

        // Redirect to dashboard
        return ResponseEntity.status(302)
                .header("Location", "http://localhost:3000/dashboard")
                .build();
    }

    @PostMapping("/resend")
    public ResponseEntity<Map<String, Object>> resendOtp(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        HttpSession session = httpRequest.getSession();
        
        String email = request.get("email");
        
        // Check if there's a pending OAuth login
        Boolean oauthLoginPending = (Boolean) session.getAttribute("oauthLoginPending");
        String pendingEmail = (String) session.getAttribute("pendingOAuthEmail");
        User pendingUser = (User) session.getAttribute("pendingOAuthUser");
        
        if (oauthLoginPending == null || !oauthLoginPending || 
            pendingEmail == null || !pendingEmail.equals(email) || pendingUser == null) {
            response.put("success", false);
            response.put("message", "No pending OAuth login found. Please try logging in again.");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // Generate and send new OTP
            otpVerificationService.generateAndSendOtp(email, pendingUser.getFullName());
            
            response.put("success", true);
            response.put("message", "New OTP has been sent to your email.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send OTP. Please try again later.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getOtpStatus(
            @RequestParam String email,
            HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        HttpSession session = httpRequest.getSession();
        
        // Check if there's a pending OAuth login
        Boolean oauthLoginPending = (Boolean) session.getAttribute("oauthLoginPending");
        String pendingEmail = (String) session.getAttribute("pendingOAuthEmail");
        
        if (oauthLoginPending != null && oauthLoginPending && 
            pendingEmail != null && pendingEmail.equals(email)) {
            
            boolean hasValidOtp = otpVerificationService.hasValidOtp(email);
            
            response.put("success", true);
            response.put("hasPendingLogin", true);
            response.put("hasValidOtp", hasValidOtp);
            response.put("email", email);
            
        } else {
            response.put("success", true);
            response.put("hasPendingLogin", false);
            response.put("hasValidOtp", false);
        }
        
        return ResponseEntity.ok(response);
    }
}
