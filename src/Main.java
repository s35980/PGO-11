import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DiscountPolicy policy = new LoyaltyDiscountPolicy();
        ReservationService service = new ReservationService(policy);

        initializeData(service);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- MediaLab System Rezerwacji ---");
            System.out.println("1. Wyświetl listę studentów");
            System.out.println("2. Wyświetl cały sprzęt");
            System.out.println("3. Utwórz nową rezerwację");
            System.out.println("4. Zwróć sprzęt");
            System.out.println("5. Wyświetl aktywne rezerwacje");
            System.out.println("6. Wyświetl raport");
            System.out.println("8. Pokaż dostępny sprzęt (posortowany po cenie)");
            System.out.println("7. Zakończ program");
            System.out.print("Wybór: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    service.printStudents();
                    break;
                case "2":
                    service.printEquipment();
                    break;
                case "3":
                    System.out.print("Podaj id studenta: ");
                    String sId = scanner.nextLine();
                    System.out.print("Podaj id sprzętu: ");
                    String eId = scanner.nextLine();
                    System.out.print("Podaj liczbę dni: ");
                    try {
                        int days = Integer.parseInt(scanner.nextLine());
                        service.createReservation(sId, eId, days);
                    } catch (NumberFormatException e) {
                        System.out.println("Błąd: Liczba dni musi być liczbą całkowitą.");
                    } catch (ReservationException e) {
                        System.out.println("BŁĄD REZERWACJI: " + e.getMessage());
                    }
                    break;
                case "4":
                    System.out.print("Podaj id rezerwacji: ");
                    String rId = scanner.nextLine();
                    service.returnEquipment(rId);
                    break;
                case "5":
                    service.printActiveReservations();
                    break;
                case "6":
                    service.printReport();
                    break;
                case "8":
                    service.printAvailableEquipmentSortedByPrice();
                    break;
                case "7":
                    running = false;
                    System.out.println("Zamykanie programu...");
                    break;
                default:
                    System.out.println("Niepoprawny wybór. Spróbuj ponownie.");
            }
        }
        scanner.close();
    }

    private static void initializeData(ReservationService service) {
        service.addStudent(new Student("S001", "Anna Kowalska", "12c", 120));
        service.addStudent(new Student("S002", "Marek Nowak", "12c", 40));
        service.addStudent(new Student("S003", "Julia Zielińska", "13a", 0));

        service.addEquipment(new LaptopSet("E001", "Lenovo ThinkPad Lab", 80, 32, true));
        service.addEquipment(new LaptopSet("E002", "Dell XPS Demo", 100, 16, false));
        service.addEquipment(new CameraKit("E003", "Sony Content Kit", 90, 3, true));
        service.addEquipment(new CameraKit("E004", "Canon Interview Kit", 70, 1, true));
    }
}