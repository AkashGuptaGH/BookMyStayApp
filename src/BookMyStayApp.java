import java.util.*;

// ================= CUSTOM EXCEPTION =================
class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}

// ================= ROOM =================
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

    public abstract void display();
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 2000); }

    public void display() {
        System.out.println(type + " | Beds: " + beds + " | ₹" + price);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 3500); }

    public void display() {
        System.out.println(type + " | Beds: " + beds + " | ₹" + price);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 6000); }

    public void display() {
        System.out.println(type + " | Beds: " + beds + " | ₹" + price);
    }
}

// ================= RESERVATION =================
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void confirm(String roomId) {
        this.roomId = roomId;
        System.out.println("CONFIRMED → " + guestName + " | " + roomType + " | " + roomId);
    }
}

// ================= INVENTORY =================
class RoomInventory {
    private Map<String, Integer> stock = new HashMap<>();

    public void addRoom(String type, int count) {
        stock.put(type, count);
    }

    public boolean exists(String type) {
        return stock.containsKey(type);
    }

    public boolean available(String type) {
        return stock.getOrDefault(type, 0) > 0;
    }

    public void decrease(String type) throws BookingException {
        if (!exists(type)) {
            throw new BookingException("Invalid room type: " + type);
        }
        if (stock.get(type) <= 0) {
            throw new BookingException("No availability for: " + type);
        }
        stock.put(type, stock.get(type) - 1);
    }

    public void increase(String type) {
        stock.put(type, stock.getOrDefault(type, 0) + 1);
    }

    public void show() {
        System.out.println("\n=== INVENTORY ===");
        stock.forEach((k, v) -> System.out.println(k + " → " + v));
    }
}

// ================= SEARCH =================
class SearchService {
    private RoomInventory inventory;

    public SearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void search(Room[] rooms) {
        System.out.println("\n=== AVAILABLE ROOMS ===");
        for (Room r : rooms) {
            if (inventory.available(r.getType())) {
                r.display();
            }
        }
    }
}

// ================= BOOKING SERVICE =================
class BookingService {
    private RoomInventory inventory;
    private Queue<Reservation> queue = new LinkedList<>();
    private List<Reservation> history = new ArrayList<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void addRequest(Reservation r) {
        queue.add(r);
        System.out.println("REQUEST → " + r.getGuestName());
    }

    // ================= UC9 VALIDATION =================
    public void process() {
        Reservation r = queue.poll();

        if (r == null) {
            System.out.println("No requests.");
            return;
        }

        try {
            validate(r);

            String roomId = UUID.randomUUID().toString();
            inventory.decrease(r.getRoomType());

            r.confirm(roomId);
            history.add(r);

        } catch (BookingException e) {
            System.out.println("BOOKING FAILED → " + e.getMessage());
        }
    }

    // ================= UC9 VALIDATION LOGIC =================
    private void validate(Reservation r) throws BookingException {

        if (r.getGuestName() == null || r.getGuestName().isEmpty()) {
            throw new BookingException("Guest name cannot be empty");
        }

        if (!inventory.exists(r.getRoomType())) {
            throw new BookingException("Room type not found: " + r.getRoomType());
        }

        if (!inventory.available(r.getRoomType())) {
            throw new BookingException("Room not available: " + r.getRoomType());
        }
    }

    // ================= UC8 + UC9 =================
    public void showHistory() {
        System.out.println("\n=== BOOKING HISTORY ===");
        for (Reservation r : history) {
            System.out.println(r.getGuestName() + " | " + r.getRoomType());
        }
    }
}

// ================= MAIN =================
public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("BOOKMY STAY SYSTEM (UC1–UC9)");

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 2);
        inventory.addRoom("Double Room", 1);
        inventory.addRoom("Suite Room", 1);

        // Rooms
        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // Search
        SearchService search = new SearchService(inventory);
        search.search(rooms);

        // Booking
        BookingService service = new BookingService(inventory);

        service.addRequest(new Reservation("Lakshmi", "Single Room"));
        service.addRequest(new Reservation("Akash", "Suite Room"));
        service.addRequest(new Reservation("Priya", "Double Room"));
        service.addRequest(new Reservation("Ravi", "Deluxe Room")); // INVALID (UC9)

        service.process();
        service.process();
        service.process();
        service.process();

        service.showHistory();
        inventory.show();
    }
}