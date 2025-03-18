package com.cinebuzz.apigateway.controller;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cinebuzz.apigateway.dto.MovieDto;
import com.cinebuzz.apigateway.dto.TheatreDto;
import com.cinebuzz.apigateway.model.LoginForm;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class UIController {

    private static final Logger logger = LoggerFactory.getLogger(UIController.class);

    private final RestTemplate restTemplate;

    @Value("${cinebuzz.api.base-url}") 
    private String baseApiUrl;

    public UIController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @GetMapping("/")
    public String home(Model model) {
        return "home"; 
    }
    @GetMapping("/index")
    public String redirectToIndex() {
        return "index"; // Redirect to index.html after home.html
    }
    

    
    @PostMapping("/admin_login")
    public String handleLogin(@Valid @ModelAttribute("loginForm") LoginForm loginForm, 
                              BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "admin_login";
        }

        // Hardcoded credentials
        final String ADMIN_USERNAME = "Shivani";
        final String ADMIN_PASSWORD = "123";

        if (ADMIN_USERNAME.equals(loginForm.getUsername()) && ADMIN_PASSWORD.equals(loginForm.getPassword())) {
            return "redirect:/dashboard";  // Redirect to dashboard if login is successful
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "admin_login"; // Stay on login page and show error
        }
    }

    @GetMapping("/admin_login")
    public String showAdminLoginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm()); // Add empty login form object
        return "admin_login";
    }

        
        @GetMapping("/dashboard")
        public String dashboard() {
            return "dashboard";
        } 
            
      


        @GetMapping("/manage_theatre")
        public String showManageTheatresPage(Model model) {
            String apiUrl = baseApiUrl + "/theatres";
            List<Map<String, Object>> theatres = fetchApiData(apiUrl);
            model.addAttribute("theatres", theatres);
            return "manage_theatre";
        }

   
        @PostMapping("/add-theatre")
        public ResponseEntity<String> addTheatre(@RequestBody TheatreDto theatre) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);  

            HttpEntity<TheatreDto> requestEntity = new HttpEntity<>(theatre, headers);

            // Forward request to Theatre Service
            ResponseEntity<String> response = restTemplate.postForEntity(baseApiUrl, requestEntity, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        
    }


        @GetMapping("/edittheatre/{id}")
        public String editTheatreForm(@PathVariable Long id, Model model) {
            String apiUrl = baseApiUrl + "/" + id;
            Map<String, Object> theatre = restTemplate.getForObject(apiUrl, Map.class);
            model.addAttribute("theatre", theatre);
            return "edit_theatre";
        }

        // Handle Update Request
        @PostMapping("/updatetheatre")
        public String updateTheatre(@ModelAttribute TheatreDto theatreDto) {
            String apiUrl = baseApiUrl + "/" + theatreDto.getTheatreId();
            restTemplate.put(apiUrl, theatreDto);
            return "redirect:/manage_theatre";
        }

        @GetMapping("/deletetheatre/{id}") 
        public String deleteTheatreRedirect(@PathVariable Long id) {
            String apiUrl = "http://localhost:9090/api/theatres/" + id;
            restTemplate.delete(apiUrl);
            return "redirect:/manage_theatre";
        }
       
        @GetMapping("/manage_movies")
        public String showManageMoviesPage(Model model) {
            String apiUrl = baseApiUrl + "/movies";
            List<Map<String, Object>> movies = fetchApiData(apiUrl);
            
            System.out.println("Fetched movies: " + movies); // Debugging line

            model.addAttribute("movies", movies);
            return "manage_movies";
        }

        @PostMapping("/add-movie")
        public String addMovie(@ModelAttribute MovieDto movieDto) {  
            String apiUrl = baseApiUrl + "/movies";  // Make sure endpoint is correct

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<MovieDto> request = new HttpEntity<>(movieDto, headers);

            restTemplate.postForEntity(apiUrl, request, Void.class);

            return "redirect:/manage_movies"; // Redirect to Manage Movies Page
        }

        @PostMapping("/deletemovie/{id}")
        public String deleteMovie(@PathVariable Long id) {
            String apiUrl = "http://localhost:9090/api/movies/" + id; // Call DELETE API via Gateway
            restTemplate.delete(apiUrl);  // Manually trigger DELETE

            return "redirect:/manage_movies";  // Redirect back to the UI
        }


        @GetMapping("/edit-movie/{id}")
        public String showEditMoviePage(@PathVariable Long id, Model model) {
        Map<String, Object> movie = restTemplate.getForObject(baseApiUrl + "/movies/" + id, Map.class);
        model.addAttribute("movie", movie);
        return "edit_movie";
    }

        @PostMapping("/update-movie/{id}")
        public String updateMovie(@PathVariable Long id, @ModelAttribute MovieDto movieDto) {
            putApiData(baseApiUrl + "/movies/" + id, movieDto);
            return "redirect:/manage_movies";
        }
        private void putApiData(String url, Object requestBody) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
            restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Void.class);
        }
 
        @GetMapping("/manage_ticket")
        public String showTickets(Model model) {
            String apiUrl = baseApiUrl + "/tickets";
            logger.info("Fetching tickets from API: {}", apiUrl);
     
            List<Map<String, Object>> tickets = fetchApiData(apiUrl);
            model.addAttribute("tickets", tickets);
     
            return "manage_ticket";
        }
     
        
        
        @PostMapping("/delete-ticket/{id}")
        public String deleteTicket(@PathVariable Long id) {
            String apiUrl = baseApiUrl + "/tickets/" + id;
            restTemplate.delete(apiUrl);
            return "redirect:/manage_ticket";
        }

