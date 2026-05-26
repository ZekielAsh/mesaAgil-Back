package com.ttip.mesa_agil.dto.requests;

import com.ttip.mesa_agil.model.enums.OrderItemStatus;

public record UpdateOrderItemRequest(OrderItemStatus status) { }
