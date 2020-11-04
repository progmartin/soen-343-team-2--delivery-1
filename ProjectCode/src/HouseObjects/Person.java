package HouseObjects;

/**
 * A class for keeping track of Person objects inside the house.
 *
 * @author a_richard, d_ruiz-cigana
 */
public class Person {

    /**
     * Individual's name.
     */
    String name;

    /**
     * If person is an administrator.
     */
    boolean isAdmin;

    /**
     * Type of person in relation to the house. Can be only be of a certain
     * type.
     */
    private UserTypes userType;

    /**
     * Enumeration of the different types of user relations.
     */
    public enum UserTypes {

        ADULT, CHILD, GUEST, STRANGER;
    }

    /**
     * Default constructor.
     */
    public Person() {
        this("Default", false, UserTypes.ADULT);
    }

    /**
     * Parameterized constructor.
     *
     * @param name the name of the person
     * @param isAdmin if the person is an administrator
     * @param userType the type of Person in relation to the house
     */
    public Person(String name, boolean isAdmin, UserTypes userType) {
        this.name = name;
        this.isAdmin = isAdmin;
        this.userType = userType;
    }

    /**
     * Get name of the person.
     *
     * @return the name of the person
     */
    public String getName() {
        return name;
    }

    /**
     * Get administration status.
     *
     * @return if person is an administrator
     */
    public boolean getIsAdmin() {
        return isAdmin;
    }

    /**
     * Get type of person in a String format.
     *
     * @return String format of a type of person
     */
    public String getUserTypeAsString() {
        switch (this.userType) {
            case ADULT:
                return "Adult (Family)";
            case CHILD:
                return "Child (Family)";
            case GUEST:
                return "Guest";
            case STRANGER:
                return "Stranger";

        }
        return null;
    }

    /**
     * Set the name of the person.
     *
     * @param name the name of the person
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set administrator status.
     *
     * @param isAdmin if the user is an administrator
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * Set the type of person in relation of the house.
     *
     * @param userType type of person
     */
    public void setUserType(UserTypes userType) {
        this.userType = userType;
    }

    /**
     * Returns a userType a String representation.
     *
     * @param userType String approximation of a type of person
     * @return the type of person
     */
    public static UserTypes getUserType(String userType) {
        if (userType.toUpperCase().contains("ADULT")) {
            return UserTypes.ADULT;
        } else if (userType.toUpperCase().contains("CHILD")) {
            return UserTypes.CHILD;
        } else if (userType.toUpperCase().contains("GUEST")) {
            return UserTypes.GUEST;
        } else if (userType.toUpperCase().contains("STRANGER")) {
            return UserTypes.STRANGER;
        }
        return null;
    }

    /**
     * Determines if two Persons objects are equivalent. Two Persons are
     * equivalent if and only if their names are equivalent. If the other object
     * is not an instance of Person it returns false.
     *
     * @param obj other Person to compare this Person to
     * @return true if their names are equivalent; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) {
            return false;
        }
        return this.getName().equals(((Person) obj).getName());
    }

}
