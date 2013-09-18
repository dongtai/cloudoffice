package apps.transmanager.weboffice.service.jcr;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

public class QueryParams implements Serializable
{
    private static final long serialVersionUID = 2072424432578100142L;

    //  public static final String TYPE = "okm:query";
    //  public static final String LIST = "okm:queries";
    //  public static final String LIST_TYPE = "okm:queries";
    //  public static final String CONTENT = "okm:content";
    //  public static final String NAME = "okm:name";
    //  public static final String KEYWORDS = "okm:keywords";
    //  public static final String MIME_TYPE = "okm:mimeType";
    //  public static final String AUTHOR = "okm:author";
    //  public static final String LAST_MODIFIED_FROM = "okm:lastModifiedFrom";
    //  public static final String LAST_MODIFIED_TO = "okm:lastModifiedTo";
    //  public static final String CONTEXT = "okm:context";

    private String name;
    private String keywords;
    private String content;
    private String mimeType;
    private String context;
    private String author;
    private Calendar lastModifiedFrom;
    private Calendar lastModifiedTo;
    private HashMap properties;
    private String title;
    private String important;
    
    public String getImportant()
    {
        return important;
    }
    
    public void setImportant(String important)
    {
        this.important = important;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public HashMap getProperties()
    {
        return properties;
    }

    public void setProperties(HashMap properties)
    {
        this.properties = properties;
    }

    public Calendar getLastModifiedFrom()
    {
        return lastModifiedFrom;
    }

    public void setLastModifiedFrom(Calendar lastModifiedFrom)
    {
        this.lastModifiedFrom = lastModifiedFrom;
    }

    public Calendar getLastModifiedTo()
    {
        return lastModifiedTo;
    }

    public void setLastModifiedTo(Calendar lastModifiedTo)
    {
        this.lastModifiedTo = lastModifiedTo;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append("name=");
        sb.append(name);
        sb.append(", keywords=");
        sb.append(keywords);
        sb.append(", content=");
        sb.append(content);
        sb.append(",title=");
        sb.append(title);
        sb.append(", mimeType=");
        sb.append(mimeType);
        sb.append(", context=");
        sb.append(context);
        sb.append(", author=");
        sb.append(author);
        sb.append(", lastModifiedFrom=");
        sb.append(lastModifiedFrom == null ? null : lastModifiedFrom.getTime());
        sb.append(", lastModifiedTo=");
        sb.append(lastModifiedTo == null ? null : lastModifiedTo.getTime());
        sb.append(", properties=");
        sb.append(properties);
        sb.append("]");
        return sb.toString();
    }
}
