package com.cinebuzz.movieservice;


import com.cinebuzz.movieservice.client.TheatreFeignClient;
import com.cinebuzz.movieservice.dto.MovieDto;
import com.cinebuzz.movieservice.dto.TheatreDto;
import com.cinebuzz.movieservice.entity.Movie;
import com.cinebuzz.movieservice.exception.ResourceNotFoundException;
import com.cinebuzz.movieservice.repository.MovieRepository;
import com.cinebuzz.movieservice.service.MovieServiceImpl;

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
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TheatreFeignClient theatreFeignClient;

    @InjectMocks
    private MovieServiceImpl movieService;

    private Movie movie;
    private MovieDto movieDto;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setMovieId(1L);
        movie.setMovieName("Inception");
        movie.setGenre("Sci-Fi");
        movie.setDuration(148);
        movie.setTicketBasePrice(200.0);

        movieDto = new MovieDto();
        movieDto.setMovieId(1L);
        movieDto.setMovieName("Inception");
        movieDto.setGenre("Sci-Fi");
        movieDto.setDuration(148);
        movieDto.setTicketBasePrice(200.0);
    }

    @Test
    void createMovie_ShouldReturnSavedMovieDto() {
        when(modelMapper.map(movieDto, Movie.class)).thenReturn(movie);
        when(movieRepository.save(movie)).thenReturn(movie);
        when(modelMapper.map(movie, MovieDto.class)).thenReturn(movieDto);

        MovieDto result = movieService.createMovie(movieDto);

        assertNotNull(result);
        assertEquals(movieDto.getMovieName(), result.getMovieName());
        verify(movieRepository, times(1)).save(movie);
    }

    @Test
    void getMovieById_ShouldReturnMovieDto_WhenMovieExists() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(modelMapper.map(movie, MovieDto.class)).thenReturn(movieDto);

        MovieDto result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals("Inception", result.getMovieName());
        verify(movieRepository, times(1)).findById(1L);
    }

    @Test
    void getMovieById_ShouldThrowException_WhenMovieNotFound() {
        when(movieRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> movieService.getMovieById(2L));
        verify(movieRepository, times(1)).findById(2L);
    }

    @Test
    void getAllMovies_ShouldReturnListOfMovieDtos() {
        when(movieRepository.findAll()).thenReturn(Arrays.asList(movie));
        when(modelMapper.map(movie, MovieDto.class)).thenReturn(movieDto);

        List<MovieDto> result = movieService.getAllMovies();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void updateMovie_ShouldReturnUpdatedMovieDto() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.save(movie)).thenReturn(movie);
        when(modelMapper.map(movie, MovieDto.class)).thenReturn(movieDto);

        MovieDto result = movieService.updateMovie(1L, movieDto);

        assertNotNull(result);
        assertEquals("Inception", result.getMovieName());
        verify(movieRepository, times(1)).save(movie);
    }

    @Test
    void deleteMovie_ShouldRemoveMovie_WhenExists() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        doNothing().when(movieRepository).delete(movie);

        assertDoesNotThrow(() -> movieService.deleteMovie(1L));
        verify(movieRepository, times(1)).delete(movie);
    }

    @Test
    void getTheatreDetails_ShouldReturnTheatreDto() {
        TheatreDto theatreDto = new TheatreDto();
        theatreDto.setTheatreId(10L);
        theatreDto.setTheatreName("PVR");

        when(theatreFeignClient.getTheatre(10L)).thenReturn(theatreDto);

        TheatreDto result = movieService.getTheatreDetails(10L);

        assertNotNull(result);
        assertEquals("PVR", result.getTheatreName());
        verify(theatreFeignClient, times(1)).getTheatre(10L);
    }
}

