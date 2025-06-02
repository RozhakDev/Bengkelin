/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.model;

import java.util.Objects;

/**
 * Kelas Pelanggan merepresentasikan informasi tentang pelanggan di bengkel.
 * Kelas ini mencakup atribut seperti ID pelanggan, nama, nomor telepon, alamat,
 * status keanggotaan, jumlah transaksi, dan jumlah quantity yang digunakan.
 */
public class Pelanggan {
    private String idPelanggan;
    private String nama;
    private String noTelepon;
    private String alamat;
    private boolean isMember;
    private int transactionCount;
    private int quantityUsed;
    
    /**
     * Konstruktor default untuk inisialisasi objek Pelanggan.
     * Status keanggotaan diinisialisasi sebagai non-member,
     * jumlah transaksi dan quantity digunakan diinisialisasi dengan nilai 0.
     */
    public Pelanggan() {
        this.isMember = false;
        this.transactionCount = 0;
        this.quantityUsed = 0;
    }
    
    /**
     * Konstruktor untuk inisialisasi objek Pelanggan dengan parameter.
     * 
     * @param idPelanggan ID unik untuk pelanggan (tidak boleh null atau kosong).
     * @param nama Nama pelanggan (tidak boleh null atau kosong).
     * @param noTelepon Nomor telepon pelanggan.
     * @param alamat Alamat pelanggan.
     * @throws IllegalArgumentException Jika parameter tidak memenuhi syarat validasi.
     */
    public Pelanggan(String idPelanggan, String nama, String noTelepon, String alamat) {
        this();
        this.setIdPelanggan(idPelanggan);
        this.setNama(nama);
        this.setNoTelepon(noTelepon);
        this.setAlamat(alamat);
    }
    
    /**
     * Mengembalikan ID pelanggan.
     * 
     * @return ID pelanggan sebagai String.
     */
    public String getIdPelanggan() {
        return idPelanggan;
    }
    
    /**
     * Mengatur ID pelanggan.
     * 
     * @param idPelanggan ID pelanggan yang akan diatur (tidak boleh null atau kosong).
     * @throws IllegalArgumentException Jika ID pelanggan null atau kosong.
     */
    public void setIdPelanggan(String idPelanggan) {
        if (idPelanggan == null || idPelanggan.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Pelanggan tidak boleh null atau kosong.");
        }
        this.idPelanggan = idPelanggan.trim();
    }
    
    /**
     * Mengembalikan nama pelanggan.
     * 
     * @return Nama pelanggan sebagai String.
     */
    public String getNama() {
        return nama;
    }
    
    /**
     * Mengatur nama pelanggan.
     * 
     * @param nama Nama pelanggan yang akan diatur (tidak boleh null atau kosong).
     * @throws IllegalArgumentException Jika nama pelanggan null atau kosong.
     */
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama pelanggan tidak boleh null atau kosong.");
        }
        this.nama = nama.trim();
    }
    
    /**
     * Mengembalikan nomor telepon pelanggan.
     * 
     * @return Nomor telepon pelanggan sebagai String (bisa null).
     */
    public String getNoTelepon() {
        return noTelepon;
    }
    
    /**
     * Mengatur nomor telepon pelanggan.
     * 
     * @param noTelepon Nomor telepon pelanggan yang akan diatur.
     */
    public void setNoTelepon(String noTelepon) {
        this.noTelepon = (noTelepon != null) ? noTelepon.trim() : null;
    }
    
    /**
     * Mengembalikan alamat pelanggan.
     * 
     * @return Alamat pelanggan sebagai String (bisa null).
     */
    public String getAlamat() {
        return alamat;
    }
    
    /**
     * Mengatur alamat pelanggan.
     * 
     * @param alamat Alamat pelanggan yang akan diatur.
     */
    public void setAlamat(String alamat) {
        this.alamat = (alamat != null) ? alamat.trim() : null;
    }
    
    /**
     * Memeriksa apakah pelanggan merupakan member.
     * 
     * @return true jika pelanggan adalah member, false jika bukan.
     */
    public boolean isMember() {
        return isMember;
    }
    
    /**
     * Mengatur status keanggotaan pelanggan.
     * 
     * @param member Status keanggotaan pelanggan (true jika member, false jika bukan).
     */
    public void setMember(boolean member) {
        isMember = member;
    }
    
    /**
     * Mengembalikan jumlah transaksi pelanggan.
     * 
     * @return Jumlah transaksi sebagai integer.
     */
    public int getTransactionCount() {
        return transactionCount;
    }
    
    /**
     * Mengatur jumlah transaksi pelanggan.
     * 
     * @param transactionCount Jumlah transaksi yang akan diatur (tidak boleh negatif).
     * @throws IllegalArgumentException Jika jumlah transaksi negatif.
     */
    public void setTransactionCount(int transactionCount) {
        if (transactionCount < 0) {
            throw new IllegalArgumentException("Jumlah transaksi tidak boleh negatif.");
        }
        this.transactionCount = transactionCount;
    }
    
    /**
     * Mengembalikan jumlah quantity yang digunakan oleh pelanggan.
     * 
     * @return Jumlah quantity yang digunakan sebagai integer.
     */
    public int getQuantityUsed() {
        return quantityUsed;
    }
    
    /**
     * Mengatur jumlah quantity yang digunakan oleh pelanggan.
     * 
     * @param quantityUsed Jumlah quantity yang digunakan (tidak boleh negatif).
     * @throws IllegalArgumentException Jika jumlah quantity yang digunakan negatif.
     */
    public void setQuantityUsed(int quantityUsed) {
        if (quantityUsed < 0) {
            throw new IllegalArgumentException("Jumlah quantity yang digunakan tidak boleh negatif.");
        }
        this.quantityUsed = quantityUsed;
    }
    
    /**
     * Membandingkan dua objek Pelanggan berdasarkan ID pelanggan.
     * 
     * @param o Objek yang akan dibandingkan.
     * @return true jika objek memiliki ID pelanggan yang sama, false jika tidak.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pelanggan pelanggan = (Pelanggan) o;
        return Objects.equals(idPelanggan, pelanggan.idPelanggan);
    }
    
    /**
     * Menghasilkan hash code berdasarkan ID pelanggan.
     * 
     * @return Hash code dari ID pelanggan.
     */
    @Override
    public int hashCode() {
        return Objects.hash(idPelanggan);
    }
    
    /**
     * Mengembalikan representasi string dari objek Pelanggan.
     * 
     * @return Representasi string dari objek Pelanggan.
     */
    @Override
    public String toString() {
        return "Pelanggan{" +
                "idPelanggan='" + (idPelanggan != null ? idPelanggan : "N/A") + '\'' +
                ", nama='" + (nama != null ? nama : "N/A") + '\'' +
                ", noTelepon='" + (noTelepon != null ? noTelepon : "N/A") + '\'' +
                ", alamat='" + (alamat != null ? alamat : "N/A") + '\'' +
                ", isMember=" + isMember +
                ", transactionCount=" + transactionCount +
                ", quantityUsed=" + quantityUsed +
                '}';
    }
}
