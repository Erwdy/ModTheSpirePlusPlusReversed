package com.evacipated.cardcrawl.modthespire.ui;

import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.draco.modlist.ModListProperties;
import com.evacipated.cardcrawl.modthespire.draco.mods.DJModPanelCheckBoxList;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class JModPanelCheckBoxList extends JList<ModPanel> {
  protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
  
  private ModSelectWindow parent;
  
  public ModListProperties properties;
  
  public JModPanelCheckBoxList(final ModSelectWindow parent) {
    this.parent = parent;
    setDragEnabled(true);
    setDropMode(DropMode.INSERT);
    setSelectionMode(0);
    setTransferHandler(new ListItemTransferHandler());
    setCellRenderer(new CellRenderer());
    addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            int index = JModPanelCheckBoxList.this.locationToIndex(e.getPoint());
            if (index != -1) {
              ModPanel modPanel = JModPanelCheckBoxList.this.getModel().getElementAt(index);
              parent.setModInfo(modPanel.info);
              if (e.getX() <= modPanel.checkBox.getWidth() && 
                modPanel.checkBox.isEnabled()) {
                modPanel.checkBox.setSelected(!modPanel.checkBox.isSelected());
                JModPanelCheckBoxList.this.repaint();
              } 
            } 
          }
        });
    publishBoxChecked();
    DJModPanelCheckBoxList.BuildContextMenu(this);
  }
  
  public void publishBoxChecked() {
    for (int i = 0; i < getModel().getSize(); i++)
      ((ModPanel)getModel().getElementAt(i)).recalcModWarnings(this); 
  }
  
  public JModPanelCheckBoxList(ModSelectWindow parent, DefaultListModel<ModPanel> model) {
    this(parent);
    setModel(model);
  }
  
  public File[] getAllMods() {
    File[] ret = new File[getModel().getSize()];
    for (int i = 0; i < getModel().getSize(); i++)
      ret[i] = ((ModPanel)getModel().getElementAt(i)).modFile; 
    return ret;
  }
  
  public File[] getCheckedMods() {
    int size = 0;
    for (int i = 0; i < getModel().getSize(); i++) {
      if (((ModPanel)getModel().getElementAt(i)).isSelected())
        size++; 
    } 
    File[] ret = new File[size];
    int j = 0;
    for (int k = 0; k < getModel().getSize(); k++) {
      if (((ModPanel)getModel().getElementAt(k)).isSelected()) {
        ret[j] = ((ModPanel)getModel().getElementAt(k)).modFile;
        j++;
      } 
    } 
    return ret;
  }
  
  public void toggleAllMods() {
    int on = 0;
    int visibleMods = 0;
    int i;
    for (i = 0; i < getModel().getSize(); i++) {
      ModPanel modPanel = getModel().getElementAt(i);
      if (!modPanel.isFilteredOut()) {
        visibleMods++;
        if (modPanel.isSelected())
          on++; 
      } 
    } 
    if (on > visibleMods / 2) {
      for (i = 0; i < getModel().getSize(); i++) {
        ModPanel modPanel = getModel().getElementAt(i);
        if (!modPanel.isFilteredOut())
          modPanel.setSelected(false); 
      } 
    } else {
      for (i = 0; i < getModel().getSize(); i++) {
        ModPanel modPanel = getModel().getElementAt(i);
        if (!modPanel.isFilteredOut())
          modPanel.setSelected(true); 
      } 
    } 
    publishBoxChecked();
  }
  
  public synchronized void setUpdateIcon(ModInfo info, ModSelectWindow.UpdateIconType type) {
    for (int i = 0; i < getModel().getSize(); i++) {
      if (info.equals(((ModPanel)getModel().getElementAt(i)).info)) {
        ((ModPanel)getModel().getElementAt(i)).setUpdateIcon(type);
        break;
      } 
    } 
    repaint();
  }
  
  protected class CellRenderer implements ListCellRenderer<ModPanel> {
    private final JPanel hiddenItem = new JPanel();
    
    public CellRenderer() {
      this.hiddenItem.setPreferredSize(new Dimension(0, 0));
    }
    
    public Component getListCellRendererComponent(JList<? extends ModPanel> list, ModPanel value, int index, boolean isSelected, boolean cellHasFocus) {
      JCheckBox checkbox = value.checkBox;
      value.setBackground(isSelected ? JModPanelCheckBoxList.this.getSelectionBackground() : JModPanelCheckBoxList.this.getBackground());
      value.setForeground(isSelected ? JModPanelCheckBoxList.this.getSelectionForeground() : JModPanelCheckBoxList.this.getForeground());
      checkbox.setFont(JModPanelCheckBoxList.this.getFont());
      checkbox.setFocusPainted(false);
      checkbox.setBorderPainted(false);
      if (value.isFilteredOut())
        return this.hiddenItem; 
      return value;
    }
  }
}
