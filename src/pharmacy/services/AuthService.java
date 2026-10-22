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
            throw new AuthenticationException(username);
        }


        // FIXXXXXXXXXXXXXX 
        User user = users.findByUsername(username).orElseThrow(() -> new AuthenticationException("Invalid username or password."));
        

        // FIXXXXXXXXXXXX
        if (!user.isActive()) {
            throw new AuthenticationException("This account is deactivated. Ask an administrator to reactivate it.");
        }

        if (!user.checkPassword(password)) {
            throw new AuthenticationException(username);
        }

        return user;
    }
}