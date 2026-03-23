import java.util.*;

/**
 * BookMyStayApp
 * UC1–UC9 Implementation
 */

// =========================
// Abstract Room
// =========================
abstract class Room {
    protected String type;
    protected int beds;
    protected double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public abstract void displayDetails();
}

// =========================
// Room Types
// =========================
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000.0);
    }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500.0);
    }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000.0);
    }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

// =========================
// Reservation
// =========================
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void confirm(String roomId) {
        this.roomId = roomId;
        System.out.println("Reservation confirmed for " + guestName +
                " | Room Type: " + roomType +
                " | Room ID: " + roomId);
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName +
                " | Room Type: " + roomType +
                " | Room ID: " + roomId);
    }
}

// =========================
// Inventory
// =========================
class RoomInventory {
    private HashMap<String, Integer> availability = new HashMap<>();

    public void addRoomType(String roomType, int count) {
        availability.put(roomType, count);
    }

    public boolean isAvailable(String roomType) {
        return availability.getOrDefault(roomType, 0) > 0;
    }

    public void decrementAvailability(String roomType) {
        int current = availability.getOrDefault(roomType, 0);

        if (current <= 0) {
            System.out.println("Error: Cannot reduce inventory below zero for " + roomType);
            return;
        }

        availability.put(roomType, current - 1);
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public void displayInventory() {
        System.out.println("\n=== Current Room Inventory ===");
        for (String roomType : availability.keySet()) {
            System.out.println(roomType + " | Available: " + availability.get(roomType));
        }
    }
}

// =========================
// Search Service
// =========================
class RoomSearchService {
    private RoomInventory inventory;

    public RoomSearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void searchAvailableRooms(Room[] rooms) {
        System.out.println("\n=== Available Rooms ===");
        for (Room room : rooms) {
            int availability = inventory.getAvailability(room.getType());
            if (availability > 0) {
                room.displayDetails();
                System.out.println("Available: " + availability);
            }
        }
    }
}

// =========================
// UC9: Custom Exception
// =========================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// =========================
// UC9: Validator
// =========================
class BookingValidator {
    private Set<String> validRoomTypes;

    public BookingValidator(Set<String> validRoomTypes) {
        this.validRoomTypes = validRoomTypes;
    }

    public void validate(Reservation reservation, RoomInventory inventory)
            throws InvalidBookingException {

        if (reservation == null) {
            throw new InvalidBookingException("Reservation cannot be null");
        }

        if (reservation.getGuestName() == null || reservation.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name is required");
        }

        if (!validRoomTypes.contains(reservation.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + reservation.getRoomType());
        }

        if (!inventory.isAvailable(reservation.getRoomType())) {
            throw new InvalidBookingException(
                    "Room not available for type: " + reservation.getRoomType());
        }
    }
}

// =========================
// Booking Service
// =========================
class BookingService {
    private RoomInventory inventory;
    private Queue<Reservation> requestQueue = new LinkedList<>();
    private BookingValidator validator;

    public BookingService(RoomInventory inventory, BookingValidator validator) {
        this.inventory = inventory;
        this.validator = validator;
    }

    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
    }

    public Reservation processNextRequest() {
        Reservation reservation = requestQueue.poll();

        if (reservation == null) {
            System.out.println("No pending requests.");
            return null;
        }

        try {
            // UC9 validation
            validator.validate(reservation, inventory);

            String roomId = UUID.randomUUID().toString();
            inventory.decrementAvailability(reservation.getRoomType());
            reservation.confirm(roomId);

            return reservation;

        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed: " + e.getMessage());
            return null;
        }
    }
}

// =========================
// UC7: Add-On Service
// =========================
class AddOnService {
    private String name;
    private double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public void display() {
        System.out.println(name + " | ₹" + cost);
    }

    public double getCost() {
        return cost;
    }
}

// =========================
// UC7: Add-On Manager
// =========================
class AddOnServiceManager {
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        serviceMap.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }

    public void displayServices(String reservationId) {
        List<AddOnService> services = serviceMap.getOrDefault(reservationId, new ArrayList<>());

        if (services.isEmpty()) {
            System.out.println("No add-ons for " + reservationId);
            return;
        }

        double total = 0;
        System.out.println("\nAdd-ons for Reservation ID: " + reservationId);

        for (AddOnService s : services) {
            s.display();
            total += s.getCost();
        }

        System.out.println("Total Add-On Cost: ₹" + total);
    }
}

// =========================
// UC8: Booking History
// =========================
class BookingHistory {
    private List<Reservation> confirmedBookings = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        if (reservation != null) {
            confirmedBookings.add(reservation);
        }
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(confirmedBookings);
    }

    public void displayHistory() {
        System.out.println("\n=== Booking History ===");

        if (confirmedBookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }

        for (Reservation r : confirmedBookings) {
            r.displayReservation();
        }
    }
}

// =========================
// UC8: Reporting
// =========================
class BookingReportService {
    public void generateSummary(List<Reservation> reservations) {
        System.out.println("\n=== Booking Summary Report ===");

        if (reservations.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        Map<String, Integer> countMap = new HashMap<>();

        for (Reservation r : reservations) {
            countMap.put(
                    r.getRoomType(),
                    countMap.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        for (String type : countMap.keySet()) {
            System.out.println(type + " booked: " + countMap.get(type));
        }

        System.out.println("Total Bookings: " + reservations.size());
    }
}

// =========================
// Main Application
// =========================
public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("Welcome to Hotel Booking System");

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        // Room Types (UC9 validation)
        Set<String> validRoomTypes = new HashSet<>();
        validRoomTypes.add("Single Room");
        validRoomTypes.add("Double Room");
        validRoomTypes.add("Suite Room");

        BookingValidator validator = new BookingValidator(validRoomTypes);

        // Rooms
        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };

        // Search
        RoomSearchService search = new RoomSearchService(inventory);
        search.searchAvailableRooms(rooms);

        // Booking Service
        BookingService bookingService = new BookingService(inventory, validator);

        // Requests (including invalid ones for UC9 demo)
        bookingService.addRequest(new Reservation("Lakshmi", "Single Room"));
        bookingService.addRequest(new Reservation("", "Suite Room")); // invalid
        bookingService.addRequest(new Reservation("Ravi", "Deluxe Room")); // invalid
        bookingService.addRequest(new Reservation("Priya", "Double Room"));

        // UC8: History
        BookingHistory history = new BookingHistory();

        // Process bookings
        Reservation r1 = bookingService.processNextRequest();
        Reservation r2 = bookingService.processNextRequest();
        Reservation r3 = bookingService.processNextRequest();
        Reservation r4 = bookingService.processNextRequest();

        history.addReservation(r1);
        history.addReservation(r4);

        // UC7: Add-ons
        AddOnServiceManager manager = new AddOnServiceManager();
        AddOnService breakfast = new AddOnService("Breakfast", 500);
        AddOnService spa = new AddOnService("Spa", 1500);

        if (r1 != null) {
            manager.addService(r1.getRoomId(), breakfast);
            manager.addService(r1.getRoomId(), spa);
            manager.displayServices(r1.getRoomId());
        }

        // UC8 Outputs
        history.displayHistory();

        BookingReportService report = new BookingReportService();
        report.generateSummary(history.getAllReservations());

        // Final Inventory
        inventory.displayInventory();
    }
}