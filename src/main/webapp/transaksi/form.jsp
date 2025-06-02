<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 25/05/2025
  Time: 20.42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tambah Transaksi Baru - Bengkelin</title>
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
</head>
<body>
<h1>Buat Transaksi Baru</h1>

<c:if test="${not empty errorMessage}">
    <div style="color: red;">
        <p><strong>Error:</strong> <c:out value="${errorMessage}" /></p>
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/transaksi?action=create" method="post" id="formTransaksi">
    <fieldset>
        <legend>Informasi Transaksi & Pelanggan</legend>

        <label for="noTransaksi">Nomor Transaksi:</label><br/>
        <input type="text" id="noTransaksi" name="noTransaksi" placeholder="Contoh: TRX001" required><br/><br/>

        <label for="tanggal">Tanggal Transaksi:</label><br/>
        <input type="date" id="tanggal" name="tanggal" value="${todayDate}" required><br/><br/>

        <label for="pelanggan">Pelanggan:</label><br/>
        <select id="pelanggan" name="pelanggan" class="select2-basic-single" required>
            <option value="">-- Pilih Pelanggan --</option>
            <c:forEach var="p" items="${pelangganList}">
                <option value="${p.idPelanggan}">${p.nama} (${p.idPelanggan}) <c:if test="${p.isMember()}">(Member)</c:if></option>
            </c:forEach>
        </select>
    </fieldset>

    <br/>

    <fieldset>
        <legend>Detail Layanan & Spare Part</legend>

        <label for="jenisService">Jenis Service Utama:</label><br/>
        <select id="jenisService" name="jenisService" required>
            <option value="">-- Pilih Jenis Service --</option>
            <c:forEach var="type" items="${serviceTypes}">
                <option value="${type.name()}">${type.toString().replace("_", " ")}</option>
            </c:forEach>
        </select>

        <br/><br/>
        <strong>Spare Part Digunakan:</strong><br/>
        <div id="spareparts-container"></div>

        <br/>
        <button type="button" id="add-sparepart-btn">Tambah Spare Part</button>
    </fieldset>

    <br/>
    <div>
        <a href="${pageContext.request.contextPath}/transaksi?action=list">Batal</a>
        <button type="submit">Simpan Transaksi</button>
    </div>
</form>

<footer>
    <p>&copy; <fmt:formatDate value="<%=new java.util.Date()%>" pattern="yyyy" /> Bengkelin App.</p>
</footer>

<template id="sparepart-template">
    <div class="sparepart-item">
        <hr/>
        <label>Spare Part:</label><br/>
        <select name="spareParts" class="select2-sparepart" required>
            <option value="">-- Pilih Spare Part --</option>
            <c:forEach var="sp" items="${sparePartList}">
                <option value="${sp.kodePart}" data-harga="${sp.harga}" data-stok="${sp.stok}">
                        ${sp.nama} (Stok: ${sp.stok}) - Rp <fmt:formatNumber value="${sp.harga}" type="number" minFractionDigits="0" maxFractionDigits="0"/>
                </option>
            </c:forEach>
        </select><br/>

        <label>Jumlah:</label><br/>
        <input type="number" name="quantities" min="1" value="1" required><br/>

        <p class="sparepart-stok-info" style="font-size:small;"></p>
        <button type="button" class="remove-sparepart-btn">Hapus</button>
    </div>
</template>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
<script>
    $(document).ready(function () {
        $('.select2-basic-single').select2({
            placeholder: "-- Pilih Pelanggan --",
            allowClear: true
        });

        function initializeSelect2ForSpareparts(element) {
            $(element).select2({
                placeholder: "-- Pilih Spare Part --",
                allowClear: false
            }).on('select2:select', function (e) {
                var data = e.params.data;
                var stok = $(data.element).data('stok');
                var stokInfo = $(this).closest('.sparepart-item').find('.sparepart-stok-info');
                stokInfo.text(stok !== undefined ? 'Stok: ' + stok : '');
                var quantityInput = $(this).closest('.sparepart-item').find('input[name="quantities"]');
                if (stok !== undefined) {
                    quantityInput.attr('max', stok);
                }
            });
        }

        $('#add-sparepart-btn').on('click', function () {
            var template = document.getElementById('sparepart-template').innerHTML;
            var container = $('#spareparts-container');
            var newItem = $(template);
            container.append(newItem);
            initializeSelect2ForSpareparts(newItem.find('.select2-sparepart'));
        });

        $('#spareparts-container').on('click', '.remove-sparepart-btn', function () {
            $(this).closest('.sparepart-item').remove();
        });
    });
</script>
</body>
</html>
