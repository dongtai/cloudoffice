package apps.transmanager.weboffice.util.server;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PageRanges;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;

public class PrintUtil 
{
	public  static boolean print(List param,String sourceFilePath,String pdffilePath,String oldName)
	{
		
		String name = (String)param.get(1);
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE; 
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		PrintService[] printService = PrintServiceLookup.lookupPrintServices(flavor, pras);
		PrintService service = null;
		for(int i=0;i<printService.length;i++)
		{
			if(printService[i].getName().equals(name))
			{
				service = printService[i];
			}
		}
		String copyNum = (String)param.get(2);
		if(null!=copyNum&&!"".equals(copyNum))
		{
			pras.add(new Copies(Integer.valueOf(copyNum)));
		}
		else
		{
			pras.add(new Copies(1));
		}
		String printType = (String)param.get(3);
		String start = "";
		String end = "";
		if(printType.indexOf("-")<0)//全部打印
		{
		}
		else//区间打印
		{
			start = printType.split("-")[0];
			end = printType.split("-")[1];
			pras.add(new PageRanges(Integer.valueOf(start),Integer.valueOf(end)));
		}
		
		
		if (service != null)
		{
			try 
			{ 
				File file = null;
				if(null==pdffilePath)
				{
					file = new File(sourceFilePath);
				}
				else
				{
					file = new File(pdffilePath);
				}
				final File delFile = file;
				DocPrintJob job = service.createPrintJob();//创建打印作业
				FileInputStream fis = new FileInputStream(file);//构造待打印的文件流 
				DocAttributeSet das = new HashDocAttributeSet();
				JobName jobName = new JobName(oldName,null);
				pras.add(jobName);
				Doc doc = new SimpleDoc(fis, flavor, das);
				//建立打印文件格式 
				job.print(doc, pras);//进行文件的打印
				job.addPrintJobListener(new PrintJobAdapter(){
					public void printJobCompleted(PrintJobEvent pje)
					{
						//把生成的临时文件删除掉
						delFile.delete();
					}
					
				});
				return true;
			} 
			catch(Exception e) 
			{ 
				e.printStackTrace(); 
				return false;
			} 
		}
		return false;
	}
	
	
	
	public static List getPrints()
	{
		System.out.println("获取打印机开始!");
		List list = new ArrayList();
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		//System.out.println(pras.size());
		//设置打印格式，因为未确定文件类型，这里选择AUTOSENSE 
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE; 
		//查找所有的可用打印服务
		PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras); 
		for (int i = 0; i < printService.length; i++)
		{
			list.add(printService[i].getName());
		}
		System.out.println("获取打印机结束!");
		return list;
	}
}
