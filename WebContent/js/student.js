$("document").ready(function(){
	var userName = sessionStorage.getItem("username");
});

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
	$.ajax({
	url : "/icps/upload",
	type : "POST",
	data : paramData,
	success : function(data, statue) {
		
	},
	error : function(xhr, statue, err) {
	//	var info = xhr.responseJSON[0].info;
		console.info(xhr)
	}
});
}

function logout(){
	
}


