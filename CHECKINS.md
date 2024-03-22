Use this file to commit information clearly documenting your check-ins' content. If you want to store more information/details besides what's required for the check-ins that's fine too. Make sure that your TA has had a chance to sign off on your check-in each week (before the deadline); typically you should discuss your material with them before finalizing it here.

# Check-in 1

### Current Project Ideas

1. Program that finds memory leaks in C code (scope might be too wide)
2. VSCode extension to auto type python files
3. Program to check memory usage of any executable
4. Time Complexity Analyser -> Gives the Big O Time and Space Complexity from a file
5. Loop Error Checker -> Checks if a loops will have index out of bound error
6. Static checker to ensure if all brances are usable
7. Dynamic Analysis: Monitor memory allocation and deallocation at runtime to detect memory leaks in C++/C
8. Static Analysis: analyse code to calculate whatever some relevant metrics would be for complexity (e.g. number of dependencies)
9. Syntax highlighter vscode extension that also checks loop out of bound errors

### Discussions with the TA

1. Next project's program analysis has to involve some sort of control flow analysis
2. Visualization doesn't have to be too substantional (eg. vs code plugin to annotate python types)

### Follow up Tasks

1. Talk to TA about ideas and pick an idea that would match the scope.
2. Look at libraries (that instructors will post on piazza about) which can provide the AST for a language. This is to decide which specific languages we can work on.
3. Distribute TODOs between team members

# Check-in 2

###  Planned Program Analysis

We are going to be anlysing the branch reachability of a users program. We will be performing both static and dynamic analysis for this. We will first run a static analysis to gather all possible paths in the control flow graph (CFG) of the program and then we will perform dynamic analysis to gather which blocks
in the Control Flow Graph that actually ended up being visited.

### Discussions with the TA

1. Discussed the scope and possibilities of multiple possible ideas
2. We got detailed feedback for most ideas after discussion with the professor
3. Looks like our idea is doable with a narrowed down scope of control flow analysis (eg. only integer and boolean analysis)

### Follow up Tasks

1. Figure out how narrow we can make the scope of static analysis. Also decide on how to compare the static and dynamic analysis results.
2. We still need to look at libraries that are posted on Piazza and some other libraries we can find which can provide the AST for a language. This is to decide which specific languages we can work on.

### Planned Division of responsibilities
1. Still don't have an extremely concrete division in mind
2. Thinking of splitting based on who worked on the static checker in project 1
3. Remaining members can work on also choosing intstrumentalisation library for dynamic analyis

### Summary of Progress
Summary is similar to what is described above. We have finalised our idea. We have also started thinking about implementation details and instrumentalisation details based on what we learned in the latest lecture. We will start thinking about lower level implementation details when more is covered in the lectures.

### Roadmap for the future
- Project Due: April 8 (4 weeks or so)
- Week of March 4th → Decide on programming language and library to use. High level segregation of system into components, properly defining what those components will be.
- Week of March 11th → Writing failing tests for components individually + development of core components
- Week of March 18th → Writing failing tests for components individually + development of core components. Core static and dynamic analysis should be completed
- Week of March 25th → Some sort of UI completed, Design revisions and development of CLI + Plan for video
- Week of April 1st → Final UI completion + Testing, Final Video
- Project Check-in 3 (All tasks worked on by all group members):
    - Decision made on which language and framewors. Concrete flow diagram of our system
    - Done a user study for understanding practical use of analysis
    - What tests have been done, and what tests still need to be done
- Project Check-in 4 (All tasks worked on by all group members):
    - Plans for final user study
    - Planned timeline for remaining days
    - Complete core static and dynamic analysis portions of the program
- Project Check-in 5 (All tasks worked on by all group members):
    - User study done for full programming analysis tool
    - Decision on what changes to incorporate from final user study
    - Plans for final video

# Check-in 3

