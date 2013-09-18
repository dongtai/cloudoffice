package apps.transmanager.weboffice.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;


public class VerifyCodeUtils {

		// 验证码字符个数
		private static int codeCount = 4;
		// 字体高度
		private static int fontHeight;

		private static int codeY;

		private static char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',  'J',
		   'K', 'L', 'M', 'N',  'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
		   'X', 'Y', 'Z',  '2', '3', '4', '5', '6', '7', '8', '9' };

		/**
		 * 产生随机数字或者字母
		 * @return
		 */
		public static String getValidateCode(int count){
			codeCount=count;
			// 创建一个随机数生成器类
			Random random = new Random();
			StringBuffer randomCode = new StringBuffer();
			
			for (int i = 0; i < codeCount; i++) {
			   String strRand = String.valueOf(codeSequence[random.nextInt(32)]);
			   randomCode.append(strRand);
			}
			
			return randomCode.toString();
		}	
	
	
	/**
	 * 通过验证码获取验证图片
	 * @param code
	 */
	public static BufferedImage getBufferedImage(int width,int height,String code) {
			 int x =  width / (codeCount + 1);
			 fontHeight = height - 3;
			 codeY = height - 5;
		
			// 定义图像buffer
			BufferedImage buffImg = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
			Graphics2D g = buffImg.createGraphics();
			// 将图像填充为白色
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);

			// 创建字体，字体的大小应该根据图片的高度来定。
			Font font = new Font("Times New Roman", Font.PLAIN, fontHeight);
			// 设置字体。
			g.setFont(font);

			// 画边框。
			g.setColor(Color.BLACK);
			g.drawRect(0, 0, width - 1, height - 1);

			g.setColor(Color.WHITE);
			
			for (int i = 0; i < codeCount; i++) {
			   g.drawString(code.charAt(i)+"", (i + 0.2f) * x, codeY);

			}
			g.dispose();
			
			return buffImg;
   }	
	

}
