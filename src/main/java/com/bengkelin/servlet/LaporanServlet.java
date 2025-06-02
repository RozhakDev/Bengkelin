/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.bengkelin.servlet;

import com.bengkelin.dao.LaporanDAO;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Kelas LaporanServlet digunakan untuk menangani permintaan terkait laporan.
 * Kelas ini menyediakan operasi seperti menampilkan menu laporan, formulir input,
 * dan hasil laporan berdasarkan periode atau spare part.
 */
@WebServlet(name = "LaporanServlet", urlPatterns = {"/laporan"})
public class LaporanServlet extends HttpServlet {
    
    private transient LaporanDAO laporanDAO;
    private transient PelangganDAO pelangganDAO;
    private transient SparePartDAO sparePartDAO;
    private transient TransaksiDAO transaksiDAO;
    
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    
    private static final String PAGE_MENU_LAPORAN = "/laporan/menu.jsp";
    private static final String PAGE_FORM_LAPORAN_PERIODE = "/laporan/form_periode.jsp";
    private static final String PAGE_FORM_LAPORAN_SPAREPART = "/laporan/form_sparepart.jsp";
    private static final String PAGE_HASIL_LAPORAN_PERIODE = "/laporan/result_periode.jsp";
    private static final String PAGE_HASIL_LAPORAN_SPAREPART = "/laporan/result_sparepart.jsp";
    private static final String PAGE_ERROR = "/error.jsp";
    
    private static final String ACTION_MENU = "menu";
    private static final String ACTION_FORM_PERIODE = "formPeriode";
    private static final String ACTION_FORM_SPAREPART = "formSparePart";
    private static final String ACTION_HASIL_PERIODE = "hasilPeriode";
    private static final String ACTION_HASIL_SPAREPART = "hasilSparePart";
    
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_START_DATE = "startDate";
    private static final String ATTR_END_DATE = "endDate";
    private static final String ATTR_TRANSAKSI_LIST = "transaksiList";
    private static final String ATTR_TOTAL_PENDAPATAN = "totalPendapatan";
    private static final String ATTR_RATA_RATA_PENDAPATAN = "rataRataPendapatan";
    private static final String ATTR_TOP_CUSTOMER = "topCustomer";
    private static final String ATTR_SPAREPART_LIST = "sparePartList";
    private static final String ATTR_SELECTED_SPAREPART = "selectedSparePart";
    private static final String ATTR_PELANGGAN_LIST_BY_SPAREPART = "pelangganListBySparePart";
    private static final String ATTR_TOTAL_PENGGUNAAN_SPAREPART = "totalPenggunaanSparePart";
    private static final String ATTR_PENDAPATAN_DARI_SPAREPART = "pendapatanDariSparePart";
    private static final String ATTR_KODE_PART = "kodePart";
    
    private static final String ATTR_TOTAL_PELANGGAN_MENU = "totalPelangganMenu";
    private static final String ATTR_TOTAL_TRANSAKSI_MENU = "totalTransaksiMenu";
    private static final String ATTR_TOTAL_SPAREPART_MENU = "totalSparePartMenu";
    private static final String ATTR_PENDAPATAN_BULAN_INI_MENU = "pendapatanBulanIniMenu";
    
    /**
     * Inisialisasi servlet dengan membuat instance DAO yang diperlukan.
     */
    @Override
    public void init() {
        laporanDAO = new LaporanDAO();
        pelangganDAO = new PelangganDAO();
        sparePartDAO = new SparePartDAO();
        transaksiDAO = new TransaksiDAO();
        DATE_FORMATTER.setLenient(false);
    }
    
