
<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<%@ page import="weaver.hrm.HrmUserVarify" %>
<%@ page import="weaver.hrm.User" %>
<%@ page import="weaver.systeminfo.SystemEnv" %>
<%@ page import="weaver.general.Util" %>
<%@ page import="weaver.hrm.attendance.domain.*"%>
<%@ page import="weaver.hrm.schedule.HrmAnnualManagement"%>
<%@ page import="weaver.hrm.schedule.HrmPaidSickManagement"%>
<%@ page import="weaver.hrm.schedule.manager.HrmScheduleManager"%>
<%@ page import="weaver.general.BaseBean" %>

<jsp:useBean id="RecordSet" class="weaver.conn.RecordSet" scope="page" />
<jsp:useBean id="strUtil" class="weaver.common.StringUtil" scope="page" />
<jsp:useBean id="dateUtil" class="weaver.common.DateUtil" scope="page" />
<jsp:useBean id="HrmScheduleDiffUtil" class="weaver.hrm.report.schedulediff.HrmScheduleDiffUtil" scope="page" />
<jsp:useBean id="ResourceComInfo" class="weaver.hrm.resource.ResourceComInfo" scope="page" />
<jsp:useBean id="DepartmentComInfo" class="weaver.hrm.company.DepartmentComInfo" scope="page" />
<jsp:useBean id="paidLeaveTimeManager" class="weaver.hrm.attendance.manager.HrmPaidLeaveTimeManager" scope="page" />
<jsp:useBean id="attProcSetManager" class="weaver.hrm.attendance.manager.HrmAttProcSetManager" scope="page" />
<jsp:useBean id="attVacationManager" class="weaver.hrm.attendance.manager.HrmAttVacationManager" scope="page" />
<%


User user = HrmUserVarify.getUser (request , response) ;
if(user == null)  return ;


String returnString="";

String operation=Util.null2String(request.getParameter("operation"));



//检查其它请假类型是否可行
if("checkOtherLeaveType".equals(operation)){
	int leaveType=Util.getIntValue(request.getParameter("leaveType"),0);
	int otherLeaveType=Util.getIntValue(request.getParameter("otherLeaveType"),0);
	int resourceId=Util.getIntValue(request.getParameter("resourceId"),0);
	String fromDate=Util.null2String(request.getParameter("fromDate"));
    String fromYear="";
	if(fromDate.length()>=4){
		fromYear=fromDate.substring(0,4);
	}

	int requestId=Util.getIntValue(request.getParameter("requestId"),0);

	StringBuffer sb=new StringBuffer();
	sb.append(" select 1 ")
	  .append("   from Bill_BoHaiLeave b ")
	  .append("  where b.resourceId=").append(resourceId)
	  .append("    and b.otherLeaveType=").append(otherLeaveType==1?2:1)		
	  .append("    and (b.leaveType=3  or b.leaveType=4) ");

	if(!fromYear.equals("")){
		sb.append(" and b.fromDate like '").append(fromYear).append("%'");
	}

	if(requestId>0){
		sb.append(" and b.requestId<>"+requestId);
	}

	RecordSet.executeSql(sb.toString());
	if(RecordSet.next()){
		returnString+="false";
	}else{
		returnString+="true";
	}    
} else if("getLeaveDays".equals(operation)){//根据起始日期、起始时间、结束日期、结束时间获得请假天数
	returnString = new HrmScheduleManager(request, response).getLeaveDays();
}
if("getAnnualInfo".equals(operation)){
	String resourceId = Util.null2String(request.getParameter("resourceId"));
	String currentDate = Util.null2String(request.getParameter("currentDate"));
	String userannualinfo = HrmAnnualManagement.getUserAannualInfo(resourceId,currentDate);
	String thisyearannual = Util.TokenizerString2(userannualinfo,"#")[0];
	String lastyearannual = Util.TokenizerString2(userannualinfo,"#")[1];
	String allannual = Util.TokenizerString2(userannualinfo,"#")[2];
	 float[] freezeDays = attVacationManager.getFreezeDays(resourceId);
	 if(freezeDays[0] > 0) allannual += " - "+freezeDays[0];	
	returnString = "getAnnualInfo#"+SystemEnv.getHtmlLabelName(21614,user.getLanguage())+"&nbsp;:&nbsp;"+lastyearannual+"<br>"+SystemEnv.getHtmlLabelName(21615,user.getLanguage())+"&nbsp;:&nbsp;"+thisyearannual+"<br>"+SystemEnv.getHtmlLabelName(21616,user.getLanguage())+"&nbsp;:&nbsp;"+allannual;	
}

