import java.util.Scanner;

public class Input {

    public static Integer inputIntegerPositive() {
        Scanner scannerInput = new Scanner(System.in);
        Integer input = null;
        while (input == null) {
            try {
                input = Math.abs(scannerInput.useDelimiter("\n").nextInt());
                return input;
            } catch (RuntimeException e) {
                System.out.println("Введено нечисловое значение, попробуйте еще раз:");
                input = inputIntegerPositive();
            }
        }
        return input;
    }

    public static String inputString() {
        Scanner scannerInput = new Scanner(System.in);
        return scannerInput.useDelimiter("\n").next();
    }

}
