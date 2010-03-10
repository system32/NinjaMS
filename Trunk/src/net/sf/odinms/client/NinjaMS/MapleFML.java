/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.odinms.client.NinjaMS;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;

public class MapleFML {
    public static String getFML() {
        String fmlmsg = "";
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://feeds2.feedburner.com/fmylife").openConnection();
            Scanner fml = new Scanner(con.getInputStream());
            ArrayList<String> fmls = new ArrayList<String>();
            while(fml.hasNextLine())
            {
            	String nextLine = fml.nextLine();
            	if(nextLine.contains("Today, "))
            	{
            		String[] fmltmp = nextLine.split("Today, ");
           			fmltmp[1] = fmltmp[1].split("FML")[0];
           			fmls.add(fmltmp[1]);
            	}
            }
            String sendTxt = fmls.get((int)(Math.random()*fmls.size()));
            fmlmsg = "[FML] Today, " + sendTxt + "FML";            
            con.disconnect();
        } catch (Exception e) {
            System.err.println("[FML Bot] There has been an error displaying the FML.");
            e.printStackTrace();
        }

        return fmlmsg;
    }
}