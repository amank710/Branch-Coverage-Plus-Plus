package source;

import runtime.Instrumentable;

public class SourceCode {
    @Instrumentable
    public String foo(int number, int a, boolean bool) {
        int x = 42;
        int z = 10;
        int w = 10;
        int y = number;
        boolean aa = true;


        //working:
//        if(bool) { // this will be executed
//            y = 5;
//            z = number;
//        } else if (a == 10) { // this will be executed
//            y = 10;
//        } else { // not (and (not bool) (= a 10))
//            if(bool) { //
//                y = 5;
//                z = number;
//            } else { // [!bool && a != 10 && !bool]
//                y = 30; // this will be executed
//            }
//        }

//        if(bool) { // [bool] this ill be executed
//            y = 5;
//            z = number;
//        } else if (bool) {
//            y = 10;
//        } else { // n
//            if(bool) { //
//                y = 5;
//                z = number;
//            } else {
//                y = 30; // this will be executed
//            }
//        }

        if(bool) { // this will be executed
            y = 5;
            z = number;
        } else  { // not (and (not bool) (= a 10))
            y = 10;
        }

//        //Not Working
//        if(bool) { // [bool]
//            y = 5;
//            z = number;
//        } else{ // [!bool]
//            y = 5;
//            if(bool) { // [!bool && bool]
//                y = 5;
//                z = number;
//            } else {// (!bool)
//                y = 30;
//            }
//        }

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

}
