/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.dao;

import com.bengkelin.model.Transaksi;
import com.bengkelin.model.SparePart;
import com.bengkelin.model.Pelanggan;
import com.bengkelin.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Objects;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Kelas TransaksiDAO digunakan untuk mengelola data transaksi di database.
 * Kelas ini menyediakan operasi CRUD (Create, Read, Update, Delete) serta operasi tambahan seperti
 * penambahan detail spare part dan pengambilan laporan transaksi.
 */
public class TransaksiDAO {
    
    private static final Logger logger = Logger.getLogger(TransaksiDAO.class.getName());
    
    private static final String INSERT_TRANSAKSI_SQL =
            "INSERT INTO transaksi (no_transaksi, id_pelanggan, tanggal, biaya_service, total_biaya) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_TRANSAKSI_BY_NO_SQL =
            "SELECT t.*, p.nama AS nama_pelanggan, p.no_telepon AS telepon_pelanggan, p.alamat AS alamat_pelanggan, p.is_member AS status_member_pelanggan " +
            "FROM transaksi t " +
            "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
            "WHERE t.no_transaksi = ?";
    private static final String SELECT_ALL_TRANSAKSI_SQL =
            "SELECT t.*, p.nama AS nama_pelanggan, p.no_telepon AS telepon_pelanggan, p.alamat AS alamat_pelanggan, p.is_member AS status_member_pelanggan " +
            "FROM transaksi t " +
            "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
            "ORDER BY t.tanggal DESC, t.no_transaksi DESC"; // Urutkan berdasarkan tanggal terbaru
    private static final String SELECT_TRANSAKSI_BY_PERIODE_SQL =
            "SELECT t.*, p.nama AS nama_pelanggan, p.no_telepon AS telepon_pelanggan, p.alamat AS alamat_pelanggan, p.is_member AS status_member_pelanggan " +
            "FROM transaksi t " +
            "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
            "WHERE t.tanggal BETWEEN ? AND ? " +
            "ORDER BY t.tanggal DESC, t.no_transaksi DESC";
    private static final String DELETE_TRANSAKSI_SQL = "DELETE FROM transaksi WHERE no_transaksi = ?";
    
    private static final String INSERT_TRANSAKSI_SPAREPART_SQL =
            "INSERT INTO transaksi_spare_part (no_transaksi, kode_part, jumlah) VALUES (?, ?, ?)";
    private static final String SELECT_SPAREPARTS_BY_NO_TRANSAKSI_SQL =
            "SELECT sp.kode_part, sp.nama, sp.harga AS harga_master, tsp.jumlah " +
            "FROM spare_part sp " +
            "JOIN transaksi_spare_part tsp ON sp.kode_part = tsp.kode_part " +
            "WHERE tsp.no_transaksi = ?";
    private static final String DELETE_SPAREPARTS_BY_NO_TRANSAKSI_SQL =
            "DELETE FROM transaksi_spare_part WHERE no_transaksi = ?";
    
    private static final String SELECT_POPULAR_SPAREPARTS_SQL =
            "SELECT * FROM spare_part ORDER BY jumlah_digunakan DESC LIMIT 10";
    
    /**
     * Konstruktor default untuk inisialisasi objek TransaksiDAO.
     */
    public TransaksiDAO() {
        
    }
    
