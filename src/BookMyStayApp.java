import java.util.HashMap;

/**
 * BookMyStayApp
 * Demonstrates centralized room inventory management using HashMap.
 *
 * @version 3.0
 */

// Inventory manager class
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    // Register room type with availability
    public void addRoomType(String roomType, int availability) {
        inventory.put(roomType, availability);
    }

    // Update availability
    public void updateAvailability(String roomType, int newAvailability) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, newAvailability);
        } else {
            System.out.println("Room type not found: " + roomType);
        }
    }

    // Get availability
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Display current inventory
    public void displayInventory() {
        System.out.println("=== Current Room Inventory ===");
        for (String roomType : inventory.keySet()) {
            System.out.println(roomType + " | Available: " + inventory.get(roomType));
        }
    }
}

// Main application
public class BookMyStayApp {
    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();

        // Register room types
        inventory.addRoomType("Single Room", 5);
        inventory.addRoomType("Double Room", 3);
        inventory.addRoomType("Suite Room", 2);

        // Display inventory
        inventory.displayInventory();

        // Update availability
        inventory.updateAvailability("Double Room", 4);

        // Display updated inventory
        System.out.println("\nAfter update:");
        inventory.displayInventory();
    }
}
