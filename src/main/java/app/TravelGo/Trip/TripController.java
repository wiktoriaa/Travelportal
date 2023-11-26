package app.TravelGo.Trip;

import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.User;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.CreateTripRequest;
import app.TravelGo.dto.GetTripResponse;
import app.TravelGo.dto.SimpleStringMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@RestController
@Transactional
@RequestMapping("api/trips")
public class TripController {
    private final TripService tripService;
    private final AuthService authService;

    private final UserService userService;

    @Autowired
    public TripController(TripService tripService, AuthService authService, UserService userService)  {
        this.tripService = tripService;
        this.authService = authService;
        this.userService = userService;
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
                    .gatheringPlace(trip.getGatheringPlace())
                    .tripName(trip.getTripName())
                    .rate(trip.getRate())
                    .numberOfRates(trip.getNumberOfRates())
                    .archived(trip.getArchived())
                    .participants(trip.getParticipants())
                    .tripGuides(trip.getTripGuides())
                    .description(trip.getDescription())
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
                if (!trip.getArchived()) {
                    GetTripResponse tripResponse = GetTripResponse.builder()
                            .id(trip.getId())
                            .date(trip.getDate())
                            .gatheringPlace(trip.getGatheringPlace())
                            .tripName(trip.getTripName())
                            .rate(trip.getRate())
                            .numberOfRates(trip.getNumberOfRates())
                            .archived(trip.getArchived())
                            .participants(trip.getParticipants())
                            .tripGuides(trip.getTripGuides())
                            .description(trip.getDescription())
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
        if (authService.getCurrentUser().hasRole("GUIDE")) {
            List<User> guidesList = new ArrayList<>();
            guidesList.add(authService.getCurrentUser());
            for (Long id : request.getGuidesIDs()) {
                if (userService.getUser(id).isPresent() && !guidesList.contains(userService.getUser(id).get())) {
                    guidesList.add(userService.getUser(id).get());
                }
            }

            Trip trip = Trip.builder()
                    .date(request.getDate())
                    .tripName(request.getTripName())
                    .gatheringPlace(request.getGatheringPlace())
                    .rate(0.0)
                    .numberOfRates(0)
                    .archived(false)
                    .participants(new HashSet<>())
                    .tripGuides(guidesList)
                    .description(request.getDescription())
                    .build();
            trip = tripService.createTrip(trip);
            return ResponseEntity.created(builder.pathSegment("api", "trips", "{id}")
                    .buildAndExpand(trip.getId()).toUri()).build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{trip_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTrip(@PathVariable("trip_id") Long tripId) {
        Optional<Trip> existingTripOptional = tripService.getTrip(tripId);

        if (existingTripOptional.isPresent() && (authService.getCurrentUser().hasRole("MODERATOR") || authService.getCurrentUser().hasRole("GUIDE"))) {
            Trip existingTrip = existingTripOptional.get();

            tripService.deleteTrip(existingTrip.getId());

            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{trip_id}/rate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<SimpleStringMessage> rateTrip(@PathVariable("trip_id") Long tripId, @RequestBody Map<String, Double> requestBody) {
        Optional<Trip> optionalTrip = tripService.getTrip(tripId);

        if (optionalTrip.isPresent()) {
            Trip trip = optionalTrip.get();
            User currentUser = authService.getCurrentUser();

            if (trip.getUserRates().containsKey(currentUser)) {
                Double previousRate = trip.getUserRates().get(currentUser);
                trip.setRate((trip.getRate() * trip.getNumberOfRates() - previousRate) / (trip.getNumberOfRates() - 1));
                trip.setNumberOfRates(trip.getNumberOfRates() - 1);
            }

            if (requestBody.containsKey("rate")) {
                if (trip.getParticipants().contains(currentUser)) {
                    Double rate = requestBody.get("rate");

                    trip.setRate((trip.getNumberOfRates() * trip.getRate() + rate) / (trip.getNumberOfRates() + 1));
                    trip.setNumberOfRates(trip.getNumberOfRates() + 1);
                    trip.getUserRates().put(currentUser, rate);

                    tripService.saveTrip(trip);

                    return ResponseEntity.ok(new SimpleStringMessage("Rate was added. Current rate is now " + trip.getRate()));
                } else {
                    return ResponseEntity.ok(new SimpleStringMessage("You must be enrolled to trip to rate it."));
                }
            } else {
                return ResponseEntity.badRequest().body(new SimpleStringMessage("Rate field is missing in the request JSON."));
            }
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/{trip_id}/rates")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, Double>> getRates(@PathVariable("trip_id") Long tripId) {
        Optional<Trip> optionalTrip = tripService.getTrip(tripId);

        if (optionalTrip.isPresent()) {
            Trip trip = optionalTrip.get();
            Map<String, Double> ratesMap = new HashMap<>();

            for (Map.Entry<User, Double> entry : trip.getUserRates().entrySet()) {
                ratesMap.put(entry.getKey().getUsername(), entry.getValue());
            }

            return ResponseEntity.ok(ratesMap);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{trip_id}/archive")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> archiveTrip(@PathVariable("trip_id") Long tripId) {
        if (authService.getCurrentUser().hasRole("MODERATOR") || authService.getCurrentUser().hasRole("GUIDE")) {
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

    @PostMapping("/{tripId}/enroll")
    public ResponseEntity<String> enrollToTrip(@PathVariable("tripId") Long tripId) {
        Optional<Trip> tripOptional = tripService.getTrip(tripId);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();

            User user = authService.getCurrentUser();

            if (user != null) {

                if (trip.getParticipants().contains(user)) {
                    return ResponseEntity.badRequest().body("User is already enrolled to that trip.");
                }

                trip.getParticipants().add(user);
                tripService.saveTrip(trip);

                user.getEnrolledTrips().add(trip);
                userService.saveUser(user);

                return ResponseEntity.ok("User successfully enrolled to trip.");
            } else {
                return ResponseEntity.badRequest().body("Can't find user.");
            }
        } else {
            return ResponseEntity.badRequest().body("Can't find trip.");
        }
    }
    @PostMapping("/{tripId}/withdraw")
    public ResponseEntity<String> withdrawFromTrip(@PathVariable("tripId") Long tripId) {
        Optional<Trip> tripOptional = tripService.getTrip(tripId);

        if (tripOptional.isPresent()) {
            Trip trip = tripOptional.get();

            User user = authService.getCurrentUser();

            if (user != null) {

                if (!trip.getParticipants().contains(user)) {
                    return ResponseEntity.badRequest().body("User is not enrolled to that trip.");
                }

                trip.getParticipants().remove(user);
                tripService.saveTrip(trip);

                user.getEnrolledTrips().remove(trip);
                userService.saveUser(user);

                return ResponseEntity.ok("User successfully withdrew from the trip.");
            } else {
                return ResponseEntity.badRequest().body("Can't find user.");
            }
        } else {
            return ResponseEntity.badRequest().body("Can't find trip.");
        }
    }


}
