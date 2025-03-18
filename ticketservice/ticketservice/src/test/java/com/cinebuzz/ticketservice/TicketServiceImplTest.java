package com.cinebuzz.ticketservice;

import com.cinebuzz.ticketservice.client.SeatClient;
import com.cinebuzz.ticketservice.client.ShowClient;
import com.cinebuzz.ticketservice.dto.TicketDto;
import com.cinebuzz.ticketservice.entity.Ticket;
import com.cinebuzz.ticketservice.exception.ResourceNotFoundException;
import com.cinebuzz.ticketservice.repository.TicketRepository;
import com.cinebuzz.ticketservice.service.TicketServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ShowClient showClient;

    @Mock
    private SeatClient seatClient;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket ticket;
    private TicketDto ticketDto;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticketDto = new TicketDto();
    }


    /**
     * Test Case: Getting a ticket by ID successfully.
     * Expected Outcome: Returns the correct ticket DTO.
     */
    @Test
    void getTicketById_ShouldReturnTicketDto_WhenTicketExists() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        TicketDto result = ticketService.getTicketById(1L);

        assertNotNull(result);
        assertEquals(ticket.getTicketId(), result.getTicketId());
        assertEquals(ticket.getShowId(), result.getShowId());
    }

    /**
     * Test Case: Getting a ticket by ID when it does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void getTicketById_ShouldThrowException_WhenTicketNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicketById(1L));
    }

    /**
     * Test Case: Retrieving all tickets successfully.
     * Expected Outcome: Returns a list of TicketDto objects.
     */
    @Test
    void getAllTickets_ShouldReturnListOfTicketDto() {
        List<Ticket> tickets = Arrays.asList(ticket);
        List<TicketDto> expectedDtos = Arrays.asList(ticketDto);

        when(ticketRepository.findAll()).thenReturn(tickets);

        List<TicketDto> result = ticketService.getAllTickets();

        assertEquals(expectedDtos.size(), result.size());
        assertEquals(expectedDtos.get(0).getTicketId(), result.get(0).getTicketId());
    }

    /**
     * Test Case: Canceling a ticket that exists.
     * Expected Outcome: Ticket is deleted successfully.
     */
    @Test
    void cancelTicket_ShouldDeleteTicket_WhenTicketExists() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        doNothing().when(ticketRepository).deleteById(1L);

        assertDoesNotThrow(() -> ticketService.cancelTicket(1L));

        verify(ticketRepository, times(1)).deleteById(1L);
    }

    /**
     * Test Case: Canceling a ticket that does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void cancelTicket_ShouldThrowException_WhenTicketNotFound() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.cancelTicket(1L));
    }

    /**
     * Test Case: Checking ticket availability.
     * Expected Outcome: Returns true if tickets exist, false otherwise.
     */
    @Test
    void areTicketsAvailable_ShouldReturnTrue_WhenTicketsExist() {
        when(ticketRepository.countByShowIdAndStatus(100L, "CONFIRMED")).thenReturn((long) 1);

        boolean result = ticketService.areTicketsAvailable(100L);

        assertTrue(result);
    }

    /**
     * Test Case: Checking ticket availability when no tickets exist.
     * Expected Outcome: Returns false.
     */
    @Test
    void areTicketsAvailable_ShouldReturnFalse_WhenNoTicketsExist() {
        when(ticketRepository.countByShowIdAndStatus(100L, "CONFIRMED")).thenReturn((long) 0);

        boolean result = ticketService.areTicketsAvailable(100L);

        assertFalse(result);
    }
}

