package com.evacipated.cardcrawl.modthespire.draco.ui;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.ModInfo;
import com.evacipated.cardcrawl.modthespire.ModList;
import com.evacipated.cardcrawl.modthespire.draco.modlist.ShareableModList;
import com.evacipated.cardcrawl.modthespire.draco.modlist.SimpleModList;
import com.evacipated.cardcrawl.modthespire.draco.modlist.TemporaryModList;
import com.evacipated.cardcrawl.modthespire.draco.mods.DModList;
import com.evacipated.cardcrawl.modthespire.draco.mods.DModSelectWindow;
import com.evacipated.cardcrawl.modthespire.draco.mods.LimitedModInfo;
import com.evacipated.cardcrawl.modthespire.draco.serialization.ObjectSerializerDeserializer;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.CheckBoxListDialog;
import com.evacipated.cardcrawl.modthespire.draco.ui.objects.UserProfile;
import com.evacipated.cardcrawl.modthespire.draco.util.DracosUtil;
import com.evacipated.cardcrawl.modthespire.ui.JModPanelCheckBoxList;
import com.evacipated.cardcrawl.modthespire.ui.MessageConsole;
import com.evacipated.cardcrawl.modthespire.ui.ModPanel;
import com.evacipated.cardcrawl.modthespire.ui.ModSelectWindow;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

public class DUIHelp {
  private static final Icon ICON_MULTIPLAY = new ImageIcon(ModSelectWindow.class.getResource("/assets/multiplay.gif"));
  
  private static final Icon ICON_QUICKPLAY = new ImageIcon(ModSelectWindow.class.getResource("/assets/quickplay.gif"));
  
  private static final Icon ICON_EDIT = new ImageIcon(ModSelectWindow.class.getResource("/assets/edit.gif"));
  
  private static final Icon ICON_SHARE = new ImageIcon(ModSelectWindow.class.getResource("/assets/export.gif"));
  
  private static final Icon ICON_IMPORT = new ImageIcon(ModSelectWindow.class.getResource("/assets/import.gif"));
  
  public static JButton MakeEditProfileButton(ModSelectWindow window) {
    JButton editProfileButotn = new JButton(ICON_EDIT);
    editProfileButotn.setToolTipText("Edit profile");
    editProfileButotn.addActionListener(event -> {
          JComboBox<UserProfile> profileList = window.dProfilesList;
          int index = profileList.getSelectedIndex();
          UserProfile profile = (UserProfile)profileList.getSelectedItem();
          String newProfileName = JOptionPane.showInputDialog((Component)window, "\nEnter a new profile name:", profile.getDisplayName());
          if (newProfileName == null || newProfileName.isEmpty()) {
            JOptionPane.showMessageDialog((Component)window, "Profile name was not valid!", "Failure!", -1);
            return;
          } 
          DModList.rename(profile.getId(), newProfileName);
          profileList.removeItem(new UserProfile(profile.getId()));
          profileList.insertItemAt(new UserProfile(newProfileName), index);
          profileList.setSelectedIndex(index);
        });
    return editProfileButotn;
  }
  
  public static JButton MakeExportModListButton(ModSelectWindow windowInstance) {
    JButton exportModlistButton = new JButton(ICON_SHARE);
    exportModlistButton.setToolTipText("Export modlist");
    exportModlistButton.addActionListener(event -> {
          JModPanelCheckBoxList modList = windowInstance.modList;
          File[] files = modList.getCheckedMods();
          if (files.length == 0) {
            JOptionPane.showMessageDialog((Component)windowInstance, "No mods were selected.", "Failure", -1);
            return;
          } 
          ModInfo[] oldInfos = DracosUtil.FileListToModInfo(files);
          ModInfo[] savedInfos = windowInstance.info;
          ModInfo[] infos = new ModInfo[oldInfos.length];
          for (int i = 0; i < oldInfos.length; i++) {
            for (ModInfo info : savedInfos) {
              if (info.ID.equals((oldInfos[i]).ID))
                infos[i] = info; 
            } 
          } 
          LimitedModInfo[] limitedModInfos = LimitedModInfo.ArrayFromModInfos(infos);
          SimpleModList m = new SimpleModList();
          m.SetMods(limitedModInfos);
          UserProfile currentProfile = (UserProfile)windowInstance.dProfilesList.getSelectedItem();
          m.SetNameAndId(currentProfile.getDisplayName(), currentProfile.getId());
          String key = ObjectSerializerDeserializer.SerializeAndCompress((Serializable)m);
          if (key == null) {
            JOptionPane.showMessageDialog((Component)windowInstance, "Failed to generate a modlist key.", "Failure", -1);
            return;
          } 
          DracosUtil.CopyToClipboard(key);
          String[] options = { "Save to file", "Close" };
          int chosen = OpenSimpleButtonDialog((Component)windowInstance, "Success!", "Modlist key has been copied to clipboard!", (Object[])options, options[1]);
          if (chosen == 0)
            DracosUtil.SaveStringToFile(key); 
        });
    return exportModlistButton;
  }
  
