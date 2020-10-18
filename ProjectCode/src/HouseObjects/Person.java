package HouseObjects;

/**
 *
 * @author a_richard
 */
//A class for keeping track of Person objects inside the house
public class Person {

    //individual's name
    String name;
    //true if person is admin
    boolean isAdmin;
    private UserTypes userType;

    public enum UserTypes {

        ADULT, CHILD, GUEST, STRANGER;
    }

    //default constructor
    /**
     * constructor -- default
     */
    public Person() {
        this("Default", false, UserTypes.ADULT);
    }

    //parametrized constructor
    /**
     * constructor -- parametrized
     *
     * @param name
     * @param isAdmin
     * @param userType
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
                return "Adult (family)";
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

}
