import java.io.*;
import java.util.*;

// ================= RESERVATION (SERIALIZABLE) =================
class Reservation implements Serializable {
    String guestName;
    String roomType;
    String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public void confirm(String id) {
        this.roomId = id;
    }

    public String toString() {
        return guestName + "," + roomType + "," + roomId;
    }
}

// ================= INVENTORY =================
class RoomInventory implements Serializable {
    Map<String, Integer> stock = new HashMap<>();

    public void add(String type, int count) {
        stock.put(type, count);
    }

    public synchronized boolean available(String type) {
        return stock.getOrDefault(type, 0) > 0;
    }

    public synchronized void decrease(String type) {
        stock.put(type, stock.get(type) - 1);
    }

    public synchronized void increase(String type) {
        stock.put(type, stock.getOrDefault(type, 0) + 1);
    }

    public void show() {
        System.out.println("\n=== INVENTORY ===");
        stock.forEach((k, v) -> System.out.println(k + " → " + v));
    }
}

// ================= PERSISTENCE SERVICE (UC12 CORE) =================
class PersistenceService {

    private static final String FILE = "booking_state.dat";

    // SAVE STATE
    public static void save(RoomInventory inventory, List<Reservation> history) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(FILE))) {

            out.writeObject(inventory);
            out.writeObject(history);

            System.out.println("\nSTATE SAVED TO FILE");

        } catch (IOException e) {
            System.out.println("SAVE FAILED → " + e.getMessage());
        }
    }

    // LOAD STATE
    public static Object[] load() {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(FILE))) {

            RoomInventory inventory = (RoomInventory) in.readObject();
            List<Reservation> history = (List<Reservation>) in.readObject();

            System.out.println("\nSTATE RESTORED FROM FILE");

            return new Object[]{inventory, history};

        } catch (Exception e) {
            System.out.println("NO PREVIOUS STATE FOUND (STARTING FRESH)");
            return null;
        }
    }
}

// ================= BOOKING SERVICE =================
class BookingService {

    RoomInventory inventory;
    List<Reservation> history;

    public BookingService(RoomInventory inventory, List<Reservation> history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void book(String name, String type) {

        if (!inventory.available(type)) {
            System.out.println("NOT AVAILABLE → " + type);
            return;
        }

        inventory.decrease(type);

        String id = UUID.randomUUID().toString();
        Reservation r = new Reservation(name, type);
        r.confirm(id);

        history.add(r);

        System.out.println("BOOKED → " + name);
    }

    public void showHistory() {
        System.out.println("\n=== BOOKING HISTORY ===");
        for (Reservation r : history) {
            System.out.println(r);
        }
    }
}

// ================= MAIN (UC12 RECOVERY DEMO) =================
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("BOOKMY STAY SYSTEM (UC1–UC12)");

        RoomInventory inventory;
        List<Reservation> history;

        // ================= RECOVERY =================
        Object[] state = PersistenceService.load();

        if (state == null) {
            inventory = new RoomInventory();
            history = new ArrayList<>();

            inventory.add("Single Room", 2);
            inventory.add("Double Room", 1);
            inventory.add("Suite Room", 1);

        } else {
            inventory = (RoomInventory) state[0];
            history = (List<Reservation>) state[1];
        }

        BookingService service = new BookingService(inventory, history);

        // ================= OPERATIONS =================
        service.book("Lakshmi", "Single Room");
        service.book("Akash", "Double Room");
        service.book("Priya", "Single Room");

        service.showHistory();
        inventory.show();

        // ================= SAVE STATE (UC12) =================
        PersistenceService.save(inventory, history);
    }
}