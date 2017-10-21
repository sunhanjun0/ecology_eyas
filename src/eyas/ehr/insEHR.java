package eyas.ehr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import weaver.general.Util;
import weaver.hrm.User;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.DetailTable;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;
import weaver.soa.workflow.request.Row;

public class insEHR implements Action {
    private Log log = LogFactory.getLog(insEHR.class.getName());
    private String p1; // 自定义参数1
    private String p2; // 自定义参数2
    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public String execute(RequestInfo requestinfo) {
        System.out.println("进入Action requestid=" + requestinfo.getRequestid());
        String requestid = requestinfo.getRequestid();// 请求ID
        String requestlevel = requestinfo.getRequestlevel();// 请求紧急程度
        String src = requestinfo.getRequestManager().getSrc(); // 当前操作类型	submit:提交/reject:退回
        String workflowid = requestinfo.getWorkflowid();// 流程路径ID
        String tablename = requestinfo.getRequestManager().getBillTableName();// 表单主表名称
        int billid = requestinfo.getRequestManager().getBillid();// 表单数据ID
        User usr = requestinfo.getRequestManager().getUser();// 获取当前操作用户对象
        String requestname = requestinfo.getRequestManager().getRequestname();// 请求标题
        String remark = requestinfo.getRequestManager().getRemark();// 当前用户提交时的签字意见
        int formid = requestinfo.getRequestManager().getFormid();// 表单ID
        int isbill = requestinfo.getRequestManager().getIsbill();// 是否是自定义表单
        //取主表数据
        Property[] properties = requestinfo.getMainTableInfo().getProperty();// 获取表单主字段信息
        for (int i = 0; i < properties.length; i++) {
            String name = properties[i].getName();// 主字段名称
            String value = Util.null2String(properties[i].getValue());// 主字段对应的值
            System.out.println(name + " " + value);
            log.info(name + " " + value);
        }
        // 取明细数据
        DetailTable[] detailtable = requestinfo.getDetailTableInfo().getDetailTable();// 获取所有明细表
        if (detailtable.length > 0) {
            for (int i = 0; i < detailtable.length; i++) {
                DetailTable dt = detailtable[i];// 指定明细表
                Row[] s = dt.getRow();// 当前明细表的所有数据,按行存储
                for (int j = 0; j < s.length; j++) {
                    Row r = s[j];// 指定行
                    Cell c[] = r.getCell();// 每行数据再按列存储
                    for (int k = 0; k < c.length; k++) {
                        Cell c1 = c[k];// 指定列
                        String name = c1.getName();// 明细字段名称
                        String value = c1.getValue();// 明细字段的值
                        System.out.println(name + " " + value);
                        log.info(name + " " + value);
                    }
                }
            }
        }

        //控制流程流转，增加以下两行，流程不会向下流转，表单上显示返回的自定义错误信息，这个控制只支持节点后附加操作
        requestinfo.getRequestManager().setMessageid("错误信息编号");//126221
        requestinfo.getRequestManager().setMessagecontent("返回自定义的错误信息");
        System.out.println("Action执行完成 传入参数p1=" + this.getP1() + "  p2="	+ this.getP2());
        log.info("Action执行完成 传入参数p1=" + this.getP1() + "  p2="	+ this.getP2());

        return SUCCESS;// return返回固定返回`SUCCESS`

//		//如果E8的版本是1604，也可以使用下面的代码进行控制，支持节点后、节点前、出口，注意必须返回 FAILURE_AND_CONTINUE;
//		requestinfo.getRequestManager().setMessagecontent("返回自定义的错误信息");
//		return FAILURE_AND_CONTINUE;

    }
}
