package com.stu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import weaver.conn.RecordSet;
import weaver.conn.RecordSetDataSource;
import weaver.general.BaseBean;
import weaver.general.StaticObj;
import weaver.general.Util;
import weaver.hxcy.entity.Detail;
import weaver.interfaces.datasource.DataSource;
import weaver.interfaces.workflow.action.Action;
import weaver.soa.workflow.request.Cell;
import weaver.soa.workflow.request.DetailTable;
import weaver.soa.workflow.request.Property;
import weaver.soa.workflow.request.RequestInfo;

import com.thoughtworks.xstream.alias.ClassMapper.Null;
import com.weaver.general.TimeUtil;


/**
 * 常规报销（费用分摊）流程，同步凭证数据到KIS的Action
 * 
 * @author qq
 * 
 */
public class CgbxFyftSendKisAction implements Action {
	public String execute(RequestInfo requestInfo) {
		
		// 凭证的制单人为：黄锦（ID:16400）
		// 科目通过ecology的fnabudgetfeetype表的ID查询codename（科目编码），然后通过获得的codename去金蝶的t_account表查询金蝶的科目ID
		// 第一个明细是借方，每一条明细都有一个独立的科目
		// 第二个明细是贷方，每条明细的摘要是所有借方摘要的拼接总和，所用的科目是根据员工（申请人）ID到hrmresource表的textfield1字段去查询对应的科目
		// 第三个明细是贷方，每一条明细都有一个独立的科目，每条明细的摘要是所有借方摘要的拼接总和
		
		new BaseBean().writeLog("开始常规报销（费用分摊）流程，同步凭证数据到KIS");
		Connection conn = null;
		PreparedStatement ps = null;
		RecordSet rs1 = new RecordSet();
		RecordSet rs2 = new RecordSet();

		// 申请人
		String sqr = null;
		String lastname=null;
		
		// 申请部门
		String bm = null;
		String departmentname =null;

		// 申请时间（业务日期）
		String sqsj = null;

		// 借方总额
		double debitTotal = 0;

		// 贷方总额
		double creditTotal = 0;

		// 凭证表头的摘要
		String btzy = null;
		int maxFNumber = 0;
		int maxSerialNum = 0;
		
		String jfzyzh1="";

		// 获取表单主字段信息
		Property[] properties = requestInfo.getMainTableInfo().getProperty();

		for (int i = 0; i < properties.length; i++) {
			String name = properties[i].getName();// 主字段名称
			String value = Util.null2String(properties[i].getValue());// 主字段对应的值
			System.out.println(name + " " + value);

			/**
			 * 申请人
			 */
			if (name.trim().equals("sqr")) {
				sqr = value.trim();
				new BaseBean().writeLog("申请人:"+sqr);
				
				if(sqr!=null && !"".equals(sqr)){
					new BaseBean().writeLog("查询申请人名称的语句："+"select lastname from HrmResource where id ="+ sqr);
					rs1.executeSql("select lastname from HrmResource where id ="+ sqr);
					
					if (rs1.next()) {
						lastname = rs1.getString("lastname");
						new BaseBean().writeLog("申请人名称："+lastname);
						continue;
					}
				}

				
			}

			
			/**
			 * 申请部门
			 */
			if(name.trim().equals("bm")){
				bm = value.trim();
				new BaseBean().writeLog("申请部门:"+bm);
				
				// 获取部门名称
				if(bm != null && !"".equals(bm)){
					rs2.executeSql("select departmentname from HrmDepartment where id="+ bm);
					if (rs2.next()) {
						departmentname = rs2.getString("departmentname");
						new BaseBean().writeLog("部门名称："+departmentname);
					}
					
				}
				continue;
			}


			// 申请时间
			if (name.trim().equals("sqsj")) {
				sqsj = value.trim() + " 00:00:00.000";
				new BaseBean().writeLog("申请时间:"+ sqsj);
				continue;
			}
		}

		// 用于存储所有凭证明细的集合
		List<Detail> mxList = new ArrayList<Detail>();

		// 借方摘要总和
		StringBuffer jfzyzh = new StringBuffer();
		
		
		// 处理第一个明细表(第一个明细表都是借方明细)
		DetailTable detailTable0 = requestInfo.getDetailTableInfo()
				.getDetailTable(0);
		try{
		for (int i = 0; i < detailTable0.getRow().length; i++) {
			// 定义一个明细对象，用于存储这一行的凭证明细数据
			Detail detail = new Detail();
			
			// 该贷方明细的科目为申请人对应的员工表中记录的预算科目
			detail.setKm(findKisAccountIdByUserId(sqr,requestInfo));

			for (int j = 0; j < detailTable0.getRow()[i].getCell().length; j++) {
				Cell cell = detailTable0.getRow()[i].getCell()[j];
				String name = cell.getName();// 明细字段名称
				String value = cell.getValue();// 明细字段的值

				if (name.equals("bz")) {//备注（作为摘要）
					//if (i == 0) {
						// 如果是第一条明细，那么将这一条明细的摘要作为凭证表头的摘要
						//摘要格式：备注(部门-申请人)
						btzy = value+"("+departmentname+"-"+lastname+")".trim();
					//}
					// 借方的摘要需要求总和，用于给每条贷方明细做摘要用
					if (btzy != null && !btzy.trim().equals("")) {
						jfzyzh.append(",");
						jfzyzh.append(btzy);
					}
					// 备注字段
					new BaseBean().writeLog("借款说明(摘要)：" + btzy);
					detail.setZy(btzy);
				} else if (name.equals("yskm")) {
					// 预算科目字段
					detail.setKm(findKisAccountIdByOaId(value));
				} else if (name.equals("je")) {
					// 金额字段
					detail.setJfje(Double.parseDouble(value));
					// 计算借方总额
					debitTotal += Double.parseDouble(value);
					new BaseBean().writeLog("对象中存储的借方金额为："+detail.getJfje());
				}
			}
			mxList.add(detail);
		}
		
		//摘要的处理
		jfzyzh1=jfzyzh.toString();
		if(!"".equals(jfzyzh1)){
			jfzyzh1=jfzyzh1.substring(1);
		}
		new BaseBean().writeLog("贷方摘要："+jfzyzh1);
		
		}catch (Exception e) {
			e.printStackTrace();
			new BaseBean().writeLog("捕捉借方第一个明细的异常:"+e.getMessage());
		}

		try{
		// 处理第二个明细表(第二个明细表都是贷方明细)
		DetailTable detailTable1 = requestInfo.getDetailTableInfo()
				.getDetailTable(1);

		for (int i = 0; i < detailTable1.getRow().length; i++) {
			// 定义一个明细对象，用于存储这一行的凭证明细数据
			Detail detail = new Detail();


			// 贷方明细的摘要是借方所有明细摘要的总和
			detail.setZy((jfzyzh.toString().length() > 0 ? jfzyzh.toString()
					.substring(1) : ""));

			// 该贷方明细的科目为申请人对应的员工表中记录的预算科目
			detail.setKm(findKisAccountIdByUserId(sqr,requestInfo));

			for (int j = 0; j < detailTable1.getRow()[i].getCell().length; j++) {
				Cell cell = detailTable1.getRow()[i].getCell()[j];
				String name = cell.getName();// 明细字段名称
				String value = cell.getValue();// 明细字段的值

				if (name.equals("bccxje")) {//报销冲销金额
					new BaseBean().writeLog("贷方金额为："+value);
					// 金额字段
					detail.setDfje(Double.parseDouble(value));

					// 计算贷方总额
					creditTotal += Double.parseDouble(value);
					new BaseBean().writeLog("对象中存储的贷方金额为："+detail.getDfje());
				}
			}
			mxList.add(detail);
		}
		}catch (Exception e) {
			e.printStackTrace();
			new BaseBean().writeLog("捕捉第二个明细表都是贷方明细异常:"+e.getMessage());
		}
		
		// 处理第三个明细表(第三个明细表都是贷方明细)
		DetailTable detailTable2 = requestInfo.getDetailTableInfo()
				.getDetailTable(2);
		try{
		for (int i = 0; i < detailTable2.getRow().length; i++) {
			// 定义一个明细对象，用于存储这一行的凭证明细数据
			Detail detail = new Detail();

			// 贷方明细的摘要是借方所有明细摘要的总和
			detail.setZy((jfzyzh.toString().length() > 0 ? jfzyzh.toString()
					.substring(1) : ""));
			
			// 该贷方明细的科目为申请人对应的员工表中记录的预算科目
			detail.setKm(findKisAccountIdByUserId(sqr,requestInfo));

			for (int j = 0; j < detailTable2.getRow()[i].getCell().length; j++) {
				Cell cell = detailTable2.getRow()[i].getCell()[j];
				String name = cell.getName();// 明细字段名称
				String value = cell.getValue();// 明细字段的值

				if (name.equals("skje")) {//收款金额
					new BaseBean().writeLog("贷方金额2为："+value);
					// 金额字段
					detail.setDfje(Double.parseDouble(value));

					// 计算贷方总额
					creditTotal += Double.parseDouble(value);
					new BaseBean().writeLog("对象中存储的贷方金额2为："+detail.getDfje());
				} else if (name.equals("skfs")) {//收款方式
					String skfs = value;
					if (skfs.trim().equals("0")) {
						detail.setKm(1000);
					} else {
						detail.setKm(1002);
					}
				}
			}
			mxList.add(detail);
		}
		}catch (Exception e) {
			e.printStackTrace();
			new BaseBean().writeLog("捕捉第三个明细表都是贷方明细异常:"+e.getMessage());
		}
		
		

		
		
		// 用于记录主表生成的ID
		int keyID = 0;
		
		// 往主表插数据
		try {
			DataSource dataSource = (DataSource) StaticObj
					.getServiceByFullname(("datasource.KIS"), DataSource.class);
			conn = dataSource.getConnection();
			
			new BaseBean()
			.writeLog("主表插入SQL:"
					+ "insert into t_voucher (FBrNo,FDate,FYear,FPeriod,"
									+ "FGroupID,FNumber,FReference,FExplanation,FAttachments,FEntryCount,FDebitTotal,FCreditTotal,"
									+ "FInternalInd,FChecked,FPosted,FPreparerID,FCheckerID,FPosterID,FCashierID,FHandler,"
									+ "FOwnerGroupID,FObjectName,FParameter,FSerialNum,FTranType,FTransDate,FFrameWorkID,"
									+ "FApproveID,FFootNote,FIsDailyAccount) values(0,"+sqsj+","+sqsj.substring(0, 4)+","+ sqsj.substring(5, 7)+",1,"+maxFNumber+",null,"+btzy+",0,"+mxList.size()+","+debitTotal+","+creditTotal+",null,0,0,16400,-1,-1,-1,null,"
									+ "1,null,null,"+maxFNumber+",0,"+maxSerialNum+",-1,-1,'',0)");

			ps = conn
					.prepareStatement(
							"insert into t_voucher (FBrNo,FDate,FYear,FPeriod,"
									+ "FGroupID,FNumber,FReference,FExplanation,FAttachments,FEntryCount,FDebitTotal,FCreditTotal,"
									+ "FInternalInd,FChecked,FPosted,FPreparerID,FCheckerID,FPosterID,FCashierID,FHandler,"
									+ "FOwnerGroupID,FObjectName,FParameter,FSerialNum,FTranType,FTransDate,FFrameWorkID,"
									+ "FApproveID,FFootNote,FIsDailyAccount) "
									+ "values (0,?,?,?,1,?,null,?,0,?,?,?,null,0,0,16400,-1,-1,-1,null,"
									+ "1,null,null,?,0,?,-1,-1,'',0)");
			
			new BaseBean().writeLog("1FDate：" + sqsj);
			ps.setString(1, sqsj);// FDate
			
			new BaseBean().writeLog("2FYear：" + sqsj.substring(0, 4));
			ps.setString(2, sqsj.substring(0, 4));// FYear（取年号）
			
			new BaseBean().writeLog("3FPeriod：" + sqsj.substring(5, 7));
			ps.setString(3, sqsj.substring(5, 7));// FPeriod(取月份)

			// 获取当前年号期号下的最大凭证序号
			RecordSetDataSource rskmID = new RecordSetDataSource("KIS");
			
			rskmID.executeSql("select max(FNumber) as max_number from t_voucher where FYear ="
					+ sqsj.substring(0, 4)
					+ " and FPeriod ="
					+ sqsj.substring(5, 7));
			if (rskmID.next()) {
				maxFNumber = rskmID.getInt("max_number");
			}

			maxFNumber++;

			new BaseBean().writeLog("4FNumber：" + maxFNumber);
			ps.setInt(4, maxFNumber);// FNumber
			
			new BaseBean().writeLog("5FExplanation：" + btzy);
			ps.setString(5, btzy);// FExplanation

			new BaseBean().writeLog("6FEntryCount包含的条目数量：" +  mxList.size());
			ps.setInt(6, mxList.size());// FEntryCount 包含的条目数量
			
			new BaseBean().writeLog("7FDebitTotal借方总额：" +  debitTotal);
			ps.setDouble(7, debitTotal);// FDebitTotal 借方总额
			
			new BaseBean().writeLog("8FCreditTotal 贷方总额：" +  creditTotal);
			ps.setDouble(8, creditTotal);// FCreditTotal 贷方总额
			// 获取最大序号
			
			rskmID.executeSql("select max(FSerialNum) as maxSerialNum from t_voucher");
			if (rskmID.next()) {
				maxSerialNum = rskmID.getInt("maxSerialNum");
			}
			maxSerialNum++;

			new BaseBean().writeLog("9FSerialNum 序号：" +  maxSerialNum);
			ps.setInt(9, maxSerialNum);// FSerialNum 序号，递增1
			
			new BaseBean().writeLog("10FIsDailyAccount：" +  sqsj);
			ps.setString(10, sqsj);// FIsDailyAccount

			ps.executeUpdate();
			
			//查询刚刚插入的主表主键ID
			RecordSetDataSource getKeyRs = new RecordSetDataSource("KIS");
			getKeyRs.executeSql("select max(fVoucherId) from t_voucher");
			
			if(getKeyRs.next()){
				keyID=getKeyRs.getInt(1);
			}

		} catch (Exception e) {
			new BaseBean().writeLog("插入凭证主表数据时出现异常：" + e.getMessage());
			requestInfo.getRequestManager().setMessageid(
					requestInfo.getRequestid() + "-"
							+ TimeUtil.getCurrentTimeString());// 提醒信息id
			requestInfo.getRequestManager().setMessagecontent(
					" 流程提交报错: " + e.getMessage());// 提醒信息内容
			return Action.FAILURE_AND_CONTINUE;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		new BaseBean().writeLog("开始处理明细数据");
		// 循环明细的数据，插入明细表
		for (int i = 0; i < mxList.size(); i++) {
			
			new BaseBean().writeLog("开始处理第"+i+"条明细数据");
			
			Detail detail = mxList.get(i);
			
			new BaseBean().writeLog("数据内容为："+detail);
			
			DataSource dataSource = (DataSource) StaticObj
					.getServiceByFullname("datasource.KIS", DataSource.class);
			try {
				conn = dataSource.getConnection();
				
				new BaseBean()
				.writeLog("明细表插入SQL:"
						+ "insert into t_voucherentry (FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,"
								+ "FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,"
								+ "FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID) values(0,"
						+ keyID + ",'"
						+ i+ "','"
						+ jfzyzh1 + "','"
						+ detail.getKm() + "',0,1,1.0,'"
						+ (detail.getJfje() > 0 ? 1 : 0) + "','"
						+ (detail.getJfje() > 0 ? detail.getJfje()
								: detail.getDfje()) + "','"
						+(detail.getDfje() > 0 ? detail.getDfje()
								: detail.getJfje()) + "',0.0,0,0.0,null,0,0,null,null,0,0,0)");
				
				

				ps = conn
						.prepareStatement("insert into t_voucherentry (FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,"
								+ "FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,"
								+ "FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID) values (0,?,?,?,?,0,1,1.0,?,?,?,0.0,0,0.0,null,0,0,null,null,0,0,0)");


				new BaseBean().writeLog("1：" + keyID);
				ps.setInt(1, keyID);// FVoucherID所属凭证表头ID（外键）
				
				new BaseBean().writeLog("2：" + i);
				ps.setInt(2, i);// FEntryID 条目序号 从0开始 步长为1
				
				new BaseBean().writeLog("3：" + jfzyzh1);
				ps.setString(3, jfzyzh1);// FExplanation 摘要
				
				new BaseBean().writeLog("4：" + detail.getKm());
				ps.setInt(4, detail.getKm());// FAccountID
				
				// 如果借方金额>0,就记录1；否则记录0
				new BaseBean().writeLog("5：" + (detail.getJfje() > 0 ? 1 : 0));
				ps.setInt(5, detail.getJfje() > 0 ? 1 : 0);// FDC
				
				new BaseBean().writeLog("6：" + (detail.getJfje() > 0 ? detail.getJfje()
						: detail.getDfje()));
				ps.setDouble(6, detail.getJfje() > 0 ? detail.getJfje()
						: detail.getDfje());
				
				new BaseBean().writeLog("7：" + (detail.getDfje() > 0 ? detail.getDfje()
						: detail.getJfje()));
				ps.setDouble(7, detail.getDfje() > 0 ? detail.getDfje()
						: detail.getJfje());
				
				
				ps.executeUpdate();
			} catch (Exception e) {
				new BaseBean().writeLog("插入凭证明细数据时出现异常：" + e.getMessage());
				requestInfo.getRequestManager().setMessageid(
						requestInfo.getRequestid() + "-"
								+ TimeUtil.getCurrentTimeString());// 提醒信息id
				requestInfo.getRequestManager().setMessagecontent(
						" 流程提交报错: " + e.getMessage());// 提醒信息内容
				return Action.FAILURE_AND_CONTINUE;
			} finally {
				if (ps != null) {
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		}

		new BaseBean().writeLog("常规报销（费用分摊）流程，同步凭证数据到KIS结束");
		
		return Action.SUCCESS;
	}

	/**
	 * 通过OA的科目ID查找KIS科目ID
	 * 
	 * @param oaAccountId
	 * @return
	 */
	public int findKisAccountIdByOaId(String oaAccountId) {
		RecordSet rs = null;
		RecordSetDataSource rsKis = null;
		try {
			rs = new RecordSet();
			// 通过OA的科目ID查找codename（科目编码）
			rs.executeSql("select codename from fnabudgetfeetype where id = "
					+ oaAccountId);
			String codeName = null;
			if (rs.next()) {
				codeName = rs.getString("codename");
			}

			// 通过获得的codename去金蝶的t_account表查询金蝶的科目ID
			rsKis = new RecordSetDataSource("KIS");
			rsKis.executeSql("select FAccountID from t_account where fnumber='"
					+ codeName + "'");
			if (rsKis.next()) {
				return rsKis.getInt("FAccountID");
			}
			return 0;
		} finally {
			rs = null;
			rsKis = null;
		}
	}

	/**
	 * 通过OA的用户ID查找KIS科目ID
	 * 
	 * @param userId
	 * @return
	 */
	public int findKisAccountIdByUserId(String userId,RequestInfo requestInfo) {
		RecordSet rs = null;
		RecordSetDataSource rsKis = null;
		try {
			rs = new RecordSet();
			// 通过OA的科目ID查找codename（科目编码）
			rs.executeSql("select textfield1 from hrmresource where id = "
					+ userId);
			String textfield1 = null;
			if (rs.next()) {
				textfield1 = rs.getString("textfield1");
			}
			
			// 判断借支科目是否为空，如果为空，直接报错不让提交
			if(textfield1 == null && "".equals(textfield1)){
				requestInfo.getRequestManager().setMessagecontent("流程提交出现问题: " + textfield1);// 提醒信息内容
				new BaseBean().writeLog("请填写借支科目再提交表单!");
			}
			
			// 通过获得的textfield1(员工对应的科目编号)去金蝶的t_account表查询金蝶的科目ID
			rsKis = new RecordSetDataSource("KIS");
			rsKis.executeSql("select FAccountID from t_account where fnumber='"
					+ textfield1 + "'");
			if (rsKis.next()) {
				return rsKis.getInt("FAccountID");
			}
			return 0;
		} finally {
			rs = null;
			rsKis = null;
		}
	}
}
