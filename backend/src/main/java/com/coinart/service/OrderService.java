package com.coinart.service;

import com.coinart.dto.OrderRequest;
import com.coinart.dto.OrderResponse;
import com.coinart.entity.Order;
import com.coinart.entity.User;
import com.coinart.enums.OrderStatus;
import com.coinart.enums.OrderType;
import com.coinart.exception.BadRequestException;
import com.coinart.exception.ResourceNotFoundException;
import com.coinart.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final FundsService fundsService;
    private final PortfolioService portfolioService;
    private final TradeHistoryService tradeHistoryService;

    public OrderService(OrderRepository orderRepository,
            FundsService fundsService,
            PortfolioService portfolioService,
            TradeHistoryService tradeHistoryService) {
        this.orderRepository = orderRepository;
        this.fundsService = fundsService;
        this.portfolioService = portfolioService;
        this.tradeHistoryService = tradeHistoryService;
    }

    @Transactional
    public OrderResponse placeOrder(Long userId, OrderRequest request) {
        BigDecimal totalValue = request.getPrice().multiply(request.getQuantity())
                .setScale(2, RoundingMode.HALF_UP);

        User userRef = new User();
        userRef.setId(userId);

        Order order = Order.builder()
                .symbol(request.getSymbol().toUpperCase())
                .type(request.getType())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .status(OrderStatus.EXECUTED)
                .user(userRef)
                .build();

        if (request.getType() == OrderType.BUY) {
            fundsService.deductBalance(userId, totalValue);
            portfolioService.updateOnBuy(userId, order.getSymbol(), request.getQuantity(), request.getPrice());
            Order saved = orderRepository.save(order);
            tradeHistoryService.recordTrade(userId, saved, BigDecimal.ZERO);
            return mapToResponse(saved);
        } else {
            BigDecimal costBasis = portfolioService.updateOnSell(userId, order.getSymbol(), request.getQuantity());
            fundsService.creditBalance(userId, totalValue, costBasis);
            BigDecimal realizedPnL = totalValue.subtract(costBasis);
            Order saved = orderRepository.save(order);
            tradeHistoryService.recordTrade(userId, saved, realizedPnL);
            return mapToResponse(saved);
        }
    }

    public List<OrderResponse> getOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException("Only OPEN orders can be cancelled. Current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return mapToResponse(orderRepository.save(order));
    }

    private OrderResponse mapToResponse(Order order) {
        BigDecimal totalValue = order.getPrice().multiply(order.getQuantity()).setScale(2, RoundingMode.HALF_UP);
        return OrderResponse.builder()
                .id(order.getId())
                .symbol(order.getSymbol())
                .type(order.getType())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .total(totalValue)
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
