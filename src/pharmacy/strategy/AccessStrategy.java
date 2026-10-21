package strategy;
import constant.Action;

public interface AccessStrategy {
    public boolean isAllowed(Action action);
}