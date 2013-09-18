package templates.action;

import org.apache.commons.lang.StringUtils;
import org.extremecomponents.table.bean.Row;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.interceptor.RowInterceptor;




public class SexIntercept implements RowInterceptor {
    public void addRowAttributes(TableModel tableModel, Row row) {
    }

    public void modifyRowAttributes(TableModel model, Row row) {
//        User user = (User) model.getCurrentRowBean();
//        String sex =user.getSex();
//        if (StringUtils.contains(sex, "Ů")) {
//            row.setStyle("background-color:#fdffc0;");
//        } else {
//            row.setStyle("");
//        }
    }
}