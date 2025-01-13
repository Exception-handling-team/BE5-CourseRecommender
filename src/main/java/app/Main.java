package app;

import java.util.Scanner;

public class Main {
    static final Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        App app = new App(sc);
        app.run();
    }
}
