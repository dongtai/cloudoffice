package apps.transmanager.weboffice.domain;

/**
 * TODO: 文件注释
 * <p>
 * <p>
 * EIO版本:        EIO Office V1.3
 * <p>
 * <p>
 * <p>
 */
public interface FileConstants
{
    public static String MAX_SEARCH_RESULTS_STR = "25"; // 25 Documents
    public static int MAX_SEARCH_RESULTS = Integer.parseInt(MAX_SEARCH_RESULTS_STR);

    public static String REPOSITORY_CONFIG = "repository.xml";

    ////////////////////jcr///////////////////////
    public static final String P_JCR_DATA = "jcr:data";

    public static final String NODE_FOLDER = "eiokm:folder";
    public static final String NODE_FILE = "eiokm:document";
    public static final String NODE_CONTENT = "eiokm:content";
    public static final String NODE_THUMBNAIL = "eiokm:thumbnail";
    public static final String NODE_RESOURE = "eiokm:resource";
    public static final String NODE_PATH = "eiokm:path";

    public static final String USERS_READ = "eiokm:authUsersRead";
    public static final String USERS_WRITE = "eiokm:authUsersWrite";
    public static final String ROLES_READ = "eiokm:authRolesRead";
    public static final String ROLES_WRITE = "eiokm:authRolesWrite";
    
    public static final String VERSIONCOMMENT = "eiokm:versionComment";

    public static final String RECYCLER = "Recycler";
    public static final String TRASH = RECYCLER;
    public static final String DOC = "Document";
    public static final String RECENT = "Recent";
    public static final String CONF = "configuration";
    public static final String TEMPLATE = "template";
    public static final String WORKFLOW = "Workflow";
    public static final String SHARES = "Shares";
    public static final String BOOKMARKS = "BookMarks";
    String COMPANY_ROOT = "company_";
    String USER_ROOT = "user_";
    String GROUP_ROOT = "group_";
    String ORG_ROOT = "org_";
    String TEAM_ROOT = "team_";
    String AUDIT_ROOT = "system_audit_root";
    String PUBLISHMENTS = "Publishments";
    String ARCHIVES = "Archives";
    String SIGN_ROOT = "sign_audit_root";
    

    public static final String CREATED = "eiokm:created";
    public static final String AUTHOR = "eiokm:author";
    public static final String KEYWORDS = "eiokm:keywords";
    public static final String NAME = "eiokm:name";    
    public static final String NOTIFICATION = "eiokm:notification";
    public static final String MIMETYPE = "jcr:mimeType";
    public static final String OPENLIST = "eiokm:openlist";
    public static final String DELETED = "eiokm:deleted";
    public static final String LOCKLIST = "eiokm:locklist";
    public static final String CLOSELIST = "eiokm:closelist";
    public static final String SAVELIST = "eiokm:savefile";
    public static final String LOCK = "eiokm:lock";
    public static final String USER = "eiokm:user";
    public static final String USEDSPACE = "eiokm:usedSpace";
    public static final String ALLOCATEDSPACE = "eiokm:allocatedSpace";
    public static final String SHARECOMMENT ="eiokm:sharecomment";
    public static final String PERMIT ="eiokm:permit";
    public static final String DESC = "eiokm:desc";
    public static final String SPACE = "eiokm:space";

    public static final String TITLE = "eiokm:title";
    public static final String LASTMODIFIED = "jcr:lastModified";
    public static final String LASTMODIFIER = "eiokm:lastmodifier";
    public static final String SIZE = "eiokm:size";
    public static final String IMPORTANT = "eiokm:important";

    public static final String DOCMIME = "application/vnd.ms-word";
    public static final String XLSMIME = "application/vnd.ms-excel";
    public static final String PPTMIME = "application/vnd.ms-powerpoint";
    public static final String EIOMIME = "application/eio";
    public static final String PDFMIME = "application/pdf";
    public static final String HTMLMIME = "text/html";
    public static final String RTFMIME = "application/rtf";
    public static final String EITMIME = "application/eit";
    public static final String TXTMIME = "text/plaint";
    
    public static final String MIXVERSION = "mix:versionable";
    public static final String STATUS = "eiokm:status";
    public static final String ISCHECK = "eiokm:ischeck";
    public static final String ISENTRYPT = "eiokm:isEntrypt";
    /*------------文件列表类型：个人文档、群组文档、他人共享、我的共享---------------*/
    public static final String FILELISTTYPE = "fileListType";//文档类型 
    public static final String PRIVATEFILE = "privateFile"; //个人文档
    public static final String GROUPFILE = "groupFile";     //群组文档
    public static final String SHAREDFILE = "sharedFile";   //他人共享
    public static final String SHARINGFILE = "sharingFile"; //我的共享
    
}
