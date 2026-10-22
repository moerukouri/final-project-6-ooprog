package services;

import exception.AuthenticationException;
import model.User;
import repository.UserRepository;

public class AuthService {
    private final UserRepository users;

    public AuthService(UserRepository users) {
        this.users = users;
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new AuthenticationException();
        }

        User user = users.findByUsername(username).orElseThrow(() -> new AuthenticationException());
        if (!user.isActive()) {
            throw new AuthenticationException(username);
        }

        if (!user.checkPassword(password)) {
            throw new AuthenticationException(username);
        }

        return user;
    }
}