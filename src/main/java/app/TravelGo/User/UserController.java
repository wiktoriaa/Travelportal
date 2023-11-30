package app.TravelGo.User;

import app.TravelGo.Comment.Comment;
import app.TravelGo.Comment.CommentService;
import app.TravelGo.File.FileService;
import app.TravelGo.Post.Post;
import app.TravelGo.Post.PostService;
import app.TravelGo.Trip.Trip;
import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.Role.Role;
import app.TravelGo.User.Role.RoleRepository;
import app.TravelGo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final AuthService authService;
    private final PostService postService;
    private final CommentService commentService;
    private final FileService fileService;

    @Autowired
    public UserController(UserService userService, RoleRepository roleRepository, AuthService authService, PostService postService, CommentService commentService, FileService fileService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.authService = authService;
        this.postService = postService;
        this.commentService = commentService;
        this.fileService = fileService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Iterable<GetUserResponse> getAllUsers() {
        List<GetUserResponse> usersWithoutPasswords = new ArrayList<>();
        GetUserResponse userResponse;

        for (User user : userService.getAllUsers()) {

            if (authService.getCurrentUser().hasRole("MODERATOR")) {
                userResponse = GetUserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .name(user.getName())
                        .surname(user.getSurname())
                        .roles(user.getRoles())
                        .build();
            }
            else {
                userResponse = GetUserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .name(user.getName())
                        .surname(user.getSurname())
                        .roles(user.getRoles())
                        .build();
            }

            if(authService.getCurrentUser().getUsername().equals(userResponse.getUsername())
            || authService.getCurrentUser().hasRole("MODERATOR")){
                usersWithoutPasswords.add(userResponse);
            }
        }

        return usersWithoutPasswords;
    }

    @GetMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUser(@PathVariable("user_id") Long userId) {
        Optional<User> response = userService.getUser(userId);
        if (response.isPresent()) {
            User user = response.get();
            GetUserResponse userResponse = GetUserResponse.builder()
                    .username(user.getUsername())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .roles(user.getRoles())
                    .build();
            if(authService.getCurrentUser().getUsername().equals(userResponse.getUsername())
                    || authService.getCurrentUser().hasRole("MODERATOR")) {
                return ResponseEntity.ok(userResponse);
            }
            else {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest request, UriComponentsBuilder builder) {
        User user = User.builder()
                .id(request.getId())
                .username(request.getUsername())
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .privileges(request.getPrivileges())
                .build();
        user = userService.createUser(user);

        return ResponseEntity.created(builder.pathSegment("api", "users", "{id}")
                .buildAndExpand(user.getId()).toUri()).build();
    }

    @DeleteMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteUser(@PathVariable(name = "user_id") Long id) {
        Optional<User> userOptional = userService.getUser(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if(authService.getCurrentUser().equals(user) || authService.getCurrentUser().hasRole("MODERATOR")){
                user.getRoles().clear();

                for (Trip trip : user.getEnrolledTrips()) {
                    trip.getParticipants().remove(user);
                }
                user.getEnrolledTrips().clear();

                for (Trip trip : user.getGuidedTrips()) {
                    trip.getTripGuides().remove(user);
                }
                user.getGuidedTrips().clear();

                for (Post post : postService.getPosts()){
                    if(post.getUsername().equals(user.getUsername())){
                        postService.deletePost(post.getId());
                    }
                }

                for (Comment comment : commentService.getComments()){
                    if(comment.getUsername().equals(user.getUsername())){
                        commentService.deleteComment(comment.getId());
                    }
                }
                userService.deleteUser(id);

                return ResponseEntity.accepted().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{user_id}/permission")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> grantPermission(@PathVariable(name = "user_id") Long id, @RequestBody CreatePermissionRequest request) {
        if (this.authService.getCurrentUser().hasRole("MODERATOR")) {
            Role role = this.roleRepository.findByName(request.getPermissionKey()).orElse(null);
            this.userService.addRoleToUser(id, role);
            return ResponseEntity.accepted().build();
        }

        return ResponseEntity.internalServerError().build();
    }

    @DeleteMapping("/{user_id}/permission")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> revokePermission(@PathVariable(name = "user_id") Long id, @RequestBody CreatePermissionRequest request) {
        if (this.authService.getCurrentUser().hasRole("MODERATOR")) {
            Role role = this.roleRepository.findByName(request.getPermissionKey()).orElse(null);
            this.userService.removeRoleFromUser(id, role);
            return ResponseEntity.accepted().build();
        }

        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<GetUserProfileResponse> getUserProfile(@PathVariable("id") Long id) {
        Optional<User> userOptional = userService.getUser(id);
        if (userOptional.isPresent()) {
            if(authService.getCurrentUser().equals(userOptional.get())){
            User user = userOptional.get();
            GetUserProfileResponse userResponse = GetUserProfileResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .profileImageUrl(fileService.getProfileImagesDir(user.getId()))
                    .build();
            return ResponseEntity.ok(userResponse);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/profile")
    public ResponseEntity<Void> updateProfile(
            @PathVariable("id") Long id,
            @ModelAttribute UpdateUserProfileRequest request,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        Optional<User> userOptional = userService.getUser(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (authService.getCurrentUser().equals(user)) {
                if (request.getName() != null && !request.getName().isEmpty()) {
                    user.setName(request.getName());
                }
                if (request.getSurname() != null && !request.getSurname().isEmpty()) {
                    user.setSurname(request.getSurname());
                }

                if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                    User test = this.userService.getUser(request.getEmail()).orElse(null);
                    if (test == null) {
                        user.setEmail(request.getEmail());
                    }
                }
                if (request.getPhoneNumber() != null) {
                    user.setPhoneNumber(request.getPhoneNumber());
                }


                if (profileImage != null) {
                    try {
                        fileService.uploadProfileImage(profileImage, user.getId());
                    } catch (IOException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                }
                userService.saveUser(user);

                return ResponseEntity.noContent().build();
            }
        }

        return ResponseEntity.notFound().build();
    }

}
