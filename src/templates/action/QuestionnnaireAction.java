package templates.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import templates.objectdb.Questionnaire;
import templates.service.QuestionnaireService;

import com.opensymphony.xwork2.ActionContext;

import flowform.AllSupport;
public class QuestionnnaireAction extends AllSupport{

	private HttpServletRequest request = ServletActionContext.getRequest();
	private Map<String, Object> session = ActionContext.getContext().getSession();
    private ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()); 
	private QuestionnaireService questionnaireService = (QuestionnaireService) ctx.getBean("questionnaireService");
	private Questionnaire questionnaire = new Questionnaire();
	private List<Questionnaire> questionnaires = new ArrayList<Questionnaire>();
	
	public String saveQue(){
		try {
			if(questionnaire.getUserName() == null || "null".equals(questionnaire.getUserName()) || questionnaire.getUserName().length()<1){
				questionnaire.setUserName("无名");
			}
			session.put("province", questionnaire.getProvince());
			session.put("industry", questionnaire.getIndustry());
			Date date = new Date();
			questionnaire.setqDate(date);
			questionnaireService.save(questionnaire);
			session.put("qdate", questionnaire.getqDate());
			session.put("username", questionnaire.getUserName());
			return SUCCESS;
		}
		 catch (Exception e) {
			 e.printStackTrace();
			 return "error";
			}
		
	}
	public String viewQue(){
		try {
			
		
		int a=0,b=0,c=0,d=0,e=0,f=0,g=0,h=0,j=0,k=0,l=0,m=0,n=0,o=0,
		p=0,q=0,r=0,s=0,t=0,u=0,v=0,w=0,x=0,y=0,z=0,
		a1=0,b1=0,c1=0,d1=0,e1=0;//问题的答案初始值
		questionnaires= questionnaireService.find("from Questionnaire");
		for(int i=0;i<questionnaires.size();i++){
			questionnaire=questionnaires.get(i);
			if(questionnaire.getQuestion1()==1){
				a++;
			}
			if(questionnaire.getQuestion1()==2){
				b++;
			}
			if(questionnaire.getQuestion1()==3){
				c++;
			}
			if(questionnaire.getQuestion1()==4){
				d++;
			}
			if(questionnaire.getQuestion2()==1){
				e++;
			}
			if(questionnaire.getQuestion2()==2){
				f++;
			}
			if(questionnaire.getQuestion3()==1){
				g++;
			}
			if(questionnaire.getQuestion3()==2){
				h++;
			}
			if(questionnaire.getQuestion3()==3){
				e1++;
			}
			if(questionnaire.getQuestion3()==4){
				j++;
			}
			String que4[]=questionnaire.getQuestion4().split(",");
			for(int i1=0;i1<que4.length;i1++){
				
				if("1".equals(que4[i1].trim())){
					k++;
				}
				if("2".equals(que4[i1].trim())){
					l++;
				}
				if("3".equals(que4[i1].trim())){
					m++;
				}
				if("4".equals(que4[i1].trim())){
					n++;
				}
				if("5".equals(que4[i1].trim())){
					o++;
				}
				if("6".equals(que4[i1].trim())){
					p++;
				}
				if("7".equals(que4[i1].trim())){
					q++;
				}
			}
			String que5[]=questionnaire.getQuestion5().split(",");
			for(int i1=0;i1<que5.length;i1++){
				if("1".equals(que5[i1].trim())){
					r++;
				}
				if("2".equals(que5[i1].trim())){
					s++;
				}
				if("3".equals(que5[i1].trim())){
					t++;
				}
				if("4".equals(que5[i1].trim())){
					u++;
				}
				if("5".equals(que5[i1].trim())){
					v++;
				}
			}
			String que6[]=questionnaire.getQuestion6().split(",");
			for(int i1=0;i1<que6.length;i1++){
				if("1".equals(que6[i1].trim())){
					w++;
				}
				if("2".equals(que6[i1].trim())){
					x++;
				}
				if("3".equals(que6[i1].trim())){
					y++;
				}
				if("4".equals(que6[i1].trim())){
					z++;
				}
			}
			if(questionnaire.getQuestion7()==1){
				a1++;
			}
			if(questionnaire.getQuestion7()==2){
				b1++;
			}
			if(questionnaire.getQuestion8()==1){
				c1++;
			}
			if(questionnaire.getQuestion8()==2){
				d1++;
			}
		}
		request.getSession().setAttribute("a", a);
		request.getSession().setAttribute("b", b);
		request.getSession().setAttribute("c", c);
		request.getSession().setAttribute("d", d);
		request.getSession().setAttribute("e", e);
		request.getSession().setAttribute("f", f);
		request.getSession().setAttribute("g", g);
		request.getSession().setAttribute("h", h);
		request.getSession().setAttribute("e1",e1);
		request.getSession().setAttribute("j", j);
		request.getSession().setAttribute("k", k);
		request.getSession().setAttribute("l", l);
		request.getSession().setAttribute("m", m);
		request.getSession().setAttribute("n", n);
		request.getSession().setAttribute("o", o);
		request.getSession().setAttribute("p", p);
		request.getSession().setAttribute("q", q);
		request.getSession().setAttribute("r", r);
		request.getSession().setAttribute("s", s);
		request.getSession().setAttribute("t", t);
		request.getSession().setAttribute("u", u);
		request.getSession().setAttribute("v", v);
		request.getSession().setAttribute("w", w);
		request.getSession().setAttribute("x", x);
		request.getSession().setAttribute("y", y);
		request.getSession().setAttribute("z", z);
		request.getSession().setAttribute("a1", a1);
		request.getSession().setAttribute("b1", b1);
		request.getSession().setAttribute("c1", c1);
		request.getSession().setAttribute("d1", d1);
		String result = request.getParameter("result");
		System.out.print(request.getAttribute("ddd"));
		if(result !=null || "excel".equals(request.getAttribute("excel")) ){
			return "result";
		}
		return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	public String viewExcel(){
		 try
	        {
			    String roleType = request.getParameter("roleType");
				
				if(roleType!=null){
					session.put("admin", roleType);
				}
			 request.setAttribute("excel", "excel");
	            /** *//** **********创建工作簿************ */
			    String rootPath =request.getSession().getServletContext().getRealPath("");  
//			    WritableWorkbook workbook = Workbook.createWorkbook(new File("d:/advice.xls"));
	            WritableWorkbook workbook = Workbook.createWorkbook(new File(rootPath+"/advice.xls"));
	            /** *//** **********创建工作表************ */
	            WritableSheet sheet = workbook.createSheet("工作表名称", 0);

	            /** *//** *********设置列宽**************** */
	            sheet.setColumnView(0, 15); // 第1列
	            sheet.setColumnView(1, 20); // 第2列
	            sheet.setColumnView(2, 50);
	            //设置行高
	            sheet.setRowView(0, 600, false);
	            sheet.setRowView(1, 400, false);
	            sheet.setRowView(7, 400, false);
	            //设置页边距
	            sheet.getSettings().setRightMargin(0.5);
	            //设置页脚
	            sheet.setFooter("", "", "测试页脚");
	            /** *//** ************设置单元格字体************** */
	            //字体
	            WritableFont NormalFont = new WritableFont(WritableFont.ARIAL, 10);
	            WritableFont BoldFont = new WritableFont(WritableFont.ARIAL, 14,
	                    WritableFont.BOLD);
	            WritableFont tableFont = new WritableFont(WritableFont.ARIAL, 12,
	                    WritableFont.NO_BOLD);
	            WritableFont baodanFont = new WritableFont(WritableFont.ARIAL, 10,
	                    WritableFont.BOLD);

	            /** *//** ************以下设置几种格式的单元格************ */
	            // 用于标题
	            WritableCellFormat wcf_title = new WritableCellFormat(BoldFont);
	            wcf_title.setBorder(Border.NONE, BorderLineStyle.THIN); // 线条
	            wcf_title.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
	            wcf_title.setAlignment(Alignment.CENTRE); // 水平对齐
	            wcf_title.setWrap(true); // 是否换行

	            // 用于表格标题
	            WritableCellFormat wcf_tabletitle = new WritableCellFormat(
	                    tableFont);
	            wcf_tabletitle.setBorder(Border.NONE, BorderLineStyle.THIN); // 线条
	            wcf_tabletitle.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
	            wcf_tabletitle.setAlignment(Alignment.CENTRE); // 水平对齐
	            wcf_tabletitle.setWrap(true); // 是否换行

	            // 用于正文左
	            WritableCellFormat wcf_left = new WritableCellFormat(NormalFont);
	            wcf_left.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
	            wcf_left.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
	            wcf_left.setAlignment(Alignment.LEFT);
	            wcf_left.setWrap(true); // 是否换行

	            // 用于正文左
	            WritableCellFormat wcf_center = new WritableCellFormat(NormalFont);
	            wcf_center.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
	            wcf_center.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
	            wcf_center.setAlignment(Alignment.CENTRE);
	            wcf_center.setWrap(true); // 是否换行

	            // 用于正文右
	            WritableCellFormat wcf_right = new WritableCellFormat(NormalFont);
	            wcf_right.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
	            wcf_right.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
	            wcf_right.setAlignment(Alignment.RIGHT);
	            wcf_right.setWrap(false); // 是否换行

	            // 用于跨行
	            WritableCellFormat wcf_merge = new WritableCellFormat(NormalFont);
	            wcf_merge.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
	            wcf_merge.setVerticalAlignment(VerticalAlignment.TOP); // 垂直对齐
	            wcf_merge.setAlignment(Alignment.LEFT);
	            wcf_merge.setWrap(true); // 是否换行

	            WritableCellFormat wcf_table = new WritableCellFormat(NormalFont);
	            wcf_table.setBorder(Border.ALL, BorderLineStyle.THIN); // 线条
	            wcf_table.setVerticalAlignment(VerticalAlignment.CENTRE); // 垂直对齐
	            wcf_table.setAlignment(Alignment.CENTRE);
	            wcf_table.setBackground(Colour.GRAY_25);
	            wcf_table.setWrap(true); // 是否换行

	            /** *//** ************单元格格式设置完成****************** */
	            //合并单元格,注意mergeCells(col0,row0,col1,row1) --列从0开始,col1为你要合并到第几列,行也一样
	            sheet.mergeCells(0, 0,2, 0);

	            sheet.addCell(new Label(0, 0, "用户云办公反馈表",
	                    wcf_title));
	            sheet.addCell(new Label(0, 1, "序号", wcf_table));
	            sheet.addCell(new Label(1, 1, "姓名", wcf_table));
	            sheet.addCell(new Label(2, 1, "建议", wcf_table));
	            questionnaires= questionnaireService.find("from Questionnaire");
	            for (int i = 0; i < questionnaires.size(); i++)
	            {
	                //对应你的vo类
	            	Questionnaire data = (Questionnaire) questionnaires.get(i);
	            	if(!"".equals(data.getAdvice()) || data.getAdvice().length()>0){
	            		 sheet.addCell(new Label(0, 2 + i, String.valueOf(i + 1),
	 	                        wcf_center));
	 	                sheet.addCell(new Label(1, 2 + i, data.getUserName(),
	 	                        wcf_center));
	 	                sheet
	 	                        .addCell(new Label(2, 2 + i, data.getAdvice(),
	 	                                wcf_center));
	            	}
	               
	          
	            }
	            /** *//** **********以上所写的内容都是写在缓存中的，下一句将缓存的内容写到文件中******** */
	           
	            workbook.write();
	            /** *//** *********关闭文件************* */
	            workbook.close();
	            System.out.println("导出成功");
	            // 存放url地址
	        } catch (Exception e)
	        {
	            System.out.println("在输出到EXCEL的过程中出现错误，错误原因：" + e.toString());
	        }
       return SUCCESS;
	}
	
	
	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}
	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}
	public List<Questionnaire> getQuestionnaires() {
		return questionnaires;
	}
	public void setQuestionnaires(List<Questionnaire> questionnaires) {
		this.questionnaires = questionnaires;
	}
}
