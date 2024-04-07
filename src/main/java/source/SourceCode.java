package source;

import runtime.Instrumentable;

public class SourceCode {
    @Instrumentable
    public String foo(int number, int a, boolean bool) {
        int x = 42;
        int z = 10;
        int w = 6;
        int y = number;
        int p = a;
        z /= 16 - w;
        w = 5*6/3;
//        y = (1 + 2);
        boolean b2 = bool;
        boolean aa = true;
        b2 = aa;

        if(z < w) {
            y = 5;
            z = number;
        } else {
            y = 30;
        }



//        Else: {
//            if (aa) {
//                y = 10;
//            } else {
//                y = 20;
//            }
//        }
//
//        if (y > 0) {
//            y = z;
//            x = x + w;
//        } else {
//            //empty
//            y = x;
//        }

//        if (w == 5) { // satisfiable
//            x = 0;
//        } else {  // satisfiable
//            x = 100000;
//        }
//
//        if (x > 10) {
//            y = 10;
//        }

//        System.out.println(x);

        return "done";

    }

    public int trial() {
        int hello_trial = 2;
        return 4;
    }

}
