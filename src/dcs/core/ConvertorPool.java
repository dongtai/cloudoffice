package dcs.core;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import dcs.config.Config;
import dcs.util.FileUtil;


/**
 * 外部的线程控制
 * @author Administrator
 *
 */
public class ConvertorPool {
	private ConvertorPool() {initPro();}
    
    private static ConvertorPool instance = null;
    
    private ArrayList<ConvertorObject> pool = new ArrayList<ConvertorObject>();
    //池内维护了最大为5个实例，可以根据自己的服务器性能调整最大值
    private int maxSize = 2;
    //回收机制，简单的处理下可以避免内存使用过多。转500次文档之后就释放回收文档
    private int limitConvertSize = 500;
    

    private int availSize = 0;
    
    private int current = 0;
    
    private int[] badjudge;
    
    private void initPro()
    {
    	try {
    		maxSize = Config.getinstance().getMaxSize();
    		limitConvertSize = Config.getinstance().getLimitConvertSize();
    		badjudge = Config.getinstance().isBadjudge();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			maxSize = 2;
			limitConvertSize = 500;
			badjudge = null;
			e.printStackTrace();
		}
    }
    
    private boolean isbad(int result)
    {
    	for (int i = 0; badjudge != null && i < badjudge.length; i++) {
			if(result == badjudge[i])
			{
//				LogsUtility.error("convert error "+ result);
				return true;
			}
		}
    	return false;
    }
    
    public static ConvertorPool getInstance() {
        if (instance == null) {
            instance = new ConvertorPool();
        }
        return instance;
    }
    //获取池内一个转换实例
    public synchronized ConvertorObject getConvertor() {
        if (availSize > 0) {
            return getIdleConvertor();
        } else if (pool.size() < maxSize) {
            return createNewConvertor();
        } else {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getConvertor();
        }
    }
    //使用完成需要还给池内
    public synchronized void returnConvertor(ConvertorObject convertor) {
        for (ConvertorObject co : pool) {
            if (co == convertor) {
                co.convertsize++;
                try
                {
                	File file  = new File(co.tmp);
                	FileUtil.deleteFile(file);
                }
                catch(Exception e)
                {
                	
                }
                //
                if(co.convertsize >= limitConvertSize || isbad(co.result))
                {
                	reinitNewConvertor(co);
                }
                co.available = true;
                availSize++;
                notify();
                break;
            }
        }
    }
    
   
    private synchronized ConvertorObject getIdleConvertor() {
        for (ConvertorObject co : pool) {
            if (co.available) {
                co.available = false;
                availSize--;
                return co;
            }
        }
        return null;
    }
    
    private synchronized ConvertorObject createNewConvertor() {
        ConvertorObject co = new ConvertorObject(++current);
        try {
			co.convertor = ConvertClassLoader.getNewInstance();
			String s = UUID.randomUUID().toString(); 
			String fid = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
			co.tmp = ConvertClassLoader.getLib()+"tmp"+fid;
			ConvertUtil.setTempPath(co.convertor, co.tmp);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//new Convert();
        co.available = false;
        pool.add(co);
        return co;
    }
    
    private synchronized ConvertorObject reinitNewConvertor(ConvertorObject co) {
        try {
			co.convertor = ConvertClassLoader.getNewInstance();
			ConvertUtil.setTempPath(co.convertor, co.tmp);
			co.convertsize = 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//new Convert();
        return co;
    }
    
    //包装convert类，可记录是否在使用中
    public class ConvertorObject {
        public ConvertorObject(int id) {
            this.id = id;
        }
        public int id;
        public Object convertor;
        public boolean available;
        public String tmp;
        public int convertsize;
        //0 转换成功
        //1：传入的文件，找不到
        //2：传入的文件，打开失败
        //3：转换过程异常失败
        //4：传入的文件有密码
        //5：targetFileName的后缀名错误
        //-1: 结果是null
        private int result;
        //是否已经损坏？可靠性提高
		public void setResult(int result) {
			this.result = result;
		}
    }
}
