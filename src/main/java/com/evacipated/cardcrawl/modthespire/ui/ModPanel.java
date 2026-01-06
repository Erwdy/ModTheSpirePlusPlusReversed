package com.evacipated.cardcrawl.modthespire.ui;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.draco.mods.DModPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class ModPanel extends JPanel {
  private static final Color lightRed = new Color(229, 115, 115);
  
  private static final Color lightOrange = new Color(255, 159, 0);
  
  private static final Color lightYellow = new Color(255, 238, 88);
  
  public ModInfo info;
  
  public File modFile;
  
  public JCheckBox checkBox;
  
  public InfoPanel infoPanel;
  
  private JLabel update = new JLabel();
  
  private boolean isFilteredOut = false;
  
  public boolean required = false;
  
  private static boolean dependenciesChecked(ModInfo info, JModPanelCheckBoxList parent) {
    String[] dependencies = info.Dependencies;
    boolean[] checked = new boolean[dependencies.length];
    for (int i = 0; i < parent.getModel().getSize(); i++) {
      ModPanel panel = parent.getModel().getElementAt(i);
      for (int k = 0; k < dependencies.length; k++) {
        if (panel.info != null && panel.info.ID != null && panel.info.ID.equals(dependencies[k]) && panel.checkBox.isSelected())
          checked[k] = true; 
      } 
    } 
    boolean allChecked = true;
    for (int j = 0; j < checked.length; j++) {
      if (!checked[j])
        allChecked = false; 
    } 
    return allChecked;
  }
  
  private static String[] missingDependencies(ModInfo info, JModPanelCheckBoxList parent) {
    String[] dependencies = info.Dependencies;
    boolean[] checked = new boolean[dependencies.length];
    for (int i = 0; i < parent.getModel().getSize(); i++) {
      ModPanel panel = parent.getModel().getElementAt(i);
      for (int k = 0; k < dependencies.length; k++) {
        if (panel.info != null && panel.info.ID != null && panel.info.ID.equals(dependencies[k]) && panel.checkBox.isSelected())
          checked[k] = true; 
      } 
    } 
    List<String> missing = new ArrayList<>();
    for (int j = 0; j < checked.length; j++) {
      if (!checked[j])
        missing.add(dependencies[j]); 
    } 
    String[] returnType = new String[missing.size()];
    return missing.<String>toArray(returnType);
  }
  
  public ModPanel(ModInfo info, File modFile, JModPanelCheckBoxList parent) {
    this.info = info;
    this.modFile = modFile;
    this.checkBox = new JCheckBox();
    setLayout(new BorderLayout());
    this.infoPanel = new InfoPanel();
    add(this.checkBox, "West");
    add(this.infoPanel, "Center");
    this.update.setHorizontalAlignment(0);
    this.update.setVerticalAlignment(0);
    this.update.setOpaque(true);
    this.update.setBorder(new EmptyBorder(0, 0, 0, 4));
    if (info.isWorkshop) {
      setUpdateIcon(ModSelectWindow.UpdateIconType.WORKSHOP);
    } else if (info.UpdateJSON != null && !info.UpdateJSON.isEmpty()) {
      setUpdateIcon(ModSelectWindow.UpdateIconType.CAN_CHECK);
    } else {
      setUpdateIcon(ModSelectWindow.UpdateIconType.NONE);
    } 
    add(this.update, "East");
    setBorder(new MatteBorder(0, 0, 1, 0, Color.darkGray));
    this.checkBox.addItemListener(event -> parent.publishBoxChecked());
    parent.publishBoxChecked();
  }
  
  public void recalcModWarnings(JModPanelCheckBoxList parent) {
    this.info.statusMsg = " ";
    DModPanel.preRecalcModWarning(this);
    if (this.info.MTS_Version == null) {
      this.checkBox.setEnabled(false);
      this.checkBox.setBackground(lightRed);
      this.infoPanel.setBackground(lightRed);
      this.info.statusMsg = "This mod is missing a valid ModTheSpire version number.";
      return;
    } 
    if (this.info.MTS_Version.compareTo(Loader.MTS_VERSION) > 0) {
      this.checkBox.setEnabled(false);
      this.checkBox.setBackground(lightRed);
      this.infoPanel.setBackground(lightRed);
      this.info.statusMsg = "This mod requires ModTheSpire v" + this.info.MTS_Version + " or higher.";
      return;
    } 
    if (this.checkBox.isSelected() && !dependenciesChecked(this.info, parent)) {
      this.checkBox.setBackground(lightOrange);
      this.infoPanel.setBackground(lightOrange);
      String[] missingDependencies = missingDependencies(this.info, parent);
      StringBuilder tooltip = new StringBuilder();
      tooltip.append("Missing dependencies: [");
      tooltip.append(String.join(", ", (CharSequence[])missingDependencies));
      tooltip.append("]");
      this.info.statusMsg = tooltip.toString();
    } 
    if (Loader.STS_VERSION != null && this.info.STS_Version != null && !Loader.STS_VERSION.equals(this.info.STS_Version))
      if (this.info.statusMsg == " ")
        this.info.statusMsg = "This mod explicitly supports StS " + this.info.STS_Version + ".\nYou are running StS " + Loader.STS_VERSION + ".\nYou may encounter problems running it.";  
  }
  
  public boolean isSelected() {
    return (this.checkBox.isEnabled() && this.checkBox.isSelected());
  }
  
  public void setSelected(boolean b) {
    if (this.checkBox.isEnabled())
      this.checkBox.setSelected(b); 
  }
  
  public synchronized void setUpdateIcon(ModSelectWindow.UpdateIconType type) {
    switch (type) {
      case NONE:
        this.update.setIcon((Icon)null);
        break;
      case CAN_CHECK:
        this.update.setIcon(ModSelectWindow.ICON_UPDATE);
        break;
      case CHECKING:
        this.update.setIcon(ModSelectWindow.ICON_LOAD);
        break;
      case UPDATE_AVAILABLE:
        this.update.setIcon(ModSelectWindow.ICON_WARNING);
        break;
      case UPTODATE:
        this.update.setIcon(ModSelectWindow.ICON_GOOD);
        break;
      case WORKSHOP:
        this.update.setIcon(ModSelectWindow.ICON_WORKSHOP);
        break;
    } 
  }
  
  public void filter(String[] filterKeys) {
    if (filterKeys == null) {
      this.isFilteredOut = false;
      return;
    } 
    String workshopInfoKey = "";
    try {
      workshopInfoKey = DModPanel.filter_inner(this);
    } catch (Exception ex) {
      System.out.println("ModPanel.filter failed to get workshop info of " + this.info.ID + ": " + ex);
    } 
    String modInfoKey = String.format("%s %s %s %s", new Object[] { this.info.ID, this.info.Name, String.join(" ", (CharSequence[])this.info.Authors), workshopInfoKey }).toLowerCase();
    boolean isFilteredOut = false;
    for (String filterKey : filterKeys) {
      if (!modInfoKey.contains(filterKey)) {
        isFilteredOut = true;
        break;
      } 
    } 
    this.isFilteredOut = isFilteredOut;
  }
  
  public boolean isFilteredOut() {
    return this.isFilteredOut;
  }
  
  public class InfoPanel extends JPanel {
    JLabel name = new JLabel();
    
    JLabel version = new JLabel();
    
    public InfoPanel() {
      setLayout(new BorderLayout());
      this.name.setOpaque(true);
      this.name.setText(ModPanel.this.info.Name);
      this.name.setFont(this.name.getFont().deriveFont(13.0F).deriveFont(1));
      add(this.name, "Center");
      this.version.setOpaque(true);
      this.version.setFont(this.version.getFont().deriveFont(10.0F).deriveFont(0));
      if (ModPanel.this.info.ModVersion != null) {
        this.version.setText(ModPanel.this.info.ModVersion.toString());
      } else {
        this.version.setText("missing version");
      } 
      add(this.version, "South");
      ModPanel.this.checkBox.setBackground(Color.WHITE);
      setBackground(Color.WHITE);
    }
    
    public void setBackground(Color c) {
      super.setBackground(c);
      if (this.name != null)
        this.name.setBackground(c); 
      if (this.version != null)
        this.version.setBackground(c); 
      if (ModPanel.this.update != null)
        ModPanel.this.update.setBackground(c); 
    }
  }
}
