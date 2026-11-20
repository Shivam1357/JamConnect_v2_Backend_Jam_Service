package com.jamconnect.jamservice.mapper;

import com.jamconnect.jamservice.dto.response.UserSummaryResponse;
import com.jamconnect.jamservice.entity.UserReference;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserReferenceMapper {

    UserSummaryResponse toResponse(UserReference userReference);
}