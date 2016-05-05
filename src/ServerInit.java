
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rohit
 */
public class ServerInit 
{
    public static ArrayList<Stylist> stylists ;
    public static void main(String args[])
    {
        stylistInitialise();
        try
        {
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            System.out.println("Let the magic begin");
            while (true)
            {
                //waiting for client to connect
                Socket clientSocket = listenSocket.accept();
                //creating a Connection object which will crete a thread that will handle client connection
                Connection c = new Connection(clientSocket);
            }
        }
        catch (IOException e) 
        {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
    public static void stylistInitialise()
    {
       Stylist s1 = new Stylist("Rohit","Oakland");
       Stylist s2 = new Stylist("Akash","Oakland");
       Stylist s3 = new Stylist("Ahana","SquirrelHill");
       Stylist s4 = new Stylist("Swaroop","SquirrelHill");
       Stylist s5 = new Stylist("Alka", "ShadySide");
       Stylist s6 = new Stylist("Himani","ShadySide");
       stylists = new ArrayList<>();
       stylists.add(s1);
       stylists.add(s2);
       stylists.add(s3);
       stylists.add(s4);
       stylists.add(s5);
       stylists.add(s6);
    }
    public static ArrayList<slotDiscount> getDisconts(String area, int numSlots)
   {
       ArrayList<slotDiscount> discounts = new ArrayList<>();
       float[] slotDiscounts = new float[8];
       String[] stylist_names = new String[8];
       for(int i=0;i<8;i++)
       {
           slotDiscounts[i] = 0;
           stylist_names[i] = "None";
       }
       ArrayList<Stylist> localStylists = new ArrayList<>();
       for(Stylist s: stylists)
       {
           if(s.getArea().equals(area))
           {
               localStylists.add(s);
           }
       }
       for(Stylist s: localStylists)
       {
           //int[] timeSlot = s.getTimeSlots();
           HashMap<Integer,String> freeSlots = s.freeSlots(numSlots);
           if(freeSlots.isEmpty())
           {
               continue;
           }
           float bookedSlotsDicount = ((float)(8-freeSlots.size())/8)*5;
           float durationDiscount = ((float)(numSlots)/8)*20;
           System.out.println("freeSlots of " +s.getName() + " : " + freeSlots.toString() + " bookedSlotsDicount " + bookedSlotsDicount);
           for(Integer i: freeSlots.keySet())
           {
               float totalDiscount =0;
               if(freeSlots.get(i).equals("yes"))
               {
                   totalDiscount = 5 + bookedSlotsDicount + durationDiscount;
               }
               else if(freeSlots.get(i).equals("corner"))
               {
                   totalDiscount = 2 + bookedSlotsDicount + durationDiscount;
               }
               else
               {
                   totalDiscount = bookedSlotsDicount + durationDiscount;
               }
               if(slotDiscounts[i]<=totalDiscount)
               {
                   slotDiscounts[i] = totalDiscount;
                   stylist_names[i] = s.getName();
               }
           }
       }
       for(int i=0;i<8;i++)
       {
           slotDiscount temp = new slotDiscount();
           temp.slot =i;
           temp.name =stylist_names[i];
           temp.discount = slotDiscounts[i];
           discounts.add(temp);
       }       
       return discounts;
   }
  
}
class slotDiscount
{
    int slot;
    String name;
    float discount;
}
class Connection extends Thread
{
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;

    public Connection(Socket aClientSocket)
    {
        try 
        {
            clientSocket = aClientSocket;
            in = new DataInputStream( clientSocket.getInputStream());
            out = new DataOutputStream( clientSocket.getOutputStream());
            this.start(); //starting a new thread to handle client connection
        } 
        catch (IOException e)
        {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    @Override
    public void run() 
    {
        String location = "";
        int numSlots = 0;
        try 
        {
            location = in.readUTF();
            numSlots = in.readInt();
        }
        catch (IOException ex)
        {
            
        }
        ArrayList<slotDiscount> discounts = ServerInit.getDisconts(location, numSlots);
        System.out.println("Rohit" + numSlots);
        for(slotDiscount sd: discounts)
        {
            try {
                out.writeInt(sd.slot);
                out.writeInt((int)sd.discount);
                out.writeUTF(sd.name);
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("slot: " + sd.slot + " discount: "+ sd.discount + "name: " + sd.name);
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
    public void makeBooking(String name,int slot,int numSlots)
    {
        System.out.println("@makeBooking: name : " + name + " slot: " + slot + " numSlots: " + numSlots);
        for(Stylist s : ServerInit.stylists)
        {
            if(s.getName().equals(name))
            {
                System.out.println("@makeBooking name found");
                for(int i=slot;i<slot+numSlots;i++)
                {
                    s.BookTimeSlot(i);
                }
                return;
            }
        }
    }
}