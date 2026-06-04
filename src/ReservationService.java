import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationService {
    private List<Student> students;
    private List<Equipment> equipmentList;
    private List<Reservation> reservations;
    private DiscountPolicy discountPolicy;
    private int reservationCounter = 1;

    public ReservationService(DiscountPolicy discountPolicy) {
        this.students = new ArrayList<>();
        this.equipmentList = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.discountPolicy = discountPolicy;
    }

    public void addStudent(Student student) { students.add(student); }
    public void addEquipment(Equipment equipment) { equipmentList.add(equipment); }

    public void printStudents() {
        System.out.println("--- Lista Studentów ---");
        for (Student s : students) {
            System.out.printf("[%s] %s | Punkty: %d\n", s.getId(), s.getFullName(), s.getLoyaltyPoints());
        }
    }

    public void printEquipment() {
        System.out.println("--- Lista Sprzętu ---");
        for (Equipment e : equipmentList) {
            System.out.println(e.getDisplayText());
        }
    }

    public Reservation createReservation(String studentId, String equipmentId, int days) throws ReservationException {
        if (days < 1 || days > 14) {
            throw new ReservationException("Liczba dni musi być z zakresu od 1 do 14.");
        }

        Student student = findStudentById(studentId);
        if (student == null) {
            throw new ReservationException("Nie znaleziono studenta o podanym ID: " + studentId);
        }

        Equipment equipment = findEquipmentById(equipmentId);
        if (equipment == null) {
            throw new ReservationException("Nie znaleziono sprzętu o podanym ID: " + equipmentId);
        }

        if (!equipment.isAvailable()) {
            throw new ReservationException("Sprzęt " + equipment.getName() + " nie jest aktualnie dostępny.");
        }

        String resId = String.format("R%03d", reservationCounter++);
        Reservation reservation = new Reservation(resId, student, equipment, days);
        equipment.setAvailable(false);
        reservations.add(reservation);

        System.out.println("Utworzono rezerwację " + resId + ".");
        System.out.println("Sprzęt: " + equipment.getName());
        System.out.printf("Koszt: %.2f PLN\n", reservation.calculateTotalCost(discountPolicy));
        System.out.println("Status: " + reservation.getStatus());

        return reservation;
    }

    public void returnEquipment(String reservationId) {
        Reservation reservation = findReservationById(reservationId);
        if (reservation == null) {
            System.out.println("Błąd: Nie znaleziono rezerwacji o podanym ID.");
            return;
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            System.out.println("Błąd: Ta rezerwacja nie jest już aktywna.");
            return;
        }

        reservation.setStatus(ReservationStatus.RETURNED);
        reservation.getEquipment().setAvailable(true);

        double totalCost = reservation.calculateTotalCost(discountPolicy);
        int pointsGained = (int) (totalCost / 10);
        reservation.getStudent().addLoyaltyPoints(pointsGained);

        System.out.printf("Zwrócono sprzęt. Student otrzymał %d punkty lojalnościowe.\n", pointsGained);
    }

    public void printActiveReservations() {
        System.out.println("--- Aktywne Rezerwacje ---");
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.ACTIVE) {
                System.out.println(r.getDisplayText());
            }
        }
    }

    public void printReport() {
        System.out.println("--- Raport Zakończonych Rezerwacji ---");
        double totalRevenue = 0;

        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.RETURNED) {
                System.out.println(r.getDisplayText());
                totalRevenue += r.calculateTotalCost(discountPolicy);
            }
        }

        System.out.printf("Łączny przychód: %.2f PLN\n", totalRevenue);

        Student topStudent = null;
        for (Student s : students) {
            if (topStudent == null || s.getLoyaltyPoints() > topStudent.getLoyaltyPoints()) {
                topStudent = s;
            }
        }
        if (topStudent != null) {
            System.out.println("Top student (punkty lojalnościowe): " + topStudent.getFullName() +
                    " (" + topStudent.getLoyaltyPoints() + " pkt)");
        }
    }

    public void printAvailableEquipmentSortedByPrice() {
        System.out.println("--- Dostępny sprzęt (od najtańszego) ---");

        List<Equipment> availableSorted = equipmentList.stream()
                .filter(Equipment::isAvailable)
                .sorted(Comparator.comparingDouble(Equipment::calculateDailyPrice))
                .collect(Collectors.toList());

        if (availableSorted.isEmpty()) {
            System.out.println("Brak dostępnego sprzętu w tej chwili.");
            return;
        }

        for (Equipment e : availableSorted) {
            System.out.println(e.getDisplayText());
        }
    }

    private Student findStudentById(String id) {
        return students.stream().filter(s -> s.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    private Equipment findEquipmentById(String id) {
        return equipmentList.stream().filter(e -> e.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    private Reservation findReservationById(String id) {
        return reservations.stream().filter(r -> r.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
}