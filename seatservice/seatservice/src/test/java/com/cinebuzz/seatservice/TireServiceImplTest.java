package com.cinebuzz.seatservice;


import com.cinebuzz.seatservice.dto.TierDto;
import com.cinebuzz.seatservice.entity.Tier;
import com.cinebuzz.seatservice.exception.ResourceNotFoundException;
import com.cinebuzz.seatservice.repository.TierRepository;
import com.cinebuzz.seatservice.service.TierServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TierServiceImplTest {

    @Mock
    private TierRepository tierRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TierServiceImpl tierService;

    private Tier tier;
    private TierDto tierDto;

    @BeforeEach
    void setUp() {
        tier = new Tier(); // Sample Tier entity
        tierDto = new TierDto(); // Sample DTO
    }

    /**
     * Test Case: Creating a tier successfully.
     * Expected Outcome: The tier is saved and returned with correct details.
     */
    @Test
    void createTier_ShouldReturnTierDto_WhenValid() {
        when(modelMapper.map(tierDto, Tier.class)).thenReturn(tier);
        when(tierRepository.save(tier)).thenReturn(tier);
        when(modelMapper.map(tier, TierDto.class)).thenReturn(tierDto);

        TierDto result = tierService.createTier(tierDto);

        assertNotNull(result);
        assertEquals(tierDto.getTierName(), result.getTierName());
        verify(tierRepository, times(1)).save(tier);
    }

    /**
     * Test Case: Retrieving an existing tier by ID.
     * Expected Outcome: The tier is found and returned as a DTO.
     */
    @Test
    void getTierById_ShouldReturnTierDto_WhenTierExists() {
        when(tierRepository.findById(1L)).thenReturn(Optional.of(tier));
        when(modelMapper.map(tier, TierDto.class)).thenReturn(tierDto);

        TierDto result = tierService.getTierById(1L);

        assertNotNull(result);
        assertEquals(tierDto.getTierName(), result.getTierName());
    }

    /**
     * Test Case: Retrieving a tier by ID when it does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void getTierById_ShouldThrowException_WhenTierNotFound() {
        when(tierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tierService.getTierById(1L));
    }

    /**
     * Test Case: Retrieving all tiers.
     * Expected Outcome: A list of TierDto objects is returned.
     */
    @Test
    void getAllTiers_ShouldReturnListOfTierDto() {
        List<Tier> tiers = Arrays.asList(tier);
        List<TierDto> tierDtos = Arrays.asList(tierDto);

        when(tierRepository.findAll()).thenReturn(tiers);
        when(modelMapper.map(tier, TierDto.class)).thenReturn(tierDto);

        List<TierDto> result = tierService.getAllTiers();

        assertEquals(1, result.size());
        assertEquals(tierDto.getTierName(), result.get(0).getTierName());
    }

    /**
     * Test Case: Updating an existing tier with valid data.
     * Expected Outcome: Tier details are updated successfully.
     */
    @Test
    void updateTier_ShouldUpdateTier_WhenValidData() {
        Tier updatedTier = new Tier();
        TierDto updatedTierDto = new TierDto();

        when(tierRepository.findById(1L)).thenReturn(Optional.of(tier));
        when(tierRepository.save(any(Tier.class))).thenReturn(updatedTier);
        when(modelMapper.map(updatedTier, TierDto.class)).thenReturn(updatedTierDto);

        TierDto result = tierService.updateTier(1L, updatedTierDto);

        assertNotNull(result);
        assertEquals("Gold", result.getTierName());
        assertEquals(550.00, result.getTicketPriceMultiplier());
        verify(tierRepository, times(1)).save(any(Tier.class));
    }

    /**
     * Test Case: Updating a tier that does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void updateTier_ShouldThrowException_WhenTierNotFound() {
        when(tierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tierService.updateTier(1L, tierDto));
    }

    /**
     * Test Case: Deleting an existing tier.
     * Expected Outcome: The tier is successfully deleted.
     */
    @Test
    void deleteTier_ShouldDeleteTier_WhenTierExists() {
        when(tierRepository.findById(1L)).thenReturn(Optional.of(tier));
        doNothing().when(tierRepository).delete(tier);

        assertDoesNotThrow(() -> tierService.deleteTier(1L));
        verify(tierRepository, times(1)).delete(tier);
    }

    /**
     * Test Case: Deleting a tier that does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void deleteTier_ShouldThrowException_WhenTierNotFound() {
        when(tierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tierService.deleteTier(1L));
    }
}
