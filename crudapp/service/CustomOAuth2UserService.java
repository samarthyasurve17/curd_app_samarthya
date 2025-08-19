package com.crudapp.service;

import com.crudapp.model.User;
import com.crudapp.model.CustomOAuth2User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String googleId = oauth2User.getAttribute("sub");
        String profilePicture = oauth2User.getAttribute("picture");

        // Check if user already exists
        Optional<User> existingUser = userService.findByGoogleId(googleId);
        User user;

        if (existingUser.isPresent()) {
            // Update existing user
            user = existingUser.get();
            user.setFullName(name);
            user.setProfilePicture(profilePicture);
            userService.saveUser(user);
        } else {
            // Check if user exists by email (link accounts)
            User emailUser = userService.findByEmail(email);
            if (emailUser != null) {
                user = emailUser;
                user.setGoogleId(googleId);
                user.setProfilePicture(profilePicture);
                user.setAuthProvider("GOOGLE");
                userService.saveUser(user);
            } else {
                // Create new user for Google OAuth
                user = new User(googleId, email, name, profilePicture);
                userService.saveUser(user);
            }
        }

        return new CustomOAuth2User(oauth2User, user);
    }
}
