/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.bengkelin.servlet;

import com.bengkelin.dao.SparePartDAO;
import com.bengkelin.model.SparePart;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Kelas SparePartServlet digunakan untuk menangani permintaan terkait spare part.
 * Kelas ini menyediakan operasi seperti menampilkan daftar spare part, formulir input,
 * pencarian, pembaruan, penghapusan, dan laporan spare part populer.
 */
@WebServlet(name = "SparePartServlet", urlPatterns = {"/sparepart"})
public class SparePartServlet extends HttpServlet {
    
    private transient SparePartDAO sparePartDAO;
    
    private static final String PAGE_LIST_SPAREPART = "/sparepart/list.jsp";
    private static final String PAGE_FORM_SPAREPART = "/sparepart/form.jsp";
    private static final String PAGE_POPULAR_SPAREPART = "/sparepart/popular.jsp";
    private static final String PAGE_ERROR = "/error.jsp";
    
    private static final String ACTION_LIST = "list";
    private static final String ACTION_NEW = "new";
    private static final String ACTION_CREATE = "create";
    private static final String ACTION_EDIT = "edit";
    private static final String ACTION_UPDATE = "update";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_POPULAR = "popular";
    private static final String ACTION_SEARCH = "search";
    
    private static final String ATTR_SPAREPART_LIST = "sparePartList";
    private static final String ATTR_SPAREPART = "sparePart";
    private static final String ATTR_FORM_ACTION_URL = "formActionUrl";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String ATTR_SEARCH_KEYWORD = "searchKeyword";
    private static final String ATTR_TOTAL_USAGE = "totalUsage";
    private static final String ATTR_VIEW_MODE = "viewMode";
    
    /**
     * Inisialisasi servlet dengan membuat instance DAO yang diperlukan.
     */
    @Override
    public void init() {
        sparePartDAO = new SparePartDAO();
    }
    
