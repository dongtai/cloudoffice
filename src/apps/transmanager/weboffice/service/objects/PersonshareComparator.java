package apps.transmanager.weboffice.service.objects;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import apps.transmanager.weboffice.databaseobject.NewPersonshareinfo;
import apps.transmanager.weboffice.databaseobject.Personshareinfo;

public class PersonshareComparator  implements Comparator
{
	public int compare(Object o1, Object o2)
    {
		Date date1 = null;
		Date date2 = null;
		String name1 = null;
		String name2 = null;
		if((o1 instanceof Personshareinfo) && (o2 instanceof NewPersonshareinfo))
        {
        	date1 = ((Personshareinfo)o1).getDate();
        	date2 = ((NewPersonshareinfo)o2).getDate();
        	name1 = ((Personshareinfo)o1).getUserinfoByShareowner().getRealName();
			name2 = ((NewPersonshareinfo)o2).getUserinfoByShareowner().getRealName();
        }
		else if((o1 instanceof NewPersonshareinfo) && (o2 instanceof Personshareinfo))
		{
			date1 = ((NewPersonshareinfo)o1).getDate();
        	date2 = ((Personshareinfo)o2).getDate();
        	name1 = ((NewPersonshareinfo)o1).getUserinfoByShareowner().getRealName();
			name2 = ((Personshareinfo)o2).getUserinfoByShareowner().getRealName();
		}
		else if((o1 instanceof Personshareinfo) && (o2 instanceof Personshareinfo))
		{
			date1 = ((Personshareinfo)o1).getDate();
        	date2 = ((Personshareinfo)o2).getDate();
        	name1 = ((Personshareinfo)o1).getUserinfoByShareowner().getRealName();
			name2 = ((Personshareinfo)o2).getUserinfoByShareowner().getRealName();
		}
		else
		{
			date1 = ((NewPersonshareinfo)o1).getDate();
        	date2 = ((NewPersonshareinfo)o2).getDate();
        	name1 = ((NewPersonshareinfo)o1).getUserinfoByShareowner().getRealName();
			name2 = ((NewPersonshareinfo)o2).getUserinfoByShareowner().getRealName();
		}
		if(date1.before(date2))
		{
			return 1;
		}
		else if (date1.after(date2))
		{
			return -1;
		}
		else
		{
			return ((RuleBasedCollator)Collator.getInstance(Locale.CHINA)).compare(name1, name2);
		}
    }
}
