import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public void addMedicine(Medicine m) throws SQLException {
        String sql = "INSERT INTO medicines (name, batch_no, quantity, price, expiry_date, supplier) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getBatchNo());
            ps.setInt(3, m.getQuantity());
            ps.setDouble(4, m.getPrice());
            if (m.getExpiryDate() != null) {
                ps.setDate(5, Date.valueOf(m.getExpiryDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, m.getSupplier());
            ps.executeUpdate();
        }
    }

    public void updateMedicine(Medicine m) throws SQLException {
        String sql = "UPDATE medicines SET name=?, batch_no=?, quantity=?, price=?, expiry_date=?, supplier=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getBatchNo());
            ps.setInt(3, m.getQuantity());
            ps.setDouble(4, m.getPrice());
            if (m.getExpiryDate() != null) {
                ps.setDate(5, Date.valueOf(m.getExpiryDate()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, m.getSupplier());
            ps.setInt(7, m.getId());
            ps.executeUpdate();
        }
    }

    public void deleteMedicine(int id) throws SQLException {
        String sql = "DELETE FROM medicines WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public Medicine getMedicineById(int id) throws SQLException {
        String sql = "SELECT * FROM medicines WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Medicine m = mapRowToMedicine(rs);
                    return m;
                } else {
                    return null;
                }
            }
        }
    }

    public List<Medicine> getAllMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToMedicine(rs));
            }
        }
        return list;
    }

    // Medicines with expiry between today and target (next 'days')
    public List<Medicine> getExpiringWithinDays(int days) throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE expiry_date BETWEEN ? AND ?";
        LocalDate today = LocalDate.now();
        LocalDate target = today.plusDays(days);
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(today));
            ps.setDate(2, Date.valueOf(target));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToMedicine(rs));
                }
            }
        }
        return list;
    }

    private Medicine mapRowToMedicine(ResultSet rs) throws SQLException {
        Medicine m = new Medicine();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("name"));
        m.setBatchNo(rs.getString("batch_no"));
        m.setQuantity(rs.getInt("quantity"));
        m.setPrice(rs.getDouble("price"));
        Date d = rs.getDate("expiry_date");
        if (d != null) m.setExpiryDate(d.toLocalDate());
        m.setSupplier(rs.getString("supplier"));
        return m;
    }
}
