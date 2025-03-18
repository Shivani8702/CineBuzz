package com.cinebuzz.offerservice;


import com.cinebuzz.offerservice.client.ShowServiceClient;
import com.cinebuzz.offerservice.client.TicketServiceClient;
import com.cinebuzz.offerservice.dto.OfferDto;
import com.cinebuzz.offerservice.entity.Offer;
import com.cinebuzz.offerservice.exception.ResourceNotFoundException;
import com.cinebuzz.offerservice.repository.OfferRepository;
import com.cinebuzz.offerservice.service.OfferServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ShowServiceClient showServiceClient;

    @Mock
    private TicketServiceClient ticketServiceClient;

    @InjectMocks
    private OfferServiceImpl offerService;

    private Offer offer;
    private OfferDto offerDto;

    @BeforeEach
    void setUp() {
        // Initializing test data with BigDecimal for precision
        offer = new Offer(1L, "Discount Offer", new BigDecimal("10.50"));
        offerDto = new OfferDto(1L, 101L, "Discount Offer", new BigDecimal("10.50"));
    }

    /**
     * Test Case: Creating an offer when the show exists and tickets are available.
     * Expected Outcome: Offer is saved and returned successfully.
     */
    @Test
    void createOffer_ShouldReturnOfferDto_WhenShowExistsAndTicketsAvailable() {
        when(showServiceClient.doesShowExist(101L)).thenReturn(true);
        when(ticketServiceClient.areTicketsAvailable(101L)).thenReturn(true);
        when(modelMapper.map(offerDto, Offer.class)).thenReturn(offer);
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);

        OfferDto result = offerService.createOffer(offerDto);

        assertNotNull(result);
        assertEquals(offerDto.getShowId(), result.getShowId());
        assertEquals(0, offerDto.getDiscountPercentage().compareTo(result.getDiscountPercentage())); // BigDecimal comparison
        verify(offerRepository, times(1)).save(any(Offer.class));
    }

    /**
     * Test Case: Attempting to create an offer when the show does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void createOffer_ShouldThrowException_WhenShowDoesNotExist() {
        when(showServiceClient.doesShowExist(101L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> offerService.createOffer(offerDto));
    }

    /**
     * Test Case: Fetching an existing offer by ID.
     * Expected Outcome: Offer is returned successfully.
     */
    @Test
    void getOfferById_ShouldReturnOfferDto_WhenOfferExists() {
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);

        OfferDto result = offerService.getOfferById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOfferId());
        assertEquals(0, offerDto.getDiscountPercentage().compareTo(result.getDiscountPercentage())); // BigDecimal comparison
    }

    /**
     * Test Case: Fetching an offer by ID when it does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void getOfferById_ShouldThrowException_WhenOfferNotFound() {
        when(offerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> offerService.getOfferById(1L));
    }

    /**
     * Test Case: Retrieving all available offers.
     * Expected Outcome: A list of offers is returned.
     */
    @Test
    void getAllOffers_ShouldReturnOfferList() {
        List<Offer> offers = Arrays.asList(offer);
        List<OfferDto> offerDtos = Arrays.asList(offerDto);

        when(offerRepository.findAll()).thenReturn(offers);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);

        List<OfferDto> result = offerService.getAllOffers();

        assertEquals(1, result.size());
        assertEquals(0, offerDto.getDiscountPercentage().compareTo(result.get(0).getDiscountPercentage())); // BigDecimal comparison
    }

    /**
     * Test Case: Updating an existing offer with valid data.
     * Expected Outcome: The offer details are updated and returned.
     */
    @Test
    void updateOffer_ShouldUpdateOffer_WhenValidData() {
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(showServiceClient.doesShowExist(101L)).thenReturn(true);
        when(ticketServiceClient.areTicketsAvailable(101L)).thenReturn(true);
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);
        when(modelMapper.map(offer, OfferDto.class)).thenReturn(offerDto);

        OfferDto result = offerService.updateOffer(1L, offerDto);

        assertNotNull(result);
        assertEquals("Discount Offer", result.getOfferDetails());
        assertEquals(0, offerDto.getDiscountPercentage().compareTo(result.getDiscountPercentage())); // BigDecimal comparison
    }

    /**
     * Test Case: Deleting an existing offer.
     * Expected Outcome: Offer is successfully deleted without any exceptions.
     */
    @Test
    void deleteOffer_ShouldDeleteOffer_WhenOfferExists() {
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        doNothing().when(offerRepository).deleteById(1L);

        assertDoesNotThrow(() -> offerService.deleteOffer(1L));
        verify(offerRepository, times(1)).deleteById(1L);
    }
}
