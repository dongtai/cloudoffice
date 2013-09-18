package apps.transmanager.weboffice.constants.server;

/**
 * 文件系统中使用的常量定义
 * <p>
 * <p>
 * @author  徐文平
 * @version 2.0
 * @see     
 * @since   web2.0
 */
public interface RepositoryCons
{

	/**
	 * 文件系统中内容改变时候的事件类型。
	 */
	int CREATE_SPACE_EVENT = 0;                         // 建立空间
	int DELETE_SPACE_EVENT = CREATE_SPACE_EVENT + 1;    // 删除空间
	int NEW_FILE_EVENT = DELETE_SPACE_EVENT + 1;        //  新建文件
	int NEW_FOLDER_EVENT = NEW_FILE_EVENT + 1;          //  新建文件夹
	int RENAME_EVENT = NEW_FOLDER_EVENT + 1;            //  修改名字
	int DELETE_EVENT = RENAME_EVENT + 1;                //  删除
	int COPY_EVENT = DELETE_EVENT + 1;                  //  copy文件或文件夹
	int MOVE_EVENT = COPY_EVENT + 1;                    //  移动文件或文件夹
	int CREATE_VERSION_EVENT = MOVE_EVENT + 1;          //  建立版本
	int DELETE_VERSION_EVENT = CREATE_VERSION_EVENT + 1;      // 删除版本
	
	
}
