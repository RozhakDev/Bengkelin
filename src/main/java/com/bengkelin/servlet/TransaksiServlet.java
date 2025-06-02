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
import com.bengkelin.util.DateUtil;
import com.bengkelin.util.TarifService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Kelas TransaksiServlet digunakan untuk menangani permintaan terkait transaksi.
 * Kelas ini menyediakan operasi seperti menampilkan daftar transaksi, formulir input,
 * detail transaksi, pembuatan, dan penghapusan transaksi.
 */
@WebServlet(name = "TransaksiServlet", urlPatterns = {"/transaksi"})
public class TransaksiServlet extends HttpServlet {
    
    private transient TransaksiDAO transaksiDAO;
    private transient PelangganDAO pelangganDAO;
    private transient SparePartDAO sparePartDAO;
    
    private static final String PAGE_LIST_TRANSAKSI = "/transaksi/list.jsp";
    private static final String PAGE_FORM_TRANSAKSI = "/transaksi/form.jsp";
    private static final String PAGE_DETAIL_TRANSAKSI = "/transaksi/detail.jsp";
    private static final String PAGE_ERROR = "/error.jsp";
    
    private static final String ACTION_LIST = "list";
    private static final String ACTION_NEW = "new";
    private static final String ACTION_CREATE = "create";
    private static final String ACTION_DETAIL = "detail";
    private static final String ACTION_DELETE = "delete";
    
    private static final String ATTR_TRANSAKSI_LIST = "transaksiList";
    private static final String ATTR_TRANSAKSI = "transaksi";
    private static final String ATTR_PELANGGAN_LIST = "pelangganList";
    private static final String ATTR_SPAREPART_LIST = "sparePartList";
    private static final String ATTR_SERVICE_TYPES = "serviceTypes";
    private static final String ATTR_TODAY_DATE = "todayDate";
    private static final String ATTR_START_DATE = "startDate";
    private static final String ATTR_END_DATE = "endDate";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    
    /**
     * Inisialisasi servlet dengan membuat instance DAO yang diperlukan.
     */
    @Override
    public void init() {
        transaksiDAO = new TransaksiDAO();
        pelangganDAO = new PelangganDAO();
        sparePartDAO = new SparePartDAO();
    }
    
