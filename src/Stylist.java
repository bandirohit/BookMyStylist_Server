
/**
 * MDT final project server program
 *
 * @author Rohit Bandi
 */
import java.util.HashMap;

/**
 * Stylist class contains details of the stylist.
 *
 */
public class Stylist {

    /**
     * Name of the stylist
     */
    private String name;
    /**
     * Area stylist like to work
     */
    private String area;
    /**
     * timeSlots is a array which contains availability of a stylist If stylist
     * is booked for particular time slot, corresponding element in the array is
     * marked as 1
     */
    private int[] timeSlots;

    /**
     * Constructor of stylist class.
     *
     * @param name name of the stylist
     * @param area area in which stylist choose to work
     */
    public Stylist(String name, String area) {
        this.name = name;
        this.area = area;
        timeSlots = new int[8];
    }

    /**
     * getTimeSlots give the time slots of the stylist.
     *
     * @return timeSlots array
     */
    public int[] getTimeSlots() {
        return timeSlots;
    }

    /**
     * getArea is getter method for area.
     *
     * @return area in which stylist choose to work
     */
    public String getArea() {
        return area;
    }

    /**
     * getName getter method for name.
     *
     * @return name of the stylist
     */
    public String getName() {
        return name;
    }

    /**
     * BookTimeSlot method marks the timeSlots array for a given index.
     *
     * @param i index of the array we want to mark
     */
    public void BookTimeSlot(int i) {
        System.out.println("Booked" + name);
        timeSlots[i] = 1;
    }

    /**
     * numBookedSlots method returns the number of slots stylist got booked
     *
     * @return number of slots stylist got booked
     */
    public int numBookedSlots() {
        int count = 0;
        for (int i = 0; i < timeSlots.length; i++) {
            if (timeSlots[i] == 1) {
                count++;
            }
        }
        return count;
    }

    /**
     * freeSlots checks calculates the free slots of a stylists, and assigns
     * each slot with its adjacency.
     *
     * @param numslots number of slots we want to book
     * @return HashMap which contains slot and adjacency of the slot.
     */
    public HashMap<Integer, String> freeSlots(int numslots) {
        HashMap<Integer, String> freeSlots = new HashMap<>();
        for (int i = 0; i <= timeSlots.length - numslots; i++) {
            if (timeSlots[i] == 0) {
                //check if there are enough slots
                int flag_enoughLength = 1;
                for (int j = i; j < i + numslots; j++) {
                    if (timeSlots[j] == 1) {
                        flag_enoughLength = 0;
                        break;
                    }
                }
                //if there are enough slots check if it has adj slots booked
                if (flag_enoughLength == 1) {
                    int flag_adj = 0;
                    if (i != 0) {
                        if (timeSlots[i - 1] == 1) {
                            flag_adj = 1;
                        }

                    }
                    if (i + numslots != timeSlots.length) {
                        if (timeSlots[i + numslots] == 1) {
                            flag_adj = 1;
                        }
                    }
                    switch (flag_adj) {
                        case 2:
                            freeSlots.put(i, "corner");
                            break;
                        case 1:
                            freeSlots.put(i, "yes");
                            break;
                        default:
                            freeSlots.put(i, "no");
                            break;
                    }
                }
            }

        }
        return freeSlots;
    }
}
