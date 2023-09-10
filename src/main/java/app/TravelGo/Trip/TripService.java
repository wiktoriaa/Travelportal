package app.TravelGo.Trip;

import app.TravelGo.Document.Document;
import app.TravelGo.Document.DocumentService;
import app.TravelGo.Post.Post;
import app.TravelGo.dto.CreateTripRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    private final TripRepository tripRepository;


    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public Optional<Trip> getTrip(Long tripId) {
        return tripRepository.findById(tripId);
    }

    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }


    public boolean deleteTrip(Long tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);
        if (optionalTrip.isPresent()) {

            tripRepository.deleteById(tripId);
            return true;
        }
        return false;
    }
    public boolean archiveTrip(Long tripId) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);

        if (optionalTrip.isPresent()) {
            Trip trip = optionalTrip.get();
            trip.setArchived(true);
            tripRepository.save(trip);
            return true;
        }

        return false;
    }

    public List<Trip> getTrips() {
        return tripRepository.findAll();
    }

    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }
}
