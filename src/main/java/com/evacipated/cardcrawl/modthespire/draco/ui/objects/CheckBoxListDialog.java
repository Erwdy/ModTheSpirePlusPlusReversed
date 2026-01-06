package com.evacipated.cardcrawl.modthespire.draco.ui.objects;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

public class CheckBoxListDialog extends JDialog {
  private JList<CheckboxListItem> list;
  
  private DefaultListModel<CheckboxListItem> listModel;
  
  private int buttonSelected = -1;
  
  private int nextIndex = 1;
  
  public CheckBoxListDialog(Frame parent, String title, boolean modal, String[] data, String[] buttonOptions) {
    super(parent, title, modal);
    setLayout(new BorderLayout());
    setLocationRelativeTo(parent);
    this.listModel = new DefaultListModel<>();
    for (String s : data)
      this.listModel.addElement(new CheckboxListItem(s)); 
    this.list = new JList<>(this.listModel);
    this.list.setCellRenderer(new CheckboxListRenderer());
    this.list.setSelectionMode(0);
    this.list.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent event) {
            JList<CheckBoxListDialog.CheckboxListItem> list = (JList<CheckBoxListDialog.CheckboxListItem>)event.getSource();
            int index = list.locationToIndex(event.getPoint());
            if (index != -1) {
              CheckBoxListDialog.CheckboxListItem item = list.getModel().getElementAt(index);
              if (!item.isSelected()) {
                item.setSelected(true);
                item.setIndex(CheckBoxListDialog.this.nextIndex++);
              } else {
                item.setSelected(false);
                int removedIndex = item.getIndex();
                item.setIndex(0);
                for (int i = 0; i < CheckBoxListDialog.this.listModel.getSize(); i++) {
                  CheckBoxListDialog.CheckboxListItem otherItem = CheckBoxListDialog.this.listModel.getElementAt(i);
                  if (otherItem.getIndex() > removedIndex)
                    otherItem.setIndex(otherItem.getIndex() - 1); 
                } 
                CheckBoxListDialog.this.nextIndex--;
              } 
              list.repaint();
            } 
          }
        });
    add(new JScrollPane(this.list), "Center");
    JPanel buttonPanel = new JPanel();
    for (int i = 0; i < buttonOptions.length; i++) {
      JButton button = new JButton(buttonOptions[i]);
      final int finalI = i;
      button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              CheckBoxListDialog.this.buttonSelected = finalI;
              CheckBoxListDialog.this.setVisible(false);
            }
          });
      buttonPanel.add(button);
    } 
    add(buttonPanel, "South");
    pack();
  }
  
  public void setVisible(boolean b) {
    if (b)
      this.buttonSelected = -1; 
    super.setVisible(b);
  }
  
  public List<String> getSelectedValuesList() {
    List<String> selectedValues = new ArrayList<>();
    for (int i = 1; i < this.nextIndex; i++) {
      for (int j = 0; j < this.listModel.getSize(); j++) {
        CheckboxListItem item = this.listModel.getElementAt(j);
        if (item.getIndex() == i) {
          selectedValues.add(item.toString());
          break;
        } 
      } 
    } 
    return selectedValues;
  }
  
  public int getButtonSelected() {
    return this.buttonSelected;
  }
  
  private class CheckboxListRenderer extends JCheckBox implements ListCellRenderer<CheckboxListItem> {
    private CheckboxListRenderer() {}
    
    public Component getListCellRendererComponent(JList<? extends CheckBoxListDialog.CheckboxListItem> list, CheckBoxListDialog.CheckboxListItem value, int index, boolean isSelected, boolean cellHasFocus) {
      setEnabled(list.isEnabled());
      setSelected(value.isSelected());
      setFont(list.getFont());
      setBackground(list.getBackground());
      setForeground(list.getForeground());
      setText(value.toString() + ((value.getIndex() > 0) ? (" (" + value.getIndex() + ")") : ""));
      return this;
    }
  }
  
  private class CheckboxListItem {
    private String label;
    
    private boolean isSelected;
    
    private int index;
    
    public CheckboxListItem(String label) {
      this.label = label;
      this.isSelected = false;
      this.index = 0;
    }
    
    public boolean isSelected() {
      return this.isSelected;
    }
    
    public void setSelected(boolean isSelected) {
      this.isSelected = isSelected;
    }
    
    public int getIndex() {
      return this.index;
    }
    
    public void setIndex(int index) {
      this.index = index;
    }
    
    public String toString() {
      return this.label;
    }
  }
}
