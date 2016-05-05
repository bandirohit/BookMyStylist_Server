
import java.util.ArrayList;
import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Rohit
 */
public class Stylist
{
    private String area;
    private int[] timeSlots;
    private String name;
    public Stylist(String name, String area)
    {
        this.name = name;
        this.area = area;
        timeSlots = new int[8];
    }
    public int[] getTimeSlots()
    {
        return timeSlots;
    }
    public String getArea()
    {
        return area;
    }
    public String getName()
    {
        return name;
    }
    public void BookTimeSlot(int i)
    {
        System.out.println("Booked" + name);
        timeSlots[i]=1;
    }
    public int numBookedSlots()
    {
        int count = 0;
        for(int i=0;i<timeSlots.length;i++)
        {
            if(timeSlots[i]==1)
            {
                count++;
            }
        }
        return count;
    }
    public HashMap<Integer,String> freeSlots(int numslots) // check this code
    {
        HashMap<Integer,String> freeSlots = new HashMap<Integer,String>();
        for(int i=0;i<=timeSlots.length-numslots;i++)
        {
            if(timeSlots[i]==0)
            {
                //check if there are enough slots
                int flag_enoughLength=1;
                for(int j=i;j<i+numslots;j++)
                {
                    if(timeSlots[j]==1)
                    {
                        flag_enoughLength = 0;
                        break;
                    }
                }
                //if there are enough slots check if it has adj slots booked
                if(flag_enoughLength == 1)
                {
                   int flag_adj = 0;
                   /*
                   if(i == 0 /*|| i+numslots == timeSlots.length)
                   {
                       //flag_adj = 1;
                       flag_adj =2;
                   }
                   
                   else
                   {
                    */ 
                       if(i!=0)
                       {
                           if(timeSlots[i-1]==1)
                           {
                                flag_adj = 1;
                           }
                           
                       }
                       if(i+numslots != timeSlots.length)
                       {
                           if(timeSlots[i+numslots] ==1)
                           {
                               flag_adj = 1;
                           }
                       }
                   //}
                   if(flag_adj==2)
                   {
                       freeSlots.put(i, "corner");
                   }
                   else if(flag_adj==1)
                   {
                       freeSlots.put(i, "yes");
                   }
                   else
                   {
                       freeSlots.put(i,"no");
                   }
                }
            }
            
        }
        return freeSlots;
    }
}
