package demo;

import runtime.*;

class Demo
{
    @Instrumentable
    public void foo(int x, int y)
    {
        if (x < 0)
        {
            x = 0;
        }
        else
        {
            x = x + 1;
        }

        if (y > 3 && x < 0)
        {
            y = 3;
        }

        if (x >= 0 || y <= 5)
        {
            x = 1;
            y = 5;
        }
    }

    @Instrumentable
    public int bar(int a, int b)
    {
        int ret = 0;

        if (a < 0) 
        {
            int c = a - 9;
            if (b < 0)
            {
                if (c < 0)
                {
                    ret = 1;
                }
                else
                {
                    ret = 5;
                }
            }
            else
            {
                if (a == 9 && c > 0)
                {
                    ret = -1;
                }
                else
                {
                    ret = 0;
                }
            }
        }
        else
        {
            if (a > 0 && b > 0)
            {
                int d = a + b;
                if ((d-a) != b)
                {
                    ret = -1;
                }
                else
                {
                    ret = 0;
                }
            }
        }

        return ret;
    }
}