  public static JButton MakeImportModListButton(ModSelectWindow windowInstance) {
    JButton exportModlistButton = new JButton(ICON_IMPORT);
    exportModlistButton.setToolTipText("Import modlist");
    exportModlistButton.addActionListener(event -> {
          Object[] result = OpenInputDialog(false, "Import");
          String modlistKey = (String)result[0];
          boolean asNewProfile = ((Boolean)result[1]).booleanValue();
          if (modlistKey == null)
            return; 
          if (modlistKey.isEmpty()) {
            JOptionPane.showMessageDialog((Component)windowInstance, "Mod key was not valid!", "Failure!", -1);
            return;
          } 
          modlistKey = modlistKey.trim();
          ShareableModList m = (ShareableModList)ObjectSerializerDeserializer.DecompressAndDeserialize(modlistKey);
          if (m == null) {
            JOptionPane.showMessageDialog((Component)windowInstance, "Could not decrypt mod key!", "Failure!", -1);
            return;
          } 
          String newProfileId = m.GetId();
          if (newProfileId.isEmpty())
            newProfileId = "UNDEFINED"; 
          String newProfileDisplayName = m.GetDisplayName();
          if (newProfileDisplayName.isEmpty())
            newProfileDisplayName = "UNDEFINED"; 
          if (asNewProfile) {
            JComboBox<UserProfile> profileList = windowInstance.dProfilesList;
            for (int i = 0; i < profileList.getItemCount(); i++) {
              UserProfile p = profileList.getItemAt(i);
              if (p.getId().equals(m.GetId()))
                newProfileId = newProfileId + UUID.randomUUID().toString(); 
            } 
            profileList.addItem(new UserProfile(newProfileDisplayName, newProfileId));
            profileList.setSelectedIndex(profileList.getItemCount() - 1);
          } 
          if (m instanceof SimpleModList)
            DModSelectWindow.ImportModList(((SimpleModList)m).GetMods()); 
        });
    return exportModlistButton;
  }
  
  public static JButton MakeMultiPlayButton(ModSelectWindow window) {
    JButton multiplayProfileButton = new JButton(ICON_MULTIPLAY);
    multiplayProfileButton.setToolTipText("Multiplay");
    multiplayProfileButton.addActionListener(event -> {
          String[] profileList = DModSelectWindow.GetProfileListAsArray();
          String[] buttonOptions = { "Play", "Export", "Cancel" };
          CheckBoxListDialog checkBoxListDialog = new CheckBoxListDialog((Frame)window, "Select modlists to play with", true, profileList, buttonOptions);
          int buttonSelected = 1;
          ArrayList<String> totalMods = new ArrayList<>();
          while (buttonSelected == 1) {
            checkBoxListDialog.setVisible(true);
            buttonSelected = checkBoxListDialog.getButtonSelected();
            totalMods = GetMultiplayAllMods(checkBoxListDialog);
            if (buttonSelected == 1) {
              TemporaryModList temporaryModList1 = new TemporaryModList(totalMods);
              ArrayList<ModInfo> modInfos = DModList.ToModInfo((ModList)temporaryModList1, Loader.ALLMODINFOS);
              ModInfo[] modInfosArray = new ModInfo[modInfos.size()];
              for (int j = 0; j < modInfos.size(); j++) {
                ModInfo i = modInfos.get(j);
                modInfosArray[j] = i;
              } 
              OpenMultiplayExportWindow((JDialog)checkBoxListDialog, modInfosArray);
            } 
          } 
          if (buttonSelected != 0)
            return; 
          DModSelectWindow.SaveCurrentModlist();
          totalMods = GetMultiplayAllMods(checkBoxListDialog);
          TemporaryModList temporaryModList = new TemporaryModList(totalMods);
          DefaultListModel<ModPanel> newModel = (DefaultListModel<ModPanel>)window.modList.getModel();
          temporaryModList.loadModsInOrder(newModel, Loader.ALLMODINFOS, window.modList);
          DModSelectWindow.Play();
        });
    return multiplayProfileButton;
  }
  
