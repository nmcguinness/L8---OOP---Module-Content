package t04_linkedlist.demos.de02;

import java.util.*;

public class Demo {
    public static void run()
    {
        new Demo().start();
    }

    public void start()
    {
//        var linkedList = new LinkedList<String>(List.of("a","b","c"));
//
//        ListIterator<String> iter = linkedList.listIterator();
//
//        iter.next();  //a
//        iter.next();  //b
//        System.out.println(iter.next());  //c
//
//        iter.previous();
//        System.out.println(iter.next()); //c
//
//        iter.previous();
//        iter.set("C");                   //set() sets value in place and doesnt advance iterator
//        System.out.println(iter.next());


//        Deque<String> tasks = new LinkedList<>();
//
//        tasks.offer("load");       // enqueue (tail)
//        tasks.offer("parse");
//
//        tasks.forEach(s -> System.out.println(s));
//
//        String t1 = tasks.poll();  // dequeue (head) → "load"
//
//        tasks.forEach(s -> System.out.println(s));
//
//        String t2 = tasks.peek();  // look at head → "parse"
//
//      //  Iterator<String> iterList = tasks.iterator();
//       // Iterator<String> descIterLit = tasks.descendingIterator();
//
//        tasks.forEach(s -> System.out.println(s));
//

        var letters = new LinkedList<Character>();
        for (char c : "aaabbcc".toCharArray())
            letters.add(c);

        var it = letters.listIterator();
        char prev = 0;
        while (it.hasNext()) {
            char cur = it.next();
            if (prev != 0 && cur != prev) {
                it.previous();    // move back to insertion point
                it.add('|');     // mark boundary by inserting bar character
                it.next();
            }
            prev = cur;
        }

    }
}

