package com.coinart.controller;

import com.coinart.dto.InstrumentDTO;
import com.coinart.service.MarketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    @GetMapping
    public ResponseEntity<List<InstrumentDTO>> getAllInstruments() {
        return ResponseEntity.ok(marketService.getAllInstruments());
    }
}
