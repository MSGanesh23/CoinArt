package com.coinart.service;

import com.coinart.dto.AuthResponse;
import com.coinart.dto.LoginRequest;
import com.coinart.dto.RegisterRequest;
import com.coinart.entity.Funds;
import com.coinart.entity.User;
import com.coinart.enums.Role;
import com.coinart.exception.BadRequestException;
import com.coinart.repository.FundsRepository;
import com.coinart.repository.UserRepository;
import com.coinart.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

        private final UserRepository userRepository;
        private final FundsRepository fundsRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final AuthenticationManager authenticationManager;
        private final UserDetailsService userDetailsService;

        public AuthService(UserRepository userRepository,
                        FundsRepository fundsRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil,
                        AuthenticationManager authenticationManager,
                        UserDetailsService userDetailsService) {
                this.userRepository = userRepository;
                this.fundsRepository = fundsRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtUtil = jwtUtil;
                this.authenticationManager = authenticationManager;
                this.userDetailsService = userDetailsService;
        }

        @Transactional
        public AuthResponse register(RegisterRequest request) {
                if (userRepository.existsByEmail(request.getEmail())) {
                        throw new BadRequestException(
                                        "An account with email '" + request.getEmail() + "' already exists");
                }

                User user = User.builder()
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .role(Role.USER)
                                .build();

                User savedUser = userRepository.save(user);

                Funds funds = Funds.builder()
                                .user(savedUser)
                                .balance(BigDecimal.ZERO)
                                .invested(BigDecimal.ZERO)
                                .pnl(BigDecimal.ZERO)
                                .build();
                fundsRepository.save(funds);

                String token = jwtUtil.generateToken(userDetailsService.loadUserByUsername(savedUser.getEmail()));

                return AuthResponse.builder()
                                .token(token)
                                .username(savedUser.getUsername())
                                .email(savedUser.getEmail())
                                .role(savedUser.getRole().name())
                                .build();
        }

        public AuthResponse login(LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
                String token = jwtUtil.generateToken(userDetails);

                User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

                return AuthResponse.builder()
                                .token(token)
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .build();
        }
}
