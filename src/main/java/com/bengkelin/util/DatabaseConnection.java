/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bengkelin.util;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Kelas utilitas DatabaseConnection digunakan untuk mengelola koneksi ke database MySQL.
 * Kelas ini dirancang sebagai kelas final untuk mencegah pewarisan.
 */
public final class DatabaseConnection {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bengkelin_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /**
     * Konstruktor privat untuk mencegah instansiasi kelas utilitas ini.
     * Penggunaan konstruktor ini akan menghasilkan pengecualian UnsupportedOperationException.
     */
    private DatabaseConnection() {
        throw new UnsupportedOperationException("Kelas utilitas DatabaseConnection tidak dapat diinstansiasi.");
    }
    
    /**
     * Metode statis untuk mendapatkan koneksi ke database MySQL.
     * 
     * @return Objek Connection yang merepresentasikan koneksi ke database.
     * @throws SQLException Jika terjadi kesalahan saat menghubungkan ke database.
     *         Pengecualian ini juga mencakup kasus ketika driver JDBC tidak ditemukan.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL (" + JDBC_DRIVER + ") tidak ditemukan di classpath.", e);
        }
    }
}
