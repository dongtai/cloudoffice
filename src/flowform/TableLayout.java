package flowform;

import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.view.AbstractHtmlView;
import org.extremecomponents.table.view.DefaultStatusBar;
import org.extremecomponents.util.HtmlBuilder;

public class TableLayout extends AbstractHtmlView 
{
	protected void beforeBodyInternal(TableModel model) {
        getTableBuilder().tableStart();
        getTableBuilder().theadStart();
        getTableBuilder().filterRow();
        getTableBuilder().headerRow();
        getTableBuilder().theadEnd();
        getTableBuilder().tbodyStart();
    }
    protected void afterBodyInternal(TableModel model) {
        getCalcBuilder().defaultCalcLayout();
        getTableBuilder().tbodyEnd();
        getTableBuilder().tableEnd();
        toolbar(getHtmlBuilder(), getTableModel());
        statusBar(getHtmlBuilder(), getTableModel());
    }
    protected void toolbar(HtmlBuilder html, TableModel model) {
        new MyToolbar(html, model).layout();
    }
    protected void statusBar(HtmlBuilder html, TableModel model) {
        new DefaultStatusBar(html, model).layout();
    }
}
