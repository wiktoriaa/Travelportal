package app.TravelGo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

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

    public boolean hasRole(Long userId, String searchedRole) {
        User user = this.getUser(userId).get();

        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(searchedRole));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
