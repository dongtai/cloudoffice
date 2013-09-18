package apps.transmanager.weboffice.servlet.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import apps.moreoffice.ext.share.QueryDb;
import apps.transmanager.weboffice.databaseobject.Users;
import apps.transmanager.weboffice.util.beans.PageConstant;

public class UploadPic extends HttpServlet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void init() throws ServletException
    {
        super.init();
        
        try
        {
        	/*********获取数据库连接地址********************************/
        	ServletContext application = getServletContext();
        	String confile=application.getRealPath("/WEB-INF/classes/conf/funcConfig.properties");
        	System.out.println("==========="+confile);
        	File file=new File(confile);
        	if (file.exists())
        	{
        		Properties prop = new Properties(); 
        		InputStream in = new BufferedInputStream(new FileInputStream(confile));

                try { 
                    prop.load(in); 
                    String url = prop.getProperty("jdbc-0.proxool.driver-url").trim(); 
                    String username = prop.getProperty("jdbc-0.user").trim(); 
                    String psw = prop.getProperty("jdbc-0.password").trim();
                    String urlip=prop.getProperty("server.urlip");
                    if (urlip!=null)
                    {
                    	urlip=urlip.trim();
                    }
                    String urlname=prop.getProperty("server.urlname");
                    if (urlname!=null)
                    {
                    	urlname=urlname.trim();
                    }
//                    System.out.println(url+"==="+username+"==="+psw);
                    QueryDb.link=url+"?user="+username+"&password="+psw+"&useUnicode=true&characterEncoding=utf-8";
                    QueryDb.urlip=urlip;
                    QueryDb.urlname=urlname;
                } catch (IOException e) { 
                    e.printStackTrace(); 
                } 

        	}
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
    }
	public void service(HttpServletRequest request, HttpServletResponse res) throws ServletException,
        IOException
    {
    	res.setContentType("application/octet-stream");
    	try
        {
    		String actiontype=request.getParameter("actiontype");
    		if ("download".equals(actiontype))
    		{
    			//下载jre插件
    			String filename=request.getParameter("filename");
    			res.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
                ServletOutputStream out = res.getOutputStream();
                
                String fPath = request.getSession().getServletContext().getRealPath("/setup")+ File.separatorChar;
	            File f = new File(fPath);
	            if (!f.exists())
	            {
	            	f.mkdirs();
	            }
	            fPath+=filename;
	            FileInputStream in = new FileInputStream(fPath);
    			
    		    try {
    		    	BufferedInputStream bis =  new BufferedInputStream(in);
    		    	BufferedOutputStream bos = new BufferedOutputStream(out);
    		    	byte[] buff = new byte[2048];
    		    	int bytesRead=0;
    		    	while ((bytesRead = bis.read(buff, 0, buff.length))!=-1 ) {
    		    		bos.write(buff, 0, bytesRead);
    		    	}
    		    	bos.close();
    		    	bis.close();
    		    } catch (Exception e) {
    		      e.printStackTrace();
    		    }
                out.close();
                
    		}
    		else if ("flowpic".equals(actiontype))//生成流程图
    		{
    			String flowid=request.getParameter("flowid");
    			try
            	{
            		int width = 800, height = 600;//图片宽和高
                    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

                    // 获取图形上下文   
                    Graphics g = image.getGraphics();

                    //生成随机类   
                    Random random = new Random();

                    // 设定背景色   
                    g.setColor(new Color(255, 255, 255));
                    g.fillRect(0, 0, width, height);

                    //设定字体   
                    g.setFont(new Font("宋体", Font.PLAIN, 18));


                    // 取随机产生的认证码(4位数字)   
                    String sRand = "";
                    for (int i = 0; i < 4; i++)
                    {
                        String rand = String.valueOf(random.nextInt(10));
                        sRand += rand;
                        // 将认证码显示到图象中   
                        g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110),
                            20 + random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成   
                        int a = random.nextBoolean() ? 1 : -1;
                        g.drawString(rand, 13 * i + 6, 16 + a * random.nextInt(5));
                    }
                  //画边框   
                    g.setColor(new Color(200,200,150)); 
                    g.fillRect(100,100,100,100);//绘制流程框

                    g.setColor(new Color(255,255,255));
                    g.drawString("领导签批", 110,150);
                    g.drawString("日期：", 110,170);
                    
                    drawArrow(g,20,150,80);//绘制箭头线
                    
                    //释放Graphics资源
                    g.dispose();
                    //image.flush();            

            		OutputStream os = res.getOutputStream();
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
            	}
            	catch (Exception e)
            	{
            		e.printStackTrace();
            	}
    		}
    		else
    		{
	            DataInputStream   in = new DataInputStream(request.getInputStream()); 
	            
	            // 上传的类型
	            String fPath = request.getSession().getServletContext().getRealPath("filedisks/uploadfile/talk/resource")+ File.separatorChar;
	            File f = new File(fPath);
	            if (!f.exists())
	            {
	            	f.mkdirs();
	            }
	            String filename=System.currentTimeMillis()+".jpg";
	            fPath+=filename;
	            File file = new File(fPath);
	            FileOutputStream tempos = new  FileOutputStream(file);
	    		int count=0;
	    		byte[] data=new byte[2048];
	            while ((count = in.read(data, 0, 2048)) != -1) { 
	            	tempos.write(data, 0, count);
	            }
	            tempos.flush();
	            tempos.close();
	            in.close();
	            //插入数据库
	            Users userInfo = (Users)request.getSession().getAttribute(PageConstant.LG_SESSION_USER);//当前登录的用户编号
	            request.getSession().setAttribute(userInfo.getId()+"-screen", "/filedisks/uploadfile/talk/resource/"+filename);
    		}
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
	public void drawArrow(Graphics g,int x,int y,int width)
	{
		g.setColor(new Color(0,0,0));
        g.fillRect(x,y+6,width-10,3);
        
        int[][] rgb; 
        try {
             File png_ref = new File("/root/aa.png");
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
             } catch (Exception e) {
               e.printStackTrace();
             }
	}
    /**
     * 
     * @param b
     * @return
     */
    public int convert(byte[] b)
    {
        int ret = 0;
        ret = (b[0] & 0xFF) | ((b[1] << 8) & 0xFF00) | ((b[2] << 16) & 0xFF0000)
            | ((b[3] << 24) & 0xFF000000);
        return ret;
    }
}
