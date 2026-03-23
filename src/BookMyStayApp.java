import java.util.*;

// ================= ROOM MODEL =================
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

    public abstract void displayDetails();
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 2000.0); }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | ₹" + price);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 3500.0); }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | ₹" + price);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 6000.0); }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | ₹" + price);
    }
}

// ================= RESERVATION =================
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean cancelled = false;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void confirm(String roomId) {
        this.roomId = roomId;
        System.out.println("CONFIRMED → " + guestName + " | " + roomType + " | ID: " + roomId);
    }

    public void cancel() {
        cancelled = true;
        System.out.println("CANCELLED → " + guestName + " | " + roomType);
    }

    public boolean isCancelled() {
        return cancelled;
    }
}

// ================= INVENTORY =================
class RoomInventory {
    private HashMap<String, Integer> availability = new HashMap<>();

    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public boolean isAvailable(String type) {
        return availability.getOrDefault(type, 0) > 0;
    }

    public void decrement(String type) {
        availability.put(type, availability.get(type) - 1);
    }

    public void increment(String type) {
        availability.put(type, availability.getOrDefault(type, 0) + 1);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void show() {
        System.out.println("=== INVENTORY ===");
        for (String k : availability.keySet()) {
            System.out.println(k + " -> " + availability.get(k));
        }
    }
}

// ================= SEARCH =================
class RoomSearchService {
    private RoomInventory inventory;

    public RoomSearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void search(Room[] rooms) {
        System.out.println("=== AVAILABLE ROOMS ===");
        for (Room r : rooms) {
            if (inventory.getAvailability(r.getType()) > 0) {
                r.displayDetails();
            }
        }
    }
}

// ================= BOOKING =================
class BookingService {
    private RoomInventory inventory;
    private Queue<Reservation> queue = new LinkedList<>();
    private HashMap<String, String> allocations = new HashMap<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void addRequest(Reservation r) {
        queue.add(r);
        System.out.println("REQUEST ADDED → " + r.getGuestName());
    }

    public void process() {
        Reservation r = queue.poll();
        if (r == null) {
            System.out.println("No requests.");
            return;
        }

        if (inventory.isAvailable(r.getRoomType())) {
            String id = UUID.randomUUID().toString();
            inventory.decrement(r.getRoomType());
            allocations.put(id, r.getGuestName());
            r.confirm(id);
        } else {
            System.out.println("NOT AVAILABLE → " + r.getGuestName());
        }
    }

    // ================= UC7: CANCEL =================
    public void cancel(String guestName, String roomType) {
        inventory.increment(roomType);
        System.out.println("UC7 CANCELLED → " + guestName + " | " + roomType);
    }
}

// ================= MAIN APP =================
public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("WELCOME TO BOOKMY STAY");

        // UC3 Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("Single Room", 2);
        inventory.addRoomType("Double Room", 1);
        inventory.addRoomType("Suite Room", 1);

        // UC2 Rooms
        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // UC4 Search
        RoomSearchService search = new RoomSearchService(inventory);
        search.search(rooms);

        // UC5 + UC6 Booking
        BookingService service = new BookingService(inventory);

        service.addRequest(new Reservation("Lakshmi", "Single Room"));
        service.addRequest(new Reservation("Akash", "Suite Room"));
        service.addRequest(new Reservation("Priya", "Double Room"));
        service.addRequest(new Reservation("Ravi", "Double Room"));

        service.process();
        service.process();
        service.process();
        service.process();

        // UC7 Cancel example
        service.cancel("Akash", "Suite Room");

        // Final state
        inventory.show();
    }
}