    /**
     * Menambahkan data transaksi baru ke database.
     * 
     * @param transaksi Objek Transaksi yang akan ditambahkan (tidak boleh null).
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika data pada objek Transaksi tidak valid.
     */
    public void tambahTransaksi(Transaksi transaksi) throws SQLException {
        Objects.requireNonNull(transaksi, "Objek Transaksi tidak boleh null untuk ditambahkan.");
        Objects.requireNonNull(transaksi.getPelanggan(), "Pelanggan dalam objek Transaksi tidak boleh null.");
        Objects.requireNonNull(transaksi.getTanggal(), "Tanggal dalam objek Transaksi tidak boleh null.");
        if (transaksi.getNoTransaksi() == null || transaksi.getNoTransaksi().trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor Transaksi tidak boleh kosong.");
        }
        if (transaksi.getPelanggan().getIdPelanggan() == null || transaksi.getPelanggan().getIdPelanggan().trim().isEmpty()){
            throw new IllegalArgumentException("ID Pelanggan dalam objek Transaksi tidak boleh kosong.");
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            simpanDetailTransaksiUtama(conn, transaksi);
            
            if (transaksi.getSpareParts() != null && !transaksi.getSpareParts().isEmpty()) {
                simpanDetailTransaksiSpareParts(conn, transaksi);
            }
            
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException exRollback) {
                    logger.log(Level.SEVERE, "Error saat rollback transaksi penambahan", exRollback);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException exClose) {
                    logger.log(Level.SEVERE, "Error saat menutup koneksi setelah transaksi penambahan", exClose);
                }
            }
        }
    }
    
    /**
     * Mengambil data transaksi dari database berdasarkan nomor transaksi.
     * 
     * @param noTransaksi Nomor transaksi yang akan dicari (tidak boleh null atau kosong).
     * @return Objek Transaksi jika ditemukan, atau null jika tidak ditemukan.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika nomor transaksi null atau kosong.
     */
    public Transaksi dapatkanTransaksiByNo(String noTransaksi) throws SQLException {
        if (noTransaksi == null || noTransaksi.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor transaksi tidak boleh null atau kosong untuk pencarian.");
        }
        Transaksi transaksi = null;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_TRANSAKSI_BY_NO_SQL)) {
            
            stmt.setString(1, noTransaksi.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    transaksi = mapRowToTransaksiWithPelanggan(rs);
                    
                    transaksi.setSpareParts(dapatkanDetailSparePartsByNoTransaksi(conn, noTransaksi.trim()));
                }
            }
        }
        return transaksi;
    }
    
    /**
     * Mengambil semua data transaksi dari database.
     * 
     * @return Daftar objek Transaksi sebagai List<Transaksi>.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     */
    public List<Transaksi> semuaTransaksi() throws SQLException {
        List<Transaksi> daftarTransaksi = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); 
                Statement stmt = conn.createStatement(); 
                ResultSet rs = stmt.executeQuery(SELECT_ALL_TRANSAKSI_SQL)) {
            
            while (rs.next()) {
                Transaksi transaksi = mapRowToTransaksiWithPelanggan(rs);
                
                daftarTransaksi.add(transaksi);
            }
        }
        return daftarTransaksi;
    }
    
    /**
     * Mengambil data transaksi dari database berdasarkan periode tanggal tertentu.
     * 
     * @param startDate Tanggal awal periode (tidak boleh null).
     * @param endDate Tanggal akhir periode (tidak boleh null).
     * @return Daftar objek Transaksi sebagai List<Transaksi>.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws NullPointerException Jika startDate atau endDate adalah null.
     */
    public List<Transaksi> getTransaksiByPeriode(Date startDate, Date endDate) throws SQLException {
        Objects.requireNonNull(startDate, "Tanggal awal (startDate) tidak boleh null.");
        Objects.requireNonNull(endDate, "Tanggal akhir (endDate) tidak boleh null.");
        
        List<Transaksi> daftarTransaksi = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(SELECT_TRANSAKSI_BY_PERIODE_SQL)) {
            
            stmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
            stmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaksi transaksi = mapRowToTransaksiWithPelanggan(rs);
                    
                    daftarTransaksi.add(transaksi);
                }
            }
            return daftarTransaksi;
        }
    }
    
    /**
     * Menghapus data transaksi dari database berdasarkan nomor transaksi.
     * 
     * @param noTransaksi Nomor transaksi yang akan dihapus (tidak boleh null atau kosong).
     * @return true jika penghapusan berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika nomor transaksi null atau kosong.
     */
    public boolean hapusTransaksi(String noTransaksi) throws SQLException {
        if (noTransaksi == null || noTransaksi.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor transaksi tidak boleh null atau kosong untuk dihapus.");
        }
        
        Connection conn = null;
        boolean rowDeletedFromTransaksiUtama = false;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Mulai transaksi
            
            hapusDetailTransaksiSpareParts(conn, noTransaksi.trim());
            
            rowDeletedFromTransaksiUtama = hapusDetailTransaksiUtama(conn, noTransaksi.trim());

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException exRollback) {
                    logger.log(Level.SEVERE, "Error saat rollback penghapusan transaksi", exRollback);
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException exClose) {
                    logger.log(Level.SEVERE, "Error saat menutup koneksi setelah penghapusan transaksi", exClose);
                }
            }
        }
        return rowDeletedFromTransaksiUtama;
    }
    
    /**
     * Mengambil 10 spare part paling populer berdasarkan jumlah digunakan.
     * 
     * @return Daftar objek SparePart sebagai List<SparePart>.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     */
    public List<SparePart> getSparePartPopuler() throws SQLException {
        List<SparePart> daftarSparePartPopuler = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); 
                Statement stmt = conn.createStatement(); 
                ResultSet rs = stmt.executeQuery(SELECT_POPULAR_SPAREPARTS_SQL)) {
            
            while (rs.next()) {
                SparePart sparePart = new SparePart();
                sparePart.setKodePart(rs.getString("kode_part"));
                sparePart.setNama(rs.getString("nama"));
                sparePart.setHarga(rs.getDouble("harga"));
                sparePart.setStok(rs.getInt("stok"));
                sparePart.setJumlahDigunakan(rs.getInt("jumlah_digunakan"));
                daftarSparePartPopuler.add(sparePart);
            }
        }
        return daftarSparePartPopuler;
    }
    
    /**
     * Menyimpan detail transaksi utama ke database.
     * 
     * @param conn Koneksi database yang sedang aktif.
     * @param transaksi Objek Transaksi yang akan disimpan.
     * @throws SQLException Jika terjadi kesalahan saat menyimpan data transaksi.
     */
    private void simpanDetailTransaksiUtama(Connection conn, Transaksi transaksi) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_TRANSAKSI_SQL)) {
            stmt.setString(1, transaksi.getNoTransaksi());
            stmt.setString(2, transaksi.getPelanggan().getIdPelanggan());
            stmt.setTimestamp(3, new java.sql.Timestamp(transaksi.getTanggal().getTime()));
            stmt.setDouble(4, transaksi.getBiayaService());
            stmt.setDouble(5, transaksi.getTotalBiaya());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Menyimpan detail spare part transaksi ke database.
     * 
     * @param conn Koneksi database yang sedang aktif.
     * @param transaksi Objek Transaksi yang berisi detail spare part.
     * @throws SQLException Jika terjadi kesalahan saat menyimpan data spare part.
     */
    private void simpanDetailTransaksiSpareParts(Connection conn, Transaksi transaksi) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_TRANSAKSI_SPAREPART_SQL)) {
            for (SparePart sparePart : transaksi.getSpareParts()) {
                stmt.setString(1, transaksi.getNoTransaksi());
                stmt.setString(2, sparePart.getKodePart());
                stmt.setInt(3, sparePart.getJumlahDigunakan());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    /**
     * Menghapus detail transaksi utama dari database.
     * 
     * @param conn Koneksi database yang sedang aktif.
     * @param noTransaksi Nomor transaksi yang akan dihapus.
     * @return true jika penghapusan berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan saat menghapus data transaksi.
     */
    private boolean hapusDetailTransaksiUtama(Connection conn, String noTransaksi) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_TRANSAKSI_SQL)) {
            stmt.setString(1, noTransaksi);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Menghapus detail spare part transaksi dari database.
     * 
     * @param conn Koneksi database yang sedang aktif.
     * @param noTransaksi Nomor transaksi yang akan dihapus.
     * @throws SQLException Jika terjadi kesalahan saat menghapus data spare part.
     */
    private void hapusDetailTransaksiSpareParts(Connection conn, String noTransaksi) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_SPAREPARTS_BY_NO_TRANSAKSI_SQL)) {
            stmt.setString(1, noTransaksi);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Mengambil detail spare part berdasarkan nomor transaksi.
     * 
     * @param conn Koneksi database yang sedang aktif.
     * @param noTransaksi Nomor transaksi yang akan dicari.
     * @return Daftar objek SparePart sebagai List<SparePart>.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data spare part.
     */
    private List<SparePart> dapatkanDetailSparePartsByNoTransaksi(Connection conn, String noTransaksi) throws SQLException {
        List<SparePart> daftarSparePartTransaksi = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_SPAREPARTS_BY_NO_TRANSAKSI_SQL)) {
            stmt.setString(1, noTransaksi);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SparePart sp = new SparePart();
                    sp.setKodePart(rs.getString("kode_part"));
                    sp.setNama(rs.getString("nama"));
                    sp.setHarga(rs.getDouble("harga_master"));
                    sp.setJumlahDigunakan(rs.getInt("jumlah"));
                    daftarSparePartTransaksi.add(sp);
                }
            }
        }
        return daftarSparePartTransaksi;
    }
    
    /**
     * Memetakan baris hasil query ResultSet menjadi objek Transaksi dengan informasi pelanggan.
     * 
     * @param rs ResultSet yang berisi data transaksi dan pelanggan dari database.
     * @return Objek Transaksi yang telah dipetakan.
     * @throws SQLException Jika terjadi kesalahan saat membaca data dari ResultSet.
     */
    private Transaksi mapRowToTransaksiWithPelanggan(ResultSet rs) throws SQLException {
        Transaksi transaksi = new Transaksi();
        transaksi.setNoTransaksi(rs.getString("no_transaksi"));
        transaksi.setTanggal(rs.getTimestamp("tanggal"));
        transaksi.setBiayaService(rs.getDouble("biaya_service"));
        transaksi.setTotalBiaya(rs.getDouble("total_biaya"));
        
        String idPelanggan = rs.getString("id_pelanggan");
        if (idPelanggan != null) {
            Pelanggan pelanggan = new Pelanggan();
            pelanggan.setIdPelanggan(idPelanggan);
            pelanggan.setNama(rs.getString("nama_pelanggan"));
            pelanggan.setNoTelepon(rs.getString("telepon_pelanggan"));
            pelanggan.setAlamat(rs.getString("alamat_pelanggan"));
            pelanggan.setMember(rs.getBoolean("status_member_pelanggan"));
            transaksi.setPelanggan(pelanggan);
        } else {
            transaksi.setPelanggan(null);
        }
        return transaksi;
    }
}
