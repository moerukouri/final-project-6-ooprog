package strategy;

public interface AccessStrategy {
    public boolean isAllowed(Action action);
}