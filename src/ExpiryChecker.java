import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExpiryChecker {
    private MedicineDAO dao = new MedicineDAO();

    /**
     * Start the expiry checker:
     * - initialDelaySeconds: how many seconds to wait before the FIRST run (we use 10)
     * - daysAhead: how many days ahead to treat as "expiring soon" (e.g., 7)
     *
     * By design this version is SILENT when there are no alerts (no noisy "No medicines..." prints).
     */
    public void start(int daysAhead) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            try {
                List<Medicine> expiring = dao.getExpiringWithinDays(daysAhead);
                if (!expiring.isEmpty()) {
                    try (PrintWriter out = new PrintWriter(new FileWriter("expiry_alerts.txt", true))) {
                        out.println("----- Alert at " + LocalDate.now() + " -----");
                        for (Medicine m : expiring) {
                            String line = String.format("Medicine: %s | Expiry: %s | Qty: %d | Supplier: %s",
                                    m.getName(), m.getExpiryDate(), m.getQuantity(), m.getSupplier());
                            out.println(line);
                            System.out.println(line); // print only when there is an alert
                        }
                    }
                }
                // If no expiring medicines: DO NOTHING (silent)
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Start after 10 seconds so app startup prompt isn't interrupted.
        // Then run every 24 hours. For dev/testing change the 24 and HOURS to 1 and MINUTES.
        scheduler.scheduleAtFixedRate(task, 10, 24, TimeUnit.HOURS);

        // --- Dev tip (uncomment to test quickly) ---
        // scheduler.scheduleAtFixedRate(task, 5, 1, TimeUnit.MINUTES);
    }
}
