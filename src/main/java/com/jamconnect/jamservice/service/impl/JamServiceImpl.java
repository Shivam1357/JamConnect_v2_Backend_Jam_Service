package com.jamconnect.jamservice.service.impl;

import com.jamconnect.jamservice.dto.request.AddressRequest;
import com.jamconnect.jamservice.dto.request.CreateJamRequest;
import com.jamconnect.jamservice.dto.request.GenreRequest;
import com.jamconnect.jamservice.dto.request.InstrumentRequest;
import com.jamconnect.jamservice.dto.request.UpdateJamLocationRequest;
import com.jamconnect.jamservice.dto.request.UpdateJamRequest;
import com.jamconnect.jamservice.dto.response.JamResponse;
import com.jamconnect.jamservice.entity.Jam;
import com.jamconnect.jamservice.entity.JamAddress;
import com.jamconnect.jamservice.entity.JamGenre;
import com.jamconnect.jamservice.entity.JamInstrument;
import com.jamconnect.jamservice.entity.UserReference;
import com.jamconnect.jamservice.enums.JamStatus;
import com.jamconnect.jamservice.event.payload.outgoing.JamAddressSnapshot;
import com.jamconnect.jamservice.event.payload.outgoing.JamCreatedEvent;
import com.jamconnect.jamservice.event.payload.outgoing.JamDeletedEvent;
import com.jamconnect.jamservice.event.payload.outgoing.JamGenreSnapshot;
import com.jamconnect.jamservice.event.payload.outgoing.JamInstrumentSnapshot;
import com.jamconnect.jamservice.event.payload.outgoing.JamLocationUpdatedEvent;
import com.jamconnect.jamservice.event.payload.outgoing.JamUpdatedEvent;
import com.jamconnect.jamservice.event.producer.JamEventProducer;
import com.jamconnect.jamservice.exception.BusinessRuleException;
import com.jamconnect.jamservice.exception.JamNotFoundException;
import com.jamconnect.jamservice.exception.UnauthorizedJamActionException;
import com.jamconnect.jamservice.mapper.AddressMapper;
import com.jamconnect.jamservice.mapper.GenreMapper;
import com.jamconnect.jamservice.mapper.InstrumentMapper;
import com.jamconnect.jamservice.mapper.JamMapper;
import com.jamconnect.jamservice.repository.JamRepository;
import com.jamconnect.jamservice.service.JamService;
import com.jamconnect.jamservice.service.UserReferenceResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class JamServiceImpl implements JamService {

    private final JamRepository jamRepository;
    private final UserReferenceResolver userReferenceResolver;

    private final JamMapper jamMapper;
    private final AddressMapper addressMapper;
    private final GenreMapper genreMapper;
    private final InstrumentMapper instrumentMapper;

    private final JamEventProducer jamEventProducer;

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    @Transactional
    @Override
    public JamResponse createJam(UUID organizerId, CreateJamRequest request) {
        validateTimeRange(request.getStartTime(), request.getEndTime());

        UserReference organizer = userReferenceResolver.resolve(organizerId);

        Jam jam = jamMapper.toEntity(request);
        jam.setOrganizer(organizer);
        jam.setStatus(JamStatus.UPCOMING);
        jam.setCurrentParticipantCount(0);

        attachAddress(jam, request.getAddress());
        attachGenres(jam, request.getGenres());
        attachInstruments(jam, request.getInstruments());

        Jam saved = jamRepository.save(jam);
        log.info("Jam created: id={}, organizerId={}", saved.getId(), organizerId);

        jamEventProducer.sendJamCreatedEvent(buildJamCreatedEvent(saved));

        return jamMapper.toResponse(saved);
    }

    private void attachAddress(Jam jam, AddressRequest addressRequest) {
        JamAddress address = addressMapper.toEntity(addressRequest);
        address.setJam(jam);
        jam.setAddress(address);
    }

    private void attachGenres(Jam jam, List<GenreRequest> genreRequests) {
        Set<JamGenre> genres = new HashSet<>();
        for (GenreRequest gr : genreRequests) {
            JamGenre genre = genreMapper.toEntity(gr);
            genre.setJam(jam);
            genres.add(genre);
        }
        jam.setGenres(genres);
    }

    private void attachInstruments(Jam jam, List<InstrumentRequest> instrumentRequests) {
        if (instrumentRequests == null) {
            return;
        }
        Set<JamInstrument> instruments = new HashSet<>();
        for (InstrumentRequest ir : instrumentRequests) {
            JamInstrument instrument = instrumentMapper.toEntity(ir);
            instrument.setJam(jam);
            instruments.add(instrument);
        }
        jam.setInstruments(instruments);
    }

    // ---------------------------------------------------------------
    // Read
    // ---------------------------------------------------------------

    @Override
    public JamResponse getJamById(UUID jamId) {
        return jamMapper.toResponse(getJamEntityOrThrow(jamId));
    }

    @Override
    public Page<JamResponse> listUpcomingJams(Pageable pageable) {
        return jamRepository.findByStatus(JamStatus.UPCOMING, pageable).map(jamMapper::toResponse);
    }

    @Override
    public Page<JamResponse> listJamsByOrganizer(UUID organizerId, Pageable pageable) {
        return jamRepository.findByOrganizerId(organizerId, pageable).map(jamMapper::toResponse);
    }

    // ---------------------------------------------------------------
    // Update (core fields)
    // ---------------------------------------------------------------

    @Transactional
    @Override
    public JamResponse updateJam(UUID jamId, UUID requesterId, UpdateJamRequest request) {
        Jam jam = getJamEntityOrThrow(jamId);
        assertIsOrganizer(jam, requesterId);
        assertEditable(jam);

        LocalDateTime effectiveStart = request.getStartTime() != null ? request.getStartTime() : jam.getStartTime();
        LocalDateTime effectiveEnd = request.getEndTime() != null ? request.getEndTime() : jam.getEndTime();
        validateTimeRange(effectiveStart, effectiveEnd);

        Set<String> changedFields = diffFields(jam, request);

        jamMapper.updateEntityFromRequest(request, jam);
        Jam saved = jamRepository.save(jam);

        log.info("Jam updated: id={}, changedFields={}", jamId, changedFields);
        jamEventProducer.sendJamUpdatedEvent(buildJamUpdatedEvent(saved, changedFields));

        return jamMapper.toResponse(saved);
    }

    private Set<String> diffFields(Jam existing, UpdateJamRequest request) {
        Set<String> changed = new HashSet<>();
        if (request.getTitle() != null && !request.getTitle().equals(existing.getTitle())) changed.add("title");
        if (request.getDescription() != null && !request.getDescription().equals(existing.getDescription())) changed.add("description");
        if (request.getSkillLevel() != null && request.getSkillLevel() != existing.getSkillLevel()) changed.add("skillLevel");
        if (request.getStartTime() != null && !request.getStartTime().equals(existing.getStartTime())) changed.add("startTime");
        if (request.getEndTime() != null && !request.getEndTime().equals(existing.getEndTime())) changed.add("endTime");
        if (request.getMaxCapacity() != null && !request.getMaxCapacity().equals(existing.getMaxCapacity())) changed.add("maxCapacity");
        return changed;
    }

    // ---------------------------------------------------------------
    // Update (location only — separate endpoint, mirrors User Service's
    // UserAddressUpdatedEvent split)
    // ---------------------------------------------------------------

    @Transactional
    @Override
    public JamResponse updateJamLocation(UUID jamId, UUID requesterId, UpdateJamLocationRequest request) {
        Jam jam = getJamEntityOrThrow(jamId);
        assertIsOrganizer(jam, requesterId);
        assertEditable(jam);

        JamAddress address = jam.getAddress();
        if (address == null) {
            address = addressMapper.toEntity(request.getAddress());
            address.setJam(jam);
            jam.setAddress(address);
        } else {
            // addressMapper.updateEntityFromRequest(request.getAddress(), address);
        }

        Jam saved = jamRepository.save(jam);
        log.info("Jam location updated: id={}", jamId);

        jamEventProducer.sendJamLocationUpdatedEvent(buildJamLocationUpdatedEvent(saved));

        return jamMapper.toResponse(saved);
    }

    // ---------------------------------------------------------------
    // Cancel (soft — history is preserved, status flips to CANCELLED)
    // ---------------------------------------------------------------

    @Transactional
    @Override
    public void cancelJam(UUID jamId, UUID requesterId, String reason) {
        Jam jam = getJamEntityOrThrow(jamId);
        assertIsOrganizer(jam, requesterId);

        if (jam.getStatus() == JamStatus.COMPLETED) {
            throw new BusinessRuleException("Cannot cancel a jam that has already completed", HttpStatus.CONFLICT);
        }
        if (jam.getStatus() == JamStatus.CANCELLED) {
            throw new BusinessRuleException("Jam is already cancelled", HttpStatus.CONFLICT);
        }

        jam.setStatus(JamStatus.CANCELLED);
        jamRepository.save(jam);

        log.info("Jam cancelled: id={}, reason={}", jamId, reason);
        jamEventProducer.sendJamDeletedEvent(JamDeletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .jamId(jam.getId())
                .organizerId(jam.getOrganizer().getId())
                .reason(reason)
                .deletedAt(LocalDateTime.now())
                .build());
    }

    // ---------------------------------------------------------------
    // Package-private helpers used by other services (participant/review)
    // ---------------------------------------------------------------

    Jam getJamEntityOrThrow(UUID jamId) {
        return jamRepository.findById(jamId).orElseThrow(() -> new JamNotFoundException(jamId));
    }

    private void assertIsOrganizer(Jam jam, UUID requesterId) {
        if (!jam.getOrganizer().getId().equals(requesterId)) {
            throw new UnauthorizedJamActionException("Only the organizer can perform this action");
        }
    }

    private void assertEditable(Jam jam) {
        if (jam.getStatus() == JamStatus.COMPLETED || jam.getStatus() == JamStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "Cannot edit a jam that is " + jam.getStatus(), HttpStatus.CONFLICT);
        }
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (end != null && !end.isAfter(start)) {
            throw new BusinessRuleException("endTime must be after startTime", HttpStatus.BAD_REQUEST);
        }
    }

    // ---------------------------------------------------------------
    // Event payload builders
    // ---------------------------------------------------------------

    private JamCreatedEvent buildJamCreatedEvent(Jam jam) {
        return JamCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .jamId(jam.getId())
                .organizerId(jam.getOrganizer().getId())
                .title(jam.getTitle())
                .description(jam.getDescription())
                .skillLevel(jam.getSkillLevel())
                .startTime(jam.getStartTime())
                .endTime(jam.getEndTime())
                .maxCapacity(jam.getMaxCapacity())
                .status(jam.getStatus())
                .address(toAddressSnapshot(jam.getAddress()))
                .genres(jam.getGenres().stream().map(this::toGenreSnapshot).toList())
                .instruments(jam.getInstruments().stream().map(this::toInstrumentSnapshot).toList())
                .createdAt(jam.getCreatedAt())
                .build();
    }

    private JamUpdatedEvent buildJamUpdatedEvent(Jam jam, Set<String> changedFields) {
        return JamUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .jamId(jam.getId())
                .title(jam.getTitle())
                .description(jam.getDescription())
                .skillLevel(jam.getSkillLevel())
                .startTime(jam.getStartTime())
                .endTime(jam.getEndTime())
                .maxCapacity(jam.getMaxCapacity())
                .currentParticipantCount(jam.getCurrentParticipantCount())
                .status(jam.getStatus())
                .changedFields(changedFields)
                .updatedAt(jam.getUpdatedAt())
                .build();
    }

    private JamLocationUpdatedEvent buildJamLocationUpdatedEvent(Jam jam) {
        return JamLocationUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(LocalDateTime.now())
                .jamId(jam.getId())
                .address(toAddressSnapshot(jam.getAddress()))
                .updatedAt(jam.getUpdatedAt())
                .build();
    }

    private JamAddressSnapshot toAddressSnapshot(JamAddress address) {
        if (address == null) {
            return null;
        }
        return JamAddressSnapshot.builder()
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .formattedAddress(address.getFormattedAddress())
                .placeId(address.getPlaceId())
                .build();
    }

    private JamGenreSnapshot toGenreSnapshot(JamGenre genre) {
        return JamGenreSnapshot.builder()
                .genreId(genre.getGenreId())
                .genreName(genre.getGenreName())
                .build();
    }

    private JamInstrumentSnapshot toInstrumentSnapshot(JamInstrument instrument) {
        return JamInstrumentSnapshot.builder()
                .instrumentId(instrument.getInstrumentId())
                .instrumentName(instrument.getInstrumentName())
                .slotsNeeded(instrument.getSlotsNeeded())
                .build();
    }
}
