package com.cinebuzz.seatservice;


import com.cinebuzz.seatservice.dto.SeatDto;
import com.cinebuzz.seatservice.entity.Seat;
import com.cinebuzz.seatservice.entity.Tier;
import com.cinebuzz.seatservice.exception.ResourceNotFoundException;
import com.cinebuzz.seatservice.repository.SeatRepository;
import com.cinebuzz.seatservice.repository.TierRepository;
import com.cinebuzz.seatservice.service.SeatServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceImplTest {

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private TierRepository tierRepository;

    @InjectMocks
    private SeatServiceImpl seatService;

    private Seat seat;
    private SeatDto seatDto;
    private Tier tier;

    @BeforeEach
    void setUp() {
        tier = new Tier(); // Creating a Tier entity for reference
        seat = new Seat(tier, "A1", "AVAILABLE");
        seatDto = new SeatDto(1L, 1L, "A1", "AVAILABLE");
    }

    /**
     * Test Case: Creating a seat successfully.
     * Expected Outcome: Seat is saved and returned with correct details.
     */
    @Test
    void createSeat_ShouldReturnSeatDto_WhenTierExists() {
        when(tierRepository.findById(1L)).thenReturn(Optional.of(tier));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);

        SeatDto result = seatService.createSeat(seatDto);

        assertNotNull(result);
        assertEquals(seatDto.getSeatNumber(), result.getSeatNumber());
        assertEquals(seatDto.getStatus(), result.getStatus());
        verify(seatRepository, times(1)).save(any(Seat.class));
    }

    /**
     * Test Case: Creating a seat when the tier does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void createSeat_ShouldThrowException_WhenTierNotFound() {
        when(tierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> seatService.createSeat(seatDto));
    }

    /**
     * Test Case: Retrieving an existing seat by ID.
     * Expected Outcome: SeatDto is returned with correct details.
     */
    @Test
    void getSeatById_ShouldReturnSeatDto_WhenSeatExists() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        SeatDto result = seatService.getSeatById(1L);

        assertNotNull(result);
        assertEquals(seatDto.getSeatNumber(), result.getSeatNumber());
    }

    /**
     * Test Case: Retrieving a seat by ID when it does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void getSeatById_ShouldThrowException_WhenSeatNotFound() {
        when(seatRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> seatService.getSeatById(1L));
    }

    /**
     * Test Case: Retrieving all available seats.
     * Expected Outcome: A list of seats is returned.
     */
    @Test
    void getAllSeats_ShouldReturnSeatList() {
        List<Seat> seats = Arrays.asList(seat);
        when(seatRepository.findAll()).thenReturn(seats);

        List<SeatDto> result = seatService.getAllSeats();

        assertEquals(1, result.size());
        assertEquals(seatDto.getSeatNumber(), result.get(0).getSeatNumber());
    }

    /**
     * Test Case: Updating an existing seat with valid data.
     * Expected Outcome: Seat details are updated successfully.
     */
    @Test
    void updateSeat_ShouldUpdateSeat_WhenValidData() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(seatRepository.save(any(Seat.class))).thenReturn(seat);

        SeatDto updatedSeatDto = new SeatDto(1L, 1L, "A2", "RESERVED");
        SeatDto result = seatService.updateSeat(1L, updatedSeatDto);

        assertNotNull(result);
        assertEquals("A2", result.getSeatNumber());
        assertEquals("RESERVED", result.getStatus());
        verify(seatRepository, times(1)).save(any(Seat.class));
    }

    /**
     * Test Case: Updating a seat that does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void updateSeat_ShouldThrowException_WhenSeatNotFound() {
        when(seatRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> seatService.updateSeat(1L, seatDto));
    }

    /**
     * Test Case: Deleting an existing seat.
     * Expected Outcome: Seat is successfully deleted without any exceptions.
     */
    @Test
    void deleteSeat_ShouldDeleteSeat_WhenSeatExists() {
        when(seatRepository.existsById(1L)).thenReturn(true);
        doNothing().when(seatRepository).deleteById(1L);

        assertDoesNotThrow(() -> seatService.deleteSeat(1L));
        verify(seatRepository, times(1)).deleteById(1L);
    }

    /**
     * Test Case: Deleting a seat that does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void deleteSeat_ShouldThrowException_WhenSeatNotFound() {
        when(seatRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> seatService.deleteSeat(1L));
    }

    /**
     * Test Case: Reserving a seat successfully.
     * Expected Outcome: Seat status is updated to "RESERVED".
     */
    @Test
    void reserveSeat_ShouldReserveSeat_WhenSeatIsAvailable() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        seatService.reserveSeat(1L);

        assertEquals("RESERVED", seat.getStatus());
        verify(seatRepository, times(1)).save(seat);
    }

    /**
     * Test Case: Reserving a seat that does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void reserveSeat_ShouldThrowException_WhenSeatNotFound() {
        when(seatRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> seatService.reserveSeat(1L));
    }

    /**
     * Test Case: Reserving a seat that is already reserved.
     * Expected Outcome: IllegalStateException is thrown.
     */
    @Test
    void reserveSeat_ShouldThrowException_WhenSeatIsAlreadyReserved() {
        seat.setStatus("RESERVED");
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));

        assertThrows(IllegalStateException.class, () -> seatService.reserveSeat(1L));
    }
}
