package com.coinart.service;

import com.coinart.dto.FundsDTO;
import com.coinart.dto.FundsTransactionRequest;
import com.coinart.entity.Funds;
import com.coinart.exception.InsufficientFundsException;
import com.coinart.exception.ResourceNotFoundException;
import com.coinart.repository.FundsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class FundsService {

    private final FundsRepository fundsRepository;

    public FundsService(FundsRepository fundsRepository) {
        this.fundsRepository = fundsRepository;
    }

    public FundsDTO getBalance(Long userId) {
        Funds funds = getOrThrow(userId);
        return mapToDTO(funds);
    }

    @Transactional
    public FundsDTO deposit(Long userId, FundsTransactionRequest request) {
        Funds funds = getOrThrow(userId);
        funds.setBalance(funds.getBalance().add(request.getAmount()));
        fundsRepository.save(funds);
        return mapToDTO(funds);
    }

    @Transactional
    public FundsDTO withdraw(Long userId, FundsTransactionRequest request) {
        Funds funds = getOrThrow(userId);
        if (funds.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient balance. Available: $" + funds.getBalance() + ", Requested: $" + request.getAmount());
        }
        funds.setBalance(funds.getBalance().subtract(request.getAmount()));
        fundsRepository.save(funds);
        return mapToDTO(funds);
    }

    @Transactional
    public void deductBalance(Long userId, BigDecimal amount) {
        Funds funds = getOrThrow(userId);
        if (funds.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(
                    "Insufficient balance. Available: $" + funds.getBalance() + ", Required: $" + amount);
        }
        funds.setBalance(funds.getBalance().subtract(amount));
        funds.setInvested(funds.getInvested().add(amount));
        fundsRepository.save(funds);
    }

    @Transactional
    public void creditBalance(Long userId, BigDecimal amount, BigDecimal costBasis) {
        Funds funds = getOrThrow(userId);
        funds.setBalance(funds.getBalance().add(amount));
        BigDecimal newInvested = funds.getInvested().subtract(costBasis);
        funds.setInvested(newInvested.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newInvested);
        fundsRepository.save(funds);
    }

    private Funds getOrThrow(Long userId) {
        return fundsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Funds account not found for user"));
    }

    private FundsDTO mapToDTO(Funds funds) {
        BigDecimal totalValue = funds.getBalance().add(funds.getInvested());
        return FundsDTO.builder()
                .balance(funds.getBalance())
                .invested(funds.getInvested())
                .pnl(funds.getPnl())
                .totalValue(totalValue)
                .build();
    }
}