  private static ArrayList<String> GetMultiplayAllMods(CheckBoxListDialog multiplayBox) {
    ArrayList<String> totalMods = new ArrayList<>();
    for (String profile : multiplayBox.getSelectedValuesList()) {
      ModList newList = new ModList(profile);
      for (String mod : newList.mods) {
        if (!totalMods.contains(mod))
          totalMods.add(mod); 
      } 
    } 
    return totalMods;
  }
  
  private static void OpenMultiplayExportWindow(JDialog parent, ModInfo[] modsToExport) {
    if (modsToExport.length == 0) {
      JOptionPane.showMessageDialog(parent, "No mods were selected.", "Failure", -1);
      return;
    } 
    LimitedModInfo[] limitedModInfos = LimitedModInfo.ArrayFromModInfos(modsToExport);
    SimpleModList m = new SimpleModList();
    m.SetMods(limitedModInfos);
    UserProfile currentProfile = new UserProfile("Multiplay_" + UUID.randomUUID().toString());
    m.SetNameAndId(currentProfile.getDisplayName(), currentProfile.getId());
    String key = ObjectSerializerDeserializer.SerializeAndCompress((Serializable)m);
    if (key == null) {
      JOptionPane.showMessageDialog(parent, "Failed to generate a modlist key.", "Failure", -1);
      return;
    } 
    DracosUtil.CopyToClipboard(key);
    String[] options = { "Save to file", "Close" };
    int chosen = OpenSimpleButtonDialog(parent, "Success!", "Modlist key has been copied to clipboard!", (Object[])options, options[1]);
    if (chosen == 0)
      DracosUtil.SaveStringToFile(key); 
  }
  
  public static JButton MakeQuickPlayButton(ModSelectWindow window) {
    JButton quickplayProfileButton = new JButton(ICON_QUICKPLAY);
    quickplayProfileButton.setToolTipText("Import and play");
    quickplayProfileButton.addActionListener(event -> {
          Object[] result = OpenInputDialog(true, "Play");
          String modlistKey = (String)result[0];
          if (modlistKey == null)
            return; 
          if (modlistKey.isEmpty()) {
            JOptionPane.showMessageDialog((Component)window, "Mod key was not valid!", "Failure!", -1);
            return;
          } 
          modlistKey = modlistKey.trim();
          ShareableModList m = (ShareableModList)ObjectSerializerDeserializer.DecompressAndDeserialize(modlistKey);
          if (m == null) {
            JOptionPane.showMessageDialog((Component)window, "Could not decrypt mod key!", "Failure!", -1);
            return;
          } 
          if (m instanceof SimpleModList) {
            TemporaryModList temporaryModList = new TemporaryModList(new ArrayList());
            DefaultListModel<ModPanel> newModel = (DefaultListModel<ModPanel>)window.modList.getModel();
            temporaryModList.loadModsInOrder(newModel, Loader.ALLMODINFOS, window.modList);
            DModSelectWindow.ImportModList(((SimpleModList)m).GetMods());
            DModSelectWindow.Play();
          } 
        });
    return quickplayProfileButton;
  }
  
  public static int OpenSimpleButtonDialog(Component parent, String title, String body, Object[] options, Object defaultOption) {
    int result = JOptionPane.showOptionDialog(parent, body, title, 0, 3, null, options, defaultOption);
    return result;
  }
  
