import java.util.Scanner;

public class App {
    public App() {
    }

    public static void main(String[] args) throws Exception {
        String key;
        String value;
        CacheItem item;

        CacheService.setMaximunitems(10);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Select Option: 1 insert cache 2 get cache 3 Exit");
            String input = scanner.nextLine();
            switch (input) {
            case "1":
                System.out.println("Enter key:");
                key = scanner.nextLine();
                System.out.println("Enter value");
                value = scanner.nextLine();
                item = new CacheItem(key, value, 1);
                CacheService.PutCache(item);
                break;

            case "2":
                System.out.println("Enter key:");
                key = scanner.nextLine();
                item = (CacheItem) CacheService.GetCache(key, null);
                if (item == null) {
                    System.out.println("Cache miss");
                } else {
                    System.out.println(item.GetValue());
                }
                break;

            case "3":
                scanner.close();
                System.exit(0);
                break;
            }
        }

    }
}
