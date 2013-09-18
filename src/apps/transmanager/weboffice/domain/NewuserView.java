package apps.transmanager.weboffice.domain;


/**
 * NewuserView entity. @author MyEclipse Persistence Tools
 */

public class NewuserView implements SerializableAdapter
{


    // Fields    

    private Long userId;
    private String email;

    // Constructors

    /** default constructor */
    public NewuserView()
    {
    }

    /** full constructor */
    public NewuserView(Long userId, String email)
    {
        this.userId = userId;
        this.email = email;
    }

    // Property accessors

    public Long getUserId()
    {
        return this.userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof NewuserView))
            return false;
        NewuserView castOther = (NewuserView)other;

        return ((this.getUserId() == castOther.getUserId()) || (this.getUserId() != null
            && castOther.getUserId() != null && this.getUserId().equals(castOther.getUserId())))
            && ((this.getEmail() == castOther.getEmail()) || (this.getEmail() != null
                && castOther.getEmail() != null && this.getEmail().equals(castOther.getEmail())));
    }

    public int hashCode()
    {
        int result = 17;

        result = 37 * result + (getUserId() == null ? 0 : this.getUserId().hashCode());
        result = 37 * result + (getEmail() == null ? 0 : this.getEmail().hashCode());
        return result;
    }


    }