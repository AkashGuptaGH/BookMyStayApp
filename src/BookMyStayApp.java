/**
 * BookMyStayApp
 * Demonstrates basic room types and static availability.
 *
 * @version 2.0
 */

// Abstract class representing a generalized Room
abstract class Room {
    protected String type;
    protected int beds;
    protected double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
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

// Main application
public class BookMyStayApp {
    public static void main(String[] args) {
        // Static availability variables
        int singleRoomAvailability = 5;
        int doubleRoomAvailability = 3;
        int suiteRoomAvailability = 2;

        // Create room objects
        Room single = new SingleRoom();
        Room doubleR = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Print details and availability
        System.out.println("=== Room Types & Availability ===");
        single.displayDetails();
        System.out.println("Available: " + singleRoomAvailability);

        doubleR.displayDetails();
        System.out.println("Available: " + doubleRoomAvailability);

        suite.displayDetails();
        System.out.println("Available: " + suiteRoomAvailability);
    }
}
