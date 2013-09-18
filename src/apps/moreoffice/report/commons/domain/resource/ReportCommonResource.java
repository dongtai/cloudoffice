package apps.moreoffice.report.commons.domain.resource;

/**
 * 报表公共资源
 * 
 * <p>
 * <p>
 * @YOZO版本:   report V1.0
 * <p>
 * @作者:       User266(胡鹏云)
 * <p>
 * @日期:       2012-8-2
 * <p>
 * @负责人:      User266(胡鹏云)
 * <p>
 * @负责小组:    report
 * <p>
 * <p>
 */
public interface ReportCommonResource
{
    // ----------表间规则----------
    final String READ = "提数";
    final String MODIFY = "修改";
    final String ADDDETAIL = "补充明细";
    final String DELDETAIL = "删除明细";
    final String NEWFORM = "新建表单";
    final String DELFORM = "删除表单";

    // ----------数据类型----------
    final String DATATYPE = "数据类型";
    final String TEXT = "文本";
    final String NUMBER = "数字";
    final String DATE = "日期";
    final String PICTURE = "图片";
    final String FILE = "附件";

    // ----------数据规范----------
    final String DATARULE = "数据规范";
    final String SYSTEMDEFINED = "系统预定义";
    final String SYSVAR = "系统变量";
    final String AUTONUM = "自动编号";
    final String DOWNLIST = "下拉列表";
    final String TREESELECT = "树型选择";
    final String LISTSELECT = "列表选择";

    // ----------系统变量----------
    final String CURRENTDATE = "当前日期";
    final String CURRENTDATETIME = "当前日期时间";
    final String CURRENTORGUSER = "当前用户所在部门";
    final String CURRENTUSERNAME = "当前用户姓名";
    final String CURRENTLOGINNAME = "当前用户登录账号";
    final String[] SYSVARS = new String[]{CURRENTDATE, CURRENTDATETIME, CURRENTORGUSER,
        CURRENTUSERNAME, CURRENTLOGINNAME};

    // ----------自动编号----------
    final String FIXEDWORDS = "固定文字";
    final String DATEVAR = "日期变量";
    final String NUMBERS = "顺序号位数";

    // ----------其它----------
    final String ASC = "升序";
    final String DESC = "降序";

    // ----------返回信息----------
    final String NO_ERROR = "操作成功";
}