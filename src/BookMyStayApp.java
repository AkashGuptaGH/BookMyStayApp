import java.util.*;

class Service {
    private String serviceId;
    private String name;
    private double price;

    public Service(String serviceId, String name, double price) {
        this.serviceId = serviceId;
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " (₹" + price + ")";
    }
}

class Reservation {
    private String reservationId;
    private String guestName;

    public Reservation(String reservationId, String guestName) {
        this.reservationId = reservationId;
        this.guestName = guestName;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }
}

class AddOnServiceManager {

    // reservationId -> list of services
    private Map<String, List<Service>> reservationServices = new HashMap<>();

    // Add service to reservation
    public void addService(String reservationId, Service service) {
        reservationServices
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    // Get services for reservation
    public List<Service> getServices(String reservationId) {
        return reservationServices.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total cost
    public double calculateTotalCost(String reservationId) {
        return getServices(reservationId)
                .stream()
                .mapToDouble(Service::getPrice)
                .sum();
    }

    // Print services
    public void printServices(String reservationId) {
        List<Service> services = getServices(reservationId);

        System.out.println("\nReservation ID: " + reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        System.out.println("Selected Add-on Services:");
        for (Service s : services) {
            System.out.println("- " + s);
        }

        System.out.println("Total Add-on Cost: ₹" + calculateTotalCost(reservationId));
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {

        Reservation reservation = new Reservation("R101", "Lakshmi");

        Service spa = new Service("S1", "Spa", 1500);
        Service breakfast = new Service("S2", "Breakfast", 500);
        Service pickup = new Service("S3", "Airport Pickup", 1000);

        AddOnServiceManager manager = new AddOnServiceManager();

        // Guest selects multiple add-ons
        manager.addService(reservation.getReservationId(), spa);
        manager.addService(reservation.getReservationId(), breakfast);
        manager.addService(reservation.getReservationId(), pickup);

        // Display add-ons + cost
        manager.printServices(reservation.getReservationId());
    }
}