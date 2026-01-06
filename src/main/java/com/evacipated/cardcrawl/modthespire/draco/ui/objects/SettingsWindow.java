package com.evacipated.cardcrawl.modthespire.draco.ui.objects;

import com.evacipated.cardcrawl.modthespire.Loader;
import com.evacipated.cardcrawl.modthespire.draco.modcompatibility.ModCompatibilityManager;
import com.evacipated.cardcrawl.modthespire.draco.ui.DUIHelp;
import com.evacipated.cardcrawl.modthespire.draco.util.MtSPPConfigManager;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListCellRenderer;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;

public class SettingsWindow extends JDialog {
  public static String settings_checkforupdatesonstartup = "mtspp_checkforupdatesonstartup";
  
  public static String settings_noloadingscreen = "mtspp_noloadingscreen";
  
  public static String settings_defaultTheme = "mtspp_defaulttheme";
  
  public static String settings_skipmegacritintro = "mtspp_skipmegacritintro";
  
  public SettingsWindow() {
    super((Frame)Loader.ex, "Settings");
    setDefaultCloseOperation(2);
    setLayout(new BorderLayout());
    String[] categories = { "General", "Game", "Mod Compatibility" };
    JList<String> categoryList = new JList<>(categories);
    categoryList.setSelectionMode(0);
    CardLayout cardLayout = new CardLayout();
    JPanel settingsPanel = new JPanel(cardLayout);
    JPanel generalPanel = makeGeneralTab();
    settingsPanel.add(generalPanel, categories[0]);
    JPanel gamePanel = makeGameTab();
    settingsPanel.add(gamePanel, categories[1]);
    JPanel modPanel = ModCompatibilityManager.MakeSettingsPanel();
    settingsPanel.add(modPanel, categories[2]);
    categoryList.addListSelectionListener(e -> {
          String selectedCategory = categoryList.getSelectedValue();
          cardLayout.show(settingsPanel, selectedCategory);
        });
    categoryList.setSelectedIndex(0);
    JSplitPane splitPane = new JSplitPane(1, new JScrollPane(categoryList), settingsPanel);
    splitPane.setDividerLocation(150);
    add(splitPane, "Center");
    setSize(600, 400);
    setLocationRelativeTo((Component)null);
  }
  
  public JPanel makeGeneralTab() {
    JPanel generalPanel = new JPanel();
    generalPanel.setLayout(new BoxLayout(generalPanel, 1));
    generalPanel.add(createCheckForUpdatesCheckbox());
    generalPanel.add(createRevertLoadingScreenCheckbox());
    generalPanel.add(createThemeComboBox());
    return generalPanel;
  }
  
