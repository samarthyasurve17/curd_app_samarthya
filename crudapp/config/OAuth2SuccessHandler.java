package com.crudapp.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.crudapp.model.User;
import com.crudapp.service.UserService;
import com.crudapp.service.OtpVerificationService;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserService userService;
    
    @Autowired
    private OtpVerificationService otpVerificationService;

    @Value("${app.oauth.allowed-emails:}")
    private String allowedEmails;

    @Value("${app.oauth.auto-provision.enabled:true}")
    private boolean autoProvisionEnabled;

    private Set<String> allowedEmailSet;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");
        String picture = oauth2User.getAttribute("picture");

        if (allowedEmailSet == null) {
            allowedEmailSet = Arrays.stream(allowedEmails.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }
        
        // Find the user in database
        User user = userService.findByEmail(email);
        
        if (user == null) {
            boolean hasAllowlist = allowedEmailSet != null && !allowedEmailSet.isEmpty();
            String emailLower = email != null ? email.toLowerCase() : null;
            boolean isAllowed = hasAllowlist ? allowedEmailSet.contains(emailLower) : autoProvisionEnabled;
            if (!isAllowed) {
                // Not allowed to auto-provision
                getRedirectStrategy().sendRedirect(request, response,
                        "http://localhost:3000/login?error=unauthorized_email");
                return;
            }
            // Auto-provision a new Google user
            user = new User(googleId, email, name != null ? name : email, picture);
            user = userService.saveUser(user);
        }

        if (user != null) {
            try {
                // Generate and send OTP to user's email
                otpVerificationService.generateAndSendOtp(email, name != null ? name : user.getFullName());
                
                // Store user info in session for OTP verification
                HttpSession session = request.getSession();
                session.setAttribute("pendingOAuthUser", user);
                session.setAttribute("pendingOAuthEmail", email);
                session.setAttribute("oauthLoginPending", true);
                
                // Redirect to OTP verification page
                getRedirectStrategy().sendRedirect(request, response, 
                    "http://localhost:3000/verify-otp?email=" + email);
                    
            } catch (Exception e) {
                // If OTP sending fails, redirect with error
                getRedirectStrategy().sendRedirect(request, response, 
                    "http://localhost:3000/login?error=otp_send_failed");
            }
        } else {
            // Fallback (should not happen): redirect with error
            getRedirectStrategy().sendRedirect(request, response, 
                "http://localhost:3000/login?error=user_creation_failed");
        }
    }
}
