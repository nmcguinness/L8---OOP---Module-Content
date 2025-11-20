package t03_arraylist.demos.de03;

import java.util.ArrayList;
import java.util.List;

public class Demo {
    public static void run()
    {
        new Demo().start();
    }

    private void start() {
        Weapon w1 = new Weapon("m1", "mod1",
                50, 14, 50, 8);

        System.out.println(w1); //string-ify the ref and call toString

        //encapsulation (private with G&Ss)

        //polymorphism - use `new` to make a new object and store address in child type
        Weapon w2 = new Grenade("g1", "gmod1",
                33, 1, 100, 1,
                ExplosiveType.Frag, 25);

        Weapon w3 = new MachineGun("ma1", "mamod1", 20, 120, 500, 20,
                25, FireMode.Single);

        ArrayList<Weapon> weaponList = new ArrayList<>(List.of(w1, w2));
    }

    public void print(/**/){
        /// 
    }
}
