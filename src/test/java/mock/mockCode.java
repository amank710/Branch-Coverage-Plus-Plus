package mock;

import runtime.Instrumentable;

public class mockCode {
    int a = 2;
    boolean b = true;

    @Instrumentable
    public String checkA() {
        // should only go down else branch
        if (a > 4) {
            return("BIG A");
        } else {
            return("SMALL A");
        }
    }

    @Instrumentable
    public String checkB() {
        System.out.println("Start checkB");
        if (b) {
           return("B true to yourself");
        } else {
           return("B not true to yourself");
        }
    }
}
