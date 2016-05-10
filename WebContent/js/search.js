var selectDept = $("#dept");
var selectMajor = $("#major");
var inputName = $("input[name='name']");
var selectSex = $("select[name='sex'");
//分页
var condation=null;
var curPage = 1;//当前页
var limit = 20;

var bodyParam={
		"name": $(inputName).val(),
		"dept": $(selectDept).val(),
		"major": $(selectMajor).val(),
		"sex": $(selectSex).val()
};
//dot
var tml = $("#temp-list").text();
var tempFn = null;

$("#major").focus(function(){
	if(!$(selectDept).val()){
		alert("请先选择院系");
		return;
	}else{
		$.ajax({
			url : "/icps/code",
			type : "POST",
			data : {"type": "major","code": $(selectDept).val()},
			success : function(data, statue) {
				var html = "";
				var majors = data[0]['info'];
				for(var i in majors){
					html+= "<option value='"+majors[i]['code'] +"'>";
					html+=majors[i]['cname']+"</option>";
				}
				
				$(selectMajor).append(html);
			},
			error : function(xhr, statue, err) {
				var info = xhr.responseJSON[0].info;
				alert(info);
			}
		});
	}
});
$(document).ready(function(){
	var html = "";
	$.ajax({
		url : "/icps/code",
		type : "POST",
		data : {"type": "dept"},
		success : function(data, statue) {
			var depts = data[0]['info'];
			for(var i in depts){
				html+= "<option value='"+depts[i]['code'] +"'>";
				html+=depts[i]['cname']+"</option>";
			}
			console.info(html);
			$(selectDept).append(html);
		},
		error : function(xhr, statue, err) {
			var info = xhr.responseJSON[0].info;
			alert(info);
		}
	});
	searchstu();
});
function searchstu(){
	search();
	var bodyData={
			"name": $(inputName).val(),
			"dept": $(selectDept).val(),
			"major": $(selectMajor).val(),
			"sex": $(selectSex).val()
	};
	//分页
	$.ajax({
			"url":"/icps/count",
			"type": "POST",
			"data": bodyParam,
			success: function(xhr,ret){
				var count = xhr[0].info;
				if( count ){
					var pageSum = Math.ceil(count/limit) ;//总页数
					$(".tcdPageCode").createPage({
				        pageCount:pageSum,
				        current:curPage,
				        backFn:function(p){
				        	curPage = p;
				            search(p);
				        }
				    });
				}
			}
	});
}
function search(){
	var bodyData={
			"name": $(inputName).val(),
			"dept": $(selectDept).val(),
			"major": $(selectMajor).val(),
			"sex": $(selectSex).val(),
			"curpage": curPage
	};
	//bodyParam["curpage"]=curPage;
	$.ajax({
		"url":"/icps/search",
		"type": "POST",
		"data": bodyData,
		success: function(xhr,ret){
			var con = $("#contmp");
			var xx = xhr[0]['info'];
			console.info(xx.length);
			//if(xx.length > 0 ){
				var tempFn = doT.template(tml);
				con.html(tempFn(xx));
			//}else{
			///	con.html();
			//}
		},
		error: function(xhr, stu){
			console.info(xhr);
			alert("登陆超时");
			setTimeout(function(){
				window.location.href="login.html";
			}, 2000);
		}
});
}