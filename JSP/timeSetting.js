    /*
    *  TODO
     *  请在此处编写javascript代码
     */
    $(document).ready(function(){

        //alert($("#scbg").length);
        $("#scbg").bind("click",function(){
            $("input[name='check_node_1']").each(function(){
                var index=$(this).val();
                var jsgs=$("#field9209_"+index).val();
                var count=0;
                var y_sum=0;
                if(scy>0){
                    count++;
                    y_sum = y_sum + scy;
                }
                if(zcy>0){
                    if(jsgs=='A_SQH' || jsgs=='B_SQH'){}
                    else{
                        count++;
                        y_sum = y_sum + zcy;}
                }
                if(xcy>0){
                    if(jsgs=='A_SQH' || jsgs=='B_SQH'){}
                    else{
                        count++;
                        y_sum = y_sum + xcy;}
                }
                if(qqy>0){
                    if(jsgs=='A_SZX' || jsgs=='B_SZX' || jsgs=='D_SZX'){}
                    else{
                        count++;
                        y_sum = y_sum + qqy;}
                }
                if(hqy>0){
                    if(jsgs=='A_SZX' || jsgs=='B_SZX' || jsgs=='D_SZX'){}
                    else{
                        count++;
                        y_sum = y_sum + hqy;}
                }
                if(zhy2>0){
                    if(jsgs=='A_SQH' || jsgs=='B_SQH' || jsgs=='A_SZX' || jsgs=='B_SZX' || jsgs=='D_SZX'){}
                    else{
                        count++;
                        y_sum = y_sum + zhy2;}
                }
                var pjy=0;
                if(count>0){
                    pjy= y_sum/count; //平均样
                    pjy= pjy.toFixed(2);
                }

                var zhy=formateInt($("#field7100_"+index).val()); //综合样
                var zhjg=""; //综合结果
                if(jsgs.indexOf('A') >= 0){
                    if(pjy==0){ zhjg=zhy;}
                    else{
                        zhjg=Math.max(pjy,zhy);
                    }
                }else if(jsgs.indexOf('B') >= 0){
                    if(pjy==0){zhjg=zhy;}
                    else{
                        zhjg=Math.min(pjy,zhy);
                    }
                }else if(jsgs=='C'){
                    zhjg=zhy;
                }else if(jsgs.indexOf('D')>=0){
                    zhjg=pjy;
                }else if(jsgs=='E'){
                    zhjg=Math.max(scy,zcy);
                    zhjg=Math.max(zhjg,xcy);
                    zhjg=Math.max(zhjg,qqy);
                    zhjg=Math.max(zhjg,hqy);
                }else if(jsgs=='F'){
                    if(scy!=null){
                        zhjg=scy;
                    }else if(zcy!=null){
                        zhjg=zcy;
                    }else if(xcy!=null){
                        zhjg=xcy;
                    }else if(qqy!=null){
                        zhjg=qqy;
                    }else if(hqy!=null){
                        zhjg=hqy;
                    }else if(zhy2!=null){
                        zhjg=zhy2;
                    }
                }
                //console.log("index:"+index+" pjy:"+pjy+" zhy:"+zhy+" zhjg:"+zhjg+" count:"+count);

                if(zhjg==0){
                    zhjg="";
                }

                $("#field7101_"+index).val(zhjg);
                $("#field7101_"+index+"span").html(zhjg);
            });
        })

    });

function formateInt(num){

    if(num=="") num="0";

    return Number(num)

}

