package eyas.ehr;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import java.util.HashMap;
import java.util.Map;

public class GetLeaveDay extends BaseBean{
    private float leaveDay;   // 请假天数
    private String leaveMsg;  // 请假信息
    private String ksrq;   //开始日期
    private String jsrq;   //结束日期
    private String kssj;   //开始时间
    private String jssj;   //结束时间
    private int qjlx;   //请假类型
    private int sqrID;  //OA中的申请人ID
    private Map leavemap=new HashMap();  //返回类型MAP
    // private String ks = ksrq + " " + kssj ;  //拼接开始日期和开始时间
    // private String js = jsrq + " " + jssj ;  //拼接结束日期和结束时间
    private void LeaveDay(){
        writeLog("进入日期计算过程");
        writeLog("-----------计算开始-----------");
        writeLog(ksrq);
        writeLog(jsrq);
        writeLog(kssj);
        writeLog(jssj);
        String sql =
                "declare @result varchar(100) " +
                "declare @leave_time numeric(10,2) " +
                "declare @leave_days numeric(10,2) "+
                "exec OA_up_kq_leavetime "
                        + "'" + ksrq + " " + kssj + "'" +","
                        + "'" + jsrq + " " + jssj + "'" + ",0,"
                        + "'" + qjlx + "'" + ","
                        + getsqrBH(sqrID) + ",0,0,@result out,@leave_time out,@leave_days out " +
                        "select @result as MSG,@leave_time as TIME,@leave_days as DAY";
        writeLog("sql语句打印：" + sql);
        RecordSetDataSource rs = new RecordSetDataSource("EHR_test");
        try {
            rs.executeSql(sql);
            writeLog("sql执行完成");
        } catch (Exception e) {
            e.printStackTrace();
            writeLog(e.getMessage());
        }

        if (rs.next()) {
            try {
                leaveMsg = rs.getString("MSG");
                writeLog(leaveMsg);
            } catch (Exception e) {
                e.printStackTrace();
                writeLog(e.getMessage());
            }
            try {
                leaveDay = rs.getFloat("DAY");
                writeLog(leaveDay);
            } catch (Exception e) {
                e.printStackTrace();
                writeLog(e.getMessage());
            }
        } else {
            writeLog("数据异常");
        }
        writeLog("leaveMsg："+leaveMsg+"leaveDay:"+leaveDay);
        writeLog("-----------计算结束-----------");
    }

    private String getsqrBH(int sqrID) {
        this.sqrID = sqrID;
        RecordSet rs1 = new RecordSet();
        String sql1 = "select outkey from Hrmresource where id =" + sqrID;
        writeLog("sql1语句打印：" + sql1);
        try {
            rs1.executeSql(sql1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sqrNum = null;
        if (rs1.next()) {
            sqrNum = rs1.getString("outkey");
            writeLog("获取EHR员工编号:" + sqrNum);
        } else {
            writeLog("获取员工编号失败，请检查sql1语句");
        }
        return sqrNum;
    }

    public Map getLeavemap() {
        LeaveDay();
        writeLog("-----------执行方法getleavemessage-----------");
        leavemap.put("day",leaveDay);
        writeLog("leaveDay数据写入");
        leavemap.put("msg", leaveMsg);
        writeLog("leaveMsg数据写入");
        writeLog("----------执行完毕-----------");
        return leavemap;
    }

    public void setKsrq(String ksrq) {
        this.ksrq = ksrq;
    }

    public void setJsrq(String jsrq) {
        this.jsrq = jsrq;
    }

    public void setKssj(String kssj) {
        this.kssj = kssj;
    }

    public void setJssj(String jssj) {
        this.jssj = jssj;
    }

    public void setQjlx(int qjlx) {
        this.qjlx = qjlx;
    }

    public void setSqrID(int sqrID) {
        this.sqrID = sqrID;
    }
}