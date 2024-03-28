package runtime;

import java.lang.reflect.Method;

class RunnerSingleton
{
    public static Method executable = null;

    public static void main(String[] args)
    {
        System.out.println("RunnerSingleton: main()");
        if (executable != null)
        {
            try
            {
                System.out.println("RunnerSingleton: Invoking executable");
                executable.invoke(null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("RunnerSingleton: executable is null");
        }
    }
}
