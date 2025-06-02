/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.model;

import java.util.Objects;

/**
 * Kelas SparePart merepresentasikan informasi tentang spare part di bengkel.
 * Kelas ini mencakup atribut seperti kode part, nama, harga, stok, dan jumlah digunakan.
 */
public class SparePart {
    private String kodePart;
    private String nama;
    private double harga;
    private int stok;
    private int jumlahDigunakan;
    
    /**
     * Konstruktor default untuk inisialisasi objek SparePart.
     * Jumlah digunakan diinisialisasi dengan nilai 0.
     */
    public SparePart() {
        this.jumlahDigunakan = 0;
    }
    
    /**
     * Konstruktor untuk inisialisasi objek SparePart dengan parameter.
     * 
     * @param kodePart Kode unik untuk spare part (tidak boleh null atau kosong).
     * @param nama Nama spare part (tidak boleh null atau kosong).
     * @param harga Harga spare part (tidak boleh negatif).
     * @param stok Stok awal spare part (tidak boleh negatif).
     * @throws IllegalArgumentException Jika parameter tidak memenuhi syarat validasi.
     */
    public SparePart(String kodePart, String nama, double harga, int stok) {
        this();
        this.setKodePart(kodePart);
        this.setNama(nama);
        this.setHarga(harga);
        this.setStok(stok);
    }
    
    /**
     * Mengembalikan kode part dari spare part.
     * 
     * @return Kode part sebagai String.
     */
    public String getKodePart() {
        return kodePart;
    }
    
    /**
     * Mengatur kode part untuk spare part.
     * 
     * @param kodePart Kode part yang akan diatur (tidak boleh null atau kosong).
     * @throws IllegalArgumentException Jika kode part null atau kosong.
     */
    public void setKodePart(String kodePart) {
        if (kodePart == null || kodePart.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode spare part tidak boleh null atau kosong.");
        }
        this.kodePart = kodePart.trim();
    }
    
    /**
     * Mengembalikan nama spare part.
     * 
     * @return Nama spare part sebagai String.
     */
    public String getNama() {
        return nama;
    }
    
    /**
     * Mengatur nama untuk spare part.
     * 
     * @param nama Nama spare part yang akan diatur (tidak boleh null atau kosong).
     * @throws IllegalArgumentException Jika nama spare part null atau kosong.
     */
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama spare part tidak boleh null atau kosong.");
        }
        this.nama = nama.trim();
    }
    
    /**
     * Mengembalikan harga spare part.
     * 
     * @return Harga spare part sebagai double.
     */
    public double getHarga() {
        return harga;
    }
    
    /**
     * Mengatur harga untuk spare part.
     * 
     * @param harga Harga spare part yang akan diatur (tidak boleh negatif).
     * @throws IllegalArgumentException Jika harga spare part negatif.
     */
    public void setHarga(double harga) {
        if (harga < 0) {
            throw new IllegalArgumentException("Harga spare part tidak boleh negatif.");
        }
        this.harga = harga;
    }
    
    /**
     * Mengembalikan stok spare part.
     * 
     * @return Stok spare part sebagai integer.
     */
    public int getStok() {
        return stok;
    }
    
    /**
     * Mengatur stok untuk spare part.
     * 
     * @param stok Stok spare part yang akan diatur (tidak boleh negatif).
     * @throws IllegalArgumentException Jika stok spare part negatif.
     */
    public void setStok(int stok) {
        if (stok < 0) {
            throw new IllegalArgumentException("Stok spare part tidak boleh negatif.");
        }
        this.stok = stok;
    }
    
    /**
     * Mengembalikan jumlah spare part yang telah digunakan.
     * 
     * @return Jumlah digunakan sebagai integer.
     */
    public int getJumlahDigunakan() {
        return jumlahDigunakan;
    }
    
    /**
     * Mengatur jumlah digunakan untuk spare part.
     * 
     * @param jumlahDigunakan Jumlah digunakan yang akan diatur (tidak boleh negatif).
     * @throws IllegalArgumentException Jika jumlah digunakan negatif.
     */
    public void setJumlahDigunakan(int jumlahDigunakan) {
        if (jumlahDigunakan < 0) {
            throw new IllegalArgumentException("Jumlah digunakan tidak boleh negatif.");
        }
        this.jumlahDigunakan = jumlahDigunakan;
    }
    
    /**
     * Mengurangi stok spare part berdasarkan jumlah tertentu.
     * 
     * @param jumlah Jumlah yang akan dikurangkan dari stok (harus positif).
     * @throws IllegalArgumentException Jika jumlah pengurangan tidak valid (<= 0).
     * @throws IllegalStateException Jika stok tidak mencukupi untuk pengurangan.
     */
    public void kurangiStok(int jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah untuk mengurangi stok harus positif.");
        }
        if (this.stok < jumlah) {
            throw new IllegalStateException(
                    "Stok untuk spare part '" + this.nama + "' tidak mencukupi (tersisa: " + this.stok + ", dibutuhkan: " + jumlah + ")."
            );
        }
        this.stok -= jumlah;
        this.jumlahDigunakan += jumlah;
    }
    
    /**
     * Membandingkan dua objek SparePart berdasarkan kode part.
     * 
     * @param o Objek yang akan dibandingkan.
     * @return true jika objek memiliki kode part yang sama, false jika tidak.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SparePart sparePart = (SparePart) o;
        return Objects.equals(kodePart, sparePart.kodePart);
    }
    
    /**
     * Menghasilkan hash code berdasarkan kode part.
     * 
     * @return Hash code dari kode part.
     */
    @Override
    public int hashCode() {
        return Objects.hash(kodePart);
    }
    
    /**
     * Mengembalikan representasi string dari objek SparePart.
     * 
     * @return Representasi string dari objek SparePart.
     */
    @Override
    public String toString() {
        return "SparePart{" +
                "kodePart='" + (kodePart != null ? kodePart : "N/A") + '\'' +
                ", nama='" + (nama != null ? nama : "N/A") + '\'' +
                ", harga=" + harga +
                ", stok=" + stok +
                ", jumlahDigunakan=" + jumlahDigunakan +
                '}';
    }
}
