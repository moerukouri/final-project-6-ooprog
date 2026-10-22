package ui;

public class Display {
    private Display() {} // Prevents Instantiation
    private static final int PADDING = 3;
    
    public static void println(String string) {
        System.out.println(string);
    } 

    public static void println() {
        System.out.println();
    }
    
    public static void print(String string) {
        System.out.print(string);
    }

    public static void title(String title) {
        println();
        println("=".repeat(PADDING * 2 + title.length()));
        println(" ".repeat(PADDING) + title.toUpperCase() + " ".repeat(PADDING));
        println("=".repeat(PADDING * 2 + title.length()));
        println();
    }

    public static void feedback(String feedback) {
        String[] parts = feedback.split("\n");
        int longestElement = getLongestElementLength(parts);
        
        println();
        println("-".repeat(PADDING * 2 + feedback.length()));

        for (String part : parts) {
            System.out.println(part + " ".repeat(longestElement - part.length()));
        }

        println("-".repeat(PADDING * 2 + feedback.length()));
        println();
    }

    private static int getLongestElementLength(String[] parts) {
        int longestLength = -1;

        for (String part : parts) {
            longestLength = Math.max(longestLength, part.length());
        }

        return longestLength;
    }
}