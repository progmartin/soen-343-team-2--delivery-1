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
     * @param userType the type 
     */
    public Person(String name, boolean isAdmin, UserTypes userType) {
        this.name = name;
        this.isAdmin = isAdmin;
        this.userType = userType;
    }

    //get methods
    /**
     * Get Name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get Admin Status
     *
     * @return isAdmin
     */
    public boolean getIsAdmin() {
        return isAdmin;
    }

    public String getUserType() {
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

    //set methods
    /**
     * Set Name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set Admin Status
     *
     * @param isAdmin
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setUserType(UserTypes userType) {
        this.userType = userType;
    }

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

    @Override
    public boolean equals(Object obj) {
        return this.getName().equals(((Person) obj).getName());
    }

}
