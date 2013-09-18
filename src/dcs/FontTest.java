package dcs;

import java.awt.GraphicsEnvironment;

public class FontTest {
	// 获取并打印本机字体列表
	public void getFontList()

	{
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		String[] fontList = ge.getAvailableFontFamilyNames();
		for (int i = 0; i < fontList.length; i++) {
			System.out.println(fontList[i]);
		}
	}

	public static void main(String[] args) {
		FontTest gfl = new FontTest();
		System.out.println("000000000000000");
		gfl.getFontList();
		System.out.println("111111111111111");
	}
}
