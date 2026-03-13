import java.util.*;

/**
 * BookMyStayApp
 * Demonstrates reservation confirmation and safe room allocation.
 *
 * @version 6.0
 */

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

    // Add request to queue
    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Booking request queued for " + reservation.getGuestName());
    }

    // Process next request
    public void processNextRequest() {
        Reservation reservation = requestQueue.poll();
        if (reservation == null) {
            System.out.println("No pending requests.");
            return;
        }

        String roomType = reservation.getRoomType();
        if (inventory.isAvailable(roomType)) {
            // Generate unique room ID
            String roomId = UUID.randomUUID().toString();

            // Ensure uniqueness
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
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        BookingService bookingService = new BookingService(inventory);

        // Guests submit requests
        bookingService.addRequest(new Reservation("Lakshmi", "Single Room"));
        bookingService.addRequest(new Reservation("Akash", "Suite Room"));
        bookingService.addRequest(new Reservation("Priya", "Double Room"));
        bookingService.addRequest(new Reservation("Ravi", "Double Room")); // should fail if unavailable

        // Process requests in FIFO order
        bookingService.processNextRequest();
        bookingService.processNextRequest();
        bookingService.processNextRequest();
        bookingService.processNextRequest();
    }
}
