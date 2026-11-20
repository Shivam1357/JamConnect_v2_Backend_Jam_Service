package com.jamconnect.jamservice.mapper;

import com.jamconnect.jamservice.dto.response.JamReviewResponse;
import com.jamconnect.jamservice.entity.JamReview;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserReferenceMapper.class)
public interface JamReviewMapper {

    JamReviewResponse toResponse(JamReview review);
}