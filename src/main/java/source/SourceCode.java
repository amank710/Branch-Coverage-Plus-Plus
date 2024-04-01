package source;

import runtime.Instrumentable;

public class SourceCode {
    @Instrumentable
    public String foo(int number) {
        int x = 42;
        int y = number;
        int z = 5;
        int w = 14;

        if (y > 0) {
            y = z;
            x = x + w;
        } else {
            //empty
            y = x;
        }

        y = x + y;

        if (y <= 0) {
            x = y;
        }
//        System.out.println(x);

        return "done";

    }

}
