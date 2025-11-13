package t03_arraylist.demos.de01;

import java.util.ArrayList;
import java.util.List;

//Problem: Prettify a list of names as quick as we can
//Learn: Iterator, ListIterator, ArrayList, LinkedList
//Aside: System.nanotime, JUnit + time
public class Demo {

    public static void run() {
       // Demo myDemo = new Demo();
        new Demo().start(); //create an instance and call start
    }
    private void start(){
        ArrayList<String> nList = new ArrayList<>(List.of("eva", "BOB", "fiona", "John"));

        System.out.println("Return a list, use enhanced for loop...");
        ArrayList<String> results1 = prettifyList_ver1(nList);
        results1.forEach((String s) -> System.out.println(s));

        System.out.println("Return a list, use traditional for loop...");
        ArrayList<String> results2 = prettifyList_ver2(nList);
        results2.forEach((String s) -> System.out.println(s));

        System.out.println("Mutate a list, use traditional for loop...");
        prettifyList_ver3(nList);
        nList.forEach((String s) -> System.out.println(s));

    }
    public ArrayList<String> prettifyList_ver1(ArrayList<String> list)      /// eg "BOB" -> "Bob"
    {
        ArrayList<String> prettyList = new ArrayList<>();
        String finalName = "";
        for(String s : list){
            s = s.trim(); //remove spaces
            s = s.toLowerCase(); //lowercase
            finalName = s.substring(0,1).toUpperCase() + s.substring(1);
            prettyList.add(finalName);
        }
        return prettyList.isEmpty() ? null : prettyList;
    }

    public ArrayList<String> prettifyList_ver2(ArrayList<String> list)      /// eg "BOB" -> "Bob"
    {
        ArrayList<String> prettyList = new ArrayList<>();
        String finalName = "";
        String s = "";
        for(int i = 0; i < list.size(); i++){
            s = list.get(i);
            s = s.trim(); //remove spaces
            s = s.toLowerCase(); //lowercase
            finalName = s.substring(0,1).toUpperCase() + s.substring(1);
            prettyList.add(finalName);
        }
        return prettyList.isEmpty() ? null : prettyList;
    }

    public void prettifyList_ver3(ArrayList<String> list)      /// eg "BOB" -> "Bob"
    {
        String finalName = "";
        String s = "";
        for(int i = 0; i < list.size(); i++)
        {
            s = list.get(i);
            s = s.trim();
            finalName = s.substring(0,1).toUpperCase()
                    + s.substring(1).toLowerCase();
            list.set(i, finalName);
        }
    }

}

