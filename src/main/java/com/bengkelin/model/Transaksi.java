/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.model;

import com.bengkelin.util.TarifService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Kelas Transaksi merepresentasikan informasi tentang transaksi di bengkel.
 * Kelas ini mencakup atribut seperti nomor transaksi, pelanggan, tanggal,
 * biaya service, total biaya, daftar spare part, dan jumlah item spare part.
 */
public class Transaksi {
    private String noTransaksi;
    private Pelanggan pelanggan;
    private Date tanggal;
    private double biayaService;
    private double totalBiaya;
    private List<SparePart> spareParts;
    private int jumlahItemSparePart;
    
    /**
     * Konstruktor default untuk inisialisasi objek Transaksi.
     * Daftar spare part diinisialisasi sebagai list kosong,
     * dan jumlah item spare part diinisialisasi dengan nilai 0.
     */
    public Transaksi() {
        this.spareParts = new ArrayList<>();
        this.jumlahItemSparePart = 0;
    }
    
    /**
     * Konstruktor untuk inisialisasi objek Transaksi dengan parameter.
     * 
     * @param noTransaksi Nomor unik untuk transaksi (tidak boleh null atau kosong).
     * @param pelanggan Objek Pelanggan yang terlibat dalam transaksi (tidak boleh null).
     * @param tanggal Tanggal transaksi (tidak boleh null).
     * @param biayaService Biaya service awal (tidak boleh negatif).
     * @throws IllegalArgumentException Jika parameter tidak memenuhi syarat validasi.
     */
    public Transaksi(String noTransaksi, Pelanggan pelanggan, Date tanggal, double biayaService) {
        this();
        this.setNoTransaksi(noTransaksi);
        this.setPelanggan(pelanggan);
        this.setTanggal(tanggal);
        this.setBiayaService(biayaService);
    }
    
    /**
     * Mengembalikan nomor transaksi.
     * 
     * @return Nomor transaksi sebagai String.
     */
    public String getNoTransaksi() {
        return noTransaksi;
    }
    
    /**
     * Mengatur nomor transaksi.
     * 
     * @param noTransaksi Nomor transaksi yang akan diatur (tidak boleh null atau kosong).
     * @throws IllegalArgumentException Jika nomor transaksi null atau kosong.
     */
    public void setNoTransaksi(String noTransaksi) {
        if (noTransaksi == null || noTransaksi.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor transaksi tidak boleh null atau kosong.");
        }
        this.noTransaksi = noTransaksi.trim();
    }
    
    /**
     * Mengembalikan objek Pelanggan yang terlibat dalam transaksi.
     * 
     * @return Objek Pelanggan.
     */
    public Pelanggan getPelanggan() {
        return pelanggan;
    }
    
    /**
     * Mengatur objek Pelanggan yang terlibat dalam transaksi.
     * 
     * @param pelanggan Objek Pelanggan yang akan diatur (tidak boleh null).
     * @throws NullPointerException Jika objek Pelanggan adalah null.
     */
    public void setPelanggan(Pelanggan pelanggan) {
        this.pelanggan = Objects.requireNonNull(pelanggan, "Objek Pelanggan tidak boleh null.");
    }
    
    /**
     * Mengembalikan tanggal transaksi.
     * 
     * @return Tanggal transaksi sebagai objek Date.
     */
    public Date getTanggal() {
        return tanggal;
    }
    
    /**
     * Mengatur tanggal transaksi.
     * 
     * @param tanggal Tanggal transaksi yang akan diatur (tidak boleh null).
     * @throws NullPointerException Jika tanggal transaksi adalah null.
     */
    public void setTanggal(Date tanggal) {
        this.tanggal = Objects.requireNonNull(tanggal, "Tanggal transaksi tidak boleh null.");
    }
    
    /**
     * Mengembalikan biaya service.
     * 
     * @return Biaya service sebagai double.
     */
    public double getBiayaService() {
        return biayaService;
    }
    
    /**
     * Mengatur biaya service.
     * 
     * @param biayaService Biaya service yang akan diatur (tidak boleh negatif).
     * @throws IllegalArgumentException Jika biaya service negatif.
     */
    public void setBiayaService(double biayaService) {
        if (biayaService < 0) {
            throw new IllegalArgumentException("Biaya service tidak boleh negatif.");
        }
        this.biayaService = biayaService;
    }
    
    /**
     * Mengembalikan total biaya transaksi.
     * 
     * @return Total biaya sebagai double.
     */
    public double getTotalBiaya() {
        return totalBiaya;
    }
    
