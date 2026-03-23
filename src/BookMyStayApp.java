import java.util.*;

/**
 * BookMyStayApp
 * UC1–UC10 Implementation
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

    public abstract void displayDetails();
}

// =========================
// Room Types
// =========================
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000);
    }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | ₹" + price);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500);
    }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | ₹" + price);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000);
    }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | ₹" + price);
    }
}

// =========================
// Reservation
// =========================
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled = false;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isCancelled() { return isCancelled; }

    public void confirm(String roomId) {
        this.roomId = roomId;
        System.out.println("Confirmed: " + guestName + " | " + roomType + " | ID: " + roomId);
    }

    public void cancel() {
        isCancelled = true;
        System.out.println("Cancelled: " + guestName + " | Room ID: " + roomId);
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName +
                " | Type: " + roomType +
                " | ID: " + roomId +
                " | Status: " + (isCancelled ? "Cancelled" : "Active"));
    }
}

// =========================
// Inventory
// =========================
class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public boolean isAvailable(String type) {
        return availability.getOrDefault(type, 0) > 0;
    }

    public void decrement(String type) {
        int val = availability.getOrDefault(type, 0);
        if (val > 0) availability.put(type, val - 1);
    }

    public void increment(String type) {
        availability.put(type, availability.getOrDefault(type, 0) + 1);
    }

    public void display() {
        System.out.println("\nInventory:");
        for (String k : availability.keySet()) {
            System.out.println(k + " -> " + availability.get(k));
        }
    }
}

// =========================
// UC9 Exception
// =========================
class InvalidBookingException extends Exception {
    public InvalidBookingException(String msg) {
        super(msg);
    }
}

// =========================
// UC9 Validator
// =========================
class BookingValidator {
    private Set<String> validTypes;

    public BookingValidator(Set<String> validTypes) {
        this.validTypes = validTypes;
    }

    public void validate(Reservation r, RoomInventory inv)
            throws InvalidBookingException {

        if (r == null)
            throw new InvalidBookingException("Reservation null");

        if (r.getGuestName() == null || r.getGuestName().isEmpty())
            throw new InvalidBookingException("Invalid name");

        if (!validTypes.contains(r.getRoomType()))
            throw new InvalidBookingException("Invalid room type");

        if (!inv.isAvailable(r.getRoomType()))
            throw new InvalidBookingException("Room unavailable");
    }
}

// =========================
// Booking Service
// =========================
class BookingService {
    private Queue<Reservation> queue = new LinkedList<>();
    private RoomInventory inventory;
    private BookingValidator validator;

    public BookingService(RoomInventory inv, BookingValidator val) {
        this.inventory = inv;
        this.validator = val;
    }

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation process() {
        Reservation r = queue.poll();

        if (r == null) return null;

        try {
            validator.validate(r, inventory);
            String id = UUID.randomUUID().toString();
            inventory.decrement(r.getRoomType());
            r.confirm(id);
            return r;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }
}

// =========================
// UC7 Add-ons
// =========================
class AddOnService {
    String name;
    double cost;

    public AddOnService(String n, double c) {
        name = n;
        cost = c;
    }

    public double getCost() { return cost; }

    public void display() {
        System.out.println(name + " ₹" + cost);
    }
}

class AddOnServiceManager {
    Map<String, List<AddOnService>> map = new HashMap<>();

    public void add(String id, AddOnService s) {
        map.computeIfAbsent(id, k -> new ArrayList<>()).add(s);
    }

    public void show(String id) {
        List<AddOnService> list = map.getOrDefault(id, new ArrayList<>());
        double total = 0;

        for (AddOnService s : list) {
            s.display();
            total += s.getCost();
        }
        System.out.println("Total Add-on: ₹" + total);
    }
}

// =========================
// UC8 History
// =========================
class BookingHistory {
    List<Reservation> list = new ArrayList<>();

    public void add(Reservation r) {
        if (r != null) list.add(r);
    }

    public List<Reservation> getAll() {
        return new ArrayList<>(list);
    }

    public void show() {
        System.out.println("\nHistory:");
        for (Reservation r : list) {
            r.displayReservation();
        }
    }
}

class BookingReportService {
    public void report(List<Reservation> list) {
        Map<String, Integer> count = new HashMap<>();

        for (Reservation r : list) {
            count.put(r.getRoomType(),
                    count.getOrDefault(r.getRoomType(), 0) + 1);
        }

        System.out.println("\nReport:");
        for (String k : count.keySet()) {
            System.out.println(k + ": " + count.get(k));
        }
    }
}

// =========================
// UC10 Cancellation Service
// =========================
class CancellationService {

    private RoomInventory inventory;
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void cancelReservation(Reservation r) {

        // Validation
        if (r == null) {
            System.out.println("Cancellation failed: Reservation not found");
            return;
        }

        if (r.isCancelled()) {
            System.out.println("Cancellation failed: Already cancelled");
            return;
        }

        // Rollback
        rollbackStack.push(r.getRoomId());
        inventory.increment(r.getRoomType());
        r.cancel();

        System.out.println("Rollback successful for Room ID: " + r.getRoomId());
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack: " + rollbackStack);
    }
}

// =========================
// MAIN
// =========================
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inv = new RoomInventory();
        inv.addRoomType("Single Room", 2);
        inv.addRoomType("Double Room", 1);
        inv.addRoomType("Suite Room", 1);

        Set<String> valid = new HashSet<>();
        valid.add("Single Room");
        valid.add("Double Room");
        valid.add("Suite Room");

        BookingValidator validator = new BookingValidator(valid);
        BookingService booking = new BookingService(inv, validator);

        booking.addRequest(new Reservation("Lakshmi", "Single Room"));
        booking.addRequest(new Reservation("Akash", "Suite Room"));

        BookingHistory history = new BookingHistory();

        Reservation r1 = booking.process();
        Reservation r2 = booking.process();

        history.add(r1);
        history.add(r2);

        // Add-ons
        AddOnServiceManager addOn = new AddOnServiceManager();
        addOn.add(r1.getRoomId(), new AddOnService("Breakfast", 500));

        // UC10 Cancellation
        CancellationService cancelService = new CancellationService(inv);
        cancelService.cancelReservation(r1);

        // Outputs
        history.show();
        new BookingReportService().report(history.getAll());
        addOn.show(r1.getRoomId());
        cancelService.showRollbackStack();
        inv.display();
    }
}