if("getPSInfo".equals(operation)){
	String resourceId = Util.null2String(request.getParameter("resourceId"));
	String currentDate = Util.null2String(request.getParameter("currentDate"));
	String userpslinfo = HrmPaidSickManagement.getUserPaidSickInfo(resourceId, currentDate);
	String thisyearpsldays = ""+Util.getFloatValue(Util.TokenizerString2(userpslinfo,"#")[0], 0);
	String lastyearpsldays = ""+Util.getFloatValue(Util.TokenizerString2(userpslinfo,"#")[1], 0);
	String allpsldays = ""+Util.getFloatValue(Util.TokenizerString2(userpslinfo,"#")[2], 0);
	 float[] freezeDays = attVacationManager.getFreezeDays(resourceId);
	 if(freezeDays[1] > 0) allpsldays += " - "+freezeDays[1];	
	returnString ="getPSInfo#"+ SystemEnv.getHtmlLabelName(24039,user.getLanguage())+"&nbsp;:&nbsp;"+lastyearpsldays+"<br>"+SystemEnv.getHtmlLabelName(24040,user.getLanguage())+"&nbsp;:&nbsp;"+thisyearpsldays+"<br>"+SystemEnv.getHtmlLabelName(24041,user.getLanguage())+"&nbsp;:&nbsp;"+allpsldays;
	BaseBean datelog = new BaseBean();
	datelog.writeLog(returnString);
}

if("getTXInfo".equals(operation)){
	String resourceId = Util.null2String(request.getParameter("resourceId"));
	String paidLeaveDays = String.valueOf(paidLeaveTimeManager.getCurrentPaidLeaveDaysByUser(resourceId));
	 float[] freezeDays = attVacationManager.getFreezeDays(resourceId);
	 if(freezeDays[2] > 0) paidLeaveDays += " - "+freezeDays[2];	
	returnString ="getTXInfo#"+SystemEnv.getHtmlLabelName(82854,user.getLanguage())+"&nbsp;:&nbsp;"+paidLeaveDays;	
}

if("initInfo".equals(operation)){
    String resourceId = Util.null2String(request.getParameter("resourceId"));
	String currentDate = Util.null2String(request.getParameter("currentDate"));
	String bohai = Util.null2String(request.getParameter("bohai"));
	int nodetype = strUtil.parseToInt(Util.null2String(request.getParameter("nodetype")));
	int workflowid = strUtil.parseToInt(Util.null2String(request.getParameter("workflowid")));
	String userannualinfo = HrmAnnualManagement.getUserAannualInfo(resourceId,currentDate);
	String thisyearannual = Util.TokenizerString2(userannualinfo,"#")[0];
	String lastyearannual = Util.TokenizerString2(userannualinfo,"#")[1];
	String allannual = Util.TokenizerString2(userannualinfo,"#")[2];
	String userpslinfo = HrmPaidSickManagement.getUserPaidSickInfo(resourceId, currentDate);
	String thisyearpsldays = ""+Util.getFloatValue(Util.TokenizerString2(userpslinfo,"#")[0], 0);
	String lastyearpsldays = ""+Util.getFloatValue(Util.TokenizerString2(userpslinfo,"#")[1], 0);
	String allpsldays = ""+Util.getFloatValue(Util.TokenizerString2(userpslinfo,"#")[2], 0);
	String paidLeaveDays = String.valueOf(paidLeaveTimeManager.getCurrentPaidLeaveDaysByUser(resourceId));
	String allannualValue = allannual;
	String allpsldaysValue = allpsldays;
	String paidLeaveDaysValue = paidLeaveDays;
	float[] freezeDays = attVacationManager.getFreezeDays(resourceId);
	if(freezeDays[0] > 0) allannual += " - "+freezeDays[0];
	if(freezeDays[1] > 0) allpsldays += " - "+freezeDays[1];
	if(freezeDays[2] > 0) paidLeaveDays += " - "+freezeDays[2];
	
	float realAllannualValue = strUtil.parseToFloat(allannualValue, 0);
	float realAllpsldaysValue = strUtil.parseToFloat(allpsldaysValue, 0);
	float realPaidLeaveDaysValue = strUtil.parseToFloat(paidLeaveDaysValue, 0);
	
	if(bohai.equals("true")){
			realAllannualValue = (float)strUtil.round(realAllannualValue - freezeDays[0]);
			realAllpsldaysValue = (float)strUtil.round(realAllpsldaysValue - freezeDays[1]);
			realPaidLeaveDaysValue = (float)strUtil.round(realPaidLeaveDaysValue - freezeDays[2]);
	}else{
		if(attProcSetManager.isFreezeNode(workflowid, nodetype)) {
			realAllannualValue = (float)strUtil.round(realAllannualValue - freezeDays[0]);
			realAllpsldaysValue = (float)strUtil.round(realAllpsldaysValue - freezeDays[1]);
			realPaidLeaveDaysValue = (float)strUtil.round(realPaidLeaveDaysValue - freezeDays[2]);
		}
	}
	returnString =allannualValue+"#"+allpsldaysValue+"#"+paidLeaveDaysValue+"#"+realAllannualValue+"#"+realAllpsldaysValue+"#"+realPaidLeaveDaysValue;
}
%>

<%=Util.toHtml(returnString)%>