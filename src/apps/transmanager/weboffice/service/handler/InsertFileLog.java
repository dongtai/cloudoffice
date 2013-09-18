package apps.transmanager.weboffice.service.handler;

import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.objects.FileOperLog;
import apps.transmanager.weboffice.service.server.FileSystemService;

public class InsertFileLog implements Runnable
{
	private String[] paths;
	private Long userid;
	private int optype;
	private FileOperLog fileOperLog;
	private Integer editname;
	private String opresult;
	private String opScript;

	public InsertFileLog(String[] paths, Long userid, int optype,FileOperLog fileOperLog,Integer editname) {
		this.paths=paths;
		this.userid=userid;
		this.optype=optype;
		this.fileOperLog=fileOperLog;
		this.editname=editname;
	}
	public InsertFileLog(String[] paths, Long userid, int optype,String opresult,String opScript,FileOperLog fileOperLog,Integer editname) {
		this.paths=paths;
		this.userid=userid;
		this.optype=optype;
		this.fileOperLog=fileOperLog;
		this.editname=editname;
		this.opresult=opresult;
		this.opScript=opresult;
	}
	@Override
	public void run() {
		try {
			FileSystemService fileSystemService = (FileSystemService)ApplicationContext.getInstance().getBean(FileSystemService.NAME);
	    	if (opresult==null || opresult.length()<1)
	    	{
	    		fileSystemService.insertFileListLog(paths, userid, optype,fileOperLog,editname);//16为下载
	    	}
	    	else
	    	{
	    		fileSystemService.insertFileListLog(paths, userid, optype,opresult,opScript,fileOperLog,editname);//16为下载
	    	}
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
