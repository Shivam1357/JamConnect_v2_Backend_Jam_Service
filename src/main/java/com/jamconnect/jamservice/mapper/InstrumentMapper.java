package com.jamconnect.jamservice.mapper;

import com.jamconnect.jamservice.dto.request.InstrumentRequest;
import com.jamconnect.jamservice.dto.response.InstrumentResponse;
import com.jamconnect.jamservice.entity.JamInstrument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstrumentMapper {

    InstrumentResponse toResponse(JamInstrument instrument);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jam", ignore = true)
    JamInstrument toEntity(InstrumentRequest request);
}