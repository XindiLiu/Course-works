import java.io.*;
import java.util.Scanner;

/**
 * CashSystem
 */
public class CashSystem {
    public static void main(String[] args) throws IOException {
        ProductMap proMap = new ProductMap();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("products.txt"));
        } catch (Exception e) {
            System.exit(1);
        }
        while (scanner.hasNext()) {
            try {
                String name = scanner.nextLine();
                double price = Double.parseDouble(scanner.nextLine());
                String details = scanner.nextLine();
                proMap.addProduct(new Product(name, price, details));
            } catch (Exception e) {
                break;
            }
        }
        new ProductGUI(proMap);
    }
}
