/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.bengkelin.servlet;

import com.bengkelin.dao.PelangganDAO;
import com.bengkelin.model.Pelanggan;

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
 * Kelas PelangganServlet digunakan untuk menangani permintaan terkait pelanggan.
 * Kelas ini menyediakan operasi CRUD (Create, Read, Update, Delete) melalui servlet.
 */
@WebServlet(name = "PelangganServlet", urlPatterns = {"/pelanggan"})
public class PelangganServlet extends HttpServlet {
    
    private transient PelangganDAO pelangganDAO;
    
    private static final String PAGE_LIST_PELANGGAN = "/pelanggan/list.jsp";
    private static final String PAGE_CREATE_PELANGGAN_FORM = "/pelanggan/create.jsp";
    private static final String PAGE_EDIT_PELANGGAN_FORM = "/pelanggan/edit.jsp";
    private static final String PAGE_ERROR = "/error.jsp";
    
    private static final String ACTION_LIST = "list";
    private static final String ACTION_NEW = "new";
    private static final String ACTION_CREATE = "create";
    private static final String ACTION_EDIT = "edit";
    private static final String ACTION_UPDATE = "update";
    private static final String ACTION_DELETE = "delete";
    
    private static final String ATTR_PELANGGAN_LIST = "listPelanggan";
    private static final String ATTR_PELANGGAN = "pelanggan";
    private static final String ATTR_FORM_ACTION_URL = "formActionUrl";
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    
    /**
     * Inisialisasi servlet dengan membuat instance PelangganDAO.
     */
    @Override
    public void init() {
        pelangganDAO = new PelangganDAO();
    }
    
    /**
     * Menangani permintaan GET untuk operasi seperti menampilkan daftar pelanggan,
     * menampilkan formulir tambah/edit, atau menghapus pelanggan.
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
                    showNewCustomerForm(request, response);
                    break;
                case ACTION_EDIT:
                    showEditCustomerForm(request, response);
                    break;
                case ACTION_DELETE:
                    deleteCustomer(request, response);
                    break;
                case ACTION_LIST:
                default:
                    listCustomers(request, response);
                    break;
            }
        } catch (SQLException ex) {
            log("Error database di PelangganServlet (doGet): " + ex.getMessage(), ex);
            request.setAttribute("javax.servlet.error.exception", ex);
            request.setAttribute(ATTR_ERROR_MESSAGE, "Terjadi error database saat memproses permintaan Anda.");
            forwardToPage(request, response, PAGE_ERROR);
        }
    }
    
    /**
     * Menangani permintaan POST untuk operasi seperti menambah atau memperbarui pelanggan.
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
            response.sendRedirect(request.getContextPath() + "/pelanggan?action=" + ACTION_LIST + "&" + ATTR_ERROR_MESSAGE + "=InvalidAction");
            return;
        }
        
        try {
            switch (action) {
                case ACTION_CREATE:
                    createCustomer(request, response);
                    break;
                case ACTION_UPDATE:
                    updateCustomer(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/pelanggan?action=" + ACTION_LIST + "&" + ATTR_ERROR_MESSAGE + "=UnknownAction");
                    break;
            }
        } catch (SQLException ex) {
            log("Error database di PelangganServlet (doPost): " + ex.getMessage(), ex);
            repopulateFormOnError(request, response, action, "Terjadi error database: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log("Input tidak valid di PelangganServlet (doPost): " + ex.getMessage(), ex);
            repopulateFormOnError(request, response, action, "Input tidak valid: " + ex.getMessage());
        }
    }
    
    /**
     * Mengisi ulang formulir jika terjadi kesalahan selama pemrosesan POST.
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
        Pelanggan pelanggan = new Pelanggan();
        
        String id = request.getParameter("id");
        if (id != null && !id.isEmpty()) {
            if (ACTION_UPDATE.equals(originalAction)) {
                try {
                    Pelanggan existing = pelangganDAO.dapatkanPelangganById(id);
                    if (existing != null) {
                        pelanggan = existing;
                    }
                } catch (SQLException e) {
                    log("Gagal mengambil pelanggan saat repopulate form update: " + e.getMessage(), e);
                }
            }
            pelanggan.setIdPelanggan(id);
        }
        trySetPelangganFromRequest(request, pelanggan);
        request.setAttribute(ATTR_PELANGGAN, pelanggan);
        
        if (ACTION_CREATE.equals(originalAction)) {
            request.setAttribute(ATTR_FORM_ACTION_URL, request.getContextPath() + "/pelanggan?action=" + ACTION_CREATE);
            forwardToPage(request, response, PAGE_CREATE_PELANGGAN_FORM);
        } else if (ACTION_UPDATE.equals(originalAction)) {
            request.setAttribute(ATTR_FORM_ACTION_URL, request.getContextPath() + "/pelanggan?action=" + ACTION_UPDATE);
            forwardToPage(request, response, PAGE_EDIT_PELANGGAN_FORM);
        } else {
            forwardToPage(request, response, PAGE_ERROR);
        }
    }
    
    /**
     * Menampilkan daftar pelanggan dari database.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void listCustomers(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        List<Pelanggan> listPelanggan = pelangganDAO.semuaPelanggan();
        request.setAttribute(ATTR_PELANGGAN_LIST, listPelanggan);
        forwardToPage(request, response, PAGE_LIST_PELANGGAN);
    }
    
    /**
     * Menampilkan formulir untuk menambah pelanggan baru.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showNewCustomerForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setAttribute(ATTR_PELANGGAN, new Pelanggan());
        request.setAttribute(ATTR_FORM_ACTION_URL, request.getContextPath() + "/pelanggan?action=" + ACTION_CREATE);
        forwardToPage(request, response, PAGE_CREATE_PELANGGAN_FORM);
    }
    
    /**
     * Menampilkan formulir untuk mengedit pelanggan yang sudah ada.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan database.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void showEditCustomerForm(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/pelanggan?action=" + ACTION_LIST + "&" + ATTR_ERROR_MESSAGE + "=MissingIdForEdit");
            return;
        }
        
        Pelanggan existingPelanggan = pelangganDAO.dapatkanPelangganById(id);
        if (existingPelanggan == null) {
            response.sendRedirect(request.getContextPath() + "/pelanggan?action=" + ACTION_LIST + "&" + ATTR_ERROR_MESSAGE + "=CustomerNotFound");
            return;
        }
        request.setAttribute(ATTR_PELANGGAN, existingPelanggan);
        request.setAttribute(ATTR_FORM_ACTION_URL, request.getContextPath() + "/pelanggan?action=" + ACTION_UPDATE);
        forwardToPage(request, response, PAGE_EDIT_PELANGGAN_FORM); // Menggunakan edit.jsp
    }
    
    /**
     * Menambahkan pelanggan baru ke database.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan database.
     * @throws IOException Jika terjadi kesalahan I/O.
     * @throws IllegalArgumentException Jika input tidak valid.
     */
    private void createCustomer(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, IllegalArgumentException {
        Pelanggan newPelanggan = createPelangganFromRequest(request);
        pelangganDAO.tambahPelanggan(newPelanggan);
        response.sendRedirect(request.getContextPath() + "/pelanggan?action=" + ACTION_LIST + "&" + ATTR_SUCCESS_MESSAGE + "=CustomerAdded");
    }
    
    /**
     * Memperbarui data pelanggan di database.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan database.
     * @throws IOException Jika terjadi kesalahan I/O.
     * @throws IllegalArgumentException Jika input tidak valid.
     */
    private void updateCustomer(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, IllegalArgumentException {
        Pelanggan pelangganToUpdate = createPelangganFromRequest(request);
        pelangganDAO.updatePelanggan(pelangganToUpdate);
        response.sendRedirect(request.getContextPath() + "/pelanggan?action=" + ACTION_LIST + "&" + ATTR_SUCCESS_MESSAGE + "=CustomerUpdated");
    }
    
    /**
     * Menghapus pelanggan dari database berdasarkan ID.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param response Objek HttpServletResponse untuk mengirim respons ke client.
     * @throws SQLException Jika terjadi kesalahan database.
     * @throws IOException Jika terjadi kesalahan I/O.
     * @throws ServletException Jika terjadi kesalahan servlet.
     */
    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, ServletException {
        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            handleGenericError(request, response, "ID Pelanggan tidak valid untuk dihapus.", null);
            return;
        }
        pelangganDAO.hapusPelanggan(id);
        response.sendRedirect(request.getContextPath() + "/pelanggan?action=" + ACTION_LIST + "&" + ATTR_SUCCESS_MESSAGE + "=CustomerDeleted");
    }
    
    /**
     * Membuat objek Pelanggan dari parameter request.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @return Objek Pelanggan yang telah dibuat.
     * @throws IllegalArgumentException Jika input tidak valid.
     */
    private Pelanggan createPelangganFromRequest(HttpServletRequest request) throws IllegalArgumentException {
        String id = request.getParameter("id");
        String nama = request.getParameter("nama");
        String noTelepon = request.getParameter("noTelepon");
        String alamat = request.getParameter("alamat");
        boolean isMember = "on".equalsIgnoreCase(request.getParameter("isMember")) || "true".equalsIgnoreCase(request.getParameter("isMember"));
        
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID Pelanggan tidak boleh kosong.");
        }
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama Pelanggan tidak boleh kosong.");
        }
        
