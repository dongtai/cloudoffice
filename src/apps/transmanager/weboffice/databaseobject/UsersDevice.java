package apps.transmanager.weboffice.databaseobject;

import java.util.Date;

import javax.persistence.Column;
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
 * 用户的设备信息。
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */

@ Entity
@ Table(name = "userdevice")
public class UsersDevice implements SerializableAdapter
{
    @ Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    @ GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_users_device_gen")
    @ GenericGenerator(name = "seq_users_device_gen", strategy = "native", parameters = {@ Parameter(name = "sequence", value = "SEQ_USERS_DEVICE_ID")})
    private Long id;
    @ ManyToOne()
    @ OnDelete(action = OnDeleteAction.CASCADE)
    private Users user; // 
    @Column(length = 255)
    private String mobDevName; //
    @Column(length = 255)
    private String mobDevOS; //
    @Column(length = 100)
    private String mobDevID; //    
    private Date mobDevTime; //    
    private Short mobDevStatus = 0; // 1表示该设备需要禁用 0表示该设备正常

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Users getUser()
    {
        return user;
    }

    public void setUser(Users user)
    {
        this.user = user;
    }

    public String getMobDevName()
    {
        return mobDevName;
    }

    public void setMobDevName(String mobDevName)
    {
        this.mobDevName = mobDevName;
    }

    public String getMobDevOS()
    {
        return mobDevOS;
    }

    public void setMobDevOS(String mobDevOS)
    {
        this.mobDevOS = mobDevOS;
    }

    public String getMobDevID()
    {
        return mobDevID;
    }

    public void setMobDevID(String mobDevID)
    {
        this.mobDevID = mobDevID;
    }
    
    public Date getMobDevTime()
    {
        return mobDevTime;
    }

    public void setMobDevTime(Date mobDevTime)
    {
        this.mobDevTime = mobDevTime;
    }
    
    
    public Short getMobDevStatus()
    {
        return mobDevStatus;
    }

    public void setMobDevStatus(Short mobDevStatus)
    {
        this.mobDevStatus = mobDevStatus;
    }
}
