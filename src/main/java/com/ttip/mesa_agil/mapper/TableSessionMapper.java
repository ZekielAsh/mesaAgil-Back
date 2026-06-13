package com.ttip.mesa_agil.mapper;

import com.ttip.mesa_agil.dto.responses.TableSessionDetailsResponse;
import com.ttip.mesa_agil.model.TableSession;

public class TableSessionMapper {

    public static TableSessionDetailsResponse toResponse(TableSession session) {

        return new TableSessionDetailsResponse(
                session.getId(),
                session.getTable().getId(),
                session.getTable().getNumber(),
                session.getCustomerCount(),
                session.getActive(),
                session.getStartedAt(),
                session.getEndedAt()
        );
    }
}
