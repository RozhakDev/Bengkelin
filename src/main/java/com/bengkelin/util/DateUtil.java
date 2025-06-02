/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Kelas utilitas DateUtil digunakan untuk memanipulasi dan memvalidasi tanggal.
 * Kelas ini dirancang sebagai kelas final untuk mencegah pewarisan.
 */
public final class DateUtil {
    
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    private static final SimpleDateFormat DATE_FORMATTER = createDefaultFormatter();
    
    /**
     * Metode privat untuk membuat formatter tanggal dengan format default.
     * 
     * @return Objek SimpleDateFormat dengan format "yyyy-MM-dd" dan mode lenient dinonaktifkan.
     */
    private static SimpleDateFormat createDefaultFormatter() {
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        formatter.setLenient(false);
        return formatter;
    }
    
    /**
     * Konstruktor privat untuk mencegah instansiasi kelas utilitas ini.
     * Penggunaan konstruktor ini akan menghasilkan pengecualian UnsupportedOperationException.
     */
    private DateUtil() {
        throw new UnsupportedOperationException("Kelas utilitas DateUtil tidak dapat diinstansiasi.");
    }
    
    /**
     * Mengonversi string tanggal menjadi objek Date.
     * 
     * @param dateString String yang berisi tanggal dalam format "yyyy-MM-dd".
     * @return Objek Date yang merepresentasikan tanggal dari string input.
     * @throws ParseException Jika string tanggal tidak sesuai dengan format yang ditentukan.
     * @throws IllegalArgumentException Jika string tanggal null atau kosong.
     */
    public static Date parseDate(String dateString) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("String tanggal tidak boleh null atau kosong.");
        }
        
        return DATE_FORMATTER.parse(dateString.trim());
    }
    
    /**
     * Mengonversi objek Date menjadi string dengan format "yyyy-MM-dd".
     * 
     * @param date Objek Date yang akan diformat.
     * @return String yang berisi representasi tanggal dalam format "yyyy-MM-dd".
     * @throws NullPointerException Jika objek Date adalah null.
     */
    public static String formatDate(Date date) {
        Objects.requireNonNull(date, "Objek Date tidak boleh null untuk diformat.");
        
        return DATE_FORMATTER.format(date);
    }
    
    /**
     * Memvalidasi apakah string tanggal sesuai dengan format "yyyy-MM-dd".
     * 
     * @param dateString String yang akan divalidasi.
     * @return true jika string tanggal valid, false jika tidak valid atau null/kosong.
     */
    public static boolean isValidDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }
        try {
            DATE_FORMATTER.parse(dateString.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Mendapatkan tanggal hari ini dalam format "yyyy-MM-dd".
     * 
     * @return String yang berisi tanggal hari ini dalam format "yyyy-MM-dd".
     */
    public static String getToday() {
        return formatDate(new Date());
    }
    
    /**
     * Menghitung jumlah hari antara dua tanggal.
     * 
     * @param startDate Tanggal awal.
     * @param endDate Tanggal akhir.
     * @return Jumlah hari antara startDate dan endDate.
     * @throws NullPointerException Jika salah satu atau kedua tanggal adalah null.
     */
    public static long daysBetween(Date startDate, Date endDate) {
         Objects.requireNonNull(startDate, "Tanggal awal (startDate) tidak boleh null.");
        Objects.requireNonNull(endDate, "Tanggal akhir (endDate) tidak boleh null.");
        
        long diffInMillis = endDate.getTime() - startDate.getTime();
        return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }
}
