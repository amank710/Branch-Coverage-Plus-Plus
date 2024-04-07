package source;

import runtime.Instrumentable;

public class SourceCode {
    @Instrumentable
    public String foo(int number, int a, boolean bool) {
        int x = 42;
        int z = 10;
        int w = 6;
        int y = number;

        //working
//        if(number == 1) {
//            y = 5;
//            z = number;
//        } else {
//            y = 30;
//            if(false) {
//                y = 5;
//                z = number;
//            } else if(a == 1) {
//                y = 30;
//            }
//        }
        //[{[13, 16]=[[14, 15]]}, {[16, 24]=[[17, 18, 22]]}]

//        //working:
//        if(bool) { // this will be executed
//            y = 5;
//            z = number;
//        } else { // not (and (not bool) (= a 10))
//            z = number;
//        }
        //[{[16, 19]=[[17, 18]]}, {[19, 21]=[[20]]}]

        // else if + each condition is dynamic
//        if(bool) { // this will be executed
//            y = 5;
//            z = number;
//        } else if(number == 1) { // not (and (not bool) (= a 10))
//            z = number;
//        } else {
//            z = number;
//        }
        //[{[24, 27]=[[25, 26]]}, {[27, 29]=[[28]]}, {[29, 31]=[[30]]}]

        //else if + but the condition is static true
        //working
//        if(bool) { // this will be executed
//            y = 5;
//            z = number;
//        } else if(true) { // not (and (not bool) (= a 10))
//            z = number;
//        } else {
//            z = number;
//        }
        //[{[37, 40]=[[38, 39]]}, {[40, 42]=[[41]]}]

        //else if + but the condition is static false
        //working
//        if(bool) { // this will be executed
//            y = 5;
//            z = number;
//        } else if(false) { // not (and (not bool) (= a 10))
//            z = number;
//        } else {
//            z = number;
//        }
        //[{[49, 52]=[[50, 51]]}, {[54, 56]=[[55]]}]

//        //working
//        if(true) { // this will be executed
//            y = 5;
//            z = number;
//        } else if(false) { // not (and (not bool) (= a 10))
//            z = number;
//        } else {
//            z = number;
//        }
        //[{[58, 61]=[[59, 60]]}]

        //working
//        if(bool) { // [bool]
//            y = 5;
//            z = number;
//        } else if(number == 1) { // [!bool && (= number 1)]
//            z = number;
//        } else if(bool) { // [!bool && ! (= number 1) && bool]
//            z = number;
//        } else {
//            z = number;
//        }
        //[{[71, 74]=[[72, 73]]}, {[74, 76]=[[75]]}, {[78, 80]=[[79]]}]


        //WOrking
//
        if(false) { // [bool] this ill be executed
            y = 5;
            z = number;
        } else if (false) {// [!bool && bool]
            y = 10; // not be executed
        } else  {
            y = 30; // not be executed
            if(false) { // [!bool && !bool && bool]
                y = 5;
                z = number;
            } else {//(and (not (bool)) (not (= number 1)) (= a 1))
                y = 30; // this will not be executed
            }
        }

//        if(true) { // this will be executed
//            y = 10;
//            if(false) { // this will be executed
//                y = 5;
//                z = number;
//            } else if(true) { // this will be executed
//                y = 20;
//            }
//            y = 10;
//        }

//        if(true) { // this will be executed
//            y = 10;
//        } else if(true) {
//            y = 20;
//        }
//
//        if(bool) { // this will be executed
//            y = 10;
//        } else if(number==1) {
//            y = 20;
//        }


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

    public int trial() {
        int hello_trial = 2;
        return 4;
    }

}
