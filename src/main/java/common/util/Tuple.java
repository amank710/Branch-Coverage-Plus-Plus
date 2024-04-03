package common.util;

public class Tuple<A, B>
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
}
