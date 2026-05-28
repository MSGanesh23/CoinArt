package com.coinart.controller;

import com.coinart.dto.FundsDTO;
import com.coinart.dto.FundsTransactionRequest;
import com.coinart.entity.User;
import com.coinart.exception.ResourceNotFoundException;
import com.coinart.repository.UserRepository;
import com.coinart.service.FundsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funds")
public class FundsController {

    private final FundsService fundsService;
    private final UserRepository userRepository;

    public FundsController(FundsService fundsService, UserRepository userRepository) {
        this.fundsService = fundsService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<FundsDTO> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(fundsService.getBalance(getUserId(userDetails)));
    }

    @PostMapping("/deposit")
    public ResponseEntity<FundsDTO> deposit(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FundsTransactionRequest request) {
        return ResponseEntity.ok(fundsService.deposit(getUserId(userDetails), request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<FundsDTO> withdraw(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FundsTransactionRequest request) {
        return ResponseEntity.ok(fundsService.withdraw(getUserId(userDetails), request));
    }

    private Long getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
