<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Laporan Periode ${param.startDate} - ${param.endDate}</title>
</head>
<body>
<div>
    <div>
        <h1>Laporan Transaksi</h1>
        <p>Periode: ${param.startDate} - ${param.endDate}</p>

        <button onclick="window.print()">Cetak Laporan</button>
        <a href="laporan?action=formPeriode">Kembali</a>
    </div>

    <div>
        <div>
            <div>
                <h3>Total Transaksi</h3>
                <p><fmt:formatNumber value="${transaksiList.size()}"/></p>
            </div>
            <div>
                <h3>Total Pendapatan</h3>
                <p>Rp<fmt:formatNumber value="${totalPendapatan}"/></p>
            </div>
            <div>
                <h3>Rata-rata per Transaksi</h3>
                <p>Rp<fmt:formatNumber value="${rataRataPendapatan}"/></p>
            </div>
            <div>
                <c:if test="${not empty topCustomer}">
                    <h3>Pelanggan Teraktif</h3>
                    <p>${topCustomer.nama} (${topCustomer.idPelanggan})</p>
                    <p>${topCustomer.transactionCount} Transaksi</p>
                </c:if>
            </div>
        </div>

        <table border="1" cellpadding="4" cellspacing="0">
            <thead>
            <tr>
                <th>No. Transaksi</th>
                <th>Tanggal</th>
                <th>Pelanggan</th>
                <th>Jumlah Item SP</th>
                <th>Total Biaya</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${transaksiList}" var="transaksi">
                <tr>
                    <td>${transaksi.noTransaksi}</td>
                    <td><fmt:formatDate value="${transaksi.tanggal}" pattern="dd-MM-yyyy"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty transaksi.pelanggan}">
                                ${transaksi.pelanggan.nama}
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </td>
                    <td><fmt:formatNumber value="${transaksi.jumlahItemSparePart}"/></td>
                    <td>Rp<fmt:formatNumber value="${transaksi.totalBiaya}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <c:if test="${empty transaksiList}">
            <div>
                <p>Tidak ada transaksi pada periode ini</p>
            </div>
        </c:if>
    </div>
</div>
</body>
</html>
