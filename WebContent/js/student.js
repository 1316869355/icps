function uploadInfo(){
	var name= $("#name").val(),
			sex= $("select[name='sex']").val() ,
			region = $("select[name='region']").val(),
			relax = $("select[name='relax']").val(),
			blood = $("select[name='blood']").val(),
			start=$("select[name='start'").val();
	
	var paramData={
			"name": name,
			"sex": sex,
			"region": region,
			"relax": relax,
			"blood": blood,
			"start": start
	};
	console.info(paramData);
	/*$.ajax({
	url : "/icps/upload",
	type : "POST",
	data : paramData,
	success : function(data, statue) {
		//由于接收到的是字符串，所以要先转化为 json数据
		//配置response.setContentType("application/json");后，
		//servlet 返回的数据是json数据
		//	var str = eval(data);将字符串转换为json对象
		sessionStorage.setItem("username", $("#card_no").val());
	},
	error : function(xhr, statue, err) {
		var info = xhr.responseJSON[0].info;
		sp.innerHTML = info;
	}
});*/
}