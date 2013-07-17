/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 *
 * @author Thimo
 */
/**
 * Handles drag & drop row reordering
 */
public class TableRowTransferHandler extends TransferHandler {
   private final DataFlavor localObjectFlavor = new DataFlavor(Integer.class, "Integer Row Index");
   //private final DataFlavor localObjectFlavor = new ActivationDataFlavor(Integer.class, DataFlavor.javaJVMLocalObjectMimeType, "Integer Row Index");
   private JTable table;

   public TableRowTransferHandler(JTable table) {
      this.table = table;
   }

   @Override
   protected Transferable createTransferable(JComponent c) {
      assert (c == table);
      return new DataHandler(new Integer(table.getSelectedRow()), localObjectFlavor.getMimeType());
   }

   @Override
   public boolean canImport(TransferHandler.TransferSupport info) {
      boolean b = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
      table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
      return b;
   }

   @Override
   public int getSourceActions(JComponent c) {
      return TransferHandler.COPY_OR_MOVE;
   }

   @Override
   public boolean importData(TransferHandler.TransferSupport info) {
      JTable target = (JTable) info.getComponent();
      JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
      int drop = dl.getRow() - 1;
      int index = table.getSelectedRow();
      int count = table.getSelectedRowCount();
      int max = table.getModel().getRowCount() - 1;
      if (index < 0 || index > max)
         index = max;
      target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      CSVTableModel model = null;
      try {
         if (index != -1 && index != drop) {
            // @TODO Reorder table
            if (index < drop) {
                drop = drop - count + 1;
            }
            model = (CSVTableModel)table.getModel();
            model.moveRow(index, index + count -1, drop);

            target.getSelectionModel().setSelectionInterval(drop, drop + count - 1);

            return true;
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
      return false;
   }

   @Override
   protected void exportDone(JComponent c, Transferable t, int act) {
      if (act == TransferHandler.MOVE) {
         table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
   }

}