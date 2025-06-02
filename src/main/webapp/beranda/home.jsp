<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.23
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
    <title>Dashboard - Aplikasi Bengkelin</title>
</head>
<body>

<div id="sidebar">
    <a href="${pageContext.request.contextPath}/home">BengkelinApp</a>
    <nav>
        <a href="${pageContext.request.contextPath}/home">Dashboard</a>
        <a href="${pageContext.request.contextPath}/pelanggan?action=list">Pelanggan</a>
        <a href="${pageContext.request.contextPath}/sparepart?action=list">Spare Part</a>
        <a href="${pageContext.request.contextPath}/transaksi?action=list">Transaksi</a>
        <a href="${pageContext.request.contextPath}/laporan?action=menu">Laporan</a>
    </nav>
</div>

<div>
    <header>
        <button id="sidebar-toggle">Menu</button>
        <h2>Dashboard Ringkasan</h2>
    </header>

    <main>
        <c:if test="${not empty errorMessage}">
            <div>
                <p>Error Sistem</p>
                <p><c:out value="${errorMessage}" /></p>
            </div>
        </c:if>

        <div>
            <div>
                <p>Total Pelanggan</p>
                <p><c:out value="${totalPelanggan}" default="0"/></p>
            </div>
            <div>
                <p>Total Jenis Spare Part</p>
                <p><c:out value="${totalSparePart}" default="0"/></p>
            </div>
            <div>
                <p>Transaksi Hari Ini</p>
                <p><c:out value="${transaksiHariIni}" default="0"/></p>
            </div>
            <div>
                <p>Pendapatan Bulan Ini</p>
                <p>Rp
                    <c:choose>
                        <c:when test="${pendapatanBulanIni != null}">
                            <fmt:formatNumber value="${pendapatanBulanIni}" type="number" minFractionDigits="0" maxFractionDigits="0"/>
                        </c:when>
                        <c:otherwise>0</c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>

        <div>
            <div>
                <h3>5 Transaksi Terkini</h3>
                <c:choose>
                    <c:when test="${not empty recentTransactions}">
                        <ul>
                            <c:forEach var="trx" items="${recentTransactions}">
                                <li>
                                    <div>
                                        <div>
                                            <a href="${pageContext.request.contextPath}/transaksi?action=detail&noTransaksi=${trx.noTransaksi}">
                                                <c:out value="${trx.noTransaksi}"/>
                                            </a>
                                            <p>
                                                <c:out value="${trx.pelanggan.nama}" default="Pelanggan Tidak Diketahui"/> -
                                                <fmt:formatDate value="${trx.tanggal}" pattern="dd MMM yyyy, HH:mm"/>
                                            </p>
                                        </div>
                                        <p>
                                            Rp <fmt:formatNumber value="${trx.totalBiaya}" type="number" minFractionDigits="0" maxFractionDigits="0"/>
                                        </p>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p>Tidak ada transaksi terkini.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <div>
                <h3>Spare Part Stok Rendah (<c:out value="${LOW_STOCK_THRESHOLD}" default="5"/> unit)</h3>
                <c:choose>
                    <c:when test="${not empty lowStockItems}">
                        <ul>
                            <c:forEach var="item" items="${lowStockItems}">
                                <li>
                                    <div>
                                        <p><c:out value="${item.nama}"/></p>
                                        <p>Kode: <c:out value="${item.kodePart}"/></p>
                                        <span>Stok: <c:out value="${item.stok}"/></span>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:when>
                    <c:otherwise>
                        <p>Tidak ada spare part dengan stok rendah.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App. Hak Cipta Dilindungi.</p>
    </footer>
</div>

<script>
    const sidebar = document.getElementById('sidebar');
    const sidebarToggle = document.getElementById('sidebar-toggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', () => {
            sidebar.style.display = (sidebar.style.display === 'none') ? 'block' : 'none';
        });
    }
</script>

</body>
</html>
