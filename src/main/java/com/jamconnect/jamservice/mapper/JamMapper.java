package com.jamconnect.jamservice.mapper;

import com.jamconnect.jamservice.dto.request.CreateJamRequest;
import com.jamconnect.jamservice.dto.request.UpdateJamRequest;
import com.jamconnect.jamservice.dto.response.JamResponse;
import com.jamconnect.jamservice.entity.Jam;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        uses = {UserReferenceMapper.class, AddressMapper.class, GenreMapper.class, InstrumentMapper.class})
public interface JamMapper {

    JamResponse toResponse(Jam jam);

    /**
     * Maps only the simple scalar fields from the create request.
     * organizer, address, genres, instruments, status, and
     * currentParticipantCount are set explicitly in JamService,
     * since they need entity back-references or business defaults
     * that a plain field-to-field mapping can't express.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "instruments", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentParticipantCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Jam toEntity(CreateJamRequest request);

    /**
     * Partial update — only non-null fields on the request overwrite
     * the existing entity. Location/genres/instruments are handled
     * by separate endpoints, not this method.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "instruments", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentParticipantCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(UpdateJamRequest request, @MappingTarget Jam jam);
}