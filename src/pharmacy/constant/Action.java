package constant;

public enum Action {
    LOGIN("Login"),
    LOGOUT("Logout"),
    RECORD_TRANSACTION("Record Transaction"),
    SEARCH_SALE("Search Sale"),
    SEARCH_OWN_SALE("Search Own Sale"),
    VIEW_OWN_SALES("View Own Sales"),
    VIEW_ALL_SALES("View All Sales"),
    GENERATE_REPORTS("Generate Report"),
    VIEW_ALL_MEDICINE("View All Medicine"),
    ADD_MEDICINE("Add Medicine"),
    SEARCH_MEDICINE("Search Medicine"),
    UPDATE_MEDICINE("Update Medicine"),
    DELETE_MEDICINE("Delete Medicine"),
    ADD_USER("Add User"),
    VIEW_ALL_USERS("View All Users"),
    SEARCH_USER("Search User"),
    UPDATE_USER("Update User"),
    DELETE_USER("Delete User"),
    CHANGE_OWN_PASSWORD("Change My Password");

    private final String label;
    private Action(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}