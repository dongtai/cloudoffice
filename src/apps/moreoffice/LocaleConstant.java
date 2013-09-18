package apps.moreoffice;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import apps.transmanager.weboffice.service.config.WebConfig;
import apps.transmanager.weboffice.service.context.ApplicationContext;

public class LocaleConstant {
	public static LocaleConstant instance = new LocaleConstant();
	WebConfig webConfig = (WebConfig)ApplicationContext.getInstance().getBean("webConfigBean");
	Locale locale = new Locale(webConfig.getLanguage(),webConfig.getCountry());
	private ResourceBundle rBundle = ResourceBundle.getBundle("message",locale);
	
	/**
	 * 获取无变量的资源方法
	 * @param key 键值
	 * @return 对应的value
	 */
	public String getValue(String key){
		String value = "";
		try {
			value = rBundle.getString(key);
		} catch (MissingResourceException e) {
			e.printStackTrace();
			return value;
		}
		return value;
	}
	
	/**
	 * 获取有变量的资源方法
	 * @param key 键值
	 * @param arrays Object ... 可变数目的参数
	 * @return 对应的value
	 */
	public String getValue(String key,Object ...arrays){
		String value = "";
		try {
			value = rBundle.getString(key);
			value = MessageFormat.format(value,arrays);
		} catch (MissingResourceException e) {
			e.printStackTrace();
			return value;
		}
		return value;
	}
}
