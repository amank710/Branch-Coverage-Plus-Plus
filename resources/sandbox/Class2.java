import runtime.*;

class Class2
{
    @Instrumentable
    public void foo(int x)
    {
        if (x < 4)
        {
            System.out.println("x is less than 4");
        }
        else
        {
            System.out.println("x is greater than or equal to 4");
        }
    }

    @Instrumentable
    public void goo(int y)
    {
        if (y > 5) {
            if (y > 7) {
                System.out.println("y is greater than 7");
            } else {
                System.out.println("y is between 5 and 7");
            }
        }
        else if (y < 4)
        {
            System.out.println("y is less than 4");
        }
        else
        {
            System.out.println("y is greater than or equal to 4");
        }
    }
}