        Pelanggan pelanggan = new Pelanggan();
        pelanggan.setIdPelanggan(id);
        pelanggan.setNama(nama);
        pelanggan.setNoTelepon(noTelepon);
        pelanggan.setAlamat(alamat);
        pelanggan.setMember(isMember);
        
        return pelanggan;
    }
    
    /**
     * Mengisi objek Pelanggan dari parameter request.
     * 
     * @param request Objek HttpServletRequest yang berisi permintaan dari client.
     * @param pelanggan Objek Pelanggan yang akan diisi.
     */
    private void trySetPelangganFromRequest(HttpServletRequest request, Pelanggan pelanggan) {
        if (pelanggan.getIdPelanggan() == null || pelanggan.getIdPelanggan().isEmpty()) {
            pelanggan.setIdPelanggan(request.getParameter("id"));
        }
        pelanggan.setNama(request.getParameter("nama"));
        pelanggan.setNoTelepon(request.getParameter("noTelepon"));
        pelanggan.setAlamat(request.getParameter("alamat"));
        pelanggan.setMember("on".equalsIgnoreCase(request.getParameter("isMember")) || "true".equalsIgnoreCase(request.getParameter("isMember")));
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
     * @param throwable Objek Throwable yang menyebabkan kesalahan.
     * @throws ServletException Jika terjadi kesalahan servlet.
     * @throws IOException Jika terjadi kesalahan I/O.
     */
    private void handleGenericError(HttpServletRequest request, HttpServletResponse response, String message, Throwable throwable)
            throws ServletException, IOException {
        if (throwable != null) {
            log("Error di PelangganServlet: " + message, throwable);
        } else {
            log("Error di PelangganServlet: " + message);
        }
        request.setAttribute("javax.servlet.error.exception", throwable);
        request.setAttribute(ATTR_ERROR_MESSAGE, message);
        forwardToPage(request, response, PAGE_ERROR);
    }
}
