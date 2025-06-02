<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Daftar Pelanggan - Bengkelin</title>
</head>
<body>
<h1>Manajemen Data Pelanggan</h1>

<p><a href="${pageContext.request.contextPath}/pelanggan?action=new">Tambah Pelanggan Baru</a></p>

<c:if test="${not empty param.successMessage}">
    <div style="color: green; margin-bottom: 10px;">
        <c:choose>
            <c:when test="${param.successMessage == 'CustomerAdded'}">Pelanggan berhasil ditambahkan.</c:when>
            <c:when test="${param.successMessage == 'CustomerUpdated'}">Data pelanggan berhasil diperbarui.</c:when>
            <c:when test="${param.successMessage == 'CustomerDeleted'}">Pelanggan berhasil dihapus.</c:when>
            <c:otherwise>Operasi berhasil.</c:otherwise>
        </c:choose>
    </div>
</c:if>

<c:if test="${not empty param.errorMessage}">
    <div style="color: red; margin-bottom: 10px;">
        <c:out value="${param.errorMessage}" />
    </div>
</c:if>

<c:if test="${not empty errorMessage}">
    <div style="color: red; margin-bottom: 10px;">
        <c:out value="${errorMessage}" />
    </div>
</c:if>

<table border="1" cellpadding="5" cellspacing="0">
    <tr>
        <th>ID Pelanggan</th>
        <th>Nama</th>
        <th>No. Telepon</th>
        <th>Alamat</th>
        <th>Member</th>
        <th>Aksi</th>
    </tr>
    <c:choose>
        <c:when test="${not empty listPelanggan}">
            <c:forEach var="pelanggan" items="${listPelanggan}">
                <tr>
                    <td><c:out value="${pelanggan.idPelanggan}" /></td>
                    <td><c:out value="${pelanggan.nama}" /></td>
                    <td><c:out value="${pelanggan.noTelepon}" /></td>
                    <td><c:out value="${pelanggan.alamat}" /></td>
                    <td>
                        <c:choose>
                            <c:when test="${pelanggan.member}">Ya</c:when>
                            <c:otherwise>Tidak</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a href="${pageContext.request.contextPath}/pelanggan?action=edit&id=${pelanggan.idPelanggan}">Edit</a> |
                        <a href="${pageContext.request.contextPath}/pelanggan?action=delete&id=${pelanggan.idPelanggan}"
                           onclick="return confirm('Apakah Anda yakin ingin menghapus pelanggan ini?');">Hapus</a>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="6" style="text-align: center;">Tidak ada data pelanggan.</td>
            </tr>
        </c:otherwise>
    </c:choose>
</table>

<footer style="margin-top: 20px; font-size: small; color: gray;">
    &copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App
</footer>

</body>
</html>
