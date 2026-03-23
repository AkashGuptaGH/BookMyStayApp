import java.util.*;
import java.util.concurrent.*;

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
    String guestName;
    String roomType;
    String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public void confirm(String id) {
        this.roomId = id;
        System.out.println("CONFIRMED → " + guestName + " | " + roomType + " | " + id);
    }
}

// ================= INVENTORY (THREAD SAFE) =================
class RoomInventory {
    private Map<String, Integer> stock = new HashMap<>();

    public void add(String type, int count) {
        stock.put(type, count);
    }

    public synchronized boolean isAvailable(String type) {
        return stock.getOrDefault(type, 0) > 0;
    }

    // CRITICAL SECTION (UC11)
    public synchronized void allocate(String type) throws BookingException {
        if (!stock.containsKey(type)) {
            throw new BookingException("Invalid room type");
        }

        if (stock.get(type) <= 0) {
            throw new BookingException("No rooms available for " + type);
        }

        stock.put(type, stock.get(type) - 1);
    }

    public synchronized void release(String type) {
        stock.put(type, stock.getOrDefault(type, 0) + 1);
    }

    public void show() {
        System.out.println("\n=== INVENTORY ===");
        stock.forEach((k, v) -> System.out.println(k + " → " + v));
    }
}

// ================= BOOKING QUEUE (SHARED RESOURCE) =================
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void add(Reservation r) {
        queue.add(r);
    }

    public synchronized Reservation poll() {
        return queue.poll();
    }
}

// ================= CONCURRENT PROCESSOR (UC11 CORE) =================
class BookingProcessor {
    private RoomInventory inventory;
    private BookingQueue queue;
    private ExecutorService executor;

    public BookingProcessor(RoomInventory inventory, BookingQueue queue) {
        this.inventory = inventory;
        this.queue = queue;
        this.executor = Executors.newFixedThreadPool(3);
    }

    public void startProcessing() {

        for (int i = 0; i < 6; i++) {

            executor.execute(() -> {
                Reservation r = queue.poll();

                if (r == null) return;

                try {
                    inventory.allocate(r.roomType);

                    String id = UUID.randomUUID().toString();
                    r.confirm(id);

                } catch (BookingException e) {
                    System.out.println("FAILED → " + r.guestName + " | " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {}
    }
}

// ================= MAIN =================
public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("BOOKMY STAY SYSTEM (UC1–UC11)");

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.add("Single Room", 2);
        inventory.add("Double Room", 1);

        // Shared queue
        BookingQueue queue = new BookingQueue();

        // Simulate concurrent requests (UC11)
        queue.add(new Reservation("Lakshmi", "Single Room"));
        queue.add(new Reservation("Akash", "Single Room"));
        queue.add(new Reservation("Priya", "Single Room"));
        queue.add(new Reservation("Ravi", "Double Room"));
        queue.add(new Reservation("John", "Double Room"));
        queue.add(new Reservation("Sara", "Single Room"));

        // Processor
        BookingProcessor processor = new BookingProcessor(inventory, queue);
        processor.startProcessing();

        // Final state
        inventory.show();
    }
}