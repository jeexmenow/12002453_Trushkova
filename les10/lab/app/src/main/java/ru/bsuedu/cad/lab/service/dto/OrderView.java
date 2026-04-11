package ru.bsuedu.cad.lab.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderView(
        Integer id,
        String customerName,
        LocalDateTime orderDate,
        BigDecimal totalPrice,
        String status,
        String shippingAddress) {}
