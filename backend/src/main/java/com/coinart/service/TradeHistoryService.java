package com.coinart.service;

import com.coinart.dto.TradeHistoryDTO;
import com.coinart.entity.Order;
import com.coinart.entity.TradeHistory;
import com.coinart.entity.User;
import com.coinart.repository.TradeHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TradeHistoryService {

    private final TradeHistoryRepository tradeHistoryRepository;

    public TradeHistoryService(TradeHistoryRepository tradeHistoryRepository) {
        this.tradeHistoryRepository = tradeHistoryRepository;
    }

    @Transactional
    public void recordTrade(Long userId, Order order, BigDecimal realizedPnL) {
        BigDecimal totalValue = order.getPrice().multiply(order.getQuantity()).setScale(2, RoundingMode.HALF_UP);

        User userRef = new User();
        userRef.setId(userId);

        TradeHistory trade = TradeHistory.builder()
                .symbol(order.getSymbol())
                .type(order.getType())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .total(totalValue)
                .realizedPnl(realizedPnL != null ? realizedPnL : BigDecimal.ZERO)
                .user(userRef)
                .build();

        tradeHistoryRepository.save(trade);
    }

    public List<TradeHistoryDTO> getHistory(Long userId) {
        return tradeHistoryRepository.findByUserIdOrderByExecutedAtDesc(userId).stream()
                .map(t -> TradeHistoryDTO.builder()
                        .id(t.getId())
                        .symbol(t.getSymbol())
                        .type(t.getType())
                        .price(t.getPrice())
                        .quantity(t.getQuantity())
                        .total(t.getTotal())
                        .realizedPnl(t.getRealizedPnl())
                        .executedAt(t.getExecutedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
