package app.TravelGo.User;

import app.TravelGo.User.Role.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Iterable<User> getAllUsers() { return userRepository.findAll(); }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUser(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) { return userRepository.findByUsername(username); }

    public User saveUser(User user) { return userRepository.save(user); }


    public boolean hasRole(Long userId, String searchedRole) {
        User user = this.getUser(userId).get();

        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(searchedRole));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void addRoleToUser(Long userId, Role newRole) {
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            Set<Role> roles = user.getRoles();
            roles.add(newRole);
            userRepository.save(user);
        }
    }

    public void removeRoleFromUser(Long userId, Role role) {
        User user = userRepository.findById(userId).orElse(null);

        if (user != null && user.hasRole(role.getName())) {
            Set<Role> roles = user.getRoles();
            roles.remove(role);
            userRepository.save(user);
        }
    }
}