    /**
     * Menangani permintaan GET untuk menampilkan menu laporan, formulir input, atau hasil laporan.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = Optional.ofNullable(request.getParameter("action")).orElse(ACTION_MENU);
        
        try {
            switch (action) {
                case ACTION_FORM_PERIODE:
                    showFormLaporanPeriode(request, response);
                    break;
                case ACTION_FORM_SPAREPART:
                    showFormLaporanSparePart(request, response);
                    break;
                case ACTION_HASIL_PERIODE:
                    processLaporanPeriode(request, response);
                    break;
                case ACTION_HASIL_SPAREPART:
                    processLaporanSparePart(request, response);
                    break;
                case ACTION_MENU:
                default:
                    showMenuLaporan(request, response);
                    break;
            }
        } catch (SQLException ex) {
            handleGenericError(request, response, "Terjadi error database saat memproses laporan: " + ex.getMessage(), ex);
        } catch (ParseException ex) {
            handleGenericError(request, response, "Format tanggal tidak valid. Harap gunakan format YYYY-MM-DD.", ex);
        }
    }
    
    /**
     * Menangani permintaan POST untuk memproses hasil laporan.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            handleGenericError(request, response, "Aksi tidak ditemukan.", null);
            return;
        }
        
        try {
            switch (action) {
                case ACTION_HASIL_PERIODE:
                    processLaporanPeriode(request, response);
                    break;
                case ACTION_HASIL_SPAREPART:
                    processLaporanSparePart(request, response);
                    break;
                default:
                    showMenuLaporan(request, response);
                    break;
            }
        } catch (SQLException ex) {
            handleGenericError(request, response, "Terjadi error database saat memproses laporan: " + ex.getMessage(), ex);
        } catch (ParseException ex) {
            handleGenericError(request, response, "Format tanggal tidak valid. Harap gunakan format YYYY-MM-DD.", ex);
        }
    }
    
    /**
     * Menampilkan menu laporan dengan data statistik dasar.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showMenuLaporan(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        prepareMenuData(request);
        forwardToPage(request, response, PAGE_MENU_LAPORAN);
    }
    
    /**
     * Menyiapkan data statistik dasar untuk ditampilkan di menu laporan.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private void prepareMenuData(HttpServletRequest request) throws SQLException {
        request.setAttribute(ATTR_TOTAL_PELANGGAN_MENU, pelangganDAO.semuaPelanggan().size());
        request.setAttribute(ATTR_TOTAL_TRANSAKSI_MENU, transaksiDAO.semuaTransaksi().size());
        request.setAttribute(ATTR_TOTAL_SPAREPART_MENU, sparePartDAO.semuaSparePart().size());
        request.setAttribute(ATTR_PENDAPATAN_BULAN_INI_MENU, calculatePendapatanBulanIni());
    }
    
    /**
     * Menghitung total pendapatan bulan ini dari semua transaksi.
     * 
     * @return Total pendapatan bulan ini.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private double calculatePendapatanBulanIni() throws SQLException {
        Calendar cal = Calendar.getInstance();
        Date firstDayOfMonth = getFirstDayOfMonth(cal);
        Date lastDayOfMonth = getLastDayOfMonth(cal);
        
        List<Transaksi> transaksiBulanIni = transaksiDAO.getTransaksiByPeriode(firstDayOfMonth, lastDayOfMonth);
        return transaksiBulanIni.stream().mapToDouble(Transaksi::getTotalBiaya).sum();
    }
    
    /**
     * Menampilkan formulir laporan periode dengan nilai default untuk tanggal.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showFormLaporanPeriode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SimpleDateFormat inputDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = inputDateFormatter.format(new Date());
        request.setAttribute("defaultStartDate", todayStr);
        request.setAttribute("defaultEndDate", todayStr);
        forwardToPage(request, response, PAGE_FORM_LAPORAN_PERIODE);
    }
    
    /**
     * Menampilkan formulir laporan spare part dengan daftar spare part yang tersedia.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showFormLaporanSparePart(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        request.setAttribute(ATTR_SPAREPART_LIST, sparePartDAO.semuaSparePart());
        forwardToPage(request, response, PAGE_FORM_LAPORAN_SPAREPART);
    }
    
    /**
     * Memproses laporan periode berdasarkan rentang tanggal yang diberikan.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ParseException Jika format tanggal tidak valid.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void processLaporanPeriode(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ParseException, ServletException, IOException {
        String startStr = request.getParameter(ATTR_START_DATE);
        String endStr = request.getParameter(ATTR_END_DATE);
        
        request.setAttribute(ATTR_START_DATE, startStr);
        request.setAttribute(ATTR_END_DATE, endStr);
        
        if (!isValidDateRangeInput(startStr, endStr)) {
            request.setAttribute(ATTR_ERROR_MESSAGE, "Tanggal awal dan akhir harus diisi.");
            showFormLaporanPeriode(request, response);
            return;
        }
        
        Date startDate;
        Date endDate;
        try {
            startDate = DATE_FORMATTER.parse(startStr);
            endDate = DATE_FORMATTER.parse(endStr);
        } catch (ParseException e) {
            log("Error parsing tanggal di processLaporanPeriode: " + e.getMessage() + " Input: start='" + startStr + "', end='" + endStr + "'", e);
            request.setAttribute(ATTR_ERROR_MESSAGE, "Format tanggal tidak valid. Harap gunakan format YYYY-MM-DD.");
            showFormLaporanPeriode(request, response); // Kembali ke form
            return;
        }
        
        if (startDate.after(endDate)) {
            request.setAttribute(ATTR_ERROR_MESSAGE, "Tanggal awal tidak boleh setelah tanggal akhir.");
            showFormLaporanPeriode(request, response); // Kembali ke form
            return;
        }
        
        List<Transaksi> transaksiList = laporanDAO.getTransaksiByPeriode(startDate, endDate);
        
        prepareDataLaporanPeriode(request, transaksiList, startStr, endStr);
        forwardToPage(request, response, PAGE_HASIL_LAPORAN_PERIODE);
    }
    
    /**
     * Memeriksa apakah rentang tanggal valid (tidak kosong).
     * 
     * @param startStr Tanggal awal sebagai string.
     * @param endStr Tanggal akhir sebagai string.
     * @return true jika rentang tanggal valid, false jika tidak.
     */
    private boolean isValidDateRangeInput(String startStr, String endStr) {
        return startStr != null && !startStr.trim().isEmpty() && 
                endStr != null && !endStr.trim().isEmpty();
    }
    
