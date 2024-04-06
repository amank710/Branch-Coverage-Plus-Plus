package source;

import runtime.Instrumentable;

public class SourceCode {
    @Instrumentable
    public String foo(int number, int a) {
        int x = 42;
        int z = 10;
        int w = 10;
        int y = number;
        int p = a;

        if(z == y) {
            y = 5;
            z = number;
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

}
