package demo;

import runtime.Instrumentable;

public class SimpleDemo
{
    @Instrumentable
    public int abs(int x)
    {
        if (x < 0)
        {
            return -x;
        }
        return x;
    }

    @Instrumentable
    public boolean isSameSign(int x, int y)
    {
        if (x > 0 && y > 0)
        {
            return true;
        }
        else if (x < 0 && y < 0)
        {
            return true;
        }

        return false;
    }

    public String foo()
    {
        return "foo";
    }
}
