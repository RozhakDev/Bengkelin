/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.dao;

import com.bengkelin.model.SparePart;
import com.bengkelin.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Kelas SparePartDAO digunakan untuk mengelola data spare part di database.
 * Kelas ini menyediakan operasi CRUD (Create, Read, Update, Delete) serta operasi tambahan seperti pencarian dan pembaruan stok.
 */
public class SparePartDAO {
    
    private static final String INSERT_SPAREPART_SQL = "INSERT INTO spare_part (kode_part, nama, harga, stok, jumlah_digunakan) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_SPAREPART_BY_ID_SQL = "SELECT * FROM spare_part WHERE kode_part = ?";
    private static final String SELECT_ALL_SPAREPART_SQL = "SELECT * FROM spare_part";
    private static final String UPDATE_SPAREPART_SQL = "UPDATE spare_part SET nama = ?, harga = ?, stok = ? WHERE kode_part = ?";
    private static final String DELETE_SPAREPART_SQL = "DELETE FROM spare_part WHERE kode_part = ?";
    private static final String SELECT_POPULAR_SPAREPART_SQL = "SELECT * FROM spare_part ORDER BY jumlah_digunakan DESC";
    private static final String UPDATE_STOK_DAN_POPULARITAS_SQL = "UPDATE spare_part SET stok = stok - ?, jumlah_digunakan = jumlah_digunakan + ? WHERE kode_part = ?";
    private static final String SEARCH_SPAREPART_SQL = "SELECT * FROM spare_part WHERE nama LIKE ? OR kode_part LIKE ?";
    
