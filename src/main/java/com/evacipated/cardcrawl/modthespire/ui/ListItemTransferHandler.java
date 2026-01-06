package com.evacipated.cardcrawl.modthespire.ui;

import com.evacipated.cardcrawl.modthespire.draco.mods.DModUtils;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DragSource;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

class ListItemTransferHandler extends TransferHandler {
  protected final DataFlavor localObjectFlavor;
  
  protected int[] indices;
  
  protected int addIndex = -1;
  
  protected int addCount;
  
  public ListItemTransferHandler() {
    this.localObjectFlavor = new DataFlavor(Object[].class, "Array of items");
  }
  
  protected Transferable createTransferable(JComponent c) {
    JList<?> source = (JList)c;
    c.getRootPane().getGlassPane().setVisible(true);
    this.indices = source.getSelectedIndices();
    final Object[] transferedObjects = source.getSelectedValuesList().toArray(new Object[0]);
    return new Transferable() {
        public DataFlavor[] getTransferDataFlavors() {
          return new DataFlavor[] { ListItemTransferHandler.this.localObjectFlavor };
        }
        
        public boolean isDataFlavorSupported(DataFlavor flavor) {
          return Objects.equals(ListItemTransferHandler.this.localObjectFlavor, flavor);
        }
        
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
          if (isDataFlavorSupported(flavor))
            return transferedObjects; 
          throw new UnsupportedFlavorException(flavor);
        }
      };
  }
  
  public boolean canImport(TransferHandler.TransferSupport info) {
    return (info.isDrop() && info.isDataFlavorSupported(this.localObjectFlavor));
  }
  
  public int getSourceActions(JComponent c) {
    Component glassPane = c.getRootPane().getGlassPane();
    glassPane.setCursor(DragSource.DefaultMoveDrop);
    return 2;
  }
  
  public boolean importData(TransferHandler.TransferSupport info) {
    TransferHandler.DropLocation tdl = info.getDropLocation();
    if (!canImport(info) || !(tdl instanceof JList.DropLocation))
      return false; 
    JList.DropLocation dl = (JList.DropLocation)tdl;
    JList target = (JList)info.getComponent();
    DefaultListModel<Object> listModel = (DefaultListModel)target.getModel();
    int max = listModel.getSize();
    int index = dl.getIndex();
    index = (index < 0) ? max : index;
    index = Math.min(index, max);
    this.addIndex = index;
    try {
      Object[] values = (Object[])info.getTransferable().getTransferData(this.localObjectFlavor);
      for (int i = 0; i < values.length; i++) {
        int idx = index++;
        ModPanel mod = (ModPanel)values[i];
        mod.info = DModUtils.GetModInfoForId(mod.info.ID);
        ((ModPanel)values[i]).checkBox.addItemListener(event -> ((JModPanelCheckBoxList)target).publishBoxChecked());
        listModel.add(idx, values[i]);
        target.addSelectionInterval(idx, idx);
      } 
      this.addCount = values.length;
      return true;
    } catch (UnsupportedFlavorException|IOException ex) {
      ex.printStackTrace();
      return false;
    } 
  }
  
  protected void exportDone(JComponent c, Transferable data, int action) {
    c.getRootPane().getGlassPane().setVisible(false);
    cleanup(c, (action == 2));
  }
  
  private void cleanup(JComponent c, boolean remove) {
    if (remove && Objects.nonNull(this.indices)) {
      if (this.addCount > 0)
        for (int j = 0; j < this.indices.length; j++) {
          if (this.indices[j] >= this.addIndex)
            this.indices[j] = this.indices[j] + this.addCount; 
        }  
      JList source = (JList)c;
      DefaultListModel model = (DefaultListModel)source.getModel();
      for (int i = this.indices.length - 1; i >= 0; i--)
        model.remove(this.indices[i]); 
    } 
    this.indices = null;
    this.addCount = 0;
    this.addIndex = -1;
  }
}
