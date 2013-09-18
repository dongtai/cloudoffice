package apps.transmanager.weboffice.util.server;

import java.util.Date;
import java.util.List;

import apps.transmanager.weboffice.constants.server.Constant;
import apps.transmanager.weboffice.databaseobject.MobileSendInfo;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;
import apps.transmanager.weboffice.service.server.JQLServices;


public class BackgroundSend implements Runnable {
	String[] mobile;//手机号
	String content;//短信内容
	Long companyid;//公司id
	String companyname;//公司名称
	Users sender;//发送者
	int type;//业务类型
	Long[] outids;//业务编号
	boolean isback;//是否需要允许回复
	
	public BackgroundSend(String[] mobile,String content,Long companyid,String companyname,int type,Long[] outids,boolean isback,Users sender) {
		this.mobile = mobile;
		this.content = content;
		this.companyid=companyid;
		this.companyname=companyname;
		this.sender=sender;
		this.type=type;
		this.outids=outids;
		this.isback=isback;
	}

	@Override
	public void run() {
		try {
			if (mobile!=null && mobile.length>0 && (WebConfig.mobileopened || Constant.REGEST==type))//用户注册和手机登录不过滤短信发送
			{
				String mobiles="";
				for (int i=0;i<mobile.length;i++)
				{
					if (mobile[i]!=null && mobile[i].length()==11)
					{
						if (i==0)
						{
							mobiles+=mobile[i];
						}
						else
						{
							mobiles+=","+mobile[i];
						}
					}
				}
				if (mobiles.startsWith(","))
				{
					mobiles=mobiles.substring(1);
				}
				String ext="";
				JQLServices jqlService = (JQLServices)ApplicationContext.getInstance().getBean(JQLServices.NAME);
				if (isback)
				{
					List<Integer> list=(List<Integer>)jqlService.findAllBySql("select max(a.ext) from MobileSendInfo as a where a.isvalidate=0 and a.ext is not null and a.ext>0 ");
					Integer temp=list.get(0);
					if (temp==null)
					{
						temp=0;
					}
					if (temp>=200000)
					{
						list=(List<Integer>)jqlService.findAllBySql("select min(a.ext) from MobileSendInfo as a where a.isvalidate=0 and a.ext is not null and a.ext>0 ");
						temp=list.get(0);
						if (temp!=null && temp>1)
						{
							ext=""+(temp-1);
						}
					}
					else
					{
						ext=""+(temp+1);
					}
				}
				System.out.println("ext=================="+ext);
				
				Client client=new Client();//调用漫道科技的接口程序
				//短信发送
				String result_mt = client.mt(mobiles, content, ext, "", "510853816088228");
				if(result_mt.startsWith("-"))
				{
					System.out.print("发送失败！返回值为："+result_mt+"请查看webservice返回值对照表");
					return;
				}
				int issuccess=1;
				if (result_mt.indexOf("510853816088228")>=0)
				{
					//发送成功，将短信记录存数据库
					issuccess=0;
				}
				else
				{
					int i=0;
					while (i<10)
					{
						client=new Client();//调用漫道科技的接口程序
						result_mt = client.mt(mobiles, content, ext, "", "510853816088228");
						if (result_mt!=null && result_mt.length()>0)
						{
							issuccess=0;
							break;
						}
						Thread.sleep(20000);
						i++;
					}
				}
				for (int i=0;i<mobile.length;i++)//不管是否发送成功都进行记录
				{
					MobileSendInfo mobileSendInfo=new MobileSendInfo();
					mobileSendInfo.setMobile(mobile[i]);
					mobileSendInfo.setCompanyid(companyid);
					mobileSendInfo.setCompanyname(companyname);
					mobileSendInfo.setContent(content);
					mobileSendInfo.setSenddate(new Date());
					mobileSendInfo.setSender(sender);
					mobileSendInfo.setType(type);
					mobileSendInfo.setIssuccess(issuccess);
					mobileSendInfo.setOutid(outids[i]);
					if (ext.length()>0)
					{
						mobileSendInfo.setExt(Integer.parseInt(ext));//需要短信回复
					}
					jqlService.save(mobileSendInfo);
				}
				//输出返回标识，为小于19位的正数，String类型的。记录您发送的批次。
				System.out.print("发送成功，返回值为："+result_mt);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
}