    /**
     * Menambahkan data spare part baru ke database.
     * 
     * @param sparePart Objek SparePart yang akan ditambahkan (tidak boleh null).
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika data pada objek SparePart tidak valid.
     */
    public void tambahSparePart(SparePart sparePart) throws SQLException {
        Objects.requireNonNull(sparePart, "Objek SparePart tidak boleh null untuk ditambahkan.");
        if (sparePart.getKodePart() == null || sparePart.getKodePart().trim().isEmpty()) {
            throw new IllegalArgumentException("Kode part pada objek SparePart tidak boleh kosong.");
        }
        if (sparePart.getNama() == null || sparePart.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama pada objek SparePart tidak boleh kosong.");
        }
        if (sparePart.getHarga() < 0) {
            throw new IllegalArgumentException("Harga pada objek SparePart tidak boleh negatif.");
        }
        if (sparePart.getStok() < 0) {
            throw new IllegalArgumentException("Stok pada objek SparePart tidak boleh negatif.");
        }
        
        try (Connection conn = DatabaseConnection.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(INSERT_SPAREPART_SQL)) {
            
            stmt.setString(1, sparePart.getKodePart());
            stmt.setString(2, sparePart.getNama());
            stmt.setDouble(3, sparePart.getHarga());
            stmt.setInt(4, sparePart.getStok());
            stmt.setInt(5, 0);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Mengambil data spare part dari database berdasarkan kode part.
     * 
     * @param kodePart Kode part yang akan dicari (tidak boleh null atau kosong).
     * @return Objek SparePart jika ditemukan, atau null jika tidak ditemukan.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika kode part null atau kosong.
     */
    public SparePart dapatkanSparePartByKode(String kodePart) throws SQLException {
        if (kodePart == null || kodePart.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode part tidak boleh null atau kosong untuk pencarian.");
        }
        
        SparePart sparePart = null;
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_SPAREPART_BY_ID_SQL)) {

            stmt.setString(1, kodePart.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    sparePart = mapRowToSparePart(rs);
                }
            }
        }
        return sparePart;
    }
    
    /**
     * Mengambil semua data spare part dari database.
     * 
     * @return Daftar objek SparePart sebagai List<SparePart>.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     */
    public List<SparePart> semuaSparePart() throws SQLException {
        List<SparePart> daftarSparePart = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); 
                Statement stmt = conn.createStatement(); 
                ResultSet rs = stmt.executeQuery(SELECT_ALL_SPAREPART_SQL)) {
            
            while (rs.next()) {
                daftarSparePart.add(mapRowToSparePart(rs));
            }
        }
        return daftarSparePart;
    }
    
    /**
     * Memperbarui data spare part di database.
     * 
     * @param sparePart Objek SparePart yang akan diperbarui (tidak boleh null).
     * @return true jika pembaruan berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika data pada objek SparePart tidak valid.
     */
    public boolean updateSparePart(SparePart sparePart) throws SQLException {
        Objects.requireNonNull(sparePart, "Objek SparePart tidak boleh null untuk diperbarui.");
        if (sparePart.getKodePart() == null || sparePart.getKodePart().trim().isEmpty()) {
            throw new IllegalArgumentException("Kode part pada objek SparePart tidak boleh kosong untuk update.");
        }
        if (sparePart.getNama() == null || sparePart.getNama().trim().isEmpty()) {
            throw new IllegalArgumentException("Nama pada objek SparePart tidak boleh kosong untuk update.");
        }
        if (sparePart.getHarga() < 0) {
            throw new IllegalArgumentException("Harga pada objek SparePart tidak boleh negatif untuk update.");
        }
        if (sparePart.getStok() < 0) {
            throw new IllegalArgumentException("Stok pada objek SparePart tidak boleh negatif untuk update.");
        }
        
        boolean rowUpdated;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SPAREPART_SQL)) {

            stmt.setString(1, sparePart.getNama());
            stmt.setDouble(2, sparePart.getHarga());
            stmt.setInt(3, sparePart.getStok());
            stmt.setString(4, sparePart.getKodePart());
            rowUpdated = stmt.executeUpdate() > 0;
        }
        return rowUpdated;
    }
    
    /**
     * Menghapus data spare part dari database berdasarkan kode part.
     * 
     * @param kodePart Kode part yang akan dihapus (tidak boleh null atau kosong).
     * @return true jika penghapusan berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika kode part null atau kosong.
     */
    public boolean hapusSparePart(String kodePart) throws SQLException {
        if (kodePart == null || kodePart.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode part tidak boleh null atau kosong untuk dihapus.");
        }
        
        boolean rowDeleted;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SPAREPART_SQL)) {

            stmt.setString(1, kodePart.trim());
            rowDeleted = stmt.executeUpdate() > 0;
        }
        return rowDeleted;
    }
    
    /**
     * Mengambil daftar spare part paling populer berdasarkan jumlah digunakan.
     * 
     * @return Daftar objek SparePart sebagai List<SparePart>, diurutkan berdasarkan popularitas.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     */
    public List<SparePart> getSparePartPopuler() throws SQLException {
        List<SparePart> daftarSparePartPopuler = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_POPULAR_SPAREPART_SQL)) {

            while (rs.next()) {
                daftarSparePartPopuler.add(mapRowToSparePart(rs));
            }
        }
        return daftarSparePartPopuler;
    }
    
    /**
     * Memperbarui stok dan meningkatkan popularitas spare part berdasarkan kode part.
     * 
     * @param kodePart Kode part yang akan diperbarui (tidak boleh null atau kosong).
     * @param jumlah Jumlah yang akan dikurangi dari stok dan ditambahkan ke popularitas (harus positif).
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika kode part null/kosong atau jumlah tidak valid.
     */
    public void updateStokDanPopularitas(String kodePart, int jumlah) throws SQLException {
        if (kodePart == null || kodePart.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode part tidak boleh null atau kosong untuk update stok.");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah untuk update stok dan popularitas harus positif.");
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_STOK_DAN_POPULARITAS_SQL)) {

            stmt.setInt(1, jumlah);
            stmt.setInt(2, jumlah);
            stmt.setString(3, kodePart.trim());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Mencari spare part berdasarkan keyword (nama atau kode part).
     * 
     * @param keyword Keyword pencarian (tidak boleh null).
     * @return Daftar objek SparePart yang sesuai dengan hasil pencarian.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws NullPointerException Jika keyword adalah null.
     */
    public List<SparePart> cariSparePart(String keyword) throws SQLException {
        Objects.requireNonNull(keyword, "Keyword pencarian tidak boleh null.");
        List<SparePart> hasilPencarian = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_SPAREPART_SQL)) {
            
            String keywordLike = "%" + keyword.trim() + "%";
            stmt.setString(1, keywordLike);
            stmt.setString(2, keywordLike);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    hasilPencarian.add(mapRowToSparePart(rs));
                }
            }
        }
        return hasilPencarian;
    }
    
    /**
     * Memetakan baris hasil query ResultSet menjadi objek SparePart.
     * 
     * @param rs ResultSet yang berisi data spare part dari database.
     * @return Objek SparePart yang telah dipetakan.
     * @throws SQLException Jika terjadi kesalahan saat membaca data dari ResultSet.
     */
    private SparePart mapRowToSparePart(ResultSet rs) throws SQLException {
        SparePart sparePart = new SparePart();
        sparePart.setKodePart(rs.getString("kode_part"));
        sparePart.setNama(rs.getString("nama"));
        sparePart.setHarga(rs.getDouble("harga"));
        sparePart.setStok(rs.getInt("stok"));
        sparePart.setJumlahDigunakan(rs.getInt("jumlah_digunakan"));
        return sparePart;
    }
}
