import java.time.LocalDate;

public class Medicine {
    private int id;
    private String name;
    private String batchNo;
    private int quantity;
    private double price;
    private LocalDate expiryDate;
    private String supplier;

    public Medicine() {}

    public Medicine(String name, String batchNo, int quantity, double price, LocalDate expiryDate, String supplier) {
        this.name = name;
        this.batchNo = batchNo;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
        this.supplier = supplier;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", batchNo='" + batchNo + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", expiryDate=" + expiryDate +
                ", supplier='" + supplier + '\'' +
                '}';
    }
}
