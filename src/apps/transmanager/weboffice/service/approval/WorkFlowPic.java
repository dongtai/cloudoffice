package apps.transmanager.weboffice.service.approval;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;

import apps.transmanager.weboffice.databaseobject.flow.FlowAllNode;
import apps.transmanager.weboffice.domain.workflow.WorkFlowPicBean;
import apps.transmanager.weboffice.service.config.WebConfig;

public class WorkFlowPic
{
	public static WorkFlowPic getInstance()
	{
		return new WorkFlowPic();
	}
	/**
	 * 暂未实现通用化，数字都是写死的
	 * @param userId
	 * @param approveId
	 * @param flowlist
	 * @return
	 */
	public String getApprovePic(Long userId, Long approveId,List<WorkFlowPicBean> flowlist,List<String> auditers)
	{
		//生成的流程图放在tempfile目录下，箭头放在static\images\personalset2目录下
		System.out.println(WebConfig.TEMPFILE_FOLDER+"==========="+WebConfig.tempFilePath+"==========="+WebConfig.userPortraitPath);
		
		try
    	{
			//计算图片的长和宽，箭头长80，流程框100
			int defA=80;//箭头占用宽度
			int defR=60;//流程框占用宽度
			int defH=90;//流程框占用高度
    		int width = 100, height = 80;//图片宽和高
//    		int viewnum=5;//一行最大流程节点数
//			List<Map<String, String>> flowlist = getApprovalProcess(userId, approveId,true);
			if (flowlist!=null && flowlist.size()>0)
			{
				
				int size=flowlist.size();
				int asize=auditers.size();
				int total=size+asize;
				int rows=0;
				height=100;
				for (int i=0;i<size;i++)
				{
					WorkFlowPicBean bean=flowlist.get(i);
					String[] names=bean.getNodeValues();
					if (names.length>rows)
					{
						rows=names.length;
					}
				}
				height+=rows*33;//30为字体的高度
				width=total*(defA+defR)+80-defA;//将初始的箭头长度减去

				
				
				//以上宽度小于700，高度为100*rows
				
	            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	            // 获取图形上下文   
	            Graphics oldg = image.getGraphics();
	            //以下是渲染，效果没有变好
//	            RenderingHints renderHints =  new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//	            renderHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
	            Graphics2D g = (Graphics2D)oldg;
	            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION   ,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	            
	            // 设定背景色 ,暂不设置背景色
	            g.setColor(new Color(255, 255, 255));
	            g.fillRect(0, 0, width, height);
	            
	            //设定字体   
	            int ad=80;//箭头宽空间
	            int ah=21;
	            int yd=34;//圆宽度
	            int yh=33;
	            
	            int x=3;
	            int y=10;//位置
	            int addr=0;
	            int nx=0;
	            int ny=10;
	            for (int i=0;i<size;i++)
	            {
	            	WorkFlowPicBean bean=flowlist.get(i);
	            	Integer[] states=bean.getStates();
	            	String[] actions=bean.getActions();
					if (i>0)
					{
						x+=defR;
			            insertArrow(g,x,y+7,"arrow1.png");//绘制箭头线,width包括箭头宽度10像素
			            x+=defA;
					}
		          //画边框 ，不需要，直接园，在输入数字  
//		            g.setColor(new Color(1,86,186)); //设置边框颜色
//		            g.fillRect(x,y,defR,defH);//绘制流程框,位置、宽和高
					
					insertArrow(g,x+15,y,"y1.png");
					
		            //流程框中的文字
		            g.setColor(new Color(50,50,50));//文字颜色
		            g.setFont(new Font("宋体", Font.PLAIN, 16));//这里要看操作系统有没有这个字体
		            g.drawString(""+(i+1), x+15+13,y+20);//绘制流程序号
		            
		            String signName="送审";
		            if (i==0)
		            {
		            	signName="送审";
		            }
		            else
		            {
		            	signName="签批";
		            }
		            int cy=y+40;//文字当前绘制高度
	            	for (int n=0;n<bean.getNodeValues().length;n++)
	            	{
	            		if (n==0)
	            		{
	            			cy+=10;
	            		}
	            		else
	            		{
	            			cy+=17;//签批时间的高度
	            		}
	            		if (states[n]!=null && states[n]==2)
	            		{
	            			g.setColor(new Color(0,0,0));//文字颜色
	            		}
	            		else
	            		{
	            			g.setColor(new Color(225,4,30));//没有签的用红色文字颜色
	            		}
            		 if(actions!=null && actions[n]!=null && "1".equals(actions[n]))
            		 {
            			 signName="送审";
            		 }
	            		g.setFont(new Font("宋体", Font.PLAIN, 14));//这里要看操作系统有没有这个字体
	            		g.drawString(bean.getNodeValues()[n]+signName, x,cy);
	            		String time=bean.getTimes()[n];
	            		if (time==null)
	            		{
	            			time="";
	            		}
	            		g.setFont(new Font("宋体", Font.PLAIN, 9));//这里要看操作系统有没有这个字体
	            		cy+=18;
			            g.drawString(time, x,cy);//换行要加字体高度

	            	}
	            	nx=x;
	            	ny=y;
	            }
	            if (auditers!=null && auditers.size()>0)
	            {
	            	for (int i=0;i<auditers.size();i++)
	            	{
						nx+=defR;
			            insertArrow(g,nx,ny+7,"arrow2.png");//绘制箭头线,width包括箭头宽度10像素
			            nx+=defA;
			          //画边框 ，不需要，直接园，在输入数字  
						
						insertArrow(g,nx+15,ny,"y3.png");
						
			            //流程框中的文字
			            g.setColor(new Color(50,50,50));//文字颜色
			            g.setFont(new Font("宋体", Font.PLAIN, 16));//这里要看操作系统有没有这个字体
			            g.drawString(""+(size+i+1), nx+15+13,ny+20);//绘制流程序号
			            
		            	g.drawString(auditers.get(i)+"待签", nx,ny+40+10);
	            	}
	            }
	            
	            
	            //释放Graphics资源
	            g.dispose();
	            image.flush();            
	            String filename=userId+"_"+approveId+"-"+System.currentTimeMillis();
	        	OutputStream os = new FileOutputStream(WebConfig.tempFilePath+File.separator+filename+".jpg");
	//    		OutputStream os = res.getOutputStream();
	    		if (image != null) {
	    			try {
	    				
	    				ImageIO.write(image, "JPEG", os);
	    				os.flush();
	    	
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			} finally {
	    				os.close();
	    			}
	    		}
	    		//流程图生成结束
	    		return WebConfig.TEMPFILE_FOLDER+"/"+filename+".jpg";
			}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
		return "";
	}
	
	/**
	 * 暂未实现通用化，数字都是写死的
	 * @param userId
	 * @param approveId
	 * @param flowlist
	 * @return
	 */
	public String getnewApprovePic(Long userId, Long approveId,List<WorkFlowPicBean> flowlist)
	{
		//生成的流程图放在tempfile目录下，箭头放在static\images\personalset2目录下
		System.out.println(WebConfig.TEMPFILE_FOLDER+"==========="+WebConfig.tempFilePath+"==========="+WebConfig.userPortraitPath);
		
		try
    	{
			//计算图片的长和宽，箭头长80，流程框100
			int defA=80;//箭头占用宽度
			int defR=60;//流程框占用宽度
			int defH=90;//流程框占用高度
    		int width = 100, height = 80;//图片宽和高
//    		int viewnum=5;//一行最大流程节点数
//			List<Map<String, String>> flowlist = getApprovalProcess(userId, approveId,true);
			if (flowlist!=null && flowlist.size()>0)
			{
				
				int size=flowlist.size();
				int total=size;
				int rows=0;
				height=80;//初始高度
				for (int i=0;i<size;i++)
				{
					WorkFlowPicBean bean=flowlist.get(i);
					String[] names=bean.getNodeValues();
					if (names.length>rows)
					{
						rows=names.length;
					}
				}
				height+=rows*35;//30为字体的高度
				width=total*(defA+defR)+80-defA;//将初始的箭头长度减去

				
				
				//以上宽度小于700，高度为100*rows
				
	            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	            // 获取图形上下文   
	            Graphics oldg = image.getGraphics();
	            //以下是渲染，效果没有变好
//	            RenderingHints renderHints =  new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//	            renderHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
	            Graphics2D g = (Graphics2D)oldg;
	            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION   ,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	            
	            // 设定背景色 ,暂不设置背景色
	            g.setColor(new Color(255, 255, 255));
	            g.fillRect(0, 0, width, height);
	            
	            //设定字体   
	            int ad=80;//箭头宽空间
	            int ah=21;
	            int yd=34;//圆宽度
	            int yh=33;
	            
	            int x=3;
	            int y=10;//位置
	            int addr=0;
	            int nx=0;
	            int ny=10;
	            for (int i=0;i<size;i++)
	            {
	            	WorkFlowPicBean bean=flowlist.get(i);
	            	Integer[] states=bean.getStates();
	            	String[] actions=bean.getActions();
					if (i>0)
					{
						x+=defR;
			            
			            if (states[0]!=null && states[0]==-1)//预定义的节点
			            {
			            	insertArrow(g,x,y+7,"arrow2.png");
			            }
			            else
			            {
			            	insertArrow(g,x,y+7,"arrow1.png");//绘制箭头线,width包括箭头宽度10像素
			            }
			            x+=defA;
					}
		          //画边框 ，不需要，直接园，在输入数字  
//		            g.setColor(new Color(1,86,186)); //设置边框颜色
//		            g.fillRect(x,y,defR,defH);//绘制流程框,位置、宽和高
					
					
					if (states[0]!=null && states[0]==-1)//预定义的节点
					{
						insertArrow(g,x+15,y,"y3.png");
					}
					else
					{
						insertArrow(g,x+15,y,"y1.png");
					}
					
		            //流程框中的文字
		            g.setColor(new Color(50,50,50));//文字颜色
		            g.setFont(new Font("宋体", Font.PLAIN, 16));//这里要看操作系统有没有这个字体
		            g.drawString(""+(i+1), x+15+13,y+20);//绘制流程序号
		            
		            String signName="送审";
		            if (i==0)
		            {
		            	signName="送审";
		            }
		            else
		            {
		            	signName="签批";
		            }
		            int cy=y+40;//文字当前绘制高度
	            	for (int n=0;n<bean.getNodeValues().length;n++)
	            	{
	            		if (n==0)
	            		{
	            			cy+=10;
	            		}
	            		else
	            		{
	            			cy+=17;//签批时间的高度
	            		}
	            		int fontcolor=0;
	            		if (i==0 || (states[n]!=null && states[n]==2) || (actions[n]!=null && "1".equals(actions[n])))
	            		{
	            			g.setColor(new Color(0,0,0));//文字颜色
	            		}
	            		else if (states[n]!=null && states[n]==-1)//预定义的节点
	            		{
	            			fontcolor=1;
	            			g.setColor(new Color(50,50,50));//文字颜色
	            			signName="待签";
	            		}
	            		else
	            		{
	            			fontcolor=2;
	            			g.setColor(new Color(225,4,30));//没有签的用红色文字颜色
	            		}
	            		 if(actions!=null && actions[n]!=null && "1".equals(actions[n]))
	            		 {
	            			 signName="送审";
	            		 }
	            		
	            		g.setFont(new Font("宋体", Font.PLAIN, 14));//这里要看操作系统有没有这个字体
	            		g.drawString(bean.getNodeValues()[n], x+10,cy);
	            		String time=bean.getTimes()[n];
	            		if (time==null)
	            		{
	            			time="";
	            		}
	            		String signtime=bean.getSigntagdate()[n];//签收时间
	            		if (signtime!=null && signtime.length()>0)
	            		{
	            			g.setColor(new Color(0,0,0));//已签收的用黑色文字
	            			g.setFont(new Font("宋体", Font.PLAIN, 11));//这里要看操作系统有没有这个字体
		            		cy+=18;
				            g.drawString("签收:"+signtime, x,cy);//换行要加字体高度
				            if (fontcolor==0)
				            {
				            	g.setColor(new Color(0,0,0));
				            }
				            else if (fontcolor==1)
				            {
				            	g.setColor(new Color(50,50,50));;
				            }
				            else if (fontcolor==2)
				            {
				            	g.setColor(new Color(225,4,30));
				            }
	            		}
	            		if (time!=null && time.length()>0)
	            		{
		            		g.setFont(new Font("宋体", Font.PLAIN, 11));//这里要看操作系统有没有这个字体
		            		cy+=18;
				            g.drawString(signName+":"+time, x,cy);//换行要加字体高度
	            		}

	            	}
	            	nx=x;
	            	ny=y;
	            }

	            //释放Graphics资源
	            g.dispose();
	            image.flush();            
	            String filename=userId+"_"+approveId+"-"+System.currentTimeMillis();
	        	OutputStream os = new FileOutputStream(WebConfig.tempFilePath+File.separator+filename+".jpg");
	//    		OutputStream os = res.getOutputStream();
	    		if (image != null) {
	    			try {
	    				
	    				ImageIO.write(image, "JPEG", os);
	    				os.flush();
	    	
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			} finally {
	    				os.close();
	    			}
	    		}
	    		//流程图生成结束
	    		return WebConfig.TEMPFILE_FOLDER+"/"+filename+".jpg";
			}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
		return "";
	}
	public void drawArrow(Graphics2D g,int x,int y,int width)
	{
		g.setColor(new Color(0,0,0));
        g.fillRect(x,y+6,width-10,3);
        
        int[][] rgb; 
        try {
             File png_ref = new File(WebConfig.userPortraitPath+File.separator+"arrow.png");
             BufferedImage img = ImageIO.read(png_ref); 

             int columns = img.getWidth();
             int rows = img.getHeight();
             rgb = new int[rows][columns]; 
         
              for (int row=0; row<rows; row++){
                   for (int col=0; col<columns; col++){
                	   rgb[row][col] = img.getRGB(col, row);
                   }
              }
              g.drawImage(img, x+width-10, y, null);
              img.flush();
             }
        catch (Exception e) {
	       e.printStackTrace();
	     }
	}
	/**
	 * 
	 * @param g
	 * @param x 起始x位置
	 * @param y 起始y位置
	 * @param arrow 箭头图片名
	 */
	public void insertArrow(Graphics2D g,int x,int y,String arrow)
	{
		//arrow1.png
        int[][] rgb; 
        try {
             File png_ref = new File(WebConfig.userPortraitPath+File.separator+arrow);
             BufferedImage img = ImageIO.read(png_ref); 

             int columns = img.getWidth();
             int rows = img.getHeight();
             rgb = new int[rows][columns]; 
         
              for (int row=0; row<rows; row++){
                   for (int col=0; col<columns; col++){
                	   rgb[row][col] = img.getRGB(col, row);
                   }
              }
              g.drawImage(img, x+2, y, null);//+2是让箭头两端都空一点，箭头宽度为76，设置箭头空间为80
              img.flush();
             }
        catch (Exception e) {
	       e.printStackTrace();
	     }
	}
	
	public String writeLine(String oldpic,List<FlowAllNode> nodelist,Long formid)
	{
		try
    	{

			File file=new File(WebConfig.webContextPath+File.separator+"backflow/flowpic/"+oldpic);
            BufferedImage image = ImageIO.read(file);
            // 获取图形上下文   
            Graphics oldg = image.getGraphics();
            //以下是渲染，效果没有变好
            
//	        RenderingHints renderHints =  new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//	        renderHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            Graphics2D g = (Graphics2D)oldg;
//	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION   ,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);    
//	        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);    //画线平滑
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_DEFAULT);//画线平滑
            // 设定背景色 ,暂不设置背景色

				
            //流程框中的文字
            //g.setColor(new Color(50,50,50));//文字颜色
            //g.setFont(new Font("宋体", Font.PLAIN, 16));//这里要看操作系统有没有这个字体
            //g.drawString("测试字体的清晰度", 118,223);//绘制流程序号
            
            g.setColor(new Color(255,7,7));
            g.setStroke(new   BasicStroke(1)); 
            for (int i=0;i<nodelist.size();i++)
            {
            	FlowAllNode node=nodelist.get(i);
            	String str=node.getArrowsites();
            	try
            	{
	            	if (str!=null && str.indexOf(",")>0)
	            	{
	            		String[] temp=str.split(",");
	            		if (temp.length==4)
	            		{
	            			drawAL(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]),Integer.parseInt(temp[2]),Integer.parseInt(temp[3]),g);
	            		}
	            	}
            	}
            	catch (Exception e)
            	{
            		e.printStackTrace();
            	}
            }
            //释放Graphics资源
            g.dispose();
            image.flush(); 
            int index=oldpic.lastIndexOf('.');
            String filename=oldpic.substring(0,index)+"_"+formid+oldpic.substring(index);
            String backname="/"+WebConfig.TEMPFILE_FOLDER+"/"+filename;
        	OutputStream os = new FileOutputStream(WebConfig.webContextPath+backname);
    		if (image != null) {
    			try {
    				
    				ImageIO.write(image, "png", os);
    				os.flush();
    	
    			} catch (Exception e) {
    				e.printStackTrace();
    			} finally {
    				os.close();
    			}
    		}
    		//流程图生成结束
    		return backname;
		}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return "";
	}
	
	public static void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2)  
    {  
  
        double H = 10; // 箭头高度   
        double L = 4; // 底边的一半   
        int x3 = 0;  
        int y3 = 0;  
        int x4 = 0;  
        int y4 = 0;  
        double awrad = Math.atan(L / H); // 箭头角度   
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度   
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);  
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);  
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点   
        double y_3 = ey - arrXY_1[1];  
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点   
        double y_4 = ey - arrXY_2[1];  
  
        Double X3 = new Double(x_3);  
        x3 = X3.intValue();  
        Double Y3 = new Double(y_3);  
        y3 = Y3.intValue();  
        Double X4 = new Double(x_4);  
        x4 = X4.intValue();  
        Double Y4 = new Double(y_4);  
        y4 = Y4.intValue();  
        // 画线   
        g2.drawLine(sx, sy, ex, ey);  
        //   
        GeneralPath triangle = new GeneralPath();  
        triangle.moveTo(ex, ey);  
        triangle.lineTo(x3, y3);  
        triangle.lineTo(x4, y4);  
        triangle.closePath();  
        //实心箭头   
        g2.fill(triangle);  
        //非实心箭头   
        //g2.draw(triangle);   
    }
	public static double[] rotateVec(int px, int py, double ang,  
            boolean isChLen, double newLen) {  
  
        double mathstr[] = new double[2];  
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度   
        double vx = px * Math.cos(ang) - py * Math.sin(ang);  
        double vy = px * Math.sin(ang) + py * Math.cos(ang);  
        if (isChLen) {  
            double d = Math.sqrt(vx * vx + vy * vy);  
            vx = vx / d * newLen;  
            vy = vy / d * newLen;  
            mathstr[0] = vx;  
            mathstr[1] = vy;  
        }  
        return mathstr;  
    }
//	public static  void  main(String[] args)
//	{
//		try
//		{
//			getInstance().writeLine();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
}
