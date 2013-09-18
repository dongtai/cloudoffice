package backflow;

import apps.transmanager.weboffice.domain.SerializableAdapter;

public class ValueLabel implements SerializableAdapter
{
	private Long id;
	private String value;
	private String valuename;
	
	public ValueLabel(String value,String valuename)
	{
		this.value=value;
		this.valuename=valuename;
	}
	public Long getId()
	{
		return id;
	}
	public void setId(Long id)
	{
		this.id = id;
	}
	public String getValue()
	{
		return value;
	}
	public void setValue(String value)
	{
		this.value = value;
	}
	public String getValuename()
	{
		return valuename;
	}
	public void setValuename(String valuename)
	{
		this.valuename = valuename;
	}
}
