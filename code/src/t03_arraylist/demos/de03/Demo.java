package t03_arraylist.demos.de03;

public class Demo {
    public static void run()
    {
        new Demo().start();
    }

    private void start() {
        Weapon w1 = new Weapon("m1", "mod1",
                50, 14, 50, 8);

        System.out.println(w1); //string-ify the ref and call toString
    }
}
