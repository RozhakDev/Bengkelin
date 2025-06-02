/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.dao;

import com.bengkelin.model.Pelanggan;
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
 * Kelas PelangganDAO digunakan untuk mengelola data pelanggan di database.
 * Kelas ini menyediakan operasi CRUD (Create, Read, Update, Delete) untuk entitas Pelanggan.
 */
public class PelangganDAO {
    
    private static final String INSERT_PELANGGAN_SQL = "INSERT INTO pelanggan (id_pelanggan, nama, no_telepon, alamat, is_member) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_PELANGGAN_BY_ID_SQL = "SELECT * FROM pelanggan WHERE id_pelanggan = ?";
    private static final String SELECT_ALL_PELANGGAN_SQL = "SELECT id_pelanggan, nama, no_telepon, alamat, is_member FROM pelanggan";
    private static final String UPDATE_PELANGGAN_SQL = "UPDATE pelanggan SET nama = ?, no_telepon = ?, alamat = ?, is_member = ? WHERE id_pelanggan = ?";
    private static final String DELETE_PELANGGAN_SQL = "DELETE FROM pelanggan WHERE id_pelanggan = ?";
    
    /**
     * Menambahkan data pelanggan baru ke database.
     * 
     * @param pelanggan Objek Pelanggan yang akan ditambahkan (tidak boleh null).
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws NullPointerException Jika objek Pelanggan adalah null.
     */
    public void tambahPelanggan(Pelanggan pelanggan) throws SQLException {
        Objects.requireNonNull(pelanggan, "Objek Pelanggan tidak boleh null untuk ditambahkan.");
        
        try (Connection conn = DatabaseConnection.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(INSERT_PELANGGAN_SQL)) {
            
            stmt.setString(1, pelanggan.getIdPelanggan());
            stmt.setString(2, pelanggan.getNama());
            stmt.setString(3, pelanggan.getNoTelepon());
            stmt.setString(4, pelanggan.getAlamat());
            stmt.setBoolean(5, pelanggan.isMember());
            stmt.executeUpdate();
        }
    }
    
    /**
     * Mengambil data pelanggan dari database berdasarkan ID pelanggan.
     * 
     * @param idPelanggan ID pelanggan yang akan dicari (tidak boleh null atau kosong).
     * @return Objek Pelanggan jika ditemukan, atau null jika tidak ditemukan.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika ID pelanggan null atau kosong.
     */
    public Pelanggan dapatkanPelangganById(String idPelanggan) throws SQLException {
        if (idPelanggan == null || idPelanggan.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Pelanggan tidak boleh null atau kosong untuk pencarian.");
        }
        
        Pelanggan pelanggan = null;
        try (Connection conn = DatabaseConnection.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(SELECT_PELANGGAN_BY_ID_SQL)) {
            
            stmt.setString(1, idPelanggan.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pelanggan = mapRowToPelanggan(rs);
                }
            }
        }
        return pelanggan;
    }
    
    /**
     * Mengambil semua data pelanggan dari database.
     * 
     * @return Daftar objek Pelanggan sebagai List<Pelanggan>.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     */
    public List<Pelanggan> semuaPelanggan() throws SQLException {
        List<Pelanggan> daftarPelanggan = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); 
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SELECT_ALL_PELANGGAN_SQL)) {
            while (rs.next()) {
                daftarPelanggan.add(mapRowToPelanggan(rs));
            }
        }
        return daftarPelanggan;
    }
    
    /**
     * Memperbarui data pelanggan di database.
     * 
     * @param pelanggan Objek Pelanggan yang akan diperbarui (tidak boleh null).
     * @return true jika pembaruan berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws NullPointerException Jika objek Pelanggan adalah null.
     * @throws IllegalArgumentException Jika ID pelanggan pada objek Pelanggan null atau kosong.
     */
    public boolean updatePelanggan(Pelanggan pelanggan) throws SQLException {
        Objects.requireNonNull(pelanggan, "Objek Pelanggan tidak boleh null untuk diperbarui.");
        
        if (pelanggan.getIdPelanggan() == null || pelanggan.getIdPelanggan().trim().isEmpty()) {
            throw new IllegalArgumentException("ID Pelanggan pada objek Pelanggan tidak boleh null atau kosong untuk update.");
        }
        
        boolean rowUpdated;
        try (Connection conn = DatabaseConnection.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(UPDATE_PELANGGAN_SQL)) {
            
            stmt.setString(1, pelanggan.getNama());
            stmt.setString(2, pelanggan.getNoTelepon());
            stmt.setString(3, pelanggan.getAlamat());
            stmt.setBoolean(4, pelanggan.isMember());
            stmt.setString(5, pelanggan.getIdPelanggan());
            rowUpdated = stmt.executeUpdate() > 0;
        }
        return rowUpdated;
    }
    
    /**
     * Menghapus data pelanggan dari database berdasarkan ID pelanggan.
     * 
     * @param idPelanggan ID pelanggan yang akan dihapus (tidak boleh null atau kosong).
     * @return true jika penghapusan berhasil, false jika gagal.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika ID pelanggan null atau kosong.
     */
    public boolean hapusPelanggan(String idPelanggan) throws SQLException {
        if (idPelanggan == null || idPelanggan.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Pelanggan tidak boleh null atau kosong untuk dihapus.");
        }
        
        boolean rowDeleted;
        try (Connection conn = DatabaseConnection.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(DELETE_PELANGGAN_SQL)) {
            stmt.setString(1, idPelanggan.trim());
            rowDeleted = stmt.executeUpdate() > 0;
        }
        
        return rowDeleted;
    }
    
    /**
     * Memetakan baris hasil query ResultSet menjadi objek Pelanggan.
     * 
     * @param rs ResultSet yang berisi data pelanggan dari database.
     * @return Objek Pelanggan yang telah dipetakan.
     * @throws SQLException Jika terjadi kesalahan saat membaca data dari ResultSet.
     */
    private Pelanggan mapRowToPelanggan(ResultSet rs) throws SQLException {
        Pelanggan pelanggan = new Pelanggan();
        pelanggan.setIdPelanggan(rs.getString("id_pelanggan"));
        pelanggan.setNama(rs.getString("nama"));
        pelanggan.setNoTelepon(rs.getString("no_telepon"));
        pelanggan.setAlamat(rs.getString("alamat"));
        pelanggan.setMember(rs.getBoolean("is_member"));
        return pelanggan;
    }
}
