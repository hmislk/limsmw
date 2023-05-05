package org.openhealth.limsmw;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
public class AnalyzerListCellRenderer extends DefaultListCellRenderer  {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof Analyzer) {
            Analyzer analyzer = (Analyzer) value;
            return super.getListCellRendererComponent(list, analyzer.getName(), index, isSelected, cellHasFocus);
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }
}
