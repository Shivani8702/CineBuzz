package com.CineBuzz.theatreservice;

import com.CineBuzz.theatreservice.dto.TheatreDto;
import com.CineBuzz.theatreservice.entity.Address;
import com.CineBuzz.theatreservice.entity.Theatre;
import com.CineBuzz.theatreservice.exception.ResourceNotFoundException;
import com.CineBuzz.theatreservice.repository.AddressRepository;
import com.CineBuzz.theatreservice.repository.TheatreRepository;
import com.CineBuzz.theatreservice.service.TheatreServiceImpl;

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
class TheatreServiceImplTest {

    @Mock
    private TheatreRepository theatreRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TheatreServiceImpl theatreService;

    private Theatre theatre;
    private TheatreDto theatreDto;
    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address(1L, "Street 1", "City", "State", "123456");
        theatre = new Theatre(1L, "Cineplex", 200, 5, address);
        theatreDto = new TheatreDto();
    }

    /**
     * Test Case: Creating a theatre successfully.
     * Expected Outcome: The theatre is saved and returned as a DTO.
     */
    @Test
    void createTheatre_ShouldReturnTheatreDto_WhenValid() {
        when(modelMapper.map(theatreDto, Theatre.class)).thenReturn(theatre);
        when(addressRepository.save(address)).thenReturn(address);
        when(theatreRepository.save(theatre)).thenReturn(theatre);
        when(modelMapper.map(theatre, TheatreDto.class)).thenReturn(theatreDto);

        TheatreDto result = theatreService.createTheatre(theatreDto);

        assertNotNull(result);
        assertEquals(theatreDto.getTheatreName(), result.getTheatreName());
        verify(theatreRepository, times(1)).save(theatre);
    }

    /**
     * Test Case: Retrieving an existing theatre by ID.
     * Expected Outcome: The theatre is found and returned as a DTO.
     */
    @Test
    void getTheatreById_ShouldReturnTheatreDto_WhenTheatreExists() {
        when(theatreRepository.findById(1L)).thenReturn(Optional.of(theatre));
        when(modelMapper.map(theatre, TheatreDto.class)).thenReturn(theatreDto);

        TheatreDto result = theatreService.getTheatreById(1L);

        assertNotNull(result);
        assertEquals(theatreDto.getTheatreName(), result.getTheatreName());
    }

    /**
     * Test Case: Retrieving a theatre by ID when it does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void getTheatreById_ShouldThrowException_WhenTheatreNotFound() {
        when(theatreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> theatreService.getTheatreById(1L));
    }

    /**
     * Test Case: Retrieving all theatres.
     * Expected Outcome: A list of TheatreDto objects is returned.
     */
    @Test
    void getAllTheatres_ShouldReturnListOfTheatreDto() {
        List<Theatre> theatres = Arrays.asList(theatre);
        List<TheatreDto> theatreDtos = Arrays.asList(theatreDto);

        when(theatreRepository.findAll()).thenReturn(theatres);
        when(modelMapper.map(theatre, TheatreDto.class)).thenReturn(theatreDto);

        List<TheatreDto> result = theatreService.getAllTheatres();

        assertEquals(1, result.size());
        assertEquals(theatreDto.getTheatreName(), result.get(0).getTheatreName());
    }

    @Test
    void updateTheatre_ShouldThrowException_WhenTheatreNotFound() {
        when(theatreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> theatreService.updateTheatre(1L, theatreDto));
    }

    /**
     * Test Case: Deleting an existing theatre.
     * Expected Outcome: The theatre is successfully deleted.
     */
    @Test
    void deleteTheatre_ShouldDeleteTheatre_WhenTheatreExists() {
        when(theatreRepository.findById(1L)).thenReturn(Optional.of(theatre));
        doNothing().when(theatreRepository).delete(theatre);

        assertDoesNotThrow(() -> theatreService.deleteTheatre(1L));
        verify(theatreRepository, times(1)).delete(theatre);
    }

    /**
     * Test Case: Deleting a theatre that does not exist.
     * Expected Outcome: ResourceNotFoundException is thrown.
     */
    @Test
    void deleteTheatre_ShouldThrowException_WhenTheatreNotFound() {
        when(theatreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> theatreService.deleteTheatre(1L));
    }
}
