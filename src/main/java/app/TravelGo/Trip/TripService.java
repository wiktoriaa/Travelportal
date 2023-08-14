package app.TravelGo.Trip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripService {
    private TripRepository tripRepository;

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public Optional<Trip> getTrip(Long tripId) {
        return tripRepository.findById(tripId);
    }

    public List<Trip> getTrips() {
        return tripRepository.findAll();
    }

    public Trip createTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    public void updateTrip(Trip trip) {
        tripRepository.save(trip);
    }

    public boolean deleteTrip(Long tripId) {
        if (tripRepository.existsById(tripId)) {
            tripRepository.deleteById(tripId);
            return true;
        }
        return false;
    }
}
