package apps.transmanager.weboffice.databaseobject.bug;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Parameter;

import apps.transmanager.weboffice.domain.SerializableAdapter;

/**
 * BUG状态对应的动作表
 * @author 孙爱华
 * 2013.6.18
 *
 */
@Entity
@Table(name="stateactions")
public class StateActions implements SerializableAdapter{
	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_stateactions_gen")
	@GenericGenerator(name = "seq_stateactions_gen", strategy = "native", parameters = { @Parameter(name = "sequence", value = "SEQ_STATEACTIONS_ID") })
	private Long id;//编号
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BugStates bugStates;//对应的BUG状态
	@ManyToOne()
	@OnDelete(action=OnDeleteAction.CASCADE)
	private BugActions bugActions;//对应的BUG动作
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BugStates getBugStates() {
		return bugStates;
	}
	public void setBugStates(BugStates bugStates) {
		this.bugStates = bugStates;
	}
	public BugActions getBugActions() {
		return bugActions;
	}
	public void setBugActions(BugActions bugActions) {
		this.bugActions = bugActions;
	}
	

}
