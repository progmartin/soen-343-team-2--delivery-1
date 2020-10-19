package HouseObjects;

/**
 *
 * @author a_richard, d_ruiz-cigana
 */
//A class for keeping track of Doors attached to rooms
public class Door {

    /**
     * Unique identifier for door.
     */
    int id;

    /**
     * Whether or not door is open.
     */
    boolean open;

    /**
     * Name of the door.
     */
    public String name;

    /**
     * Default constructor.
     */
    public Door() {
        id = 0;
        open = false;
        name = "";
    }

    /**
     * Parameterized constructor.
     *
     * @param id new id of the door
     * @param open if the door is open
     * @param name name of the door
     */
    public Door(int id, boolean open, String name) {
        this.id = id;
        this.open = open;
        this.name = name;
    }

    /**
     * Get method for ID.
     *
     * @return the door's unique id
     */
    public int getID() {
        return id;
    }

    /**
     * Get method for open status.
     *
     * @return if the door is open
     */
    public boolean getOpen() {
        return open;
    }

    /**
     * Get method for the name.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set method for ID.
     *
     * @param id new unique id
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Set method for open status.
     *
     * @param open if the door is open
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * Set method for the name.
     *
     * @param name the name of the door
     */
    public void setName(String name) {
        this.name = name;
    }

}
