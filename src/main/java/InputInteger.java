import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputInteger {

    public static Integer inputInteger() {
        Scanner scannerInput = new Scanner(System.in);
        Integer input = null;
        try {
            input = scannerInput.useDelimiter("\n").nextInt();
            return input;
        } catch (RuntimeException e) {
            System.out.println("Введено нечисловое значение, попробуйте еще раз:");
            input = inputInteger();
        }
        return input;
    }

}
