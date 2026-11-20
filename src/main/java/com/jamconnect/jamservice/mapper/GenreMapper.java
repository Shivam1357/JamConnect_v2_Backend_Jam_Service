package com.jamconnect.jamservice.mapper;

import com.jamconnect.jamservice.dto.request.GenreRequest;
import com.jamconnect.jamservice.dto.response.GenreResponse;
import com.jamconnect.jamservice.entity.JamGenre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreResponse toResponse(JamGenre genre);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jam", ignore = true)
    JamGenre toEntity(GenreRequest request);
}