    /**
     * Menangani permintaan GET untuk menampilkan daftar transaksi, formulir input,
     * atau detail transaksi.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = Optional.ofNullable(request.getParameter("action")).orElse(ACTION_LIST);
        
        try {
            switch (action) {
                case ACTION_NEW:
                    showNewTransaksiForm(request, response);
                    break;
                case ACTION_DETAIL:
                    showTransaksiDetail(request, response);
                    break;
                case ACTION_LIST:
                default:
                    listTransaksi(request, response);
                    break;
            }
        } catch (SQLException ex) {
            handleGenericError(request, response, "Terjadi error database saat memproses data transaksi.", ex);
        } catch (ParseException ex) {
            handleGenericError(request, response, "Format tanggal untuk filter tidak valid.", ex);
        }
    }
    
    /**
     * Menangani permintaan POST untuk membuat atau menghapus transaksi.
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
            handleGenericError(request, response, "Aksi tidak valid.", null);
            return;
        }
        
        try {
            switch (action) {
                case ACTION_CREATE:
                    createTransaksi(request, response);
                    break;
                case ACTION_DELETE:
                    deleteTransaksi(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/transaksi?action=" + ACTION_LIST);
                    break;
            }
        } catch (SQLException ex) {
            handleGenericError(request, response, "Terjadi error database saat menyimpan data transaksi.", ex);
        } catch (ParseException ex) {
            log("Error parsing tanggal saat membuat transaksi: " + ex.getMessage(), ex);
            request.setAttribute(ATTR_ERROR_MESSAGE, "Format tanggal transaksi tidak valid. Gunakan format yyyy-MM-dd.");
            try {
                loadFormDependencies(request);
            } catch (SQLException dbEx) {
                log("Gagal memuat dependensi form setelah ParseException: " + dbEx.getMessage(), dbEx);
            }
            forwardToPage(request, response, PAGE_FORM_TRANSAKSI);
        } catch (IllegalArgumentException ex) {
            log("Input tidak valid saat membuat transaksi: " + ex.getMessage(), ex);
            request.setAttribute(ATTR_ERROR_MESSAGE, ex.getMessage());
            try {
                loadFormDependencies(request);
            } catch (SQLException dbEx) {
                log("Gagal memuat dependensi form setelah IllegalArgumentException: " + dbEx.getMessage(), dbEx);
            }
            forwardToPage(request, response, PAGE_FORM_TRANSAKSI);
        }
    }
    
    /**
     * Menampilkan daftar transaksi berdasarkan rentang tanggal atau semua transaksi.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     * @throws ParseException Jika format tanggal tidak valid.
     */
    private void listTransaksi(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException, ParseException {
        String startDateStr = request.getParameter(ATTR_START_DATE);
        String endDateStr = request.getParameter(ATTR_END_DATE);
        List<Transaksi> transaksiList;
        
        if (startDateStr != null && !startDateStr.trim().isEmpty() &&
                endDateStr != null && !endDateStr.trim().isEmpty() &&
                DateUtil.isValidDate(startDateStr) && DateUtil.isValidDate(endDateStr)) {
            Date startDate = DateUtil.parseDate(startDateStr);
            Date endDate = DateUtil.parseDate(endDateStr);
            
            if (startDate.after(endDate)) {
                request.setAttribute(ATTR_ERROR_MESSAGE, "Tanggal awal filter tidak boleh setelah tanggal akhir.");
                transaksiList = transaksiDAO.semuaTransaksi();
            } else {
                transaksiList = transaksiDAO.getTransaksiByPeriode(startDate, endDate);
                request.setAttribute(ATTR_START_DATE, startDateStr);
                request.setAttribute(ATTR_END_DATE, endDateStr);
            }
        } else {
            transaksiList = transaksiDAO.semuaTransaksi();
        }
        
        request.setAttribute(ATTR_TRANSAKSI_LIST, transaksiList);
        forwardToPage(request, response, PAGE_LIST_TRANSAKSI);
    }
    
    /**
     * Menampilkan formulir untuk menambahkan transaksi baru.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showNewTransaksiForm(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        loadFormDependencies(request);
        request.setAttribute(ATTR_TODAY_DATE, DateUtil.getToday());
        forwardToPage(request, response, PAGE_FORM_TRANSAKSI);
    }
    
    /**
     * Memuat dependensi formulir seperti daftar pelanggan, spare part, dan jenis layanan.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     */
    private void loadFormDependencies(HttpServletRequest request) throws SQLException {
        request.setAttribute(ATTR_PELANGGAN_LIST, pelangganDAO.semuaPelanggan());
        request.setAttribute(ATTR_SPAREPART_LIST, sparePartDAO.semuaSparePart());
        request.setAttribute(ATTR_SERVICE_TYPES, TarifService.JenisService.values());
    }
    
    /**
     * Menampilkan detail transaksi berdasarkan nomor transaksi.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showTransaksiDetail(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        String noTransaksi = request.getParameter("noTransaksi");
        if (noTransaksi == null || noTransaksi.trim().isEmpty()) {
            handleGenericError(request, response, "Nomor transaksi tidak valid untuk melihat detail.", null);
            return;
        }
        
        Transaksi transaksi = transaksiDAO.dapatkanTransaksiByNo(noTransaksi);
        if (transaksi == null) {
            request.setAttribute(ATTR_ERROR_MESSAGE, "Transaksi dengan nomor '" + noTransaksi + "' tidak ditemukan.");
            try {
                listTransaksi(request, response);
            } catch (ParseException e) {
                handleGenericError(request, response, "Gagal menampilkan daftar transaksi setelah detail tidak ditemukan.", e);
            }
            return;
        }
        
        request.setAttribute(ATTR_TRANSAKSI, transaksi);
        forwardToPage(request, response, PAGE_DETAIL_TRANSAKSI);
    }
    
    /**
     * Membuat transaksi baru berdasarkan data dari request.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat menyimpan data ke database.
     * @throws IOException Jika terjadi kesalahan I/O.
     * @throws ParseException Jika format tanggal tidak valid.
     * @throws IllegalArgumentException Jika input tidak valid.
     */
    private void createTransaksi(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, ParseException, IllegalArgumentException {
        
        Transaksi transaksi = new Transaksi();
        
        String noTransaksiStr = request.getParameter("noTransaksi");
        if (noTransaksiStr == null || noTransaksiStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor transaksi tidak boleh kosong.");
        }
        transaksi.setNoTransaksi(noTransaksiStr);
        
        String tanggalStr = request.getParameter("tanggal");
        if (tanggalStr == null || tanggalStr.trim().isEmpty() || !DateUtil.isValidDate(tanggalStr)) {
            throw new IllegalArgumentException("Format tanggal tidak valid atau tanggal kosong. Gunakan format yyyy-MM-dd.");
        }
        transaksi.setTanggal(DateUtil.parseDate(tanggalStr));
        
        
        String idPelanggan = request.getParameter("pelanggan");
        if (idPelanggan == null || idPelanggan.trim().isEmpty()) {
            throw new IllegalArgumentException("Pelanggan harus dipilih.");
        }
        Pelanggan pelanggan = pelangganDAO.dapatkanPelangganById(idPelanggan);
        if (pelanggan == null) {
            throw new IllegalArgumentException("Pelanggan dengan ID '" + idPelanggan + "' tidak ditemukan.");
        }
        transaksi.setPelanggan(pelanggan);
        boolean isMember = pelanggan.isMember();
        
        List<SparePart> selectedSpareParts = new ArrayList<>();
        String[] kodeParts = request.getParameterValues("spareParts");
        String[] quantitiesStr = request.getParameterValues("quantities");
        
        if (kodeParts != null && quantitiesStr != null && kodeParts.length == quantitiesStr.length) {
            for (int i = 0; i < kodeParts.length; i++) {
                if (kodeParts[i] == null || kodeParts[i].trim().isEmpty() || quantitiesStr[i] == null || quantitiesStr[i].trim().isEmpty() ) {
                    continue;
                }
                
                SparePart sparePartMaster = sparePartDAO.dapatkanSparePartByKode(kodeParts[i]);
                if (sparePartMaster == null) {
                    throw new IllegalArgumentException("Spare part dengan kode '" + kodeParts[i] + "' tidak ditemukan.");
                }
                
                int quantity;
                try {
                    quantity = Integer.parseInt(quantitiesStr[i]);
                    if (quantity <= 0) {
                        throw new IllegalArgumentException("Jumlah spare part '" + sparePartMaster.getNama() + "' harus lebih dari 0.");
                    }
                    if (quantity > sparePartMaster.getStok()) {
                        throw new IllegalArgumentException("Stok spare part '" + sparePartMaster.getNama() + "' tidak mencukupi (tersisa: " + sparePartMaster.getStok() + ").");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Format jumlah untuk spare part '" + sparePartMaster.getNama() + "' tidak valid.");
                }
                
                SparePart sparePartTransaksi = new SparePart();
                sparePartTransaksi.setKodePart(sparePartMaster.getKodePart());
                sparePartTransaksi.setNama(sparePartMaster.getNama());
                sparePartTransaksi.setHarga(sparePartMaster.getHarga());
                sparePartTransaksi.setJumlahDigunakan(quantity);
                selectedSpareParts.add(sparePartTransaksi);
            }
        }
        transaksi.setSpareParts(selectedSpareParts);
        
        String jenisServiceParam = request.getParameter("jenisService");
        if (jenisServiceParam == null || jenisServiceParam.trim().isEmpty()) {
            throw new IllegalArgumentException("Jenis service harus dipilih.");
        }
        TarifService.JenisService jenisService;
        try {
            jenisService = TarifService.JenisService.valueOf(jenisServiceParam);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Jenis service tidak valid.");
        }
        
        transaksi.hitungTotalBiaya(jenisService, isMember);
        double biayaJasaAwal = TarifService.getBiayaDasarLayanan(jenisService);
        transaksi.setBiayaService(biayaJasaAwal);
        double totalHargaSparePart = selectedSpareParts.stream() 
                .mapToDouble(sp -> sp.getHarga() * sp.getJumlahDigunakan()) 
                .sum();
        double subTotal = biayaJasaAwal + totalHargaSparePart;
        double totalAkhir = isMember ? (subTotal * TarifService.getFaktorDiskonMember()) : subTotal;
        transaksi.setTotalBiaya(totalAkhir);
        
        
        transaksiDAO.tambahTransaksi(transaksi);
        
        for (SparePart spTransaksi : selectedSpareParts) {
            sparePartDAO.updateStokDanPopularitas(spTransaksi.getKodePart(), spTransaksi.getJumlahDigunakan());
        }
        
        response.sendRedirect(request.getContextPath() + "/transaksi?action=" + ACTION_LIST + "&" + ATTR_SUCCESS_MESSAGE + "=TransaksiBerhasilDitambahkan");
    }
    
    /**
     * Menghapus transaksi berdasarkan nomor transaksi.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat menghapus data dari database.
     * @throws IOException Jika terjadi kesalahan I/O.
     * @throws ServletException Jika terjadi kesalahan servlet.
     */
    private void deleteTransaksi(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, ServletException {
        String noTransaksi = request.getParameter("noTransaksi");
        if (noTransaksi == null || noTransaksi.trim().isEmpty()) {
            handleGenericError(request, response, "Nomor transaksi tidak valid untuk dihapus.", null);
            return;
        }
        transaksiDAO.hapusTransaksi(noTransaksi);
        response.sendRedirect(request.getContextPath() + "/transaksi?action=" + ACTION_LIST + "&" + ATTR_SUCCESS_MESSAGE + "=TransaksiBerhasilDihapus");
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
            log("Error di TransaksiServlet: " + message, throwable);
        } else {
            log("Error di TransaksiServlet: " + message);
        }
        request.setAttribute("javax.servlet.error.exception", throwable);
        request.setAttribute(ATTR_ERROR_MESSAGE, message);
        forwardToPage(request, response, PAGE_ERROR);
    }
}
