/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sf.odinms.client.NinjaMS;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class BashBot {

    public static List<String> getQuotes() {
        List<String> ff = new LinkedList<String>();
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.bash.org/?random").openConnection();

            StringBuilder sb = new StringBuilder();
            con.connect();
            InputStream input = con.getInputStream();
            byte[] buf = new byte[2048];
            int read;
            while ((read = input.read(buf)) > 0) {
                sb.append(new String(buf, 0, read));
            }
            final String find = "<p class=\"qt\">";
            int firstPost = sb.indexOf(find);

            StringBuilder send = new StringBuilder();

            for (int i = firstPost + find.length(); i < sb.length(); i++) {
                char ch = sb.charAt(i);
                if (ch == '<') {
                    if (sb.charAt(i + 1) == '/') {
                        if (sb.charAt(i + 2) == 'p') {
                            break;
                        }
                    }
                }
                if (ch == '<') {
                    if (sb.charAt(i + 1) == 'b') {
                        if (sb.charAt(i + 2) == 'r') {
                           ff.add(ignore(send.toString()));
                            send = new StringBuilder();
                        }
                    }
                    send.append(ch);
                }
            }

            input.close();
            con.disconnect();
        } catch (Exception e) {
            System.err.println("[Bash Bot] There has been an error displaying the Bash.");
            e.printStackTrace();
        }


        return ff;
    }

    public static String ignore(String in) {
        in = in.replaceAll(Pattern.quote("&quot;"), "");
        in = in.replaceAll(Pattern.quote("&amp;"), "");
        in = in.replaceAll(Pattern.quote("<br />"), "");
        in = in.replaceAll(Pattern.quote("&lt;"), "<");
        in = in.replaceAll(Pattern.quote("&gt;"), ">");
        return in;
    }
}
