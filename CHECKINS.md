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
