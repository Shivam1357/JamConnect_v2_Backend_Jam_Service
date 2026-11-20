package com.jamconnect.jamservice.mapper;

import com.jamconnect.jamservice.dto.response.ParticipantResponse;
import com.jamconnect.jamservice.entity.JamParticipant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserReferenceMapper.class)
public interface ParticipantMapper {

    ParticipantResponse toResponse(JamParticipant participant);
}