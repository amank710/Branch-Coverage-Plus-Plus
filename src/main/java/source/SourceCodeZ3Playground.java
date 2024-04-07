package source;

import runtime.*;

public class SourceCodeZ3Playground {
    @Instrumentable
    public static Boolean booleanFlags(boolean flag, int ff) {

        boolean output = true;
        boolean a = false;
        int abc = 5;

        if (ff > 2) {
            System.out.println("print");
            if (flag)  {

                if (ff > 5) {
                    if (ff > 7) {
                        output = false;
                    } else {
                        output = false;
                    }
                } else {
                    if (ff < 4) {
                        if (ff == 3) {
                            output = false;
                        } else {
                            output = false;
                        }
                        output = false;
                    } else {
                        output = false;
                    }
                    output = true;
                }
            } else {
                output = false;
            }
            output = true;
        } else {
            output = false;
            if (ff < 1) {
                output = false;
            } else {
                output = false;
                if (ff == 2) {
                    output = false;
                } else {
                    output = true;
                }
            }
        }


        if (output) {
            output = false;
        } else {
            output = true;
        }

        if (abc == 5) {
            output = a;
        } else {
            output = true;
        }

        return output;
    }

}
