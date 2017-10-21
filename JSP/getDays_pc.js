
//PC端自动计算天数，过滤节假日

function cultime_init(fromDateTmp,fromTimeTmp,toDateTmp,toTimeTmp,resourceIdTmp,resultid){           
	 	$(""+fromDateTmp+","+fromTimeTmp+","+toDateTmp+","+toTimeTmp).bindPropertyChange( 
	 	function () {  
        	bindfunc(fromDateTmp,toDateTmp,toTimeTmp,fromTimeTmp,resourceIdTmp,resultid); 
        });	     
}

//根据上下午计算时间
function bindfunc(fromDateTmp,toDateTmp,toTimeTmp,fromTimeTmp,resourceIdTmp,resultid){
	//alert("bindfunc");
	var fromDate=$(fromDateTmp).val();
	var toDate= $(toDateTmp).val();
	var fromTime="";
	var toTime="";
	var resourceId=$(resourceIdTmp).val();		
	
	if(checktime(fromDate,toDate,toDateTmp,resultid)){
		return ;
	}
	
	var toTimeSel= $(toTimeTmp).find("option:selected").val();
	var fromTimeSel= $(fromTimeTmp).find("option:selected").val();				
   
	if(toDate==""||fromDate==""||toTimeSel==""||fromTimeSel==""){
		$(resultid).val("");
		$(resultid+"span").text("");
		return ;
	}

	if(toDate==fromDate&&fromTimeSel==1&&toTimeSel==0){	
		$(resultid).val("");
		$(resultid+"span").text("");							  
		return true;
	}
	
	var morstart="09:00";//上午上班
	var morend="13:30";//上午下班
	var afterstart="13:30";//下午上班
	var afterend="17:00";//下午下班

	if(fromTimeSel==0&&toTimeSel==0){
		fromTime=morstart;
		toTime=morend;
	}else if (fromTimeSel==0&&toTimeSel==1){
		fromTime=morstart;
		toTime=afterend;
	}else if (fromTimeSel==1&&toTimeSel==0){
		fromTime=afterstart;
		toTime=morend;
	}else if (fromTimeSel==1&&toTimeSel==1){
		fromTime=afterstart;
		toTime=afterend;
	}

//计算请假总天数（去除节假日），PC端返回天数正常，手机OA端返回NaN	                     						 			
   jQuery.get("/workflow/request/BillBoHaiLeaveXMLHTTP.jsp?operation=getLeaveDays&time=" + new Date(),
   {fromDate:fromDate,fromTime:fromTime,toDate:toDate,toTime:toTime,resourceId:resourceId},function(result){
		result = result.replace(/\r\n/g,'');// 去掉返回值前面的回车符
		$(resultid).val( Math.ceil(parseFloat(result).toFixed(1)*2)/2);

		//$(resultid+"span").text(Math.ceil(parseFloat(result).toFixed(1)*2)/2);
		//$(resultid).val( parseFloat(result).toFixed(1));
		//$(resultid+"span").text(parseFloat(result).toFixed(1));
	 });			 
}

//判断开始时间是否大于结束时间
function checktime(date1,date2,end,result){
	var arr1=date1.split('-');
	var arr2=date2.split('-');
	var d1=new Date();
	d1.setFullYear(arr1[0],arr1[1]-1,arr1[2] );
	 
	var d2=new Date();
	d2.setFullYear(arr2[0],arr2[1]-1,arr2[2]);
	
	var days=(d2.getTime()-d1.getTime())/(1000*3600*24);
	
	if(days<0){
		alert("结束时间不能小于开始时间!!");
		$(end).val("");
		$(end+"span").text("");
		$(result).val("");
		$(result+"span").text("");
		return true;
	}  
}