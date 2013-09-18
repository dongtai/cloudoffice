package apps.transmanager.weboffice.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name="addresssort")
public class AddressSortPo implements SerializableAdapter{

	private static final long serialVersionUID = -7111194968051306572L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_addressSort_gen")
	@GenericGenerator(name = "seq_addressSort_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_ADDRESSSORT_ID") })
	private Long id;
	@ManyToOne
	@JoinColumn(name="address_id")
	private AddressListPo address;
	@ManyToOne
	@JoinColumn(name="sort_id")
	private SortListPo sort;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne
	public AddressListPo getAddress() {
		return address;
	}
	public void setAddress(AddressListPo address) {
		this.address = address;
	}
	@ManyToOne
	public SortListPo getSort() {
		return sort;
	}
	public void setSort(SortListPo sort) {
		this.sort = sort;
	}
	
	
	

}
