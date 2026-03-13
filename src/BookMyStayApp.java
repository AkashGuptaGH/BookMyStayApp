import java.util.*;

/**
 * BookMyStayApp
 * Consolidated application implementing UC1–UC6:
 *  - UC1: Application entry & welcome message
 *  - UC2: Basic room types & static availability
 *  - UC3: Centralized inventory management
 *  - UC4: Room search & availability check
 *  - UC5: Booking request queue
 *  - UC6: Reservation confirmation & room allocation
 *
 * @version 6.0
 */

// Abstract Room class
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

// Concrete room classes
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000.0);
    }

    @Override
    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500.0);
    }

    @Override
    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000.0);
    }

    @Override
    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

// Reservation class
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId; // assigned upon confirmation

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

// Inventory service
class RoomInventory {
    private HashMap<String, Integer> availability;

    public RoomInventory() {
        availability = new HashMap<>();
    }

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

// Search service (read-only)
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

// Booking Request Queue
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Booking request added for " + reservation.getGuestName());
    }

    public Queue<Reservation> getQueue() {
        return requestQueue;
    }

    public void displayQueue() {
        System.out.println("=== Current Booking Requests (FIFO) ===");
        for (Reservation r : requestQueue) {
            r.displayReservation();
        }
    }
}

// Booking service
class BookingService {
    private RoomInventory inventory;
    private HashMap<String, Set<String>> allocatedRooms;
    private Queue<Reservation> requestQueue;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.allocatedRooms = new HashMap<>();
        this.requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Booking request queued for " + reservation.getGuestName());
    }

    public void processNextRequest() {
        Reservation reservation = requestQueue.poll();
        if (reservation == null) {
            System.out.println("No pending requests.");
            return;
        }

        String roomType = reservation.getRoomType();
        if (inventory.isAvailable(roomType)) {
            String roomId = UUID.randomUUID().toString();
            allocatedRooms.putIfAbsent(roomType, new HashSet<>());
            if (!allocatedRooms.get(roomType).contains(roomId)) {
                allocatedRooms.get(roomType).add(roomId);
                inventory.decrementAvailability(roomType);
                reservation.confirm(roomId);
            }
        } else {
            System.out.println("Sorry, " + reservation.getGuestName() +
                    " | Room Type: " + roomType + " is not available.");
        }
    }
}

// Main application
public class BookMyStayApp {
    public static void main(String[] args) {
        System.out.println("Welcome to Hotel Booking System v1.0");

        // UC3: Centralized inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        // UC2: Room objects
        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };

        // UC4: Search service
        RoomSearchService searchService = new RoomSearchService(inventory);
        searchService.searchAvailableRooms(rooms);

        // UC5: Booking request queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        bookingQueue.addRequest(new Reservation("Lakshmi", "Single Room"));
        bookingQueue.addRequest(new Reservation("Akash", "Suite Room"));
        bookingQueue.addRequest(new Reservation("Priya", "Double Room"));
        bookingQueue.displayQueue();

        // UC6: Booking service with allocation
        BookingService bookingService = new BookingService(inventory);
        bookingService.addRequest(new Reservation("Lakshmi", "Single Room"));
        bookingService.addRequest(new Reservation("Akash", "Suite Room"));
        bookingService.addRequest(new Reservation("Priya", "Double Room"));
        bookingService.addRequest(new Reservation("Ravi", "Double Room")); // should fail if unavailable

        bookingService.processNextRequest();
        bookingService.processNextRequest();
        bookingService.processNextRequest();
        bookingService.processNextRequest();

        // Final inventory state
        System.out.println("\nFinal Inventory:");
        inventory.displayInventory();
    }
}
