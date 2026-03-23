import java.util.*;

/**
 * BookMyStayApp
 * UC1–UC7 Implementation
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

    public String getRoomId() {   // ✅ UC7 support
        return roomId;
    }

    public void confirm(String roomId) {
        this.roomId = roomId;
        System.out.println("Reservation confirmed for " + guestName +
                " | Room Type: " + roomType +
                " | Room ID: " + roomId);
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + " | Room Type: " + roomType);
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
        if (isAvailable(roomType)) {
            availability.put(roomType, availability.get(roomType) - 1);
        }
    }

    public int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    public void displayInventory() {
        System.out.println("=== Current Room Inventory ===");
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
        System.out.println("=== Available Rooms ===");
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
// Booking Queue
// =========================
class BookingRequestQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();

    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Booking request added for " + reservation.getGuestName());
    }

    public void displayQueue() {
        System.out.println("=== Booking Requests ===");
        for (Reservation r : requestQueue) {
            r.displayReservation();
        }
    }
}

// =========================
// Booking Service
// =========================
class BookingService {
    private RoomInventory inventory;
    private Queue<Reservation> requestQueue = new LinkedList<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
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

        String roomType = reservation.getRoomType();

        if (inventory.isAvailable(roomType)) {
            String roomId = UUID.randomUUID().toString();
            inventory.decrementAvailability(roomType);
            reservation.confirm(roomId);
            return reservation; // ✅ return for UC7 usage
        } else {
            System.out.println("Room not available for " + reservation.getGuestName());
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

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public void display() {
        System.out.println(name + " | ₹" + cost);
    }
}

// =========================
// UC7: Add-On Manager
// =========================
class AddOnServiceManager {
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        serviceMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public void displayServices(String reservationId) {
        List<AddOnService> services = serviceMap.getOrDefault(reservationId, new ArrayList<>());

        if (services.isEmpty()) {
            System.out.println("No add-ons for " + reservationId);
            return;
        }

        double total = 0;
        System.out.println("Add-ons for Reservation ID: " + reservationId);

        for (AddOnService s : services) {
            s.display();
            total += s.getCost();
        }

        System.out.println("Total Add-On Cost: ₹" + total);
    }
}

// =========================
// Main App
// =========================
public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("Welcome to Hotel Booking System");

        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };

        RoomSearchService search = new RoomSearchService(inventory);
        search.searchAvailableRooms(rooms);

        BookingService bookingService = new BookingService(inventory);

        bookingService.addRequest(new Reservation("Lakshmi", "Single Room"));
        bookingService.addRequest(new Reservation("Akash", "Suite Room"));

        // Process bookings
        Reservation r1 = bookingService.processNextRequest();
        Reservation r2 = bookingService.processNextRequest();

        // =========================
        // UC7 Usage
        // =========================
        AddOnServiceManager manager = new AddOnServiceManager();

        AddOnService breakfast = new AddOnService("Breakfast", 500);
        AddOnService spa = new AddOnService("Spa", 1500);

        if (r1 != null) {
            manager.addService(r1.getRoomId(), breakfast);
            manager.addService(r1.getRoomId(), spa);
            manager.displayServices(r1.getRoomId());
        }

        System.out.println("\nFinal Inventory:");
        inventory.displayInventory();
    }
}