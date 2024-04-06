package source;

import runtime.Instrumentable;

public class SourceCode {
    @Instrumentable
    public String foo(int number, int a) {
        int x = 42;

        int z = number;
        int w = 10;
        int y = number;
        int p = a;
        //[{[18, 20]=[[19]]}, {[20, 22]=[[21]]}, {[32, 34]=[[33]]}, {[34, 36]=[[35]]}, {[38, 40]=[[39]]}]
        //[{[19, 21]=[[20]]}, {[21, 23]=[[22]]}, {[33, 35]=[[34]]}, {[35, 37]=[[36]]}, {[39, 41]=[[40]]}]
//        y = 5;
//        y = 10 + 2;
//        y = y + p;//:(
//        z = y - a;//:(
        if(z != y) {
            y = 5; // working
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
