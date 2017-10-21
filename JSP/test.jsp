<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page import="eyas.ehr.GetLeaveDay" %>
<%@ page import="java.util.Map" %>
<%@ page import="weaver.general.BaseBean" %>

<%
    BaseBean log = new BaseBean();
    log.writeLog("TEST START");
    GetLeaveDay getday = new GetLeaveDay();
    getday.setKsrq("2017-07-26");
    getday.setKssj("08:30");
    getday.setJsrq("2017-08-29");
    getday.setJssj("11:00");
    getday.setQjlx(12);
    getday.setSqrID(1288);
    String messge;
    Float day;
    Map TestInfo = getday.getLeavemap();
    messge = (String) TestInfo.get("msg");
    day = (Float) TestInfo.get("day");
%>

<script>
    alert(<%=messge%>);
    alert(<%=day%>);
</script>