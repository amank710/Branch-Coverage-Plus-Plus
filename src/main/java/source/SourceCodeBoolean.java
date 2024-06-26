package source;

import runtime.Instrumentable;

public class SourceCodeBoolean {
    @Instrumentable
    public static Boolean booleanFlags(boolean flag) {

        // No variable tracking
        // output is true and a is false but line 12 is satisfiable and line 14 is not
        // because we never tell z3 what the values are that we are parsing it just
        // takes boolean on face value

        boolean output = true;
        boolean a = false;
//        output = a;

        if (a) {
            output = a;
        } else if (output) {

        }

        if (a) {

        } else {

        }

        // flag has not been set in the func, it is passed
        // as we are trying to do path coverage, we need to be able to
        // include both these paths as satisfiable because we just dont know
        if (flag) {

        } else if (!flag) {

        } else if (flag && !flag) {
            // this condition should not be satisfiable and included in the paths
            // because doesnt matter the value of flag that is passed in, this will
            // never satisfy
        }



//
//        if (!true) { // Not satisfiable
//            output = true;
//        } else if (!true) {
//            output = a;
//        }

        // flag is now set to output. we know the value of output, so going forward
        // this variable should be tracked and both the if and the else conditon should not
        // be in the path
        output = true;
        boolean something = output;
        flag = something; //output = true
        if (flag) { // Satisfiable
            output = true;
        } else { // not satisfiable
            output = false;
        }


        // No variable tracking integer comparison examples below
        int abc = 5;

        if (5 == 5) { // Satisfiable
            output = a;
        } else {
            output = true;
        }

        if (abc != abc) { // Unsatisfiable
            output = a;
        } else {
            output = true;
        }

        if (abc == abc && 4 > 5) { // Unsatisfiable
            output = a;
        } else {
            output = true;
        }

//        if (flag) { // satisfiable
//            output = true;
//        } else if(flag && !flag ) { // Not satisfiable
//            output = false;
//        } else {
//            output = false;
//        }
//
//        if (flag) { // satisfiable
//            output = true;
//        } else if( true ) { //  satisfiable
//            output = false;
//        } else {  //Not satisfiable
//            output = false;
//        }
//
//
//
//
//        if (flag && false) { // Not satisfiable
//            output = true;
//        } else {
//            output = false;
//        }
//
//        if (flag && flag) { // Satisfiable
//            output = true;
//        } else {
//            output = false;
//        }
//

        return output;
    }

}
