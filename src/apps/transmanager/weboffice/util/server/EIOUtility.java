package apps.transmanager.weboffice.util.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class EIOUtility
{

//    static JFrame frame;
//    static IApplication app;

    public static void init()
    {
        /*if (frame == null)
        {
            frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setBounds(50, 50, 700, 400);
        }
        if (app == null)
        {
            app = ApplicationFactory.create(null, frame.getContentPane());
        }
        app.setShowErrorDialog(false);*/
        //frame.show();
    }

    public static void destory()
    {
        /*if (frame != null)
        {
            frame.dispose();
            frame = null;
        }
        if (app != null)
        {
            app.dispose();
            app = null;
        }*/
    }

    public static String[][] readFileByEIO(String fileName)
    {
    	try {
			List<String> list = FileUtils.readLines(new File(fileName),"UTF-8");
			ArrayList<String[]> temp = new ArrayList<String[]>();
			int cols = list.get(0).split(",").length;
			for(int i = 0; i < list.size(); i++)
			{
				String str = list.get(i);
				String[] strs = str.split(",");
				String[] strscols = new String[cols];
				System.arraycopy(strs, 0, strscols, 0, strs.length);
				temp.add(strscols);
			}
			return temp.toArray(new String[0][]);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
        /*Workbook book = null;
        try
        {

        	
        	
            book = app.getWorkbooks().openWorkbook(fileName);
            Thread.sleep(3000);
            IWorksheet sheet = book.getWorksheets().getActiveWorksheet();
            int rows = sheet.getRowCount();
            int cols = sheet.getColumnCount();
            String[][] users = new String[rows][cols];
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    String s = sheet.getCellString(i + 1, j + 1);
                    //                System.out.println("obj: " + s);
                    users[i][j] = s;
                }
            }
            app.getWorkbooks().closeAll();
            return users;
        }
        catch(Exception e)
        {
            app.getWorkbooks().closeAll();
            e.printStackTrace();
            return null;
        }*/
    }

}
