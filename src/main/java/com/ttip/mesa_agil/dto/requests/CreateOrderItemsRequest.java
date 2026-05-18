package com.ttip.mesa_agil.dto.requests;

import java.util.List;

public record CreateOrderItemsRequest(
        List<CreateOrderItemRequest> orderItemRequestList
) {
}
