package com.bn;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class FolderManagerGUI {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String SAVE_FILE = "folders.txt";

    public FolderManagerGUI() {
        frame = new JFrame("Folder Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        String[] columnNames = {"Folder Path", "Open", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
//        table.getColumn("Open").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Open").setCellEditor(new ButtonEditor(new JCheckBox(), "Open"));
//        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));
        table.getColumnModel().getColumn(1).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(1).setCellEditor(new ButtonEditor(new JCheckBox(), "Open"));
        table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));


        JScrollPane scrollPane = new JScrollPane(table);

        JButton addButton = new JButton("Add Folder");
        JButton saveButton = new JButton("Save List");
        JButton loadButton = new JButton("Load List");

        addButton.addActionListener(e -> addFolder());
        saveButton.addActionListener(e -> saveFolderList());
        loadButton.addActionListener(e -> loadFolderList());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void addFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            tableModel.addRow(new Object[]{selectedFolder.getAbsolutePath(), "Open", "Delete"});
        }
    }

    private void saveFolderList() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.write(tableModel.getValueAt(i, 0).toString());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(frame, "Folder list saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFolderList() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;
            tableModel.setRowCount(0);
            while ((line = reader.readLine()) != null) {
                tableModel.addRow(new Object[]{line, "Open", "Delete"});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        /**
		 * 
		 */
		private static final long serialVersionUID = -3705585413441861948L;

		public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value != null) ? value.toString() : "");
            return this;
        }
    }


    class ButtonEditor extends DefaultCellEditor {
        /**
		 * 
		 */
		private static final long serialVersionUID = -7149504244351907909L;
		private JButton button;
        private String action;
        private int row;

        public ButtonEditor(JCheckBox checkBox, String action) {
            super(checkBox);
            this.action = action;
            button = new JButton(action);
            button.setOpaque(true);
            button.addActionListener(e -> performAction());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            button.setText(action);
            return button;
        }

        private void performAction() {
            String folderPath = tableModel.getValueAt(row, 0).toString();
            if (action.equals("Open")) {
                openFolder(folderPath);
            } else if (action.equals("Delete")) {
                deleteFolder(folderPath);
            }
        }
    }

    private void openFolder(String folderPath) {
        try {
            Desktop.getDesktop().open(new File(folderPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFolder(String folderPath) {
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this folder?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Files.walk(Paths.get(folderPath))
                        .map(Path::toFile)
                        .sorted((o1, o2) -> -o1.compareTo(o2))
                        .forEach(File::delete);
//                tableModel.removeRow(table.getSelectedRow()); Dont remove the row in the table
                JOptionPane.showMessageDialog(frame, "Folder deleted successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FolderManagerGUI::new);
    }
}
