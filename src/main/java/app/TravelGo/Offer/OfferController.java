package app.TravelGo.Offer;

import app.TravelGo.Offer.Offer;
import app.TravelGo.Trip.TripService;
import app.TravelGo.dto.GetOfferResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@RestController
@RequestMapping("api/offer")
public class OfferController {


    final private OfferService offerService;

    @Autowired
    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping("/{offer_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetOfferResponse> getOffer(@PathVariable("offer_id") Long offerID) {
        Optional<Offer> response = offerService.getOffer(offerID);
        if (response.isPresent()) {
            Offer offer = response.get();
            GetOfferResponse tripResponse = GetOfferResponse.builder()
                    .id(offer.getId())
                    .start_date(offer.getStart_date())
                    .end_date(offer.getEnd_date())
                    .price(offer.getPrice())
                    .build();
            return ResponseEntity.ok(tripResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{offer_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteOffer(@PathVariable("offer_id") Long offerID) {
        boolean success = offerService.deleteOffer(offerID);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    //TODO createOffer
}
