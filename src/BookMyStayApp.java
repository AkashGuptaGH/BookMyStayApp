import java.util.*;

// ================= EXCEPTION =================
class BookingException extends Exception {
    public BookingException(String msg) {
        super(msg);
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
        System.out.println(type + " | ₹" + price);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 3500); }
    public void display() {
        System.out.println(type + " | ₹" + price);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 6000); }
    public void display() {
        System.out.println(type + " | ₹" + price);
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
    public String getRoomId() { return roomId; }

    public void confirm(String roomId) {
        this.roomId = roomId;
        System.out.println("CONFIRMED → " + guestName + " | " + roomType + " | " + roomId);
    }

    public void cancelMark() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}

// ================= INVENTORY =================
class RoomInventory {
    private Map<String, Integer> stock = new HashMap<>();

    public void add(String type, int count) {
        stock.put(type, count);
    }

    public boolean exists(String type) {
        return stock.containsKey(type);
    }

    public boolean available(String type) {
        return stock.getOrDefault(type, 0) > 0;
    }

    public void decrease(String type) throws BookingException {
        if (!exists(type)) throw new BookingException("Invalid room type");
        if (stock.get(type) <= 0) throw new BookingException("No availability");
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
        System.out.println("\n=== ROOMS ===");
        for (Room r : rooms) {
            if (inventory.available(r.getType())) {
                r.display();
            }
        }
    }
}

// ================= BOOKING =================
class BookingService {
    private RoomInventory inventory;
    private Queue<Reservation> queue = new LinkedList<>();
    private Map<String, Reservation> activeBookings = new HashMap<>();
    private List<Reservation> history = new ArrayList<>();

    // UC10: rollback structure
    private Stack<String> rollbackStack = new Stack<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void addRequest(Reservation r) {
        queue.add(r);
        System.out.println("REQUEST → " + r.getGuestName());
    }

    // ================= UC9 VALIDATION =================
    private void validate(Reservation r) throws BookingException {
        if (!inventory.exists(r.getRoomType())) {
            throw new BookingException("Invalid room type");
        }
        if (!inventory.available(r.getRoomType())) {
            throw new BookingException("Room not available");
        }
    }

    public void process() {
        Reservation r = queue.poll();
        if (r == null) return;

        try {
            validate(r);

            String roomId = UUID.randomUUID().toString();

            inventory.decrease(r.getRoomType());
            r.confirm(roomId);

            activeBookings.put(roomId, r);
            history.add(r);

        } catch (BookingException e) {
            System.out.println("FAILED → " + e.getMessage());
        }
    }

    // ================= UC10 CANCELLATION + ROLLBACK =================
    public void cancel(String roomId) throws BookingException {

        if (!activeBookings.containsKey(roomId)) {
            throw new BookingException("Invalid or already cancelled booking");
        }

        Reservation r = activeBookings.get(roomId);

        // LIFO rollback record
        rollbackStack.push(roomId);

        // restore inventory
        inventory.increase(r.getRoomType());

        r.cancelMark();

        activeBookings.remove(roomId);

        System.out.println("CANCELLED → " + r.getGuestName() + " | " + roomId);
    }

    public void showHistory() {
        System.out.println("\n=== HISTORY ===");
        for (Reservation r : history) {
            System.out.println(r.getGuestName() + " | " + r.getRoomType());
        }
    }

    public void showRollback() {
        System.out.println("\n=== ROLLBACK STACK (LIFO) ===");
        for (String id : rollbackStack) {
            System.out.println(id);
        }
    }
}

// ================= MAIN =================
public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("BOOKMY STAY SYSTEM (UC1–UC10)");

        RoomInventory inventory = new RoomInventory();
        inventory.add("Single Room", 2);
        inventory.add("Double Room", 1);
        inventory.add("Suite Room", 1);

        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        SearchService search = new SearchService(inventory);
        search.search(rooms);

        BookingService service = new BookingService(inventory);

        service.addRequest(new Reservation("Lakshmi", "Single Room"));
        service.addRequest(new Reservation("Akash", "Suite Room"));

        service.process();
        service.process();

        // UC10 cancellation
        try {
            service.cancel(service.activeBookings.keySet().iterator().next());
        } catch (Exception e) {
            System.out.println("CANCEL ERROR → " + e.getMessage());
        }

        service.showHistory();
        service.showRollback();
        inventory.show();
    }
}