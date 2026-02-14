package ar.com.blschool.controller;

import ar.com.blschool.dto.login.LoginRequest;
import ar.com.blschool.dto.login.LoginResponse;
import ar.com.blschool.security.jwt.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        List<String> authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        String token = jwtTokenProvider.createToken(principal.getUsername(), authorities);

        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setUsername(principal.getUsername());
        resp.setAuthorities(authorities);
        resp.setExpiresAt(Instant.now().plusSeconds(60L * jwtTokenProvider.getExpirationMinutes()).toEpochMilli());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || !a.isAuthenticated()) return ResponseEntity.status(401).build();
        List<String> authorities = a.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        LoginResponse resp = new LoginResponse();
        resp.setUsername(a.getName());
        resp.setAuthorities(authorities);
        return ResponseEntity.ok(resp);
    }
}
