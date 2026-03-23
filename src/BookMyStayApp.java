import java.util.*;

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
    private boolean cancelled = false;

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

    public String getRoomId() {
        return roomId;
    }
}

// ================= INVENTORY =================
class RoomInventory {
    private Map<String, Integer> stock = new HashMap<>();

    public void addRoom(String type, int count) {
        stock.put(type, count);
    }

    public boolean available(String type) {
        return stock.getOrDefault(type, 0) > 0;
    }

    public void decrease(String type) {
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

    // UC8: Booking History
    private List<Reservation> bookingHistory = new ArrayList<>();

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
            System.out.println("No booking requests.");
            return;
        }

        if (inventory.available(r.getRoomType())) {
            String roomId = UUID.randomUUID().toString();

            inventory.decrease(r.getRoomType());
            r.confirm(roomId);

            // UC8: store history
            bookingHistory.add(r);
        } else {
            System.out.println("NOT AVAILABLE → " + r.getGuestName());
        }
    }

    // ================= UC8: REPORTING =================

    public void showBookingHistory() {
        System.out.println("\n=== BOOKING HISTORY (UC8) ===");
        for (Reservation r : bookingHistory) {
            System.out.println(
                    r.getGuestName() + " | " +
                            r.getRoomType() + " | " +
                            r.getRoomId()
            );
        }
    }

    public void generateReport() {
        System.out.println("\n=== BOOKING REPORT (UC8) ===");

        Map<String, Integer> countByRoom = new HashMap<>();

        for (Reservation r : bookingHistory) {
            countByRoom.put(r.getRoomType(),
                    countByRoom.getOrDefault(r.getRoomType(), 0) + 1);
        }

        System.out.println("Total bookings: " + bookingHistory.size());

        for (String type : countByRoom.keySet()) {
            System.out.println(type + " → " + countByRoom.get(type));
        }
    }
}

// ================= MAIN =================
public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("WELCOME TO BOOKMY STAY SYSTEM (UC1–UC8)");

        // UC3 Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 2);
        inventory.addRoom("Double Room", 1);
        inventory.addRoom("Suite Room", 1);

        // UC2 Rooms
        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // UC4 Search
        SearchService search = new SearchService(inventory);
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

        // UC8: History + Reporting
        service.showBookingHistory();
        service.generateReport();

        // Final inventory
        inventory.show();
    }
}