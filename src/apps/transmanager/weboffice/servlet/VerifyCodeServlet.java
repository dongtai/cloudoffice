package apps.transmanager.weboffice.servlet;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import apps.transmanager.weboffice.util.VerifyCodeUtils;

public class VerifyCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 验证码图片的宽度。
	private int width = 70;

	// 验证码图片的高度。
	private int height = 24;

	// 验证码字符个数
	private int codeCount = 4;

	public void init() throws ServletException {
			// 宽度
			String strWidth = this.getInitParameter("width");
			// 高度
			String strHeight = this.getInitParameter("height");
			// 字符个数
			String strCodeCount = this.getInitParameter("codeCount");
		
			// 将配置的信息转换成数值
			try {
			   if (strWidth != null && strWidth.length() != 0) {
			    width = Integer.parseInt(strWidth);
			   }
			   if (strHeight != null && strHeight.length() != 0) {
			    height = Integer.parseInt(strHeight);
			   }
			   if (strCodeCount != null && strCodeCount.length() != 0) {
			    codeCount = Integer.parseInt(strCodeCount);
			   }
			} catch (NumberFormatException e) {
			}
	}

	public void service(HttpServletRequest req, HttpServletResponse resp)
		   throws ServletException, java.io.IOException {
			HttpSession session =req.getSession();
			
			String randomCode =VerifyCodeUtils.getValidateCode(codeCount);
			BufferedImage buffImg=VerifyCodeUtils.getBufferedImage(width, height, randomCode);
		
			// 将四位数字的验证码保存到Session中。
			System.out.println(req.getSession().getId()+"==="+randomCode.toString());
			session.setAttribute("validateCode", randomCode.toString());
		
			// 禁止图像缓存。
			resp.setHeader("Pragma", "no-cache");
			resp.setHeader("Cache-Control", "no-cache");
			resp.setDateHeader("Expires", 0);
			resp.setContentType("image/jpeg");
			Cookie co =  new Cookie("code",randomCode.toString());
		    resp.addCookie(co);
		    
			ServletOutputStream sos = resp.getOutputStream();
			ImageIO.write(buffImg, "jpeg", sos);
			sos.close();
	}	
	
}
