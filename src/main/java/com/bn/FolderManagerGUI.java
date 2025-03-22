package com.bn;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.table.TableColumnModel;

public class FolderManagerGUI {
    private static final String FOLDER_PATH = "Folder Path";
	private static final String OPEN_IN_TERMINAL = "Open in Terminal";
	private static final String DELETE = "Delete";
	public static final String OPEN = "Open";
	private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private static final String SAVE_FILE = "folders.txt";

    public FolderManagerGUI() {
        frame = new JFrame("Folder Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

//        String[] columnNames = {"Folder Path", "Open", "Delete"};
        String[] columnNames = {FOLDER_PATH, OPEN, DELETE, OPEN_IN_TERMINAL};

        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
//        table.getColumn("Open").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Open").setCellEditor(new ButtonEditor(new JCheckBox(), "Open"));
//        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));
        table.getColumnModel().getColumn(1).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(1).setCellEditor(new ButtonEditor(new JCheckBox(), OPEN));
        table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), DELETE));
        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), OPEN_IN_TERMINAL));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // Ensure the click is on the "Folder Path" column (first column)
                if (row != -1 && col == 0) {
                    String path = (String) table.getValueAt(row, col);
                    StringSelection selection = new StringSelection(path);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, null);
                    JOptionPane.showMessageDialog(null, "Path copied to clipboard: " + path);
                }
            }
        });


        setColumnSize();

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

    private void setColumnSize() {
    	// Get the table column model
    	TableColumnModel columnModel = table.getColumnModel();

    	// Set fixed widths for button columns
    	columnModel.getColumn(1).setMinWidth(80);  // Open Button
    	columnModel.getColumn(1).setMaxWidth(80);

    	columnModel.getColumn(2).setMinWidth(80);  // Delete Button
    	columnModel.getColumn(2).setMaxWidth(80);

    	columnModel.getColumn(3).setMinWidth(120); // Open in Terminal Button
    	columnModel.getColumn(3).setMaxWidth(120);

    	// Let the Folder Path column take up the remaining space
    	columnModel.getColumn(0).setResizable(true);


	}

	private void addFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            tableModel.addRow(new Object[]{selectedFolder.getAbsolutePath(), OPEN, DELETE,OPEN_IN_TERMINAL});
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
                tableModel.addRow(new Object[]{line, OPEN, DELETE, OPEN_IN_TERMINAL});
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
        private static final long serialVersionUID = 1L;
		private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private int row;

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            button = new JButton(label);
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                String folderPath = (String) table.getValueAt(row, 0);
                if (label.equals(OPEN)) {
                    openFolder(folderPath);
                } else if (label.equals(DELETE)) {
                    deleteFolder(folderPath);
                } else if (label.equals(OPEN_IN_TERMINAL)) {
                    openTerminal(folderPath);
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            this.label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }
        private void openTerminal(String folderPath) {
            try {
                String os = System.getProperty("os.name").toLowerCase();

                if (os.contains("win")) {
                    // Windows: Open Command Prompt in the folder
                    new ProcessBuilder("cmd.exe", "/c", "start", "cmd", "/k", "cd /d " + folderPath).start();
                } else if (os.contains("mac")) {
                    // macOS: Open Terminal.app in the folder
                    new ProcessBuilder("open", "-a", "Terminal", folderPath).start();
                } else {
                    // Linux: Try common terminal emulators
                    String[] terminals = {"gnome-terminal", "konsole", "x-terminal-emulator"};
                    boolean terminalOpened = false;

                    for (String terminal : terminals) {
                        try {
                            new ProcessBuilder(terminal, "--working-directory=" + folderPath).start();
                            terminalOpened = true;
                            break;
                        } catch (Exception ignored) {
                        }
                    }

                    if (!terminalOpened) {
                        JOptionPane.showMessageDialog(null, "No compatible terminal found.");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error opening terminal: " + ex.getMessage());
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
