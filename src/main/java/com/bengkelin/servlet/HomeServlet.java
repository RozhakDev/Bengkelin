/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.bengkelin.servlet;

import com.bengkelin.dao.PelangganDAO;
import com.bengkelin.dao.SparePartDAO;
import com.bengkelin.dao.TransaksiDAO;
import com.bengkelin.model.Pelanggan;
import com.bengkelin.model.SparePart;
import com.bengkelin.model.Transaksi;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kelas HomeServlet digunakan untuk menangani permintaan terkait halaman dashboard.
 * Kelas ini menyediakan data statistik seperti total pelanggan, transaksi hari ini,
 * pendapatan bulan ini, dan informasi lainnya untuk ditampilkan di halaman utama.
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    
    private transient PelangganDAO pelangganDAO;
    private transient SparePartDAO sparePartDAO;
    private transient TransaksiDAO transaksiDAO;
    
    private static final String PAGE_HOME = "/beranda/home.jsp";
    private static final String PAGE_ERROR = "/error.jsp";
    
    private static final int MAX_RECENT_TRANSACTIONS_DISPLAY = 5;
    private static final int LOW_STOCK_THRESHOLD = 5;
    
    private static final String ATTR_TOTAL_PELANGGAN = "totalPelanggan";
    private static final String ATTR_TRANSAKSI_HARI_INI = "transaksiHariIni";
    private static final String ATTR_TOTAL_SPARE_PART = "totalSparePart";
    private static final String ATTR_PENDAPATAN_BULAN_INI = "pendapatanBulanIni";
    private static final String ATTR_RECENT_TRANSACTIONS = "recentTransactions";
    private static final String ATTR_LOW_STOCK_ITEMS = "lowStockItems";
    
    /**
     * Inisialisasi servlet dengan membuat instance DAO yang diperlukan.
     */
    @Override
    public void init() {
        pelangganDAO = new PelangganDAO();
        sparePartDAO = new SparePartDAO();
        transaksiDAO = new TransaksiDAO();
    }
    
    /**
     * Menangani permintaan GET untuk memuat data dashboard.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            prepareDashboardData(request);
            forwardToPage(request, response, PAGE_HOME);
        } catch (SQLException ex) {
            log("Error database di HomeServlet (doGet): " + ex.getMessage(), ex);
            request.setAttribute("javax.servlet.error.exception", ex);
            request.setAttribute("javax.servlet.error.message", "Terjadi error database saat memuat data dashboard.");
            forwardToPage(request, response, PAGE_ERROR);
        }
    }
    
    /**
     * Menyiapkan data dashboard untuk ditampilkan di halaman utama.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private void prepareDashboardData(HttpServletRequest request) throws SQLException {
        request.setAttribute(ATTR_TOTAL_PELANGGAN, getTotalPelanggan());
        request.setAttribute(ATTR_TOTAL_SPARE_PART, getTotalSparePart());
        request.setAttribute(ATTR_TRANSAKSI_HARI_INI, getTransaksiHariIni());
        request.setAttribute(ATTR_PENDAPATAN_BULAN_INI, getPendapatanBulanIni());
        request.setAttribute(ATTR_RECENT_TRANSACTIONS, getRecentTransactions());
        request.setAttribute(ATTR_LOW_STOCK_ITEMS, getLowStockItems());
    }
    
    /**
     * Mengambil total jumlah pelanggan dari database.
     * 
     * @return Total jumlah pelanggan.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private int getTotalPelanggan() throws SQLException {
        return pelangganDAO.semuaPelanggan().size();
    }
    
    /**
     * Mengambil total jumlah spare part dari database.
     * 
     * @return Total jumlah spare part.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private int getTotalSparePart() throws SQLException {
        return sparePartDAO.semuaSparePart().size();
    }
    
    /**
     * Mengambil jumlah transaksi yang terjadi hari ini.
     * 
     * @return Jumlah transaksi hari ini.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private int getTransaksiHariIni() throws SQLException {
        Date today = new Date();
        return transaksiDAO.getTransaksiByPeriode(today, today).size();
    }
    
    /**
     * Menghitung total pendapatan bulan ini dari semua transaksi.
     * 
     * @return Total pendapatan bulan ini.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private double getPendapatanBulanIni() throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDayOfMonth = cal.getTime();
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDayOfMonth = cal.getTime();
        
        List<Transaksi> transaksiBulanIni = transaksiDAO.getTransaksiByPeriode(firstDayOfMonth, lastDayOfMonth);
        return transaksiBulanIni.stream()
                .mapToDouble(Transaksi::getTotalBiaya)
                .sum();
    }
    
    /**
     * Mengambil daftar transaksi terbaru untuk ditampilkan di dashboard.
     * Jumlah transaksi yang ditampilkan dibatasi oleh MAX_RECENT_TRANSACTIONS_DISPLAY.
     * 
     * @return Daftar transaksi terbaru sebagai List<Transaksi>.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private List<Transaksi> getRecentTransactions() throws SQLException {
        List<Transaksi> recentTransactions = transaksiDAO.semuaTransaksi();
        if (recentTransactions.size() > MAX_RECENT_TRANSACTIONS_DISPLAY) {
            return recentTransactions.subList(0, MAX_RECENT_TRANSACTIONS_DISPLAY);
        }
        return recentTransactions;
    }
    
    /**
     * Mengambil daftar spare part dengan stok di bawah batas rendah.
     * 
     * @return Daftar spare part dengan stok rendah sebagai List<SparePart>.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private List<SparePart> getLowStockItems() throws SQLException {
        return sparePartDAO.semuaSparePart().stream()
                .filter(sp -> sp.getStok() < LOW_STOCK_THRESHOLD)
                .collect(Collectors.toList());
    }
    
    /**
     * Mengarahkan permintaan ke halaman tertentu.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @param pagePath Path halaman yang akan dituju.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void forwardToPage(HttpServletRequest request, HttpServletResponse response, String pagePath) 
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(pagePath);
        dispatcher.forward(request, response);
    }
}