    /**
     * Mengatur total biaya transaksi.
     * 
     * @param totalBiaya Total biaya yang akan diatur (tidak boleh negatif).
     * @throws IllegalArgumentException Jika total biaya negatif.
     */
    public void setTotalBiaya(double totalBiaya) {
        if (totalBiaya < 0) {
            throw new IllegalArgumentException("Total biaya tidak boleh negatif.");
        }
        this.totalBiaya = totalBiaya;
    }
    
    /**
     * Mengembalikan daftar spare part yang digunakan dalam transaksi.
     * 
     * @return Daftar spare part sebagai List<SparePart>.
     */
    public List<SparePart> getSpareParts() {
        return spareParts;
    }
    
    /**
     * Mengatur daftar spare part yang digunakan dalam transaksi.
     * 
     * @param spareParts Daftar spare part yang akan diatur (bisa null, tetapi akan diinisialisasi sebagai list kosong jika null).
     */
    public void setSpareParts(List<SparePart> spareParts) {
        this.spareParts = (spareParts != null) ? spareParts : new ArrayList<>();
    }
    
    /**
     * Mengembalikan jumlah item spare part yang digunakan dalam transaksi.
     * 
     * @return Jumlah item spare part sebagai integer.
     */
    public int getJumlahItemSparePart() {
        return jumlahItemSparePart;
    }
    
    /**
     * Mengatur jumlah item spare part yang digunakan dalam transaksi.
     * 
     * @param jumlahItemSparePart Jumlah item spare part yang akan diatur (tidak boleh negatif).
     */
    public void setJumlahItemSparePart(int jumlahItemSparePart) {
        if (jumlahItemSparePart < 0) {
            this.jumlahItemSparePart = 0;
        } else {
            this.jumlahItemSparePart = jumlahItemSparePart;
        }
    }
    
    /**
     * Menghitung total biaya transaksi berdasarkan biaya service dan harga spare part.
     * Total biaya dihitung sebagai penjumlahan biaya service dan total harga spare part.
     */
    public void hitungTotalBiaya() {
        double totalHargaSparePart = 0;
        if (this.spareParts != null && !this.spareParts.isEmpty()) {
            totalHargaSparePart = this.spareParts.stream()
                    .mapToDouble(sp -> sp.getHarga() * sp.getJumlahDigunakan())
                    .sum();
        }
        this.totalBiaya = this.biayaService + totalHargaSparePart;
    }
    
    /**
     * Menghitung total biaya transaksi menggunakan utilitas TarifService.
     * 
     * @param jenisService Jenis layanan yang dipilih (tidak boleh null).
     * @param isMember Status keanggotaan pelanggan (true jika member, false jika bukan).
     */
    public void hitungTotalBiaya(TarifService.JenisService jenisService, boolean isMember) {
        if (this.spareParts == null) {
            this.spareParts = new ArrayList<>();
        }
        double biayaAkhirDariTarif = TarifService.hitungTotalBiaya(jenisService, this.spareParts, isMember);
        this.setTotalBiaya(biayaAkhirDariTarif);
    }
    
    /**
     * Membandingkan dua objek Transaksi berdasarkan nomor transaksi.
     * 
     * @param o Objek yang akan dibandingkan.
     * @return true jika objek memiliki nomor transaksi yang sama, false jika tidak.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaksi transaksi = (Transaksi) o;
        return Objects.equals(noTransaksi, transaksi.noTransaksi);
    }
    
    /**
     * Menghasilkan hash code berdasarkan nomor transaksi.
     * 
     * @return Hash code dari nomor transaksi.
     */
    @Override
    public int hashCode() {
        return Objects.hash(noTransaksi);
    }
    
    /**
     * Mengembalikan representasi string dari objek Transaksi.
     * 
     * @return Representasi string dari objek Transaksi.
     */
    @Override
    public String toString() {
        return "Transaksi{" +
            "noTransaksi='" + (noTransaksi != null ? noTransaksi : "N/A") + '\'' +
            ", pelanggan=" + (pelanggan != null ? pelanggan.getNama() : "N/A") +
            ", tanggal=" + (tanggal != null ? tanggal : "N/A") +
            ", biayaService=" + biayaService +
            ", totalBiaya=" + totalBiaya +
            ", jumlahItemSparePart=" + jumlahItemSparePart +
            ", jumlahDetailSparePartList=" + (spareParts != null ? spareParts.size() : 0) +
            '}';
    }
}
