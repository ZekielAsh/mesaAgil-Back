package com.ttip.mesa_agil.dto.websocket;

public record WebSocketEvent(
        String type,
        Object payload
) {}
