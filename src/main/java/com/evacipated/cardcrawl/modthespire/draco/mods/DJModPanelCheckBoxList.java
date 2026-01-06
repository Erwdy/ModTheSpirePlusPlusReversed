package com.evacipated.cardcrawl.modthespire.draco.mods;

import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.ui.JModPanelCheckBoxList;
import com.evacipated.cardcrawl.modthespire.ui.ModPanel;
import java.awt.Component;
import javax.swing.DefaultListModel;

public class DJModPanelCheckBoxList {
  public static void BuildContextMenu(JModPanelCheckBoxList list) {}
  
  public static void selectAllMods(JModPanelCheckBoxList list) {
    for (int i = 0; i < list.getModel().getSize(); i++) {
      ModPanel modPanel = list.getModel().getElementAt(i);
      modPanel.setSelected(true);
    } 
    list.publishBoxChecked();
  }
  
  public static void deselectAllMods(JModPanelCheckBoxList list) {
    for (int i = 0; i < list.getModel().getSize(); i++) {
      ModPanel modPanel = list.getModel().getElementAt(i);
      if (!DModPanel.getRequired(modPanel))
        modPanel.setSelected(false); 
    } 
    list.publishBoxChecked();
  }
  
  public static void removeMod(JModPanelCheckBoxList list, ModInfo info) {
    for (int i = 0; i < list.getModel().getSize(); i++) {
      ModPanel modPanel = list.getModel().getElementAt(i);
      if (modPanel.info.ID.equals(info.ID)) {
        list.remove((Component)modPanel);
        i--;
      } 
    } 
    list.publishBoxChecked();
  }
  
  public static boolean selectMod(JModPanelCheckBoxList list, String modId) {
    for (int i = 0; i < list.getModel().getSize(); i++) {
      ModPanel modPanel = list.getModel().getElementAt(i);
      if (modPanel.info.ID.equals(modId)) {
        modPanel.setSelected(true);
        list.publishBoxChecked();
        return true;
      } 
    } 
    return false;
  }
  
  public static boolean hasMod(JModPanelCheckBoxList list, String modId) {
    for (int i = 0; i < list.getModel().getSize(); i++) {
      ModPanel modPanel = list.getModel().getElementAt(i);
      if (modPanel.info.ID.equals(modId))
        return true; 
    } 
    return false;
  }
  
  public static void moveModToSlot(JModPanelCheckBoxList list, String modId, int slot) {
    for (int i = 0; i < list.getModel().getSize(); i++) {
      ModPanel modPanel = list.getModel().getElementAt(i);
      if (modPanel.info.ID.equals(modId)) {
        DefaultListModel<ModPanel> model = (DefaultListModel<ModPanel>)list.getModel();
        model.removeElement(modPanel);
        model.add(slot, modPanel);
      } 
    } 
  }
}
