package com.stu;

        import weaver.conn.RecordSet;

public class base {
    public static void main(String arg[]){
        RecordSet dbs = new RecordSet();
        dbs.execute("select * from hrmresource");
        String worknum = dbs.getString("workcode");
        System.out.println(worknum);
    }
}
