package apps.transmanager.weboffice.service.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hpsf.NoPropertySetStreamException;
import org.apache.poi.hpsf.Property;
import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.Section;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;
import org.apache.poi.util.HexDump;


public class ReadCustomPropertySets
{

    /**
     * <p>Runs the example program.</p>
     *
     * @param args Command-line arguments (unused).
     * @throws IOException if any I/O exception occurs.
     */
    
    public ReadCustomPropertySets(InputStream is)
    {
        this.is = is;
    }
    
    public Object[] getValue()
    {
        POIFSReader r = new POIFSReader();
        r.registerListener(new MyPOIFSReaderListener());
        try
        {
            r.read(is);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        return obj;
    }
    
    class MyPOIFSReaderListener implements POIFSReaderListener
    {
        public void processPOIFSReaderEvent(final POIFSReaderEvent event)
        {
            PropertySet ps = null;
            try
            {
                ps = PropertySetFactory.create(event.getStream());
                if (ps == null)
                {
                    return ;
                }
            }
            catch (NoPropertySetStreamException ex)
            {
                out("No property set stream: \"" + event.getPath() +
                    event.getName() + "\"");
                return;
            }
            catch (Exception ex)
            {
                //ex.printStackTrace();
                /*throw new RuntimeException
                    ("Property set stream \"" +
                     event.getPath() + event.getName() + "\": " + ex);*/
                return;
            }
            
            if (!(ps instanceof SummaryInformation))
            {
                return ;
            }

            /* Print the name of the property set stream: */
            out("Property set stream \"" + event.getPath() +
                event.getName() + "\":");

            /* Print the number of sections: */
            final long sectionCount = ps.getSectionCount();
            out("   No. of sections: " + sectionCount);

            /* Print the list of sections: */
            List sections = ps.getSections();
            int nr = 0;
            for (Iterator i = sections.iterator(); i.hasNext();)
            {
                /* Print a single section: */
                Section sec = (Section) i.next();
                out("   Section " + nr++ + ":");
                String s = hex(sec.getFormatID().getBytes());
                s = s.substring(0, s.length() - 1);
                out("      Format ID: " + s);

                /* Print the number of properties in this section. */
                int propertyCount = sec.getPropertyCount();
                out("      No. of properties: " + propertyCount);

                /* Print the properties: */
                Property[] properties = sec.getProperties();
                obj[0]=obj[1]=obj[2]="";
                for (int i2 = 0; i2 < properties.length; i2++)
                {
                    /* Print a single property: */
                    Property p = properties[i2];
                    long id = p.getID();
                    long type = p.getType();
                    Object value = p.getValue();
                    out("      Property ID: " + id + ", type: " + type +
                        ", value: " + value);
                    
                    if (id == 2)
                    {
                        obj[0] = value;
                    }
                    else if (id == 4)
                    {
                        obj[1] = value;
                    }
                    else if (id == 5)
                    {
                        obj[2] = value;
                    }
                    else if (id == 12)
                    {
                        obj[3] = value;
                        obj[4] = value;
                    }
                    else if (id == 13)
                    {
                        obj[4] = value;
                    }
                }
            }
        }
    }

    static void out(final String msg)
    {
        System.out.println(msg);
    }

    static String hex(final byte[] bytes)
    {
        return HexDump.dump(bytes, 0L, 0);
    }
    
    private InputStream is;
    private Object[] obj = new Object[5] ;
}
