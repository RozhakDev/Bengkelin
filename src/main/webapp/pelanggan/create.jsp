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
    <title>Tambah Pelanggan Baru - Bengkelin</title>
</head>
<body style="background-color: #f5f5f5; font-family: Arial, sans-serif; padding: 20px;">

<h1>Tambah Pelanggan Baru</h1>

<c:if test="${not empty errorMessage}">
    <div style="color: red; font-weight: bold; margin-bottom: 10px;">
        ERROR: <c:out value="${errorMessage}" />
    </div>
</c:if>

<form action="${formActionUrl}" method="post">
    <table border="1" cellpadding="5" cellspacing="0" style="background-color: white; border-collapse: collapse;">
        <tr>
            <td>ID Pelanggan</td>
            <td>
                <input type="text" name="id" value="<c:out value='${pelanggan.idPelanggan}' />" required>
            </td>
        </tr>
        <tr>
            <td>Nama Lengkap</td>
            <td>
                <input type="text" name="nama" value="<c:out value='${pelanggan.nama}' />" required>
            </td>
        </tr>
        <tr>
            <td>Nomor Telepon</td>
            <td>
                <input type="tel" name="noTelepon" value="<c:out value='${pelanggan.noTelepon}' />">
            </td>
        </tr>
        <tr>
            <td>Alamat</td>
            <td>
                <textarea name="alamat" rows="3" cols="30"><c:out value='${pelanggan.alamat}' /></textarea>
            </td>
        </tr>
        <tr>
            <td>Status Member</td>
            <td>
                <input type="checkbox" name="isMember" value="true" <c:if test="${pelanggan.member}">checked</c:if>> Ya
            </td>
        </tr>
        <tr>
            <td colspan="2" style="text-align: right;">
                <button type="submit">Simpan Pelanggan</button>
                <a href="${pageContext.request.contextPath}/pelanggan?action=list">Batal</a>
            </td>
        </tr>
    </table>
</form>

<p style="text-align: center; font-size: small; margin-top: 20px;">
    &copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App
</p>

</body>
</html>