    /**
     * Menangani permintaan GET untuk menampilkan daftar spare part, formulir input,
     * atau laporan spare part populer.
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
                    showNewSparePartForm(request, response);
                    break;
                case ACTION_EDIT:
                    showEditSparePartForm(request, response);
                    break;
                case ACTION_DELETE:
                    deleteSparePart(request, response);
                    break;
                case ACTION_POPULAR:
                    showPopularSpareParts(request, response);
                    break;
                case ACTION_SEARCH:
                case ACTION_LIST:
                default:
                    listSpareParts(request, response);
                    break;
            }
        } catch (SQLException ex) {
            handleGenericError(request, response, "Terjadi error database saat memproses data spare part.", ex);
        }
    }
    
    /**
     * Menangani permintaan POST untuk membuat atau memperbarui spare part.
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
                    createSparePart(request, response);
                    break;
                case ACTION_UPDATE:
                    updateSparePart(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/sparepart?action=" + ACTION_LIST);
                    break;
            }
        } catch (SQLException ex) {
            repopulateFormOnError(request, response, action, "Terjadi error database: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            repopulateFormOnError(request, response, action, "Input tidak valid: " + ex.getMessage());
        }
    }
    
    /**
     * Mengisi ulang formulir jika terjadi kesalahan selama proses pembuatan atau pembaruan spare part.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @param originalAction Aksi asli yang menyebabkan kesalahan.
     * @param errorMessage Pesan kesalahan yang akan ditampilkan.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void repopulateFormOnError(HttpServletRequest request, HttpServletResponse response, String originalAction, String errorMessage) 
            throws ServletException, IOException {
        request.setAttribute(ATTR_ERROR_MESSAGE, errorMessage);
        SparePart sparePart = new SparePart();
        
        String kodePart = request.getParameter("kodePart");
        if (kodePart != null && !kodePart.isEmpty()) {
            if (ACTION_UPDATE.equals(originalAction)) {
                try {
                    SparePart existing = sparePartDAO.dapatkanSparePartByKode(kodePart); // PERBAIKAN
                    if (existing != null) {
                        sparePart = existing;
                    }
                } catch (SQLException e) {
                    log("Gagal mengambil spare part saat repopulate form update: " + e.getMessage(), e);
                }
            }
            sparePart.setKodePart(kodePart);
        }
        trySetSparePartFromRequest(request, sparePart);
        request.setAttribute(ATTR_SPAREPART, sparePart);
        
        if (ACTION_CREATE.equals(originalAction)) {
            request.setAttribute(ATTR_FORM_ACTION_URL, request.getContextPath() + "/sparepart?action=" + ACTION_CREATE);
        } else if (ACTION_UPDATE.equals(originalAction)) {
            request.setAttribute(ATTR_FORM_ACTION_URL, request.getContextPath() + "/sparepart?action=" + ACTION_UPDATE);
        }
        forwardToPage(request, response, PAGE_FORM_SPAREPART);
    }
    
    /**
     * Menampilkan daftar spare part berdasarkan kata kunci pencarian atau semua spare part.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void listSpareParts(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        String searchKeyword = request.getParameter(ATTR_SEARCH_KEYWORD);
        List<SparePart> sparePartList;
        
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sparePartList = sparePartDAO.cariSparePart(searchKeyword);
            request.setAttribute(ATTR_SEARCH_KEYWORD, searchKeyword);
        } else {
            sparePartList = sparePartDAO.semuaSparePart();
        }
        
        request.setAttribute(ATTR_SPAREPART_LIST, sparePartList);
        request.setAttribute(ATTR_VIEW_MODE, ACTION_LIST);
        forwardToPage(request, response, PAGE_LIST_SPAREPART);
    }
    
    /**
     * Menampilkan laporan spare part populer berdasarkan jumlah digunakan.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showPopularSpareParts(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        List<SparePart> popularSpareParts = sparePartDAO.getSparePartPopuler();
        int totalUsage = popularSpareParts.stream().mapToInt(SparePart::getJumlahDigunakan).sum();
        
        request.setAttribute(ATTR_SPAREPART_LIST, popularSpareParts);
        request.setAttribute(ATTR_TOTAL_USAGE, totalUsage);
        request.setAttribute(ATTR_VIEW_MODE, ACTION_POPULAR);
        forwardToPage(request, response, PAGE_POPULAR_SPAREPART);
    }
    
    /**
     * Menampilkan formulir untuk menambahkan spare part baru.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showNewSparePartForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setAttribute(ATTR_SPAREPART, new SparePart());
        request.setAttribute(ATTR_FORM_ACTION_URL, request.getContextPath() + "/sparepart?action=" + ACTION_CREATE);
        forwardToPage(request, response, PAGE_FORM_SPAREPART);
    }
    
    /**
     * Menampilkan formulir untuk mengedit spare part yang ada.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat mengambil data dari database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showEditSparePartForm(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        String kodePart = request.getParameter("kodePart");
        if (kodePart == null || kodePart.trim().isEmpty()) {
            handleGenericError(request, response, "Kode spare part tidak valid untuk diedit.", null);
            return;
        }
        
        SparePart existingSparePart = sparePartDAO.dapatkanSparePartByKode(kodePart);
        if (existingSparePart == null) {
            request.setAttribute(ATTR_ERROR_MESSAGE, "Spare part dengan kode '" + kodePart + "' tidak ditemukan.");
            listSpareParts(request, response);
            return;
        }
        
        request.setAttribute(ATTR_SPAREPART, existingSparePart);
        request.setAttribute(ATTR_FORM_ACTION_URL, request.getContextPath() + "/sparepart?action=" + ACTION_UPDATE);
        forwardToPage(request, response, PAGE_FORM_SPAREPART);
    }
    
    /**
     * Membuat spare part baru berdasarkan data dari request.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat menyimpan data ke database.
     * @throws IOException Jika terjadi kesalahan I/O.
     * @throws IllegalArgumentException Jika input tidak valid.
     * @throws ServletException Jika terjadi kesalahan servlet.
     */
    private void createSparePart(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, IllegalArgumentException, ServletException {
        SparePart newSparePart = createSparePartFromRequest(request);
        newSparePart.setJumlahDigunakan(0);
        sparePartDAO.tambahSparePart(newSparePart);
        response.sendRedirect(request.getContextPath() + "/sparepart?action=" + ACTION_LIST + "&" + ATTR_SUCCESS_MESSAGE + "=SparePartAdded");
    }
    
    /**
     * Memperbarui data spare part berdasarkan data dari request.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat memperbarui data di database.
     * @throws IOException Jika terjadi kesalahan I/O.
     * @throws IllegalArgumentException Jika input tidak valid.
     * @throws ServletException Jika terjadi kesalahan servlet.
     */
    private void updateSparePart(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, IllegalArgumentException, ServletException {
        SparePart sparePartToUpdate = createSparePartFromRequest(request);
        sparePartDAO.updateSparePart(sparePartToUpdate);
        response.sendRedirect(request.getContextPath() + "/sparepart?action=" + ACTION_LIST + "&" + ATTR_SUCCESS_MESSAGE + "=SparePartUpdated");
    }
    
    /**
     * Menghapus spare part berdasarkan kode part yang diberikan.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan saat menghapus data dari database.
     * @throws IOException Jika terjadi kesalahan I/O.
     * @throws ServletException Jika terjadi kesalahan servlet.
     */
    private void deleteSparePart(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, ServletException {
        String kodePart = request.getParameter("kodePart");
        if (kodePart == null || kodePart.trim().isEmpty()) {
            handleGenericError(request, response, "Kode spare part tidak valid untuk dihapus.", null);
            return;
        }
        sparePartDAO.hapusSparePart(kodePart);
        response.sendRedirect(request.getContextPath() + "/sparepart?action=" + ACTION_LIST + "&" + ATTR_SUCCESS_MESSAGE + "=SparePartDeleted");
    }
    
    /**
     * Membuat objek SparePart dari data yang dikirim melalui request.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @return Objek SparePart yang telah diisi dengan data dari request.
     * @throws IllegalArgumentException Jika input tidak valid atau kosong.
     */
    private SparePart createSparePartFromRequest(HttpServletRequest request) throws IllegalArgumentException {
        String kodePart = request.getParameter("kodePart");
        String nama = request.getParameter("nama");
        String hargaStr = request.getParameter("harga");
        String stokStr = request.getParameter("stok");
        
        if (kodePart == null || kodePart.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode spare part tidak boleh kosong.");
        }
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama spare part tidak boleh kosong.");
        }
        if (hargaStr == null || hargaStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Harga spare part tidak boleh kosong.");
        }
        if (stokStr == null || stokStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Stok spare part tidak boleh kosong.");
        }
        
        double harga;
        int stok;
        try {
            harga = Double.parseDouble(hargaStr);
            if (harga < 0) {
                throw new IllegalArgumentException("Harga spare part tidak boleh negatif.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format harga spare part tidak valid.");
        }
        
        try {
            stok = Integer.parseInt(stokStr);
            if (stok < 0) {
                throw new IllegalArgumentException("Stok spare part tidak boleh negatif.");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Format stok spare part tidak valid.");
        }
        
        SparePart sparePart = new SparePart();
        sparePart.setKodePart(kodePart);
        sparePart.setNama(nama);
        sparePart.setHarga(harga);
        sparePart.setStok(stok);
        return sparePart;
    }
    
    /**
     * Mengisi objek SparePart dari data yang dikirim melalui request.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param sparePart Objek SparePart yang akan diisi dengan data dari request.
     */
    private void trySetSparePartFromRequest(HttpServletRequest request, SparePart sparePart) {
        if (sparePart.getKodePart() == null || sparePart.getKodePart().isEmpty()) {
            sparePart.setKodePart(request.getParameter("kodePart"));
        }
        sparePart.setNama(request.getParameter("nama"));
        try {
            String hargaStr = request.getParameter("harga");
            if (hargaStr != null && !hargaStr.isEmpty()) {
                sparePart.setHarga(Double.parseDouble(hargaStr));
            }
        } catch (NumberFormatException | NullPointerException e) {
            
        }
        try {
            String stokStr = request.getParameter("stok");
            if (stokStr != null && !stokStr.isEmpty()) {
                sparePart.setStok(Integer.parseInt(stokStr));
            }
        } catch (NumberFormatException | NullPointerException e) {
            
        }
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
            log("Error di SparePartServlet: " + message, throwable);
        } else {
            log("Error di SparePartServlet: " + message);
        }
        request.setAttribute("javax.servlet.error.exception", throwable);
        request.setAttribute(ATTR_ERROR_MESSAGE, message);
        forwardToPage(request, response, PAGE_ERROR);
    }
}
