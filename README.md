# Branch Coverage++

## Introduction
This project aims to improve your software engineering quality by identifying insufficient tests in your code. Our
project:
    - Allows you to upload your source code and JUnit 5 compatible test suite to our website
    - Analyzes your code and test suite to identify all the possible branches in your code
    - Identifes unsatisfiable branches through your code with variable-tracking and filter these paths out from
      analysis
    - Calculates the branch coverage of your test suite
    - Visualizes the line hit coverage and uncovered paths in your code

## Limitations
- Loop tracking isn't supported
- Recursive tracking isn't supported
- Path pruning based on results from calls to external functions isn't supported
- The conditional expression analysis is limited to boolean and number types
- The backend (spring-boot) needs to be restarted after each analysis

## Input Constraints
- The source code file and test suite file must have the same `package` declaration or have no `package` declaration at all
- The test file must only have dependencies to the source code file, standard Java library or our provided library
- The source code file must only have dependencies to the standard Java library or our provided library
- The test file must have at least one `@Test` method
- Each `@Test` method must only call the function of interest at-most once. Multiple `@Test` methods can call the function of interest
- Each assertion in your test file must pass for the analysis to be successful

## Usage Instructions
1. Prepare the source file

    a. Make sure to include the following import:
    ```java
        import runtime.*;
    ```
    b. For all functions of interest in your source code, add the following annotation:
    ```java
        @Instrumentable
        void foo()
        {
            ...
        }
    ```

2. Prepare the test file

    a. Make sure to include the following imports:
    ```java
        import runtime.*;
        import org.junit.jupiter.api.extension.ExtendWith;
    ```
    b. Above the test class, add the following annotation:
    ```java
        @ExtendWith(InstrumentedTestExtension.class)
    ```
    c. Above the test class, add an annotation for the class of interest. The analysis will identify Instrumentable
       functions in the specified class.
    ```java
        @Instrument(YourClass.class)
    ```

3. Upload the source file and test file to our website:
    a. To upload the source and test file, open `localhost:3000` in your browser.

    b. Upload the code file and test file. Wait for the following messages to appear: `Code file uploaded and saved.`
    and `Test file uploaded and saved.`

    c. Select, the `Process Data` button, which will navigate you to page displaying paths that were missed during the test

    d. In the tab `Uncovered Paths`, you will see specific sequences of paths that were not hit during the analysis.

    e. Using the right-hand navigation bar, in the `Chart` tab, you will see the frequency of how many times each line
    was hit

    f. We have prepared a sample source file and test file for your convenience in `sandbox/test_classes/` called
    `Demo.java` and `DemoTest.java` respectively

       i. In `Demo.foo`, we demonstrate our capability of filtering out unsatisfiable paths. The conditional on line
       10 enforces `x >= 0`, so the conditionals on line 19 and 24 will always be satisfiable. Thus, there are only
       two paths to test. Our test case will only test one of these possible branches and we will show uncovered lines
       in the other branch.
