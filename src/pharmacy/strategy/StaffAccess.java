package strategy;
import constant.Action;
import java.util.Set;

public class StaffAccess implements AccessStrategy {
    private static final Set<Action> ALLOWED = Set.of(
            Action.LOGIN,
            Action.RECORD_TRANSACTION,
            Action.SEARCH_MEDICINE,
            Action.VIEW_ALL_MEDICINE,
            Action.VIEW_OWN_SALES,
            Action.LOGOUT
        );
    
    @Override
    public boolean isAllowed(Action action) {
        return ALLOWED.contains(action);
    }
}