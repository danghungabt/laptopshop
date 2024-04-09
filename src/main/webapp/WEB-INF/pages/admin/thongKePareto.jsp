<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE HTML>
<html>
<head>
</head>
<body>
<jsp:include page="template/header.jsp"></jsp:include>
<jsp:include page="template/sidebar.jsp"></jsp:include>
<div class="col-md-9 animated bounce">
    <h3 class="page-header">Thống kê pareto</h3>

    <form class="form-inline" id="searchForm" name="searchObject">
        <div class="form-group">
            <select class="form-control" name="theothoigian">
                <option value="nam">1 năm gần đây</option>
                <option value="quy">3 tháng gần đây</option>
                <option value="thang">1 tháng gần đây</option>

            </select>
        </div>

        &nbsp;&nbsp;
        <button type="submit" class="btn btn-primary" id="btnDuyet">Duyệt</button>
    </form>

    <div id="chartContainer" style="height: 370px; width: 100%;">
    </div>
</div>
<jsp:include page="template/footer.jsp"></jsp:include>
<script>
    $(document).ready(function () {
        // Thêm sự kiện submit cho form
        $('#searchForm').submit(function (e) {
            e.preventDefault(); // Tránh sự kiện mặc định của form
            var selectedOption = $('[name="theothoigian"]').val(); // Lấy giá trị của option đã chọn
            loadData(selectedOption); // Gọi hàm để tải dữ liệu mới
        });

        // Hàm để tải dữ liệu mới
        function loadData(timePeriod) {
            var dataForDataSets = [];
            $.ajax({
                type: "GET",
                contentType: "application/json",
                url: "http://localhost:8080/laptopshop/api/don-hang/reportPareto/" + timePeriod,
                success: function (data) {
                    for (var i = 0; i < data.length; i++) {
                        dataForDataSets.push({
                            "label": data[i][0],
                            "y": data[i][1],
                            "y2": data[i][2]
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
                title: {
                    text: ""
                },
                axisY: {
                    title: "Doanh thu",
                    suffix: " VNĐ",
                    lineColor: "#4F81BC",
                    tickColor: "#4F81BC",
                    labelFontColor: "#4F81BC"
                },
                axisY2: {
                    title: "Số lượng",
                    lineColor: "#C0504E",
                    tickColor: "#C0504E",
                    labelFontColor: "#C0504E"
                },
                data: [{
                    type: "column",
                    dataPoints: dataForDataSets // Sử dụng dữ liệu mới
                }]
            });
            chart.render();
            // createPareto();
            var dps = [];
            var yValue, yTotal = 0, yPercent = 0, y2Total = 0;

            var yMax = Number.MIN_VALUE; // Giá trị khởi tạo của yMax là giá trị nhỏ nhất của số thực
            var y2Max = Number.MIN_VALUE;

            for (var i = 0; i < chart.data[0].dataPoints.length; i++) {
                yTotal += chart.data[0].dataPoints[i].y;
                y2Total += chart.data[0].dataPoints[i].y2;
            }

            // Tìm giá trị lớn nhất của axisY và axisY2
            for (var i = 0; i < chart.data[0].dataPoints.length; i++) {
                var yValue = chart.data[0].dataPoints[i].y;
                var y2Value = chart.data[0].dataPoints[i].y2;
                if (yValue > yMax) {
                    yMax = yValue;
                }
                if (y2Value > y2Max) {
                    y2Max = y2Value;
                }
            }

            for (var i = 0; i < chart.data[0].dataPoints.length; i++) {
                yValue = chart.data[0].dataPoints[i].y;
                // yPercent += (yValue / yTotal * 100);
                yPercent = chart.data[0].dataPoints[i].y2;
                dps.push({label: chart.data[0].dataPoints[i].label, y: yPercent});
            }

            chart.addTo("data", {type: "line", yValueFormatString: "0\"\"", dataPoints: dps});
            chart.data[1].set("axisYType", "secondary", false);
            chart.axisY[0].set("maximum", yMax*1.2);
            chart.axisY2[0].set("maximum", y2Max*1.2);
        }

        // Ban đầu, tải dữ liệu cho biểu đồ với tùy chọn mặc định
        loadData('nam');
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