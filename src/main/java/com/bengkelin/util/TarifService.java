/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.util;

import com.bengkelin.model.SparePart;
import java.util.List;
import java.util.Objects;

/**
 * Kelas utilitas TarifService digunakan untuk menghitung biaya layanan bengkel.
 * Kelas ini dirancang sebagai kelas final untuk mencegah pewarisan.
 */
public final class TarifService {
    
    private static final double TARIF_DASAR_GANTI_OIL = 50000.0;
    private static final double TARIF_DASAR_TUNE_UP = 75000.0;
    private static final double TARIF_DASAR_GANTI_BAN = 30000.0;
    private static final double TARIF_DASAR_LAINNYA = 40000.0;
    
    private static final double PERSENTASE_DISKON_MEMBER = 0.10; // 10% diskon
    private static final double FAKTOR_PENGALI_NON_MEMBER = 1.0;
    private static final double FAKTOR_PENGALI_MEMBER = FAKTOR_PENGALI_NON_MEMBER - PERSENTASE_DISKON_MEMBER; // Hasilnya 0.9
    
    /**
     * Enum JenisService mendefinisikan jenis-jenis layanan yang tersedia di bengkel.
     */
    public enum JenisService {
        GANTI_OIL,
        TUNE_UP,
        GANTI_BAN,
        LAINNYA
    }
    
    /**
     * Konstruktor privat untuk mencegah instansiasi kelas utilitas ini.
     * Penggunaan konstruktor ini akan menghasilkan pengecualian UnsupportedOperationException.
     */
    private TarifService() {
        throw new UnsupportedOperationException("Kelas utilitas TarifService tidak dapat diinstansiasi.");
    }
    
    /**
     * Menghitung total biaya layanan bengkel berdasarkan jenis layanan, daftar spare part, dan status keanggotaan.
     * 
     * @param jenisService Jenis layanan yang dipilih (tidak boleh null).
     * @param spareParts Daftar spare part yang digunakan (tidak boleh null, bisa kosong).
     * @param isMember Status keanggotaan pelanggan (true jika member, false jika bukan).
     * @return Total biaya layanan setelah memperhitungkan diskon member (jika ada).
     * @throws NullPointerException Jika jenisService atau spareParts adalah null.
     */
    public static double hitungTotalBiaya(JenisService jenisService, List<SparePart> spareParts, boolean isMember) {
        Objects.requireNonNull(jenisService, "Jenis layanan (jenisService) tidak boleh null.");
        Objects.requireNonNull(spareParts, "Daftar spare part (spareParts) tidak boleh null, bisa berupa list kosong.");

        double biayaDasarLayanan = getBiayaDasarLayanan(jenisService);
        
        double totalHargaSparePart = 0;
        for (SparePart sp : spareParts) {
            totalHargaSparePart += sp.getHarga() * sp.getJumlahDigunakan();
        }
        
        double subTotal = biayaDasarLayanan + totalHargaSparePart;
        
        double totalAkhir = isMember ? (subTotal * FAKTOR_PENGALI_MEMBER) : subTotal;
        
        return totalAkhir;
    }
    
    /**
     * Menghitung total biaya layanan bengkel untuk pelanggan non-member.
     * 
     * @param jenisService Jenis layanan yang dipilih (tidak boleh null).
     * @param spareParts Daftar spare part yang digunakan (tidak boleh null, bisa kosong).
     * @return Total biaya layanan tanpa diskon member.
     * @throws NullPointerException Jika jenisService atau spareParts adalah null.
     */
    public static double hitungTotalBiaya(JenisService jenisService, List<SparePart> spareParts) {
        return hitungTotalBiaya(jenisService, spareParts, false);
    }
    
    /**
     * Mendapatkan biaya dasar untuk jenis layanan tertentu.
     * 
     * @param jenisService Jenis layanan yang dipilih (tidak boleh null).
     * @return Biaya dasar layanan berdasarkan jenisService.
     * @throws NullPointerException Jika jenisService adalah null.
     */
    public static double getBiayaDasarLayanan(JenisService jenisService) {
        Objects.requireNonNull(jenisService, "Jenis layanan (jenisService) tidak boleh null untuk mendapatkan biaya dasar.");
        switch (jenisService) {
            case GANTI_OIL:
                return TARIF_DASAR_GANTI_OIL;
            case TUNE_UP:
                return TARIF_DASAR_TUNE_UP;
            case GANTI_BAN:
                return TARIF_DASAR_GANTI_BAN;
            case LAINNYA:
            default:
                return TARIF_DASAR_LAINNYA;
        }
    }
    
    /**
     * Mendapatkan faktor pengali untuk pelanggan member.
     * 
     * @return Faktor pengali member (0.9 untuk diskon 10%).
     */
    public static double getFaktorDiskonMember() {
        return FAKTOR_PENGALI_MEMBER;
    }
    
    /**
     * Mendapatkan persentase diskon untuk pelanggan member.
     * 
     * @return Persentase diskon member (0.10 atau 10%).
     */
    public static double getPersentaseDiskonMember() {
        return PERSENTASE_DISKON_MEMBER;
    }
}
