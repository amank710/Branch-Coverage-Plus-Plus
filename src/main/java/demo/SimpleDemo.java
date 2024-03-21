package demo;

import runtime.Instrumentable;

public class SimpleDemo
{
    public int abs(int x)
    {
        if (x < 0)
        {
            return -x;
        }
        return x;
    }

    @Instrumentable
    public void test()
    {
        return;
    }

    @Instrumentable
    public String foo()
    {
        return "foo";
    }
}
