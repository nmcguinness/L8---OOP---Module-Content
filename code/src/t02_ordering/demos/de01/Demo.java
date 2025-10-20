package t02_ordering.demos.de01;

import java.util.ArrayList;
import java.util.List;

public class Demo {

    public static void run()
    {
        //ordering = sorting (one or more fields)
        ArrayList<NetworkConnection> netList1 = new ArrayList<>();
        netList1.add(new NetworkConnection("192.1.1.1", 8080, "HTTPS"));

        ArrayList<NetworkConnection> netList2 = new ArrayList<>(
          List.of(
                  new NetworkConnection("A", 8080, "HTTPS"),
                  new NetworkConnection("Z", 20, "FTP"),
                  new NetworkConnection("R", 114, "SMTP"),
                  new NetworkConnection("Q", 80, "HTTP"),
                  new NetworkConnection("B", 21, "FTP")
          )
        );
        netList2.sort(null);
        System.out.println(netList2);
    }
}