### Mockup of how project is supposed to work
The project will take in as input both a Java program and a test suite written for the Java program. The project would then determine which conditional paths in the Java program are not covered by the tests. 
The sample inputs that we provided for our user study are as follows:\
The sample Java program:
``` 
class Solution {
    public static String checkMultiples(int number) {
        StringBuilder output = new StringBuilder();
        output.append(number);
        if (number % 2 == 0) {
            output.append(" is even");
        } else {
            output.append(" is odd");
        }

        if (isPrime(number)) {
            output.append(", prime");
        }

        if (isFibonacci(number)) {
            output.append(", fib");
        }
        return output.toString().trim();
    }

    // Function to check if a number is prime
    public static boolean isPrime(int num) {
        if (num <= 1)
            return false;

        // Check from 2 to n-1
        for (int i = 2; i < num; i++)
            if (num % i == 0)
                return false;

        return true;
    }

    // Helper function to check if a number is Fibonacci
    public static boolean isFibonacci(int number) {
        return isPerfectSquare(5*number*number + 4) ||
                isPerfectSquare(5*number*number - 4);
    }

    static boolean isPerfectSquare(int x)
    {
        int s = (int) Math.sqrt(x);
        return (s*s == x);
    }

}
```
The sample test suite:
```
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class SolutionTest {
    private Solution solution;

    @Before
    public void initialize() {
        solution = new Solution();
    }
    @Test
    public void testEvenNumber() {
        int number = 4;
        String expectedOutput = number + " is even";
        assertEquals(expectedOutput, Solution.checkMultiples(number));
    }

    @Test
    public void testOddNumber() {
        int number = 9;
        String expectedOutput = number + " is odd";
        assertEquals(expectedOutput, Solution.checkMultiples(number));
    }

    @Test
    public void testEvenFibNumber() {
        int number = 8;
        String expectedOutput = number + " is even, fib";
        assertEquals(expectedOutput, Solution.checkMultiples(number));
    }

    @Test
    public void testOddFibNumber() {
        int number = 9;
        String expectedOutput = number + " is odd";
        assertEquals(expectedOutput, Solution.checkMultiples(number));
    }

    @Test
    public void testOddPrimeNumber() {
        int number = 7;
        String expectedOutput = number + " is odd, prime";
        assertEquals(expectedOutput, Solution.checkMultiples(number));
    }

    @Test
    public void testOddPrimeFibNumber() {
        int number = 13;
        String expectedOutput = number + " is odd, prime, fib";
        assertEquals(expectedOutput, Solution.checkMultiples(number));
    }


// These 2 tests were eliminated so that tests wouldn't cover all paths.

    //@Test
    //public void testEvenPrimeFibNumber() {
      //  int number = 2;
      //  String expectedOutput = number + " is even, prime, fib";
      //  assertEquals(expectedOutput, Solution.checkMultiples(number));
    //}

    //@Test
    //public void testEvenNegativeNumber() {
      //  int number = -2;
      //  String expectedOutput = number + " is even, fib";
      //  assertEquals(expectedOutput, Solution.checkMultiples(number));
    //}

}
```
Our program would recognize that with these 3 conditionals, we would have 8 different paths possible: 
- Even, !Prime, !Fib
- Odd, !Prime, !Fib,
- Even, Prime, !Fib
- Odd, Prime, !Fib,
- Even, !Prime, Fib
- Odd, !Prime, Fib,
- Even, Prime, Fib
- Odd, Prime, Fib
Our project would then check to see if the test suite covers all of these paths. If any of these paths are not covered, then the tool would tell the user which paths are not yet covered by the tests.

