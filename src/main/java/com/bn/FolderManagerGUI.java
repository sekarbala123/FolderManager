package com.bn;
import java.awt.BorderLayout;
import java.awt.Color;
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

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
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
        String[] columnNames = {"!", FOLDER_PATH, OPEN, DELETE, OPEN_IN_TERMINAL};

        tableModel = new DefaultTableModel(columnNames, 0);
//        tableModel = new DefaultTableModel(columnNames, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return column == 0; // Only the "X" button should be editable (clickable)
//            }
//        };
//        table.setModel(model);

        table = new JTable(tableModel);
//        table.getColumn("Open").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Open").setCellEditor(new ButtonEditor(new JCheckBox(), "Open"));
//        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));
//        table.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
//        table.getColumnModel().getColumn(0).setCellEditor(new ButtonEditor(new JCheckBox(), "x"));
        table.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox(), OPEN));
        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox(), DELETE));
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), OPEN_IN_TERMINAL));
        table.addMouseListener(new MouseAdapter() {
            private Timer clickTimer;
            private long lastClickTime = 0;
            private static final int DOUBLE_CLICK_THRESHOLD = 300; // 300ms max for double-click

            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row != -1 && col == 1) { // Ensure click is on the Folder Path column
                    long currentTime = System.currentTimeMillis();
                    
                    if (currentTime - lastClickTime < DOUBLE_CLICK_THRESHOLD) {
                        // Double-click detected -> Stop the pending single-click action
                        if (clickTimer != null && clickTimer.isRunning()) {
                            clickTimer.stop();
                        }
                        table.editCellAt(row, col); // Enable editing
                    } else {
                        // Start a delayed action for single-click
                        clickTimer = new Timer(DOUBLE_CLICK_THRESHOLD, ev -> {
                            if (System.currentTimeMillis() - lastClickTime >= DOUBLE_CLICK_THRESHOLD) {
                                String path = (String) table.getValueAt(row, col);
                                StringSelection selection = new StringSelection(path);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(selection, null);
                                showTooltip(e, "Path copied!");

//                                showStatusMessage("Path copied: " + path); // Show message in status label
//                                JOptionPane.showMessageDialog(null, "Path copied to clipboard: " + path);
                            }
                        });
                        clickTimer.setRepeats(false);
                        clickTimer.start();
                    }
                    
                    lastClickTime = currentTime; // Update last click time
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
        
        // Create status label
//        statusLabel = new JLabel(" ");
//        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        
//        // Add to the bottom of the frame
//        JPanel statusPanel = new JPanel(new BorderLayout());
//        statusPanel.add(statusLabel, BorderLayout.CENTER);
//        
//        frame.add(statusPanel,BorderLayout.AFTER_LAST_LINE);
        
        
        frame.setVisible(true);
    }

    private void setColumnSize() {
    	// Get the table column model
    	TableColumnModel columnModel = table.getColumnModel();

    	TableColumn removeColumn = table.getColumnModel().getColumn(0);
    	removeColumn.setCellRenderer(new RemoveButtonRenderer());
    	removeColumn.setCellEditor(new RemoveButtonEditor(table));
    	removeColumn.setMaxWidth(40); // Keep the "X" button small
    	removeColumn.setMinWidth(40);

    	
    	// Set fixed widths for button columns
    	columnModel.getColumn(2).setMinWidth(80);  // Open Button
    	columnModel.getColumn(2).setMaxWidth(80);

    	columnModel.getColumn(3).setMinWidth(80);  // Delete Button
    	columnModel.getColumn(3).setMaxWidth(80);

    	columnModel.getColumn(4).setMinWidth(120); // Open in Terminal Button
    	columnModel.getColumn(4).setMaxWidth(180);
    	columnModel.getColumn(4).setResizable(true);

    	// Let the Folder Path column take up the remaining space
    	columnModel.getColumn(1).setResizable(true);


	}

    private void showTooltip(MouseEvent e, String message) {
        JWindow tooltipWindow = new JWindow();
        JLabel tooltipLabel = new JLabel(message);
        tooltipLabel.setOpaque(true);
        tooltipLabel.setBackground(new Color(0, 0, 0, 180));
        tooltipLabel.setForeground(Color.WHITE);
        tooltipLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        tooltipWindow.getContentPane().add(tooltipLabel);
        tooltipWindow.pack();

        // Get mouse cursor location on screen
        int x = e.getXOnScreen() + 10; // Offset to avoid overlap
        int y = e.getYOnScreen() + 10;

        tooltipWindow.setLocation(x, y);
        tooltipWindow.setAlwaysOnTop(true);
        tooltipWindow.setVisible(true);

        // Hide tooltip after 1.5 seconds
        Timer hideTimer = new Timer(1500, ev -> tooltipWindow.dispose());
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    
	private void addFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();
            tableModel.addRow(new Object[]{"X",selectedFolder.getAbsolutePath(), OPEN, DELETE,OPEN_IN_TERMINAL});
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
                tableModel.addRow(new Object[]{"X", line, OPEN, DELETE, OPEN_IN_TERMINAL});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
		private static final long serialVersionUID = -1L;

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
        private JTable table;
        private int row;

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            button = new JButton(label);
            button.setOpaque(true);
            button.addActionListener(e -> {
                fireEditingStopped();
                String folderPath = (String) table.getValueAt(row, 1);
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
            return button;
        }

        public Object getCellEditorValue() {
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
    
    class RemoveButtonRenderer extends JButton implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

		public RemoveButtonRenderer() {
            setText("X");
            setOpaque(true); // Ensure button is visible
            setForeground(Color.WHITE);
            setBackground(Color.RED);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setFocusPainted(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }


    class RemoveButtonEditor extends DefaultCellEditor {
        private static final long serialVersionUID = 1L;
		private final JButton button;
        private final JTable table;

        public RemoveButtonEditor(JTable table) {
            super(new JTextField()); // We don’t use the text field
            this.table = table;
            button = new JButton("X");
            button.setOpaque(true); // Ensure visibility
            button.setForeground(Color.WHITE);
            button.setBackground(Color.RED);
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            button.setFocusPainted(false);
            button.addActionListener(e -> removeRow());
        }

        private void removeRow() {
            int row = table.getSelectedRow();
            if (row != -1) {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing(); // Stop editing before removal
                }
                ((DefaultTableModel) table.getModel()).removeRow(row);
            }
        }


        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(FolderManagerGUI::new);
    }
}
