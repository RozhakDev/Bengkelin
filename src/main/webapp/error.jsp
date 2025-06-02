<%--
  Created by IntelliJ IDEA.
  User: USER
  Date: 29/05/2025
  Time: 18.15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 50px;
            color: red;
        }
        .box {
            border: 1px solid #f00;
            background-color: #fee;
            padding: 20px;
            border-radius: 5px;
        }
        a {
            display: inline-block;
            margin-top: 15px;
            text-decoration: none;
            color: #555;
        }
    </style>
</head>
<body>
<div class="box">
    <h2>Terjadi Error</h2>
    <p><%= request.getAttribute("error") %></p>
    <a href="BarangServlet">Kembali ke Daftar Barang</a>
</div>
</body>
</html>

