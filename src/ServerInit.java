
/**
 * MDT final project server program
 *
 * @author Rohit Bandi
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServerInit is class initializes some stylists and start listening for
 * clients. It's a multi threaded server program, as soon as server accepts a
 * client it forks and start listening for new client.
 *
 */
public class ServerInit {

    /**
     * ArrayList of stylists
     */
    public static ArrayList<Stylist> stylists;

    public static void main(String args[]) {
        /**
         * Initializing some stylists for prototype
         */
        stylistInitialise();
        try {
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            System.out.println("Let the magic begin");
            while (true) {
                //waiting for client to connect
                Socket clientSocket = listenSocket.accept();
                //creating a Connection object which will crete a thread that will handle client connection
                Connection c = new Connection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }

    /**
     * stylistInitialise method creates stylists and add it to stylits array
     * list.
     */
    public static void stylistInitialise() {
        Stylist s1 = new Stylist("Rohit", "Oakland");
        Stylist s2 = new Stylist("Akash", "Oakland");
        Stylist s3 = new Stylist("Ahana", "SquirrelHill");
        Stylist s4 = new Stylist("Swaroop", "SquirrelHill");
        Stylist s5 = new Stylist("Alka", "ShadySide");
        Stylist s6 = new Stylist("Himani", "ShadySide");
        stylists = new ArrayList<>();
        stylists.add(s1);
        stylists.add(s2);
        stylists.add(s3);
        stylists.add(s4);
        stylists.add(s5);
        stylists.add(s6);
    }

    /**
     * GetDiscouns is the method which calculate slot discounts depending on the
     * area and number of slots required
     *
     * @param area area in which service is requested
     * @param numSlots number of slots user requested
     * @return return a array list slotDiscount objects which contain slot
     * discount and stylist details
     */
    public static ArrayList<slotDiscount> getDisconts(String area, int numSlots) {
        ArrayList<slotDiscount> discounts = new ArrayList<>();
        float[] slotDiscounts = new float[8];
        String[] stylist_names = new String[8];
        for (int i = 0; i < 8; i++) {
            slotDiscounts[i] = 0;
            stylist_names[i] = "None";
        }
        ArrayList<Stylist> localStylists = new ArrayList<>();
        //getting stylits of given area
        for (Stylist s : stylists) {
            if (s.getArea().equals(area)) {
                localStylists.add(s);
            }
        }
        /**
         * For each local stylist compute the discount rates for his free slots
         * depending on the adjacency and the number of slots booked, Then
         * getting the maximum slot discount for a slot
         */
        for (Stylist s : localStylists) {
            HashMap<Integer, String> freeSlots = s.freeSlots(numSlots);
            if (freeSlots.isEmpty()) {
                continue;
            }
            /**
             * Discount depending on the number of free booked slots of a
             * stylist We are giving weightage to number of booked slots because
             * we want to fill time slots of a stylist completly before booking
             * a new stylist, Sending one stylist to area is more profitable
             * than sending two stylists to same are for same number of
             * customers
             */
            float bookedSlotsDicount = ((float) (8 - freeSlots.size()) / 8) * 5;
            /**
             * More number of slots a user booked more discounts he gets This is
             * grouping discount in our model
             */
            float durationDiscount = ((float) (numSlots) / 8) * 20;
            //for debugging
            System.out.println("freeSlots of " + s.getName() + " : " + freeSlots.toString() + " bookedSlotsDicount " + bookedSlotsDicount);
            /**
             * depending on the adjancy adding some discount
             */
            for (Integer i : freeSlots.keySet()) {
                float totalDiscount = 0;
                if (freeSlots.get(i).equals("yes")) {
                    totalDiscount = 5 + bookedSlotsDicount + durationDiscount;
                } else if (freeSlots.get(i).equals("corner")) {
                    totalDiscount = 2 + bookedSlotsDicount + durationDiscount;
                } else {
                    totalDiscount = bookedSlotsDicount + durationDiscount;
                }
                //if current discount of this stylist is better than existing slot discounts, replace it with this stylist
                if (slotDiscounts[i] <= totalDiscount) {
                    slotDiscounts[i] = totalDiscount;
                    stylist_names[i] = s.getName();
                }
            }
        }
        //creating discounts arraylist
        for (int i = 0; i < 8; i++) {
            slotDiscount temp = new slotDiscount();
            temp.slot = i;
            temp.name = stylist_names[i];
            temp.discount = slotDiscounts[i];
            discounts.add(temp);
        }
        return discounts;
    }

}

/**
 * slotDicount class holds details of slots
 */
class slotDiscount {

    int slot;
    String name;
    float discount;
}

class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public Connection(Socket aClientSocket) {
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start(); //starting a new thread to handle client connection
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() {
        String location = "";
        int numSlots = 0;
        try {
            location = in.readUTF();
            numSlots = in.readInt();
        } catch (IOException ex) {

        }
        ArrayList<slotDiscount> discounts = ServerInit.getDisconts(location, numSlots);
        System.out.println("Rohit" + numSlots);
        for (slotDiscount sd : discounts) {
            try {
                out.writeInt(sd.slot);
                out.writeInt((int) sd.discount);
                out.writeUTF(sd.name);
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("slot: " + sd.slot + " discount: " + sd.discount + "name: " + sd.name);
        }

        try {
            //waiting for user selection
            //sending it from makebooking servlet
            System.out.println("waiting for user to book a slot");
            int userSelectedSlot = in.readInt();
            System.out.println("users sent booking slot " + userSelectedSlot);
            makeBooking(discounts.get(userSelectedSlot).name, userSelectedSlot, numSlots);
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void makeBooking(String name, int slot, int numSlots) {
        System.out.println("@makeBooking: name : " + name + " slot: " + slot + " numSlots: " + numSlots);
        for (Stylist s : ServerInit.stylists) {
            if (s.getName().equals(name)) {
                System.out.println("@makeBooking name found");
                for (int i = slot; i < slot + numSlots; i++) {
                    s.BookTimeSlot(i);
                }
                return;
            }
        }
    }
}
