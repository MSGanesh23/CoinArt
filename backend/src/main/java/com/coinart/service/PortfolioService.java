package com.coinart.service;

import com.coinart.dto.PortfolioDTO;
import com.coinart.entity.Portfolio;
import com.coinart.entity.User;
import com.coinart.exception.InsufficientQuantityException;
import com.coinart.exception.ResourceNotFoundException;
import com.coinart.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final MarketService marketService;

    public PortfolioService(PortfolioRepository portfolioRepository, MarketService marketService) {
        this.portfolioRepository = portfolioRepository;
        this.marketService = marketService;
    }

    public List<PortfolioDTO> getPortfolio(Long userId) {
        return portfolioRepository.findByUserId(userId).stream()
                .filter(p -> p.getQuantity().compareTo(BigDecimal.ZERO) > 0)
                .map(p -> {
                    BigDecimal currentPrice = marketService.getPriceForSymbol(p.getSymbol());
                    BigDecimal investedValue = p.getAvgBuyPrice().multiply(p.getQuantity()).setScale(2,
                            RoundingMode.HALF_UP);
                    BigDecimal currentValue = currentPrice.multiply(p.getQuantity()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal unrealizedPnL = currentValue.subtract(investedValue);
                    BigDecimal unrealizedPnLPercent = investedValue.compareTo(BigDecimal.ZERO) != 0
                            ? unrealizedPnL.divide(investedValue, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100))
                            : BigDecimal.ZERO;
                    return PortfolioDTO.builder()
                            .symbol(p.getSymbol())
                            .quantity(p.getQuantity())
                            .avgBuyPrice(p.getAvgBuyPrice())
                            .currentPrice(currentPrice)
                            .currentValue(currentValue)
                            .unrealizedPnl(unrealizedPnL)
                            .unrealizedPnlPercent(unrealizedPnLPercent.setScale(2, RoundingMode.HALF_UP))
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateOnBuy(Long userId, String symbol, BigDecimal quantity, BigDecimal price) {
        Portfolio holding = portfolioRepository.findByUserIdAndSymbol(userId, symbol)
                .orElse(Portfolio.builder().symbol(symbol).quantity(BigDecimal.ZERO)
                        .avgBuyPrice(BigDecimal.ZERO).totalInvested(BigDecimal.ZERO).build());

        BigDecimal totalQty = holding.getQuantity().add(quantity);
        BigDecimal newAvg = (holding.getQuantity().multiply(holding.getAvgBuyPrice())
                .add(quantity.multiply(price)))
                .divide(totalQty, 8, RoundingMode.HALF_UP);

        holding.setQuantity(totalQty);
        holding.setAvgBuyPrice(newAvg);
        holding.setTotalInvested(newAvg.multiply(totalQty).setScale(4, RoundingMode.HALF_UP));

        if (holding.getUser() == null) {
            User userRef = new User();
            userRef.setId(userId);
            holding.setUser(userRef);
        }

        portfolioRepository.save(holding);
    }

    @Transactional
    public BigDecimal updateOnSell(Long userId, String symbol, BigDecimal quantity) {
        Portfolio holding = portfolioRepository.findByUserIdAndSymbol(userId, symbol)
                .orElseThrow(() -> new ResourceNotFoundException("No portfolio entry found for symbol: " + symbol));

        if (holding.getQuantity().compareTo(quantity) < 0) {
            throw new InsufficientQuantityException(
                    "Insufficient quantity. Available: " + holding.getQuantity() + ", Requested: " + quantity);
        }

        BigDecimal costBasis = holding.getAvgBuyPrice().multiply(quantity).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newQty = holding.getQuantity().subtract(quantity);

        if (newQty.compareTo(BigDecimal.ZERO) == 0) {
            portfolioRepository.delete(holding);
        } else {
            holding.setQuantity(newQty);
            portfolioRepository.save(holding);
        }

        return costBasis;
    }
}
