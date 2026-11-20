package com.jamconnect.jamservice.mapper;

import com.jamconnect.jamservice.dto.request.AddressRequest;
import com.jamconnect.jamservice.dto.response.AddressResponse;
import com.jamconnect.jamservice.entity.JamAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponse toResponse(JamAddress address);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jam", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    JamAddress toEntity(AddressRequest request);


}