### First User Study Results
For the user study, we first showed the participant the Java program and the incomplete test suite. We then asked the participant to identify which paths through the program are not covered and ask them to provide the inputs that would cover those paths.\
After, we did the same thing except provided the user with the information that our analysis tool would provide (the paths that are not yet covered by the tests). We then asked the participant to do the exact same task as before of finding which paths through the program are not covered and what inputs would cover those paths.\
The results of are user study are as follows:
- For the initial task, the participant was able to identify 1 major path case that was not covered (specifically the even, prime, and fibonacci case)
- The participant was not able to identify the path missed in the isPrime statement where the if condition checks for a value
- After providing the user with the expected output of our program analysis tool (line numbers of the path missed), the participant was able to instantly identify the missed test cases 
- The participant found the tool useful, especially in how it might help to reduce the effort ot find edge cases in a program 
- They also asked about what will happen if there is a function inside the if statement(s); would we also consider potential paths that occur in the function call? That is a very good point brought up by the user, and we will likely have to discuss this further within our team to see whether or not this could be considered in scope for our project. 
- They thought it would be nice to have a visualization component where the missed path lines can be highlighted in the code. At the moment, we are considering this to be out of scope for our project. 
- They voiced their preference for having the output of our tool be the contents of the lines where the tests missed a path rather than the line nubers to be more useful and helpful. 
- Lastly, they also mentioned that having test case input, if it is accurate, will also be helpful.

### Any changes to original design 
Confirmed changes made to our original design are: 
- The analysis tool will be done in Java
- We are likely to use JavaParser for generating our AST
- The focus of the program analysis will now be to determine how many of the tests written by a user accurately check every possible path through code that uses conditionals 
- Static analysis will still be on the conditionals code, NOT the test code
- Static analysis will now have to provide an array of paths, where each element of the array is line numbers corresponding to a path through the code 
- The dynamic analysis will now be to use the input provided by the static analysis, run the Java tests that are associated with the conditionals code, and compare which branches are hit by running the tests to the array of paths input

### Progress against the timeline planned in Check-In 2
Based on the timeline we provided in Check-In 2, we have finished what we planned to do by the week of March 4th, but are a bit behind schedule with getting started on the tasks we set out for ourselves for the week of March 11th.\
In terms of the specific goals set out for Check-In 3, we have finished most of the goals. We have decided on a language and framework, and come up with a concrete idea/design for the flow of our how our analysis tool will work. We have also done a user study to provide us with feedback regarding our tool and to come up with a user story of the practical use of our analysis tool. However, we still need to start writing tests for the development of our tool.\
While the Check-in goals for Check-ins 4 and 5 should remain relatively the same, there are some revisions we made to our overall timeline: 
- By the week of March 18th: 
    - Have the repo set-up done 
    - AST implementation done as far as possible 
    - Dynamic implementation started 
        - Dynamic input should be taking in an array of paths, where each element of the array is line numbers corresponding to a path 
- By the week of March 25th: 
    - Most of the static component should be done 
        - Finished the class (array of paths) that will be passed to the dynamic component as input 
    - Figure out how to run a Java test file and detect which lines are covered (for the dynamic component)
- By the week of April 1st: 
    - Have the core of the project done, so that we can spend most of the week doing bug fixes and creating the video

# Check-in 4
### Status of implementation so far
- The repo setup is complete
- The static analysis implemented so far uses the javaparser library to find conditional blocks and create a control flow graph.
- The dynamic analysis is in-progress. A custom test runner for JUnit5 is created to run user tests, finding instrmented classes and functions using Java annotations. Collecting execution information is still in-progress.
- We have changed scope to increase the complexity of the static analysis to include reachability.

### Plans for final user study.
- We plan to have a user study where the user will be given a complicated function with a lot of branch paths. Then, we will give them a limited time to write JUnit tests for their code. Using our prototype, we will see how effective their tests are and survey them on how useful they found the tool. We expect that this user study will be done during the week of April 1st.

### Planned timeline for the remaining days.
- By the end of the week of March 25th:
    - Improve the static analysis to take into account unreachable code and prune them out
    - Track covered lines from unit tests using the custom test runner

- By the end of the week of April 1st, our goals are to:
    - Integrate the static and dynamic components
    - Perform the user study
    - Fix bugs
    - Create the video

### Progress against the timeline planned for your team, including the specific goals you defined (originally as part of Check-in 2) for Check-in 4; any revisions to Check-in 5 goals.
- In the original timeline, we expected to have the core static and dynamic analysis components to be complete by now. We are behind schedule and we need to move this goal to Check-in 5.
- We expect to start working on the user interface by Check-In 5 and completing it during the week of April 1.
- With this timeline, we expect to complete the final user study early in the week of April 1.
