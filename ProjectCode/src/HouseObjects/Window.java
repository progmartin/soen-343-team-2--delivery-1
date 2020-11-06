package HouseObjects;

/**
 * A class for keeping track of Windows inside a room.
 *
 * @author a_richard, d_ruiz-cigana
 */
public class Window {

    /**
     * Unique identifier for window.
     */
    int id;

    /**
     * Whether or not the window is open.
     */
    boolean open;

    /**
     * Whether or not the window blocked by some object.
     */
    boolean blocked;

    /**
     * The name of the window.
     */
    public String name;

    /**
     * Default constructor.
     */
    public Window() {
        id = 0;
        open = false;
        blocked = false;
        name = "";
    }

    /**
     * Parameterized constructor.
     *
     * @param id new id of the window
     * @param open if the window is open
     * @param blocked if the window is blocked
     */
    public Window(int id, boolean open, boolean blocked, String name) {
        this.id = id;
        this.open = open;
        this.blocked = blocked;
        this.name = name;
    }

    /**
     * Get Window ID.
     *
     * @return the window's unique id
     */
    public int getID() {
        return id;
    }

    /**
     * Get Window open status.
     *
     * @return if the window is open
     */
    public boolean getOpen() {
        return open;
    }

    /**
     * Get Window blocked status.
     *
     * @return if the window is blocked
     */
    public boolean getBlocked() {
        return blocked;
    }

    /**
     * Get Window name.
     *
     * @return the name of the window
     */
    public String getName() {
        return name;
    }

    /**
     * Set ID.
     *
     * @param id new id of the window
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Set open status.
     *
     * @param open if the window is open
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * Set blocked status.
     *
     * @param blocked if the window is blocked
     */
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    /**
     * Set the window name.
     *
     * @param name the name of the window
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Prints the name of the window
     */
    public String toString(){
    	return name;
    }

}
