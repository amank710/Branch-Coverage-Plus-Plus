package runtime;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

/**
 * This method is used to specify one or more methods that should be instrumented.
 */
public @interface Instrumentable {}
