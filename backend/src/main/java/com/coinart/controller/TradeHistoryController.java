package com.coinart.controller;

import com.coinart.dto.TradeHistoryDTO;
import com.coinart.entity.User;
import com.coinart.exception.ResourceNotFoundException;
import com.coinart.repository.UserRepository;
import com.coinart.service.TradeHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class TradeHistoryController {

    private final TradeHistoryService tradeHistoryService;
    private final UserRepository userRepository;

    public TradeHistoryController(TradeHistoryService tradeHistoryService, UserRepository userRepository) {
        this.tradeHistoryService = tradeHistoryService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<TradeHistoryDTO>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tradeHistoryService.getHistory(getUserId(userDetails)));
    }

    private Long getUserId(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getId();
    }
}