//        @GetMapping
//        public String getOffers(Model model) {
//            String apiUrl = baseApiUrl + "/offers";
//            logger.info("Fetching offers from API: {}", apiUrl);
//     
//            List<Map<String, Object>> offers = fetchApiData(apiUrl);
//            model.addAttribute("offers", offers);
//     
//            return "manage_offer";
//        }
//     
//        @PostMapping("/add")
//        public String addOffer(@RequestParam Long showId, @RequestParam String offerDetails, @RequestParam BigDecimal discountPercentage) {
//            String apiUrl = baseApiUrl + "/offers";
//            Map<String, Object> request = new HashMap<>();
//            request.put("showId", showId);
//            request.put("offerDetails", offerDetails);
//            request.put("discountPercentage", discountPercentage);
//     
//            logger.info("Adding new offer: {}", request);
//     
//            ResponseEntity<Void> response = restTemplate.postForEntity(apiUrl, request, Void.class);
//            if (response.getStatusCode() == HttpStatus.CREATED) {
//                logger.info("Offer added successfully.");
//            }
//     
//            return "redirect:/offers";
//        }
//     
//        @GetMapping("/edit/{id}")
//        public String editOffer(@PathVariable Long id, Model model) {
//            String apiUrl = baseApiUrl + "/offers/" + id;
//            logger.info("Fetching offer for edit: {}", apiUrl);
//     
//            Map<String, Object> offer = restTemplate.getForObject(apiUrl, Map.class);
//            model.addAttribute("offer", offer);
//     
//            return "offer-edit"; 
//        }
//     
//        @PostMapping("/update/{id}")
//        public String updateOffer(@PathVariable Long id, @RequestParam Long showId, @RequestParam String offerDetails, @RequestParam BigDecimal discountPercentage) {
//            String apiUrl = baseApiUrl + "/offers/" + id;
//            Map<String, Object> request = new HashMap<>();
//            request.put("showId", showId);
//            request.put("offerDetails", offerDetails);
//            request.put("discountPercentage", discountPercentage);
//     
//            logger.info("Updating offer ID {}: {}", id, request);
//     
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
//     
//            restTemplate.exchange(apiUrl, HttpMethod.PUT, entity, Void.class);
//            return "redirect:/offers";
//        }
//     
//        @GetMapping("/delete/{id}")
//        public String deleteOffer(@PathVariable Long id) {
//            String apiUrl = baseApiUrl + "/offers/" + id;
//            logger.info("Deleting offer ID: {}", id);
//     
//            restTemplate.delete(apiUrl);
//            return "redirect:/offers";
//        }
//        
//     
        
        @GetMapping("/manage_seats")
        public String showManageSeatsPage(Model model) {
            String apiUrl = baseApiUrl + "/seats";
            List<Map<String, Object>> seats = fetchApiData(apiUrl);
            
            String tierApiUrl = baseApiUrl + "/tiers";  // Fetch tier details
            List<Map<String, Object>> tiers = fetchApiData(tierApiUrl);
            
            // Prevent null pointers by assigning empty lists if API fails
            if (seats == null) seats = new ArrayList<>();
            if (tiers == null) tiers = new ArrayList<>();
     
            model.addAttribute("seats", seats);
            model.addAttribute("tiers", tiers);  // Add tier data to model
     
            return "manage_seats";
        }
       
        
        @PostMapping("/delete-seat/{id}")
        public String deleteSeat(@PathVariable Long id) {
            String apiUrl = baseApiUrl + "/seats/" + id;
            restTemplate.delete(apiUrl);
            return "redirect:/movie_seats";
        }
        
    @GetMapping("/movies")
    public String getMovies(Model model) {
        String apiUrl = baseApiUrl + "/movies";
        logger.info("Fetching movies from API: {}", apiUrl);

        List<Map<String, Object>> movies = fetchApiData(apiUrl);
        model.addAttribute("movies", movies);

        return "movie-list";  // Thymeleaf template
    }
    

    @GetMapping("/theatres")
    public String getTheatres(Model model) {
        String apiUrl = baseApiUrl + "/theatres";
        logger.info("Fetching theatres from API: {}", apiUrl);

        List<Map<String, Object>> theatres = fetchApiData(apiUrl);
        model.addAttribute("theatres", theatres);

        return "theatre-list";
    }

    @GetMapping("/tickets")
    public String getTickets(Model model) {
        String apiUrl = baseApiUrl + "/tickets";
        logger.info("Fetching tickets from API: {}", apiUrl);

        List<Map<String, Object>> tickets = fetchApiData(apiUrl);
        model.addAttribute("tickets", tickets);

        return "ticket-list";
    }

    private List<Map<String, Object>> fetchApiData(String url) {
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error fetching data from API: {}", url, e);
            return Collections.emptyList(); 
        }
    }
    
}


