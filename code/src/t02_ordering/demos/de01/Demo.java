package t02_ordering.demos.de01;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Demo {

    public static void run()
    {
        //ordering = sorting (one or more fields)
        ArrayList<NetworkConnection> netList = new ArrayList<>(
          List.of(
                  new NetworkConnection("A", 8080, "HTTPS"),
                  new NetworkConnection("Z", 20, "FTP"),
                  new NetworkConnection("R", 114, "SMTP"),
                  new NetworkConnection("Q", 80, "HTTP"),
                  new NetworkConnection("B", 21, "FTP")
          )
        );

        //unsorted
        System.out.println("Unsorted...");
        for(NetworkConnection c: netList)
            System.out.println(c);

        //sort by port comparator defined as external comparator
        System.out.println("Port: sort by external comparator...");
        var portComp = new PortComparator(-1);
        netList.sort(portComp);
        for(NetworkConnection c: netList)
            System.out.println(c);

        //sort by port comparator defined as an anonymous comparator
        System.out.println("Port: sort by external comparator as an anonymous comparator...");
        netList.sort(new Comparator<NetworkConnection>() {
            @Override
            public int compare(NetworkConnection o1, NetworkConnection o2) {
                return Float.compare(o1.getPort(), o2.getPort());
            }
        });
        for(NetworkConnection c: netList)
            System.out.println(c);

        //sort by port comparator defined as a lambda expression
        System.out.println("Port: sort by external comparator as lambda expression...");
        netList.sort((NetworkConnection a, NetworkConnection b) -> Float.compare(a.getPort(), b.getPort()));
        for(NetworkConnection c: netList)
            System.out.println(c);


    }
}