  public static JDialog CreateModalTextPopup(JFrame parent, String title, String text) {
    JDialog dialog = new JDialog(parent, title, true);
    dialog.setDefaultCloseOperation(0);
    dialog.setLayout(new BorderLayout());
    dialog.setPreferredSize(new Dimension(500, 75));
    JLabel label = new JLabel(text);
    label.setHorizontalAlignment(0);
    dialog.add(label, "North");
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    return dialog;
  }
  
  public static Object[] OpenInputDialog(boolean temporaryInput, String titleAndResult) {
    final Object[] result = new Object[2];
    final JDialog dialog = new JDialog((Frame)Loader.ex);
    dialog.setTitle(titleAndResult);
    dialog.setModal(true);
    dialog.setLayout(new BorderLayout());
    dialog.setLocationRelativeTo((Component)Loader.ex);
    dialog.setPreferredSize(new Dimension(500, 300));
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    dialog.add(mainPanel, "Center");
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new GridBagLayout());
    mainPanel.add(contentPanel, "Center");
    GridBagConstraints c = new GridBagConstraints();
    c.fill = 2;
    c.insets = new Insets(5, 5, 5, 5);
    JLabel label1 = new JLabel("Enter modlist key:");
    c.gridx = 0;
    c.gridy = 0;
    contentPanel.add(label1, c);
    final JTextArea inputBox = new JTextArea();
    inputBox.setLineWrap(true);
    inputBox.setWrapStyleWord(true);
    JScrollPane scrollPane = new JScrollPane(inputBox);
    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 1.0D;
    c.weighty = 1.0D;
    c.fill = 1;
    contentPanel.add(scrollPane, c);
    JLabel label2 = new JLabel("OR");
    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0.0D;
    c.weighty = 0.0D;
    contentPanel.add(label2, c);
    JButton button1 = new JButton("Import from file");
    c.gridx = 0;
    c.gridy = 3;
    c.fill = 2;
    contentPanel.add(button1, c);
    if (!temporaryInput) {
      JCheckBox newProfileBox = new JCheckBox("Create as new profile", !temporaryInput);
      c.gridx = 0;
      c.gridy = 4;
      contentPanel.add(newProfileBox, c);
    } else {
      JLabel tempLabel = new JLabel("This modlist will not be saved");
      c.gridx = 0;
      c.gridy = 4;
      contentPanel.add(tempLabel, c);
    } 
    JButton button2 = new JButton(titleAndResult);
    JPanel bottomPanel = new JPanel();
    bottomPanel.setLayout(new FlowLayout(1));
    bottomPanel.add(button2);
    mainPanel.add(bottomPanel, "South");
    button1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            result[0] = DracosUtil.LoadStringFromFile();
            dialog.dispose();
          }
        });
    button2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            result[0] = inputBox.getText();
            dialog.dispose();
          }
        });
    dialog.pack();
    dialog.setVisible(true);
    result[1] = Boolean.valueOf(temporaryInput ? false : ((JCheckBox)contentPanel.getComponent(4)).isSelected());
    return result;
  }
  
  public static JScrollPane makeLogWindow() {
    JTextPane textPane = new JTextPane();
    textPane.setFont(new Font("monospaced", 0, 12));
    JScrollPane logScroller = new JScrollPane(textPane);
    MessageConsole mc = new MessageConsole(textPane);
    mc.redirectOut(null, System.out);
    mc.redirectErr(Color.RED, System.err);
    return logScroller;
  }
  
  public static Font loadFont(String path, float size) {
    try {
      InputStream is = ModSelectWindow.class.getResourceAsStream(path);
      Font font = Font.createFont(0, is);
      return font.deriveFont(0, size);
    } catch (Exception ignored) {
      ignored.printStackTrace();
      return null;
    } 
  }
  
  public static void RepaintUI() {
    SwingUtilities.updateComponentTreeUI((Component)Loader.ex);
    Loader.ex.repaint();
    Loader.ex.revalidate();
    Component modlistComponent = Loader.ex.modList.getComponent(0);
    SwingUtilities.updateComponentTreeUI(modlistComponent);
    modlistComponent.repaint();
    modlistComponent.revalidate();
    Loader.ex.modList.publishBoxChecked();
  }
}
