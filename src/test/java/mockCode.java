import runtime.Instrumentable;

public class mockCode {
    int a = 2;
    boolean b = true;

    @Instrumentable
    public void checkA() {
        if (a > 4) {
            System.out.println("BIG A");
        } else {
            System.out.println("SMALL A");
        }
        System.out.println("End checkA");
    }

    @Instrumentable
    public void checkB() {
        System.out.println("Start checkB");
        if (b) {
            System.out.println("B true to yourself");
        } else {
            System.out.println("B not true to yourself");
        }
    }
}
