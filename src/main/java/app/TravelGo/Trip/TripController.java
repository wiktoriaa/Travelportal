package app.TravelGo.Trip;

import app.TravelGo.Document.Document;
import app.TravelGo.Document.DocumentService;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.User;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/trips")
public class TripController {
    private final TripService tripService;
    private final AuthService authService;

    @Autowired
    public TripController(TripService tripService, AuthService authService)  {
        this.tripService = tripService;
        this.authService = authService;
    }

    @GetMapping("/{trip_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetTripResponse> getTrip(@PathVariable("trip_id") Long tripId) {
        Optional<Trip> response = tripService.getTrip(tripId);
        if (response.isPresent()) {
            Trip trip = response.get();
            GetTripResponse tripResponse = GetTripResponse.builder()
                    .id(trip.getId())
                    .date(trip.getDate())
                    .gathering_place(trip.getGathering_place())
                    .trip_name(trip.getTrip_name())
                    .rate(trip.getRate())
                    .number_of_rates(trip.getNumber_of_rates())
                    .archived(trip.getArchived())
                    .build();
            return ResponseEntity.ok(tripResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GetTripResponse>> getAllTrips() {
        List<Trip> trips = tripService.getTrips();

        if (!trips.isEmpty()) {
            List<GetTripResponse> tripResponses = new ArrayList<>();

            for (Trip trip : trips) {
                if(!trip.getArchived()){
                    GetTripResponse tripResponse = GetTripResponse.builder()
                            .id(trip.getId())
                            .date(trip.getDate())
                            .gathering_place(trip.getGathering_place())
                            .trip_name(trip.getTrip_name())
                            .rate(trip.getRate())
                            .number_of_rates(trip.getNumber_of_rates())
                            .archived(trip.getArchived())
                            .build();

                    tripResponses.add(tripResponse);
                }
            }

            return ResponseEntity.ok(tripResponses);
        } else {
            return ResponseEntity.noContent().build();
        }
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createTrip(@RequestBody CreateTripRequest request, UriComponentsBuilder builder) {
        Trip trip = Trip.builder()
                .date(request.getDate())
                .trip_name(request.getTrip_name())
                .gathering_place(request.getGathering_place())
                .rate(0.0)
                .number_of_rates(0)
                .archived(false)
                .build();
        trip = tripService.createTrip(trip);
        return ResponseEntity.created(builder.pathSegment("api", "trips", "{id}")
                .buildAndExpand(trip.getId()).toUri()).build();
    }


    @DeleteMapping("/{trip_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTrip(@PathVariable("trip_id") Long tripId) {
        Optional<Trip> existingTripOptional = tripService.getTrip(tripId);

        if (existingTripOptional.isPresent() && authService.getCurrentUser().hasRole("MODERATOR")) {
            Trip existingTrip = existingTripOptional.get();


            tripService.deleteTrip(existingTrip.getId());

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{trip_id}/rate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> rateTrip(@PathVariable("trip_id") Long tripId, @RequestBody Map<String, Double> requestBody) {
        Optional<Trip> optionalTrip = tripService.getTrip(tripId);

        if (optionalTrip.isPresent()) {
            Trip trip = optionalTrip.get();

            if (requestBody.containsKey("rate")) {
                Double rate = requestBody.get("rate");

                Double currentRate = (trip.getNumber_of_rates() * trip.getRate() + rate) / (trip.getNumber_of_rates() + 1);
                trip.setNumber_of_rates(trip.getNumber_of_rates() + 1);
                trip.setRate(currentRate);
                tripService.saveTrip(trip);

                return ResponseEntity.ok("Rate was added. Current rate is now " + trip.getRate());
            } else {
                return ResponseEntity.badRequest().body("Rate field is missing in the request JSON.");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{trip_id}/archive")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> archiveTrip(@PathVariable("trip_id") Long tripId) {
        if (authService.getCurrentUser().hasRole("MODERATOR")) {
            Optional<Trip> optionalTrip = tripService.getTrip(tripId);

            if (optionalTrip.isPresent()) {
                Trip trip = optionalTrip.get();
                trip.setArchived(true);
                tripService.saveTrip(trip);

                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

}
