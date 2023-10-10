package app.TravelGo.Offer;

import app.TravelGo.Post.Like.LikeService;
import app.TravelGo.Post.PostService;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.dto.GetOfferResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/offer")
public class OfferController {

    private final PostService postService;
    private final AuthService authService;
    private final LikeService likeService;

    @Autowired
    public OfferController(PostService postService, AuthService authService,
                           LikeService likeService) {
        this.postService = postService;
        this.authService = authService;
        this.likeService = likeService;
    }

    @GetMapping("/{offer_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetOfferResponse> getOffer(@PathVariable("offer_id") Long offerID) {

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{offer_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteOffer(@PathVariable("offer_id") Long offerID) {

        return ResponseEntity.notFound().build();
    }

}
