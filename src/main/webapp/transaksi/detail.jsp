<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Detail Transaksi - Bengkelin</title>
</head>
<body>
<h1>Detail Transaksi</h1>
<a href="${pageContext.request.contextPath}/transaksi?action=list">Kembali ke Daftar Transaksi</a>

<c:if test="${not empty errorMessage}">
  <div>
    <strong>Error:</strong> <c:out value="${errorMessage}" />
  </div>
</c:if>

<c:if test="${empty transaksi && empty errorMessage}">
  <div>Data transaksi tidak ditemukan.</div>
</c:if>

<c:if test="${not empty transaksi}">
  <h2>Informasi Umum</h2>
  <p>Nomor Transaksi: <c:out value="${transaksi.noTransaksi}" /></p>
  <p>Tanggal: <fmt:formatDate value="${transaksi.tanggal}" pattern="EEEE, dd MMMM yyyy HH:mm" /></p>

  <h2>Informasi Pelanggan</h2>
  <c:if test="${not empty transaksi.pelanggan}">
    <p>ID Pelanggan: <c:out value="${transaksi.pelanggan.idPelanggan}" /></p>
    <p>Nama Pelanggan: <c:out value="${transaksi.pelanggan.nama}" /></p>
    <p>No. Telepon: <c:out value="${transaksi.pelanggan.noTelepon}" default="-"/></p>
    <p>Status Member:
      <c:choose>
        <c:when test="${transaksi.pelanggan.isMember()}">Member</c:when>
        <c:otherwise>Non-Member</c:otherwise>
      </c:choose>
    </p>
  </c:if>
  <c:if test="${empty transaksi.pelanggan}">
    <p>Data pelanggan tidak tersedia.</p>
  </c:if>

  <h2>Detail Spare Part Digunakan</h2>
  <c:choose>
    <c:when test="${not empty transaksi.spareParts}">
      <table border="1">
        <thead>
        <tr>
          <th>Kode Part</th>
          <th>Nama Spare Part</th>
          <th>Harga Satuan (Rp)</th>
          <th>Jumlah</th>
          <th>Subtotal (Rp)</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="sp" items="${transaksi.spareParts}">
          <tr>
            <td><c:out value="${sp.kodePart}" /></td>
            <td><c:out value="${sp.nama}" /></td>
            <td><fmt:formatNumber value="${sp.harga}" type="number" minFractionDigits="0" maxFractionDigits="0"/></td>
            <td><c:out value="${sp.jumlahDigunakan}" /></td>
            <td><fmt:formatNumber value="${sp.harga * sp.jumlahDigunakan}" type="number" minFractionDigits="0" maxFractionDigits="0"/></td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>
    <c:otherwise>
      <p>Tidak ada spare part yang digunakan dalam transaksi ini.</p>
    </c:otherwise>
  </c:choose>

  <h2>Rincian Biaya</h2>
  <c:set var="totalHargaSparePart" value="0" />
  <c:forEach var="sp" items="${transaksi.spareParts}">
    <c:set var="totalHargaSparePart" value="${totalHargaSparePart + (sp.harga * sp.jumlahDigunakan)}" />
  </c:forEach>

  <p>Biaya Service (Jasa): Rp <fmt:formatNumber value="${transaksi.biayaService}" type="number" minFractionDigits="0" maxFractionDigits="0"/></p>
  <p>Total Harga Spare Part: Rp <fmt:formatNumber value="${totalHargaSparePart}" type="number" minFractionDigits="0" maxFractionDigits="0"/></p>

  <c:if test="${transaksi.pelanggan.isMember()}">
    <c:set var="subtotalSebelumDiskon" value="${transaksi.biayaService + totalHargaSparePart}" />
    <c:set var="nilaiDiskon" value="${subtotalSebelumDiskon * 0.10}" />
    <p>Diskon Member (10%): - Rp <fmt:formatNumber value="${nilaiDiskon}" type="number" minFractionDigits="0" maxFractionDigits="0"/></p>
  </c:if>

  <p><strong>TOTAL AKHIR:</strong> Rp <fmt:formatNumber value="${transaksi.totalBiaya}" type="number" minFractionDigits="0" maxFractionDigits="0"/></p>
</c:if>

<footer>
  <p>&copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App. All rights reserved.</p>
</footer>
</body>
</html>
