package apps.transmanager.weboffice.service.jcr;

import java.io.Serializable;
import java.util.Collection;

public class MetaData implements Serializable {
	private static final long serialVersionUID = -2268653681607536626L;

	public static final int INPUT = 1;
	public static final int TEXT_AREA = 2;
	public static final int SELECT = 3;
	public static final int SELECT_MULTI = 4;
	
	private int type;
	private Collection values;
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public Collection getValues() {
		return values;
	}
	
	public void setValues(Collection values) {
		this.values = values;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("type="); sb.append(type);
		sb.append(", values="); sb.append(values);
		sb.append("]");
		return sb.toString();
	}
}