  private JCheckBox createCheckForUpdatesCheckbox() {
    final JCheckBox checkForUpdatesOnStartup = new JCheckBox("Check for updates on startup");
    checkForUpdatesOnStartup.setSelected(MtSPPConfigManager.MTSPP_CONFIG.getBool(settings_checkforupdatesonstartup));
    checkForUpdatesOnStartup.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            MtSPPConfigManager.MTSPP_CONFIG.setBool(SettingsWindow.settings_checkforupdatesonstartup, checkForUpdatesOnStartup.isSelected());
            try {
              MtSPPConfigManager.MTSPP_CONFIG.save();
            } catch (Exception exception) {}
          }
        });
    checkForUpdatesOnStartup.setMaximumSize(checkForUpdatesOnStartup.getPreferredSize());
    checkForUpdatesOnStartup.setAlignmentX(0.0F);
    return checkForUpdatesOnStartup;
  }
  
  private JCheckBox createRevertLoadingScreenCheckbox() {
    final JCheckBox noLoadingScreenCheckbox = new JCheckBox("No MtS++ loading screen");
    noLoadingScreenCheckbox.setSelected(MtSPPConfigManager.MTSPP_CONFIG.getBool(settings_noloadingscreen));
    noLoadingScreenCheckbox.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            MtSPPConfigManager.MTSPP_CONFIG.setBool(SettingsWindow.settings_noloadingscreen, noLoadingScreenCheckbox.isSelected());
            try {
              MtSPPConfigManager.MTSPP_CONFIG.save();
            } catch (Exception exception) {}
          }
        });
    noLoadingScreenCheckbox.setMaximumSize(noLoadingScreenCheckbox.getPreferredSize());
    noLoadingScreenCheckbox.setAlignmentX(0.0F);
    return noLoadingScreenCheckbox;
  }
  
  private JPanel createThemeComboBox() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, 0));
    panel.add(new JLabel("Theme: "));
    final JComboBox<Class<? extends LookAndFeel>> themeBox = new JComboBox<>();
    try {
      themeBox.addItem((Class<? extends LookAndFeel>) Class.forName(UIManager.getSystemLookAndFeelClassName()));
    } catch (Exception exception) {}
    themeBox.addItem(FlatLightLaf.class);
    themeBox.addItem(FlatDarkLaf.class);
    themeBox.addItem(FlatIntelliJLaf.class);
    themeBox.addItem(FlatDarculaLaf.class);
    themeBox.addItem(FlatMacLightLaf.class);
    themeBox.addItem(FlatMacDarkLaf.class);
    try {
      themeBox.setSelectedItem(Class.forName(UIManager.getLookAndFeel().getClass().getName()));
    } catch (Exception exception) {}
    final SettingsWindow w = this;
    themeBox.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            Class<? extends LookAndFeel> theme = (Class<? extends LookAndFeel>)themeBox.getSelectedItem();
            try {
              UIManager.setLookAndFeel(theme.getName());
              SwingUtilities.updateComponentTreeUI(w);
              w.repaint();
              w.revalidate();
              DUIHelp.RepaintUI();
              MtSPPConfigManager.MTSPP_CONFIG.setString(SettingsWindow.settings_defaultTheme, theme.getName());
              try {
                MtSPPConfigManager.MTSPP_CONFIG.save();
              } catch (Exception exception) {}
            } catch (Exception exception) {}
          }
        });
    themeBox.setRenderer(new ListCellRenderer<Class<? extends LookAndFeel>>() {
          public Component getListCellRendererComponent(JList<? extends Class<? extends LookAndFeel>> list, Class<? extends LookAndFeel> value, int index, boolean isSelected, boolean cellHasFocus) {
            try {
              LookAndFeel f = value.newInstance();
              if (f instanceof com.formdev.flatlaf.FlatLaf)
                return new JLabel(f.getName()); 
              return new JLabel("Classic");
            } catch (Exception exception) {
              return new JLabel("");
            } 
          }
        });
    panel.add(themeBox);
    panel.setMaximumSize(panel.getPreferredSize());
    panel.setAlignmentX(0.0F);
    return panel;
  }
  
  public JPanel makeGameTab() {
    JPanel gamePanel = new JPanel();
    gamePanel.setLayout(new BoxLayout(gamePanel, 1));
    gamePanel.add(createSkipMegacritIntroCheckbox());
    return gamePanel;
  }
  
  private JCheckBox createSkipMegacritIntroCheckbox() {
    final JCheckBox skipMegaCritIntroCheckbox = new JCheckBox("Skip MegaCrit intro");
    skipMegaCritIntroCheckbox.setSelected(MtSPPConfigManager.MTSPP_CONFIG.getBool(settings_skipmegacritintro));
    skipMegaCritIntroCheckbox.addActionListener(new AbstractAction() {
          public void actionPerformed(ActionEvent e) {
            MtSPPConfigManager.MTSPP_CONFIG.setBool(SettingsWindow.settings_skipmegacritintro, skipMegaCritIntroCheckbox.isSelected());
            try {
              MtSPPConfigManager.MTSPP_CONFIG.save();
            } catch (Exception exception) {}
          }
        });
    skipMegaCritIntroCheckbox.setMaximumSize(skipMegaCritIntroCheckbox.getPreferredSize());
    skipMegaCritIntroCheckbox.setAlignmentX(0.0F);
    return skipMegaCritIntroCheckbox;
  }
}
