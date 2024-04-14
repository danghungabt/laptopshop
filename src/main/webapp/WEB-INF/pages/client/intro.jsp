<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Laptop Shop </title>
    </head>
    <body> 
           <jsp:include page="include/homeHeader.jsp"></jsp:include>

           <jsp:include page="include/introContent.jsp"></jsp:include>
           
           <jsp:include page="include/homeFooter.jsp"></jsp:include>

    </body>

    <style>
        #header-congty{
            font-size: 32px;
            text-align: center;
            margin: 20px;
        }

        .container {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;
            padding: 20px;
        }

        .column {
            margin: 20px;
            background-color: #f9f9f9;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        .title {
            margin-top: 0;
        }

        .last {
            margin-top: 0;
            font-style: italic;
        }

        .content {
            width: 100%;
            padding: 10px;
        }

        .content:not(:last-child) {
            margin-bottom: 20px;
        }

        .content ul.one {
            display: flex;
            justify-content: space-around;
        }
    </style>
</html>


		