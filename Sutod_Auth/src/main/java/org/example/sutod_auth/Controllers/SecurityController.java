package org.example.sutod_auth.Controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.sutod_auth.Entities.User;
import org.example.sutod_auth.Entities.UserDTO.UserRequestSignUp;
import org.example.sutod_auth.Entities.UserDTO.UserRequestSignIn;
import org.example.sutod_auth.Jwt.JwtCore;
import org.example.sutod_auth.Repositories.UserRepository;

import org.example.sutod_auth.Services.Impl.MailService;
import org.example.sutod_auth.Services.Impl.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class SecurityController {

    private UserRepository userRepository;

    UserService userService;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    private JwtCore jwtCore;


    private MailService emailService;


    //регистрация
    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody UserRequestSignUp userRequestSignUp){
        String pat = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9!?]).{8,}";
        //проверка наличия такого имени в бд
        if(userRepository.findByUsername(userRequestSignUp.getUsername()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        //проверка наличия такого имени в email
        if (userRepository.findByEmail(userRequestSignUp.getEmail()).isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (userRequestSignUp.getPassword().length() < 8 || userRequestSignUp.getUsername().length() < 3 || !userRequestSignUp.getPassword().matches(pat)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // от 100000 до 999999
        String codeMail = String.valueOf(code);

        //Создание юзера
        User user = new User();
        user.setEmail(userRequestSignUp.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestSignUp.getPassword()));
        user.setUsername(userRequestSignUp.getUsername());
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user.getId());

    }

    //Авторизация
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody UserRequestSignIn userRequest,  HttpServletResponse response){
        Authentication authentication = null;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword())
            );

            User user = userRepository.findByUsername(userRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            //При введиние верных логинов
//            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
        }catch (BadCredentialsException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        };
        //создание jwt token
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtCore.generateAccessToken(userDetails);
        String refreshToken = jwtCore.generateRefreshToken(userDetails);

        // Добавляем Refresh Token в HttpOnly Cookie
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/auth/refresh");
        refreshCookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok().body(accessToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No cookies found");
        }
        String refreshToken = null;
        for (Cookie c : cookies) {
            if ("refreshToken".equals(c.getName())) {
                refreshToken = c.getValue();
            }
        }
        if (refreshToken == null || !jwtCore.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String username = jwtCore.getNameJwt(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);
        String newAccessToken = jwtCore.generateAccessToken(userDetails);

        return ResponseEntity.ok().body(newAccessToken);
    }
}