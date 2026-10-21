package strategy;

public class AdminAccess implements AccessStrategy {
    @Override
    public boolean isAllowed(Action action) {
        return true;
    }
}