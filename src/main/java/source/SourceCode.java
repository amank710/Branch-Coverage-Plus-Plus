package source;

import runtime.Instrumentable;

public class SourceCode {
    @Instrumentable
    public String foo(int number, int a) {
        int x = 42;

        int z = 5;
        int w = 14;
        int y = number;
        int p = a;
        y = y + p;//:(
        z = y - a;//:(
        if(y > z) {
            w = 100;
        } else {
            w = 200;
        }
//
//        if (y > 0) {
//            y = z;
//            x = x + w;
//        } else {
//            //empty
//            y = x;
//        }

        if (number == 5) { // satisfiable
            x = 0;
        } else {  // satisfiable
            x = 100000;
        }

        if (x > 10) {
            y = 10;
        }

//        System.out.println(x);

        return "done";

    }

}
