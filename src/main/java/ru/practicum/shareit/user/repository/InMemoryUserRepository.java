package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);

        return user;
    }

    public Optional<User> findUser(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public User updateUser(User user, Long userId) {
        users.put(userId, user);
        return users.get(user.getId());
    }

    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    public boolean checkEmail(String email) {
        long count = users.values()
                .stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .count();

        return count > 0;
    }

}
