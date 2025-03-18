package com.CineBuzz.theatreservice.service;

import com.CineBuzz.theatreservice.dto.TheatreDto;
import com.CineBuzz.theatreservice.entity.Theatre;
import com.CineBuzz.theatreservice.exception.ResourceNotFoundException;
import com.CineBuzz.theatreservice.repository.AddressRepository;
import com.CineBuzz.theatreservice.repository.TheatreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheatreServiceImpl implements TheatreService {

    private final TheatreRepository theatreRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public TheatreServiceImpl(TheatreRepository theatreRepository, AddressRepository addressRepository, ModelMapper modelMapper) {
        this.theatreRepository = theatreRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public TheatreDto createTheatre(TheatreDto theatreDto) {
        Theatre theatre = modelMapper.map(theatreDto, Theatre.class);
        theatre.setAddress(addressRepository.save(theatre.getAddress()));
        Theatre savedTheatre = theatreRepository.save(theatre);
        return modelMapper.map(savedTheatre, TheatreDto.class);
    }

    @Override
    public TheatreDto getTheatreById(Long id) {
        Theatre theatre = theatreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));
        return modelMapper.map(theatre, TheatreDto.class);
    }

    @Override
    public List<TheatreDto> getAllTheatres() {
        List<Theatre> theatres = theatreRepository.findAll();
        return theatres.stream().map(theatre -> modelMapper.map(theatre, TheatreDto.class)).collect(Collectors.toList());
    }

    @Override
    public TheatreDto updateTheatre(Long id, TheatreDto theatreDto) {
        Theatre theatre = theatreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));
        theatre.setTheatreName(theatreDto.getTheatreName());
        theatre.setCapacity(theatreDto.getCapacity());
        theatre.setScreens(theatreDto.getScreens());
        Theatre updatedTheatre = theatreRepository.save(theatre);
        return modelMapper.map(updatedTheatre, TheatreDto.class);
    }

    @Override
    public void deleteTheatre(Long id) {
        Theatre theatre = theatreRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));
        theatreRepository.delete(theatre);
    }
}
