package runtime;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

/**
 * This annotation is used to specify one or more classes that should be instrumented.
 */
public @interface Instrument
{
    Class<?>[] value();
}
