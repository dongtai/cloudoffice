package apps.transmanager.weboffice.service.auth.opensso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import apps.transmanager.weboffice.constants.server.PropsConsts;
import apps.transmanager.weboffice.service.objects.PropsValue;

/**
 */
public class OpenSSOUtil
{

	private static final String GET_ATTRIBUTES = PropsValue.get(PropsConsts.OPENSSO_GET_ATTRIBUTES);
	private static final String GET_COOKIE_NAME = PropsValue.get(PropsConsts.OPENSSO_GET_COOKIE_NAME_TOKEN);
	private static final String GET_COOKIE_NAMES = PropsValue.get(PropsConsts.OPENSSO_GET_COOKIE_NAMES_FORWARD);
	private static final String VALIDATE_TOKEN = PropsValue.get(PropsConsts.OPENSSO_VALIDATE_TOKEN);
	private static OpenSSOUtil instance = new OpenSSOUtil();
	private Map<String, String[]> cookieNamesMap = new ConcurrentHashMap<String, String[]>();
	
	private OpenSSOUtil()
	{
	}
	
	public static Map<String, String> getAttributes(HttpServletRequest request,
			String serviceUrl) throws Exception
	{
		return instance._getAttributes(request, serviceUrl);
	}

	public static String getSubjectId(HttpServletRequest request,
			String serviceUrl)
	{
		return instance._getSubjectId(request, serviceUrl);
	}

	public static boolean isAuthenticated(HttpServletRequest request,
			String serviceUrl) throws IOException
	{
		return instance._isAuthenticated(request, serviceUrl);
	}

	private Map<String, String> _getAttributes(HttpServletRequest request,
			String serviceUrl) throws Exception
	{
		Map<String, String> nameValues = new HashMap<String, String>();
		String url = serviceUrl + GET_ATTRIBUTES;

		try
		{
			URL urlObj = new URL(url);
			HttpURLConnection urlc = (HttpURLConnection) urlObj.openConnection();

			urlc.setDoOutput(true);
			urlc.setRequestMethod("POST");
			urlc.setRequestProperty("Content-type",	"application/x-www-form-urlencoded");

			String[] cookieNames = _getCookieNames(serviceUrl);
			_setCookieProperty(request, urlc, cookieNames);
			OutputStreamWriter osw = new OutputStreamWriter(urlc.getOutputStream());

			osw.write("dummy");
			osw.flush();
			int responseCode = urlc.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				BufferedReader br = new BufferedReader(
						new InputStreamReader((InputStream) urlc.getContent()));
				String line = null;

				while ((line = br.readLine()) != null)
				{
					if (line.startsWith("userdetails.attribute.name="))
					{
						String name = line.replaceFirst("userdetails.attribute.name=", "");
						line = br.readLine();
						if (line.startsWith("userdetails.attribute.value="))
						{
							String value = line.replaceFirst("userdetails.attribute.value=", "");
							nameValues.put(name, value);
						}
					}
				}
			}
		}
		catch (MalformedURLException mfue)
		{
			mfue.printStackTrace();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
		return nameValues;
	}

	private String[] _getCookieNames(String serviceUrl)
	{
		String[] cookieNames = cookieNamesMap.get(serviceUrl);
		if (cookieNames != null)
		{
			return cookieNames;
		}
		List<String> cookieNamesList = new ArrayList<String>();
		try
		{
			String cookieName = null;
			String url = serviceUrl + GET_COOKIE_NAME;
			URL urlObj = new URL(url);
			HttpURLConnection urlc = (HttpURLConnection) urlObj.openConnection();

			BufferedReader br = new BufferedReader(
						new InputStreamReader((InputStream) urlc.getContent()));

			int responseCode = urlc.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK)
			{
				String line = null;
				while ((line = br.readLine()) != null)
				{
					if (line.startsWith("string="))
					{
						line = line.replaceFirst("string=", "");
						cookieName = line;
					}
				}
			}
			url = serviceUrl + GET_COOKIE_NAMES;
			urlObj = new URL(url);
			urlc = (HttpURLConnection) urlObj.openConnection();
			br = new BufferedReader(new InputStreamReader((InputStream) urlc.getContent()));

			if (urlc.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				String line = null;
				while ((line = br.readLine()) != null)
				{
					if (line.startsWith("string="))
					{
						line = line.replaceFirst("string=", "");
						if (cookieName.equals(line))
						{
							cookieNamesList.add(0, cookieName);
						}
						else
						{
							cookieNamesList.add(line);
						}
					}
				}
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}

		cookieNames = cookieNamesList.toArray(new String[cookieNamesList.size()]);
		cookieNamesMap.put(serviceUrl, cookieNames);
		return cookieNames;
	}

	private String _getSubjectId(HttpServletRequest request, String serviceUrl)
	{
		String cookieName = _getCookieNames(serviceUrl)[0];
		return getCookieValue(request, cookieName);
	}
	
	private static String getCookieValue(HttpServletRequest request, String name)
	{
		Cookie[] cookies = request.getCookies();
		if (cookies == null)
		{
			return null;
		}
		for (int i = 0; i < cookies.length; i++)
		{
			Cookie cookie = cookies[i];
			String cookieName = cookie.getName();
			if (cookieName.equalsIgnoreCase(name))
			{
				return cookie.getValue();
			}
		}
		return null;
	}

	private boolean _isAuthenticated(HttpServletRequest request,
			String serviceUrl) throws IOException
	{
		boolean authenticated = false;
		String url = serviceUrl + VALIDATE_TOKEN;
		URL urlObj = new URL(url);
		HttpURLConnection urlc = (HttpURLConnection) urlObj.openConnection();

		urlc.setDoOutput(true);
		urlc.setRequestMethod("POST");
		urlc.setRequestProperty("Content-type",	"application/x-www-form-urlencoded");

		String[] cookieNames = _getCookieNames(serviceUrl);
		_setCookieProperty(request, urlc, cookieNames);
		OutputStreamWriter osw = new OutputStreamWriter(urlc.getOutputStream());
		osw.write("dummy");
		osw.flush();
		int responseCode = urlc.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK)
		{
			String data = readContent(urlc.getInputStream());
			if (data.toLowerCase().indexOf("boolean=true") != -1)
			{
				authenticated = true;
			}
		}
		return authenticated;
	}

	private static String readContent(InputStream is) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null)
		{
			sb.append(line).append('\n');
		}
		br.close();
		return sb.toString().trim();
	}
	
	private void _setCookieProperty(HttpServletRequest request,
			HttpURLConnection urlc, String[] cookieNames)
	{
		StringBuilder sb = new StringBuilder();
		for (String cookieName : cookieNames)
		{
			String cookieValue = getCookieValue(request, cookieName);
			sb.append(cookieName);
			sb.append("=");
			sb.append(cookieValue);
			sb.append(";");
		}
		if (sb.length() > 0)
		{
			urlc.setRequestProperty("Cookie", sb.toString());
		}
	}

}
