package apps.transmanager.weboffice.util.beans;

public class Page {
	private int totalPage;        // 总页数
	private int currentPage;      // 当前页数
	private int totalRecord;      // 总记录数
	private int currentRecord;    // 当前记录
	private int pageSize=15;       // 默认每页记录数
	
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage)
	{
		this.totalPage = totalPage;
	}
	public void reSetTotalPage(int totalRecord,int pageSize) {
		if(totalRecord%pageSize==0){
			this.totalPage=totalRecord/pageSize;
		}else{
			this.totalPage=totalRecord/pageSize+1;
		}
	}
	public int getCurrentPage() {
		if(0==this.currentPage)
		{
			this.currentPage = 1;
		}
		return currentPage;
	}
	public void setCurrentPage(int currentRecord,int pageSize) {
		if(currentRecord!=0 && currentRecord%pageSize==0){
			this.currentPage=currentRecord/pageSize;
		}else{
			this.currentPage=currentRecord/pageSize+1;
		}
	}
	
	public void setCurrentPage(int currentPage)
	{
		this.currentPage = currentPage;
	}
	
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public int getCurrentRecord() {
		return currentRecord;
	}
	public void setCurrentRecord(int currentRecord) {
		this.currentRecord = currentRecord;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public void retSetTotalRecord(int totalRecord) {
		this.setTotalRecord(totalRecord);
		this.reSetTotalPage(totalRecord, this.pageSize);
	}
	
}

