package com.spring_boot.blog_application.controller;

import com.spring_boot.blog_application.entity.User;

import java.util.Date;
import java.util.Calendar;
import com.spring_boot.blog_application.service.UserDetailsServiceImpl;
import java.util.UUID;

import com.spring_boot.blog_application.service.UserService;
import com.spring_boot.blog_application.utils.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = { "http://127.0.0.1:5500", "http://localhost:5500" }) // Allow CORS for specific origins
@RequestMapping("/public")
public class PublicController {
    private static final Logger log = LoggerFactory.getLogger(PublicController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JWTUtil jwtUtil;




    @GetMapping("/health-check")
    public String healthCheck() {
        return "Ok";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


//     @PostMapping("/signup")
// public ResponseEntity<?> registerUser(@RequestBody User user) {
//     try {
       
    
//         // Generate verification token (you can use UUID for simplicity)
//         String verificationToken = UUID.randomUUID().toString();
//         Calendar calendar = Calendar.getInstance();
//         calendar.add(Calendar.HOUR, 24);

//         // Save the token with the user (you'll need to update the User entity to include this field)
//         user.setVerificationToken(verificationToken);        
//         user.setTokenExpirationDate(calendar.getTime());
//         user.setEnabled(false);  // User should not be enabled until they verify their email
//         userService.saveNewUser(user);
       

//         // Send verification email
//         String verificationUrl = "http://localhost:8080/public/verify?token=" + verificationToken;
//         emailService.sendVerificationEmail(user.getEmail(), "Email Verification", 
//             "Please click the following link to verify your email: " + verificationUrl);

//         // Return success response
//         return new ResponseEntity<>("Registration Successful. Please check your email for verification.", HttpStatus.CREATED);

//     } catch (Exception e) {
//         // Log the exception (optional)
//         e.printStackTrace();

//         // Return an error response
//         return new ResponseEntity<>("Registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
//     }
// }
@PostMapping("/signup")
public ResponseEntity<?> registerUser(@RequestBody User user) {
    try {
        // Directly save the user without email verification
        user.setEnabled(true);  // Enable user directly
        userService.saveNewUser(user);

        // Return success response
        return new ResponseEntity<>("Registration Successful.", HttpStatus.CREATED);

    } catch (Exception e) {
        // Log the exception (optional)
        e.printStackTrace();

        // Return an error response
        return new ResponseEntity<>("Registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

 


//login-smtp
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
    Map<String, Object> response = new HashMap<>();
    try {
        // Load the user entity from the database
        User loggedInUser = userService.findByUserName(user.getUserName());

        // Check if the user exists
        if (loggedInUser == null) {
            response.put("error", "User does not exist");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
       
        if (!passwordEncoder.matches(user.getPassword(), loggedInUser.getPassword())) {
            response.put("error", "Incorrect username or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        // Authenticate the user using AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

        // Load the user details from the database
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());

        // Generate the JWT token
        String jwtToken = jwtUtil.generateToken(userDetails.getUsername());

        // Prepare the response data
        response.put("user", loggedInUser);
        response.put("token", jwtToken);

        // Return the response with user data and JWT token
        return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (BadCredentialsException e) {
        log.error("Incorrect username or password: {}", e.getMessage());
        response.put("error", "Incorrect username or password");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
        log.error("Exception occurred while creating authentication token: {}", e.getMessage(), e);
        response.put("error", "Authentication failed");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}







}