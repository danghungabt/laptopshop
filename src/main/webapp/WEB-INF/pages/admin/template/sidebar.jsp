<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<%--<div class="container-fluid">--%>
<div class="col-md-3">
    <div id="sidebar">
        <ul class="nav navbar-nav side-bar">
            <li class="side-bar tmargin">
                <a href="<c:url value='/admin/don-hang' />">
                    <span class="glyphicon glyphicon-shopping-cart">&nbsp;</span>Quản lý Đơn hàng</a>
            </li>

            <li class="side-bar">
                <a href='<c:url value="/admin/san-pham"/>'>
                    <span class="glyphicon glyphicon-folder-open">&nbsp;</span>Quản lý Sản phẩm</a>
            </li>

            <li class="side-bar">
                <a href='<c:url value="/admin/danh-muc"/>'>
                    <span class="glyphicon glyphicon-tasks">&nbsp;</span>Quản lý Danh Mục</a>
            </li>

            <li class="side-bar">
                <a href='<c:url value="/admin/nhan-hieu"/>'>
                    <span class="glyphicon glyphicon-flag">&nbsp;</span>Quản lý Nhãn hiệu</a>
            </li>

            <li class="side-bar main-menu">
                <a href="<c:url value='/admin/tai-khoan' />">
                    <span class="glyphicon glyphicon-th-list">&nbsp;</span>Quản lý Tài khoản</a>
            </li>

            <li class="side-bar main-menu">
                <a href="#" id="thong-ke-link">
                    <span class="glyphicon glyphicon-signal">&nbsp;</span>Thống kê<span class="caret"></span>
                </a>
                <ul id="thong-ke-submenu" class="nav navbar-nav side-bar">
                    <li class="side-bar">
                        <a href="<c:url value='/admin/thong-ke' />">
                            Thống kê top 5 sản phẩm
                        </a>
                    </li>
                    <li class="side-bar">
                        <a href="<c:url value='/admin/thong-ke-pareto' />">
                            Thống kê Pareto
                        </a>
                    </li>
                </ul>
            </li>

            <li class="side-bar">
                <a href="<c:url value='/admin/profile' />">
                    <span class="glyphicon glyphicon-user">&nbsp;</span>
                    Thông tin cá nhân
                </a>
            </li>
        </ul>
    </div>
</div>
