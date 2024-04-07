package common.util;

import java.io.Serializable;

public class Tuple<A, B> implements Serializable
{
    private A a;
    private B b;

    public Tuple(A a, B b)
    {
        this.a = a;
        this.b = b;
    }

    public A first()
    {
        return a;
    }

    public B second()
    {
        return b;
    }

    @Override
    public String toString()
    {
        return "(" + a + ", " + b + ")";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof Tuple))
        {
            return false;
        }

        Tuple<A, B> t = (Tuple<A, B>) obj;

        return a.equals(t.first()) && b.equals(t.second());
    }
}
