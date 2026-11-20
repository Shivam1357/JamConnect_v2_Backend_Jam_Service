package com.jamconnect.jamservice.mapper;

import com.jamconnect.jamservice.dto.response.ParticipantReviewResponse;
import com.jamconnect.jamservice.entity.JamParticipantReview;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserReferenceMapper.class)
public interface JamParticipantReviewMapper {

    ParticipantReviewResponse toResponse(JamParticipantReview review);
}