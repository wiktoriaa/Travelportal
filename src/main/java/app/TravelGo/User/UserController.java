package app.TravelGo.User;

import app.TravelGo.User.Auth.AuthService;
import app.TravelGo.User.Role.Role;
import app.TravelGo.User.Role.RoleRepository;
import app.TravelGo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @Autowired
    public UserController(UserService userService, RoleRepository roleRepository, AuthService authService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.authService = authService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody Iterable<GetUserResponse> getAllUsers() {
        List<GetUserResponse> usersWithoutPasswords = new ArrayList<>();

        for (User user : userService.getAllUsers()) {
            GetUserResponse userResponse = GetUserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .privileges(user.getPrivileges())
                    .build();
            usersWithoutPasswords.add(userResponse);
        }

        return usersWithoutPasswords;
    }

    @GetMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getPost(@PathVariable("user_id") Long userId) {
        Optional<User> response = userService.getUser(userId);
        if (response.isPresent()) {
            User user = response.get();
            GetUserResponse userResponse = GetUserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .privileges(user.getPrivileges())
                    .build();
            return ResponseEntity.ok(userResponse);
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
        Optional<User> user = userService.getUser(id);
        if (user.isPresent()) {
            userService.deleteUser(user.get().getId());
            return ResponseEntity.accepted().build();
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
}
