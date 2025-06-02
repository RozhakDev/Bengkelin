<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Menu Laporan - Bengkelin</title>
</head>
<body>
<div>
    <header>
        <h1>Pusat Laporan Bengkelin</h1>
        <p>Analisis data transaksi dan operasional bengkel Anda.</p>
    </header>

    <div>
        <div>
            <p>Total Pelanggan</p>
            <p><c:out value="${totalPelangganMenu}" default="0"/></p>
        </div>
        <div>
            <p>Total Jenis Spare Part</p>
            <p><c:out value="${totalSparePartMenu}" default="0"/></p>
        </div>
        <div>
            <p>Total Transaksi</p>
            <p><c:out value="${totalTransaksiMenu}" default="0"/></p>
        </div>
        <div>
            <p>Pendapatan Bulan Ini</p>
            <p>
                <c:choose>
                    <c:when test="${pendapatanBulanIniMenu != null}">
                        Rp <fmt:formatNumber value="${pendapatanBulanIniMenu}" type="number" minFractionDigits="0" maxFractionDigits="0" />
                    </c:when>
                    <c:otherwise>Rp 0</c:otherwise>
                </c:choose>
            </p>
        </div>
    </div>

    <div>
        <div>
            <h2>Laporan Transaksi per Periode</h2>
            <p>Lihat daftar transaksi yang terjadi dalam rentang tanggal tertentu. Analisis tren penjualan dan layanan.</p>
            <a href="${pageContext.request.contextPath}/laporan?action=formPeriode">Buat Laporan</a>
        </div>

        <div>
            <h2>Laporan Pelanggan per Spare Part</h2>
            <p>Identifikasi pelanggan yang sering membeli atau menggunakan spare part tertentu. Berguna untuk strategi marketing.</p>
            <a href="${pageContext.request.contextPath}/laporan?action=formSparePart">Buat Laporan</a>
        </div>
    </div>

    <div>
        <a href="${pageContext.request.contextPath}/home">Kembali ke Dashboard Utama</a>
    </div>

    <footer>
        <p>&copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App. All rights reserved.</p>
    </footer>
</div>
</body>
</html>
