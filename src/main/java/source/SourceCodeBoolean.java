package source;

class SourceCodeBoolean {
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

        if (!true) { // Not satisfiable
            output = true;
        } else if (!true) {
            output = a;
        }
        flag = output;
        if (true) { // Satisfiable
            output = true;
        } else {
            output = false;
        }

        if (flag) { // satisfiable
            output = true;
        } else if(flag && !flag ) { // Not satisfiable
            output = false;
        } else {
            output = false;
        }

        if (flag) { // satisfiable
            output = true;
        } else if( true ) { //  satisfiable
            output = false;
        } else {  //Not satisfiable
            output = false;
        }




        if (flag && false) { // Not satisfiable
            output = true;
        } else {
            output = false;
        }

        if (flag && flag) { // Satisfiable
            output = true;
        } else {
            output = false;
        }


        return output;
    }

}