calculateOrder()
function changeQuanity(id,value,price)
	{
		$.ajax({
			type: "GET",		
			url: "http://localhost:8080/laptopshop/api/gio-hang/changSanPhamQuanity?id="+id+"&value="+value,
			success: function(result){
				calculatePrice(id,value,price);
				calculateOrder();
				console.log("sucess");	
			},
			error : function(e){
				alert("Error: ",e);
				console.log("Error" , e );
			}
		});
	}

function deleteFromCart(id)
{
	$.ajax({
		type: "GET",		
		url: "http://localhost:8080/laptopshop/api/gio-hang/deleteFromCart?id="+id,
		success: function(result){
		    var element = document.getElementById("item"+id);
			element.parentNode.removeChild(element);
			window.location.reload();
			calculateOrder();
		},
		error : function(e){
			alert("Error: ",e);
			console.log("Error" , e );
		}
	});
}

function calculatePrice(id,value,price)
{
	var element = document.getElementById("item"+id+"_total");

	element.innerHTML = value * price;
}

function calculateOrder()
{
	var element = document.getElementsByClassName("total");
	var res = 0;
	for (i = 0; i < element.length; i++) {
		console.log("beforeres1", element[i].textContent);
		res = res + parseFloat(element[i].textContent.toString());
	// 	res = res + accounting.formatMoney(element[i].textContent);
		console.log("beforeres2", element[i].textContent);
	}
	console.log("res", res);
	var element2 = document.getElementById("ordertotal");
	resConvert = accounting.formatMoney(res);
	element2.innerHTML = resConvert;
	console.log("resConvert", resConvert);
}
