<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE HTML>
<html>
<head>
</head>
<body>
<jsp:include page="template/header.jsp"></jsp:include>
<jsp:include page="template/sidebar.jsp"></jsp:include>
<div class="col-md-9 animated bounce">
	<h3 class="page-header">Thống kê Top 5 sản phẩm bán chạy</h3>
	<form class="form-inline" id="searchForm" name="searchObject">
		<div class="form-group">
			<select class="form-control" name="theothoigian" id="thoigian">
				<!-- Các option sẽ được thêm bằng JavaScript -->
			</select>
		</div>
		&nbsp;&nbsp;
		<button type="submit" class="btn btn-primary" id="btnDuyet">Duyệt</button>
	</form>

	<div id="chartContainer" style="height: 370px; width: 100%;"></div>
	</div>
</div>
<jsp:include page="template/footer.jsp"></jsp:include>
<script>

	$(document).ready(function() {
		var selectThang = $('#thoigian');
		var currentDate = new Date();
		var currentYear = currentDate.getFullYear();
		var currentMonth = currentDate.getMonth() + 1; // Tháng trong JavaScript bắt đầu từ 0

		// Tính toán tháng cuối cùng mà bạn muốn hiển thị
		var lastMonthToShow = currentMonth - 1;
		if (lastMonthToShow === 0) {
			lastMonthToShow = 12; // Nếu tháng hiện tại là tháng 1, thì tháng cuối cùng là tháng 12 của năm trước
			currentYear--; // Giảm năm đi 1
		}

		// Tạo và thêm các option vào select box
		for (var i = 1; i <= lastMonthToShow; i++) {
			var option = $('<option></option>').attr('value', i).text('Tháng ' + i + '/' + currentYear);
			selectThang.append(option);
		}

		// Thêm sự kiện submit cho form
		$('#searchForm').submit(function (e) {
			e.preventDefault(); // Tránh sự kiện mặc định của form
			var selectedOption = $('[name="theothoigian"]').val(); // Lấy giá trị của option đã chọn
			loadData(selectedOption); // Gọi hàm để tải dữ liệu mới
		});

		// Hàm để xử lý chuỗi label
		function shortenLabel(label) {
			// Giới hạn độ dài của label
			var maxLength = 40;

			// Nếu label dài hơn maxLength, cắt bớt và thêm dấu "..." ở cuối
			if (label.length > maxLength) {
				label = label.substring(0, maxLength - 3) + "...";
			}

			// Loại bỏ các ký tự không mong muốn như "("
			label = label.replace(/\(/g, '');

			return label;
		}

		// Hàm để tải dữ liệu mới
		function loadData(timePeriod) {
			var dataForDataSets = [];
			$.ajax({
				type: "GET",
				contentType: "application/json",
				url: "http://localhost:8080/laptopshop/api/don-hang/report-top-products?month=" + timePeriod,
				success: function (data) {
					for (var i = 0; i < data.length; i++) {
						var label = shortenLabel(data[i][0]);
						dataForDataSets.push({
							"label": label,
							"y": data[i][1],
						});
					}
					updateChart(dataForDataSets); // Sau khi nhận dữ liệu mới, cập nhật biểu đồ
				},
				error: function (e) {
					alert("Error: ", e);
					console.log("Error", e);
				}
			});
		}

		// Hàm để cập nhật biểu đồ
		function updateChart(dataForDataSets) {
			var chart = new CanvasJS.Chart("chartContainer", {
				animationEnabled: true,
				theme: "light2", // "light1", "light2", "dark1", "dark2"
				title:{
					text: "Top 5 sản phẩm bán chạy"
				},
				axisY: {
					title: "Doanh thu (VNĐ)"
				},
				data: [{
					type: "column",
					showInLegend: true,
					legendMarkerColor: "grey",
					legendText: "VNĐ = Việt Nam Đồng",
					dataPoints: [...dataForDataSets]
				}]
			});
			chart.render();
		}

		// Ban đầu, tải dữ liệu cho biểu đồ với tùy chọn mặc định
		loadData('1');
	});
</script>
<script src="<c:url value='/js/admin.js'/>"></script>
<script src="https://cdn.canvasjs.com/canvasjs.min.js"></script>


</body>
</html>

<%--<!DOCTYPE html>--%>
<%--<html>--%>
<%--<head>--%>
<%--<meta charset="ISO-8859-1">--%>
<%--<title>Thống Kê</title>--%>
<%--<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">--%>

<%--<script>--%>
<%--	window.onload = function() {--%>
<%--		var data = [];--%>
<%--		var label = [];--%>
<%--		var dataForDataSets = [];--%>

<%--		$.ajax({--%>
<%--			async : false,--%>
<%--			type : "GET",--%>
<%--			data : data,--%>
<%--			contentType : "application/json",--%>
<%--			url : "http://localhost:8080/laptopshop/api/don-hang/report",--%>
<%--			success : function(data) {--%>
<%--				for (var i = 0; i < data.length; i++) {--%>
<%--					label.push(data[i][0] + "/" + data[i][1]);--%>
<%--					dataForDataSets.push(data[i][2]/1000000);--%>
<%--				}--%>
<%--			},--%>
<%--			error : function(e) {--%>
<%--				alert("Error: ", e);--%>
<%--				console.log("Error", e);--%>
<%--			}--%>
<%--		});--%>

<%--		var canvas = document.getElementById('myChart');--%>
<%--		--%>
<%--		--%>
<%--		data = {--%>
<%--			labels : label,--%>
<%--			datasets : [ {--%>
<%--				label : "Tổng giá trị ( Triệu đồng)",--%>
<%--				backgroundColor : "#0000ff",--%>
<%--				borderColor : "#0000ff",--%>
<%--				borderWidth : 2,--%>
<%--				hoverBackgroundColor : "#0043ff",--%>
<%--				hoverBorderColor : "#0043ff",--%>
<%--				data : dataForDataSets,--%>
<%--			} ]--%>
<%--		};--%>
<%--		var option = {--%>
<%--			scales : {--%>
<%--				yAxes : [ {--%>
<%--					stacked : true,--%>
<%--					gridLines : {--%>
<%--						display : true,--%>
<%--						color : "rgba(255,99,132,0.2)"--%>
<%--					}--%>
<%--				} ],--%>
<%--				xAxes : [ {--%>
<%--					barPercentage: 0.5,--%>
<%--					gridLines : {--%>
<%--						display : false--%>
<%--					}--%>
<%--				} ]--%>
<%--			},--%>
<%--			maintainAspectRatio: false,--%>
<%--			legend: {--%>
<%--	            labels: {--%>
<%--	                // This more specific font property overrides the global property--%>
<%--	                fontSize: 20--%>
<%--	            }--%>
<%--			}--%>
<%--		};--%>

<%--		var myBarChart = Chart.Bar(canvas, {--%>
<%--			data : data,--%>
<%--			options : option--%>
<%--		});--%>
<%--	}--%>
<%--</script>--%>

<%--</head>--%>
<%--<body>--%>
<%--	<jsp:include page="template/header.jsp"></jsp:include>--%>
<%--	<jsp:include page="template/sidebar.jsp"></jsp:include>--%>

<%--	<div class="col-md-9 animated bounce">--%>
<%--		<h3 class="page-header">Thống kê</h3>--%>

<%--		<canvas id="myChart" width="600px" height="400px"></canvas>--%>
<%--		<h4 style="text-align: center; padding-right: 10%">Biểu đồ tổng giá trị đơn hàng hoàn thành theo tháng</h4>--%>

<%--	</div>--%>


<%--	<jsp:include page="template/footer.jsp"></jsp:include>--%>

<%--	<script type="text/javascript"--%>
<%--		src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.1.3/Chart.min.js"></script>--%>
<%--</body>--%>
<%--</html>--%>