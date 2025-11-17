package t03_arraylist.demos.de02;
import java.util.ArrayList;
import java.util.List;

public class Demo {
    public static void run()
    {
        new Demo().start();
    }
    public void start()
    {
        ArrayList<String> sList1
                = new ArrayList<>(List.of("a","b","c","d","e"));

        ArrayList<String> sList2
                = new ArrayList<>(List.of("e","d","c","b","a"));
    }

    public void print(ArrayList<String> list, boolean isForward)
    {

    }
}
