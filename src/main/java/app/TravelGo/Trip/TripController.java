package app.TravelGo.Trip;

import app.TravelGo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/trips")
public class TripController {
    final private TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
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
                    .build();
            return ResponseEntity.ok(tripResponse);
        }
        return ResponseEntity.notFound().build();
    }


    @DeleteMapping("/{trip_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteTrip(@PathVariable("trip_id") Long tripId) {
        boolean success = tripService.deleteTrip(tripId);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    //TODO createTrip, rateTrip, acrhiveTrip



}
