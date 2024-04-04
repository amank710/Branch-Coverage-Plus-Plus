package mock;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class mockCodeTest {
    mockCode testing = new mockCode();

    @Test
    public void testA() {
        assertSame("SMALL A", testing.checkA());
    }

    @Test
    public void testB() {
        assertSame("B true to yourself", testing.checkB());
    }
}
