package apps.transmanager.weboffice.util;

import java.util.Random;

public class AppUtil {

	private static char[] codeSequence = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
			   'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			   'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	
	private static Integer defCount = 6;
	
	public static String getRandomPwd(Integer count){
		StringBuffer randomCode = new StringBuffer();
		Random random = new Random();
		if(count==null || count.intValue()==0){
			count = defCount;
		}
		for(int i=0;i<count;i++){
			String strRand = String.valueOf(codeSequence[random.nextInt(36)]);
			randomCode.append(strRand);
		}
		return randomCode.toString();
	}
	
}
