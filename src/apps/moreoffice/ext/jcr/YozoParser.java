package apps.moreoffice.ext.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * 文件注释
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public class YozoParser implements Parser 
{
	private static final Set<MediaType> SUPPORTED_TYPES = Collections.unmodifiableSet(
		new HashSet<MediaType>(Arrays.asList(
			new MediaType("application", "eio"),
			new MediaType("application", "eit"),
			new MediaType("application", "eiw"))));
    public static final String YOZO_EIO_MIME_TYPE = "application/eio";
    public static final String YOZO_EIT_MIME_TYPE = "application/eit";
    public static final String YOZO_EIW_MIME_TYPE = "application/eiw";
    
    public Set<MediaType> getSupportedTypes(ParseContext context)
    {
            return SUPPORTED_TYPES;
    }

	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata) throws IOException, SAXException, TikaException
	{
		parse(stream, handler, metadata, new ParseContext());
	}

	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException,
			SAXException, TikaException
	{
		metadata.set(Metadata.CONTENT_TYPE, YOZO_EIO_MIME_TYPE);   // 需要更加具体内容确定是eio，eit，eiw等等。
		try
        {
			/*Object[] meta = application.util.Macro.getMetaData(stream);    // 获取文件属性信息。
			for (Object temp : meta)
			{
				metadata.set(property, temp);
			}*/

            // 先简单把内容作为一个整体解析
            String text = application.util.Macro.analyze(stream);
            XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
            xhtml.startDocument();
            xhtml.startElement("p");
            xhtml.characters(text);
            xhtml.endElement("p");
            xhtml.endDocument();
        }
        catch(Exception e)
        {
        }
        finally
        {
        }
	}

}
