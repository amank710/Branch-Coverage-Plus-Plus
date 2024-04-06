package demo1;

import runtime.*;

class Class1
{
    @Instrumentable
    public void foo(int x)
    {
        x = 3;
        if (x < 4)
        {
            System.out.println("x is less than 4");
        }
        else
        {
            System.out.println("x is greater than or equal to 4");
        }
    }
}
