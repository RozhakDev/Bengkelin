/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.dao;

import com.bengkelin.model.Pelanggan;
import com.bengkelin.model.SparePart;
import com.bengkelin.model.Transaksi;
import com.bengkelin.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Kelas LaporanDAO digunakan untuk menghasilkan laporan berdasarkan data transaksi,
 * pelanggan, dan spare part dari database.
 */
public class LaporanDAO {
    
    private static final String SELECT_TRANSAKSI_BY_PERIODE_SQL =
            "SELECT t.*, p.nama AS nama_pelanggan, p.no_telepon AS telepon_pelanggan, " +
            "p.alamat AS alamat_pelanggan, p.is_member AS status_member_pelanggan, " +
            "(SELECT COALESCE(SUM(tsp.jumlah), 0) FROM transaksi_spare_part tsp WHERE tsp.no_transaksi = t.no_transaksi) AS jumlah_item_spare_part " +
            "FROM transaksi t " +
            "LEFT JOIN pelanggan p ON t.id_pelanggan = p.id_pelanggan " +
            "WHERE t.tanggal BETWEEN ? AND ? " +
            "ORDER BY t.tanggal DESC, t.no_transaksi DESC";

    private static final String SELECT_PELANGGAN_BY_SPAREPART_SQL =
            "SELECT p.*, SUM(tsp.jumlah) as quantity_used " +
            "FROM pelanggan p " +
            "JOIN transaksi t ON p.id_pelanggan = t.id_pelanggan " +
            "JOIN transaksi_spare_part tsp ON t.no_transaksi = tsp.no_transaksi " +
            "WHERE tsp.kode_part = ? " +
            "GROUP BY p.id_pelanggan, p.nama, p.no_telepon, p.alamat, p.is_member";

    private static final String SELECT_SPAREPART_BY_KODE_SQL =
            "SELECT * FROM spare_part WHERE kode_part = ?";
    
    /**
     * Mengambil daftar transaksi berdasarkan periode tanggal tertentu.
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
        
        List<Transaksi> transaksiList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(SELECT_TRANSAKSI_BY_PERIODE_SQL)) {
            
            stmt.setTimestamp(1, new java.sql.Timestamp(startDate.getTime()));
            stmt.setTimestamp(2, new java.sql.Timestamp(endDate.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transaksiList.add(mapRowToTransaksiWithPelanggan(rs));
                }
            }
        }
        return transaksiList;
    }
    
    /**
     * Mengambil daftar pelanggan yang menggunakan spare part tertentu.
     * 
     * @param kodePart Kode spare part yang akan dicari (tidak boleh null atau kosong).
     * @return Daftar objek Pelanggan sebagai List<Pelanggan>.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika kodePart null atau kosong.
     */
    public List<Pelanggan> getPelangganBySparePart(String kodePart) throws SQLException {
        if (kodePart == null || kodePart.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode spare part tidak boleh null atau kosong.");
        }
        List<Pelanggan> pelangganList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(SELECT_PELANGGAN_BY_SPAREPART_SQL)) {
            stmt.setString(1, kodePart.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Pelanggan pelanggan = mapRowToPelanggan(rs);
                    pelanggan.setQuantityUsed(rs.getInt("quantity_used"));
                    pelangganList.add(pelanggan);
                }
            }
        }
        return pelangganList;
    }
    
    /**
     * Mengambil data spare part berdasarkan kode part.
     * 
     * @param kodePart Kode spare part yang akan dicari (tidak boleh null atau kosong).
     * @return Objek SparePart jika ditemukan, atau null jika tidak ditemukan.
     * @throws SQLException Jika terjadi kesalahan saat berinteraksi dengan database.
     * @throws IllegalArgumentException Jika kodePart null atau kosong.
     */
    public SparePart getSparePartByKode(String kodePart) throws SQLException {
        if (kodePart == null || kodePart.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode spare part tidak boleh null atau kosong.");
        }
        try (Connection conn = DatabaseConnection.getConnection(); 
                PreparedStatement stmt = conn.prepareStatement(SELECT_SPAREPART_BY_KODE_SQL)) {
            stmt.setString(1, kodePart.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSparePart(rs);
                }
            }
        }
        return null;
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
        
        transaksi.setJumlahItemSparePart(rs.getInt("jumlah_item_spare_part"));
        
        Pelanggan pelanggan = new Pelanggan();
        String idPelangganDB = rs.getString("id_pelanggan");
        if (idPelangganDB != null) {
            pelanggan.setIdPelanggan(idPelangganDB);
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
