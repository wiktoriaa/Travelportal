package app.TravelGo.TripDiscussion;

import app.TravelGo.File.FileService;
import app.TravelGo.Post.Post;
import app.TravelGo.Post.PostService;
import app.TravelGo.Post.PostType;
import app.TravelGo.Trip.Trip;
import app.TravelGo.Trip.TripService;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.User;
import app.TravelGo.User.UserService;
import app.TravelGo.dto.GetPostResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("api/trips")
public class TripDiscussionController {

    private final PostService postService;
    private final AuthService authService;
    private final UserService userService;
    private final TripService tripService;
    private final FileService fileService;

    @Autowired
    public TripDiscussionController(PostService postService, AuthService authService,
                                    UserService userService, TripService tripService, FileService fileService) {
        this.postService = postService;
        this.authService = authService;
        this.userService = userService;
        this.tripService = tripService;
        this.fileService = fileService;
    }

    @PostMapping("/{trip_id}/discussion")
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<Void> createPost(@PathVariable("trip_id") Long tripId,
                                    @RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam("about") String about,
                                    @RequestParam(value = "image", required = false) MultipartFile image,
                                    UriComponentsBuilder builder) throws IOException {
        Optional<Trip> tripOptional = tripService.getTrip(tripId);
        if (tripOptional.isPresent()) {
            Post post = Post.builder()
                    .title(title)
                    .content(content)
                    .likes(0L)
                    .username(this.authService.getCurrentUser().getUsername())
                    .about(about)
                    .updatedAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .type(PostType.DISCUSSION)
                    .trip(tripOptional.get())
                    .build();

            post = postService.createPost(post);

            if (image != null) {
                fileService.uploadFeaturePostImage(image, post.getId());
            }

            return ResponseEntity.created(builder.pathSegment("api", "trips", "{trip_ip}", "discussion", "{post_id}")
                    .buildAndExpand(tripId, post.getId()).toUri()).build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{trip_ip}/discussion")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GetPostResponse>> getDiscussionByTrip(@PathVariable("trip_ip") Long tripID) {
        List<Post> posts = postService.getPosts();
        List<GetPostResponse> postResponses = new ArrayList<>();

        if (tripService.getTrip(tripID).isPresent()) {
            for (Post post : posts) {
                if (post.getType() != PostType.DISCUSSION || post.getTrip() == null || !Objects.equals(post.getTrip().getId(), tripID)) {
                    continue;
                }
                Optional<User> userOptional = userService.getUserByUsername(post.getUsername());
                if (userOptional.isPresent()) {
                    GetPostResponse postResponse = GetPostResponse.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .username(post.getUsername())
                            .userID(userOptional.get().getId())
                            .about(post.getAbout())
                            .createdAt(post.getCreatedAt())
                            .likes(post.getLikes())
                            .build();
                    postResponses.add(postResponse);
                }
            }

            return ResponseEntity.ok(postResponses);
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/{trip_ip}/discussion/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GetPostResponse> getDiscussionPost(@PathVariable("trip_ip") Long tripID, @PathVariable("post_id") Long postID) {
        Optional<Post> postOptional = postService.getPost(postID);

        if (postOptional.isPresent() && postOptional.get().getType() == PostType.DISCUSSION && Objects.equals(postOptional.get().getTrip().getId(), tripID)) {
            Optional<User> userOptional = userService.getUserByUsername(postOptional.get().getUsername());
            if (userOptional.isPresent()) {
                GetPostResponse postResponse = GetPostResponse.builder()
                        .id(postOptional.get().getId())
                        .title(postOptional.get().getTitle())
                        .content(postOptional.get().getContent())
                        .username(postOptional.get().getUsername())
                        .userID(userOptional.get().getId())
                        .about(postOptional.get().getAbout())
                        .createdAt(postOptional.get().getCreatedAt())
                        .likes(postOptional.get().getLikes())
                        .build();
                return ResponseEntity.ok(postResponse);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{trip_ip}/discussion/{post_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deletePost(@PathVariable("trip_ip") Long tripID, @PathVariable("post_id") Long postID) {

        String username = this.authService.getCurrentUser().getUsername();
        Long userID = this.authService.getCurrentUser().getId();
        String postOwnerUsername = postService.getPost(postID).orElse(null).getUsername();

        if (username.equals(postOwnerUsername) || userService.hasRole(userID, "MODERATOR")) {
            Optional<Post> postOptional = postService.getPost(postID);
            if (postOptional.isPresent() && postOptional.get().getType() == PostType.DISCUSSION && Objects.equals(postOptional.get().getTrip().getId(), tripID)) {
                postService.deletePost(postID);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        }

        return ResponseEntity.notFound().build();
    }
}
