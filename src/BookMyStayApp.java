import java.util.LinkedList;
import java.util.Queue;

/**
 * BookMyStayApp
 * Demonstrates booking request intake using a queue.
 *
 * @version 5.0
 */

// Reservation class
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + " | Room Type: " + roomType);
    }
}

// Booking Request Queue
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    // Add request to queue
    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Booking request added for " + reservation.getGuestName());
    }

    // Display queued requests
    public void displayQueue() {
        System.out.println("=== Current Booking Requests (FIFO) ===");
        for (Reservation r : requestQueue) {
            r.displayReservation();
        }
    }
}

// Main application
public class BookMyStayApp {
    public static void main(String[] args) {
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Guests submit booking requests
        bookingQueue.addRequest(new Reservation("Lakshmi", "Single Room"));
        bookingQueue.addRequest(new Reservation("Akash", "Suite Room"));
        bookingQueue.addRequest(new Reservation("Priya", "Double Room"));

        // Display queued requests
        bookingQueue.displayQueue();
    }
}

