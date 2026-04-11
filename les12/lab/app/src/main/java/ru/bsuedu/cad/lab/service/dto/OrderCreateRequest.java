package ru.bsuedu.cad.lab.service.dto;

import java.util.Map;

public record OrderCreateRequest(Integer customerId, String shippingAddress, Map<Integer, Integer> items) {}
