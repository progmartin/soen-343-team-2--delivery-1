package HouseObjects;

import java.util.HashMap;
import java.util.Map.Entry;
import simulation.*;

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
     * The permissions of the person for each module and each command for the
     * module. The person can either have access to the command or not.
     */
    private HashMap<Class, HashMap<String, Boolean>> modulePermissions;

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
     * Copy Constructor.
     *
     * @param copy the person to be copied
     */
    public Person(Person copy) {
        this.name = copy.getName();
        this.isAdmin = copy.getIsAdmin();
        this.userType = copy.getUserType();
        this.modulePermissions = new HashMap<>();
        for (Entry<Class, HashMap<String, Boolean>> modulePerms : copy.getPermissions().entrySet()) {
            this.modulePermissions.put(modulePerms.getKey(), new HashMap<>());
            for (Entry<String, Boolean> permissions : modulePerms.getValue().entrySet()) {
                this.updateModulePermission(modulePerms.getKey(), permissions.getKey(), permissions.getValue());
            }
        }
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
        this.modulePermissions = new HashMap<>();
        this.initializeDefaultPermissions(SHC_Module.class);
        this.initializeDefaultPermissions(SHP_Module.class);
    }

    /**
     * Default module permissions for a person based on the their accessibility
     * and their location.
     *
     * @param module module to initial permissions for
     */
    public final void initializeDefaultPermissions(Class<? extends Module> module) {
        Module mod;
        try {
            mod = module.newInstance();
        } catch (Exception ex) {
            System.out.println("Module " + module.getName() + " does not exist");
            return;
        }

        this.modulePermissions.put(module, new HashMap<>());
        if (mod.getName().equals("SHC")) {
            if (this.userType.equals(Person.UserTypes.ADULT) || this.userType.equals(Person.UserTypes.CHILD) || this.userType.equals(Person.UserTypes.GUEST)) {
                for (String command : mod.getCommands()) {
                    this.updateModulePermission(module, command, true);
                }
            } else if (this.userType.equals(Person.UserTypes.STRANGER)) {
                for (String command : mod.getCommands()) {
                    this.updateModulePermission(module, command, false);
                }
            }
        } else if (mod.getName().equals("SHP")) {
            if (this.userType.equals(Person.UserTypes.ADULT) || this.userType.equals(Person.UserTypes.CHILD)) {
                for (String command : mod.getCommands()) {
                    this.updateModulePermission(module, command, true);
                }
            } else if (this.userType.equals(Person.UserTypes.GUEST) || this.userType.equals(Person.UserTypes.STRANGER)) {
                for (String command : mod.getCommands()) {
                    this.updateModulePermission(module, command, false);
                }
            }
        }
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
     * Get type of person.
     *
     * @return the type of person
     */
    public UserTypes getUserType() {
        return userType;
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
     * Sets the permission for the command of the module. If the module does not
     * exist for the person then it will return false.
     *
     * @param module the module class
     * @param command the command of the module
     * @param permission the permission of the command
     * @return true if the module exists and successfully set permission; false
     * otherwise
     */
    public boolean updateModulePermission(Class module, String command, boolean permission) {
        try {
            modulePermissions.get(module).put(command, permission);
        } catch (NullPointerException ex) {
            return false;
        }
        return true;
    }

    public void setModulePermission(HashMap<Class, HashMap<String, Boolean>> modulePermissions) {
        this.modulePermissions = modulePermissions;
    }

    /**
     * Gets the permission for a module and its command. If module or the
     * command cannot be found then it returns false.
     *
     * @param module the module class
     * @param command the command of the module
     * @return true if the person can perform the command; false if not. If the
     * module or the command cannot be found then false is returned
     */
    public boolean getModulePermission(Class module, String command) {
        try {
            return modulePermissions.get(module).get(command);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    /**
     * Returns the permission HashMap.
     *
     * @return the person's permissions
     */
    public HashMap<Class, HashMap<String, Boolean>> getPermissions() {
        return modulePermissions;
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

    /**
     * Prints the person's name
     *
     * @return the string representation of the person
     */
    @Override
    public String toString() {
        return name;
    }

}
