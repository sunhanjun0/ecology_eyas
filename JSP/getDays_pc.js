
//PC���Զ��������������˽ڼ���

function cultime_init(fromDateTmp,fromTimeTmp,toDateTmp,toTimeTmp,resourceIdTmp,resultid){           
	 	$(""+fromDateTmp+","+fromTimeTmp+","+toDateTmp+","+toTimeTmp).bindPropertyChange( 
	 	function () {  
        	bindfunc(fromDateTmp,toDateTmp,toTimeTmp,fromTimeTmp,resourceIdTmp,resultid); 
        });	     
}

//�������������ʱ��
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
	
	var morstart="09:00";//�����ϰ�
	var morend="13:30";//�����°�
	var afterstart="13:30";//�����ϰ�
	var afterend="17:00";//�����°�

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

//���������������ȥ���ڼ��գ���PC�˷��������������ֻ�OA�˷���NaN	                     						 			
   jQuery.get("/workflow/request/BillBoHaiLeaveXMLHTTP.jsp?operation=getLeaveDays&time=" + new Date(),
   {fromDate:fromDate,fromTime:fromTime,toDate:toDate,toTime:toTime,resourceId:resourceId},function(result){
		result = result.replace(/\r\n/g,'');// ȥ������ֵǰ��Ļس���
		$(resultid).val( Math.ceil(parseFloat(result).toFixed(1)*2)/2);

		//$(resultid+"span").text(Math.ceil(parseFloat(result).toFixed(1)*2)/2);
		//$(resultid).val( parseFloat(result).toFixed(1));
		//$(resultid+"span").text(parseFloat(result).toFixed(1));
	 });			 
}

//�жϿ�ʼʱ���Ƿ���ڽ���ʱ��
function checktime(date1,date2,end,result){
	var arr1=date1.split('-');
	var arr2=date2.split('-');
	var d1=new Date();
	d1.setFullYear(arr1[0],arr1[1]-1,arr1[2] );
	 
	var d2=new Date();
	d2.setFullYear(arr2[0],arr2[1]-1,arr2[2]);
	
	var days=(d2.getTime()-d1.getTime())/(1000*3600*24);
	
	if(days<0){
		alert("����ʱ�䲻��С�ڿ�ʼʱ��!!");
		$(end).val("");
		$(end+"span").text("");
		$(result).val("");
		$(result+"span").text("");
		return true;
	}  
}