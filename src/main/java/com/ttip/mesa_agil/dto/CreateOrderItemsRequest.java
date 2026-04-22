package com.ttip.mesa_agil.dto;

import java.util.List;

public record CreateOrderItemsRequest(
        List<CreateOrderItemRequest> orderItemRequestList
) {
}
