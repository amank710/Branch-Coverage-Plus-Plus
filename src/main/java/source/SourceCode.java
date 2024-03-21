package source;

class SourceCode {
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