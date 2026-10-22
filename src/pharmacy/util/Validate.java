package util;

import model.User;
import strategy.Action;
import exception.AccessDeniedException;

public class Validate {
    private Validate() {} // Prevents Instantiation

    public static void authorization(User user, Action action) {
        if (user == null || !user.accessStrategy().isAllowed(action)) {
            String roleName = (user == null) ? "Anonymous" : user.getRole().getLabel();
            throw new AccessDeniedException(roleName, action.getLabel());
        }
    }
}