    /**
     * Menyiapkan data untuk ditampilkan di halaman hasil laporan periode.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param transaksiList Daftar transaksi yang akan ditampilkan.
     * @param startStr Tanggal awal sebagai string.
     * @param endStr Tanggal akhir sebagai string.
     */
    private void prepareDataLaporanPeriode(HttpServletRequest request, List<Transaksi> transaksiList, String startStr, String endStr) {
        double totalPendapatan = transaksiList.stream().mapToDouble(Transaksi::getTotalBiaya).sum();
        double rataRataPendapatan = transaksiList.isEmpty() ? 0 : totalPendapatan / transaksiList.size();
        Pelanggan topCustomer = findTopCustomerByTransaksi(transaksiList);

        request.setAttribute(ATTR_TRANSAKSI_LIST, transaksiList);
        request.setAttribute(ATTR_TOTAL_PENDAPATAN, totalPendapatan);
        request.setAttribute(ATTR_RATA_RATA_PENDAPATAN, rataRataPendapatan);
        request.setAttribute(ATTR_TOP_CUSTOMER, topCustomer);
    }
    
    /**
     * Mencari pelanggan top berdasarkan jumlah transaksi.
     * 
     * @param transaksiList Daftar transaksi yang akan dianalisis.
     * @return Pelanggan dengan jumlah transaksi tertinggi, atau null jika tidak ada data.
     */
    private Pelanggan findTopCustomerByTransaksi(List<Transaksi> transaksiList) {
        if (transaksiList == null || transaksiList.isEmpty()) {
            return null;
        }
        return transaksiList.stream()
                .filter(t -> t.getPelanggan() != null)
                .collect(Collectors.groupingBy(
                        Transaksi::getPelanggan,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> {
                    Pelanggan p = entry.getKey();
                    p.setTransactionCount(entry.getValue().intValue());
                    return p;
                })
                .orElse(null);
    }
    
    /**
     * Memproses laporan penggunaan spare part berdasarkan kode part yang dipilih.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void processLaporanSparePart(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String kodePart = request.getParameter(ATTR_KODE_PART);
        request.setAttribute(ATTR_KODE_PART, kodePart); // Repopulate pilihan
        
        if (kodePart == null || kodePart.trim().isEmpty()) {
            request.setAttribute(ATTR_ERROR_MESSAGE, "Kode spare part harus dipilih.");
            showFormLaporanSparePart(request, response); // Kembali ke form
            return;
        }
        
        SparePart selectedSparePart = laporanDAO.getSparePartByKode(kodePart);
        if (selectedSparePart == null) {
            request.setAttribute(ATTR_ERROR_MESSAGE, "Spare part dengan kode '" + kodePart + "' tidak ditemukan.");
            showFormLaporanSparePart(request, response); // Kembali ke form
            return;
        }
        
        List<Pelanggan> pelangganList = laporanDAO.getPelangganBySparePart(kodePart);
        prepareDataLaporanSparePart(request, selectedSparePart, pelangganList);
        forwardToPage(request, response, PAGE_HASIL_LAPORAN_SPAREPART);
    }
    
    /**
     * Menyiapkan data untuk ditampilkan di halaman hasil laporan spare part.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param selectedSparePart Spare part yang dipilih.
     * @param pelangganList Daftar pelanggan yang menggunakan spare part tersebut.
     */
    private void prepareDataLaporanSparePart(HttpServletRequest request, SparePart selectedSparePart, List<Pelanggan> pelangganList) {
        int totalPenggunaan = 0;
        Pelanggan topCustomerBySparePart = null;
        int maxUsageBySingleCustomer = 0;
        
        if (pelangganList != null) {
            for (Pelanggan p : pelangganList) {
                if (p == null) continue;
                int quantityUsed = p.getQuantityUsed();
                totalPenggunaan += quantityUsed;
                if (quantityUsed > maxUsageBySingleCustomer) {
                    maxUsageBySingleCustomer = quantityUsed;
                    topCustomerBySparePart = p;
                }
            }
        }
        double pendapatanDariSparePart = totalPenggunaan * selectedSparePart.getHarga();
        
        request.setAttribute(ATTR_SELECTED_SPAREPART, selectedSparePart);
        request.setAttribute(ATTR_PELANGGAN_LIST_BY_SPAREPART, pelangganList);
        request.setAttribute(ATTR_TOTAL_PENGGUNAAN_SPAREPART, totalPenggunaan);
        request.setAttribute(ATTR_PENDAPATAN_DARI_SPAREPART, pendapatanDariSparePart);
        request.setAttribute(ATTR_TOP_CUSTOMER, topCustomerBySparePart);
    }
    
    /**
     * Mengembalikan tanggal pertama bulan dari objek Calendar.
     * 
     * @param cal Objek Calendar yang digunakan untuk perhitungan.
     * @return Tanggal pertama bulan sebagai Date.
     */
    private Date getFirstDayOfMonth(Calendar cal) {
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Mengembalikan tanggal terakhir bulan dari objek Calendar.
     * 
     * @param cal Objek Calendar yang digunakan untuk perhitungan.
     * @return Tanggal terakhir bulan sebagai Date.
     */
    private Date getLastDayOfMonth(Calendar cal) {
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
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
    
    /**
     * Menangani kesalahan umum dan mengarahkan ke halaman error.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @param message Pesan kesalahan yang akan ditampilkan.
     * @param throwable Objek Throwable yang menyebabkan kesalahan (boleh null).
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void handleGenericError(HttpServletRequest request, HttpServletResponse response, String message, Throwable throwable)
            throws ServletException, IOException {
        if (throwable != null) {
            log("Error di LaporanServlet: " + message, throwable);
        } else {
            log("Error di LaporanServlet: " + message);
        }
        request.setAttribute("javax.servlet.error.exception", throwable);
        request.setAttribute("javax.servlet.error.message", message);
        forwardToPage(request, response, PAGE_ERROR);
    }
}
