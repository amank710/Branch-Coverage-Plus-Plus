package source;

class SourceCodeBoolean {
    public static Boolean booleanFlags(boolean flag) {
        boolean output = false;
        if (!true) { // Not satisfiable
            output = true;
        } else if (!true) {
            output = false;
        }

        if (true) { // Satisfiable
            output = true;
        } else {
            output = false;
        }

        if (flag && !flag) { // Not satisfiable
            output = true;
        } else {
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