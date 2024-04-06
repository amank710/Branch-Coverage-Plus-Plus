import runtime.Instrumentable;

class DynamicClass
{
    @Instrumentable
    public void foo()
    {
        boolean x = true;
        if (x)
        {
            System.out.println("x is true");
        }
        else
        {
            System.out.println("x is false");
        }
    }
}
