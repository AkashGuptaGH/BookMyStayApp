import java.util.HashMap;

/**
 * BookMyStayApp
 * Demonstrates room search and availability check.
 *
 * @version 4.0
 */

// Inventory manager class
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void addRoomType(String roomType, int availability) {
        inventory.put(roomType, availability);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public HashMap<String, Integer> getAllInventory() {
        return new HashMap<>(inventory); // defensive copy
    }
}

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

// Main application
public class BookMyStayApp {
    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();

        // Register room types
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 0); // unavailable
        inventory.addRoomType("Suite Room", 2);

        // Create room objects
        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };

        // Search service
        RoomSearchService searchService = new RoomSearchService(inventory);

        // Guest initiates search
        searchService.searchAvailableRooms(rooms);
    }
}

