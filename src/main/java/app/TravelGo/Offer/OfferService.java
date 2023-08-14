package app.TravelGo.Offer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfferService {
    final private OfferRepository offerRepository;

    @Autowired
    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public Optional<Offer> getOffer(Long offerID) {
        return offerRepository.findById(offerID);
    }

     public Offer createOffer(Offer offer) {
        return offerRepository.save(offer);
    }

    public boolean deleteOffer(Long offerID) {
        if (offerRepository.existsById(offerID)) {
            offerRepository.deleteById(offerID);
            return true;
        }
        return false;
    }
}
