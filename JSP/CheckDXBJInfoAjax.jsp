<%@ page language="java" contentType="text/html; charset=GBK" %>
<%@ page import="weaver.general.*" %>
<%@ page import="weaver.conn.*" %>
<%@ page import="java.util.*" %>
<jsp:useBean id="rs" class="weaver.conn.RecordSet" scope="page" />

<%
	String requestid = Util.null2String(request.getParameter("requestid"));  //����ID (disabled)
	BaseBean log = new BaseBean();
	log.writeLog("��ȡ����id"+requestid);
	String caption = "modified by guide-wanghui 2017-08-17 ";


	int sqr = Util.getIntValue(request.getParameter("sqr"),0);    //��Աid
	log.writeLog("��ȡ��Աid"+sqr);
	int type = Util.getIntValue(request.getParameter("type"),0);  //����
	double qjts = Util.getDoubleValue(request.getParameter("qjts"),0);  //�����������
	String sqrq = Util.null2String(request.getParameter("sqrq"));  //��ѯ����  
	String returnStr = "";

	String sql = "select isnull(sum(qjzts),0) as cnt from formtable_main_16 f,workflow_requestbase wr where f.requestid = wr.requestid and wr.currentnodetype not in (0) and f.qjlx = "+type+" and sqr = "+sqr+"  and qjqsrq >= dateadd(dd,-day('"+sqrq+"')+1,'"+sqrq+"') and qjqsrq <= dateadd(dd,-day('"+sqrq+"'),dateadd(m,1,'"+sqrq+"')) "; 


	//new BaseBean().writeLog("sqr = " + sqr + ", type = " + type + ",sqrq = " + sqrq); 
	//System.out.println(sql);

	rs.executeSql(sql);

	if(rs.next()){
		//new BaseBean().writeLog("count���ڼ�¼��"); 
		double cnt = Util.getDoubleValue(rs.getString("cnt"),0);
		cnt += qjts;
		if(cnt > 1) {
			returnStr = String.valueOf(cnt);
		}
	}

//	returnStr = "day:" + qjts + ", allday:" + returnStr;

%>   

<%=returnStr%>