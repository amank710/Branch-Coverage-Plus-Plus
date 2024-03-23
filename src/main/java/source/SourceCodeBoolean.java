package source;

class SourceCodeBoolean {
    public static String checkMultiples(boolean flag) {
        StringBuilder output = new StringBuilder();
        output.append(flag);
        if (!true) { // Not satisfiable
            output.append(" is even");
        } else {
            output.append(" is odd");
        }

        if (true) { // Satisfiable
            output.append(" is even");
        } else {
            output.append(" is odd");
        }

        if (flag && !flag) { // Not satisfiable
            output.append(" is even");
        } else {
            output.append(" is odd");
        }


        if (flag && false) { // Not satisfiable
            output.append(" is even");
        } else {
            output.append(" is odd");
        }

        if (flag && flag) { // Satisfiable
            output.append(" is even");
        } else {
            output.append(" is odd");
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