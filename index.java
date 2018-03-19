
/*
 * Vetrov Search v1.2
 * Developed by Immamul Morsilin
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import java.util.*;

import jxl.*;
import jxl.read.biff.BiffException;

public class index extends JFrame {
	private JMenuBar jMenuBar;
	private JMenu file;
	private JMenuItem openFile;
	private JMenuItem loadFilter;
	private JMenuItem clearWindow;
	private String filePath;

	private ArrayList<String> listOfFilterNames;
	private ArrayList<Checkbox> filterBox;
	private ArrayList<Checkbox> userFilter;

	private boolean isCleared;
	private boolean load;

	private ExcelMod excelFile;
	private int numberOfFilters;

	private JPanel selectFilterPanel;
	private Container contentPane;

	// buttons
	private JButton continueButton;

	// app icon
	private ImageIcon img;

	// Search table

	private JTable table;
	private DefaultTableModel defTab;

	public index() {
		init();
	}

	private void init() {

		// setting icon image

		img = new ImageIcon("C:\\Users\\imorsilin\\Desktop\\icon.png");
		this.setIconImage(img.getImage());
		// setting up menu tab values
		jMenuBar = new JMenuBar();
		file = new JMenu();
		openFile = new JMenuItem();
		clearWindow = new JMenuItem();

		// setting values for filters
		listOfFilterNames = new ArrayList<String>();
		filterBox = new ArrayList<Checkbox>();
		userFilter = new ArrayList<Checkbox>();

		loadFilter = new JMenuItem();

		isCleared = true;
		load = false;

		// window setting below
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Search Engine Excel - Copyright © 2018 Micron Technology");
		setPreferredSize(new Dimension(600, 720));

		file.setText("File");
		openFile.setText("Open File");
		loadFilter.setText("Load Filter");
		clearWindow.setText("Clear Window");

		// title
		JLabel title = new JLabel("Search Engine Excel © 2018");
		title.setFont(new Font("monospaced", Font.ROMAN_BASELINE, 20));
		this.add(title, BorderLayout.PAGE_START);

		// adding the menu component to the frame
		file.add(openFile);
		file.add(clearWindow);
		file.add(loadFilter);

		jMenuBar.add(file);
		setJMenuBar(jMenuBar);

		// action for menu items the try-catch block is needed because
		// ExcelMod.getNumberOfFilters
		// throws an IOException
		openFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO can't open a file while another file is already active
				try {
					openFileAction(e);
					load = true;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		clearWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearFileAction(e);
			}
		});

		loadFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// this resets the user filter, assuming it was already filled
				if (userFilter.size() != 0) {
					userFilter.clear();
				}
				if (load) {
					filterBox = loadFilterAction(e);

					selectFilterPanel = new JPanel(new GridLayout(0, 1));
					Border border = BorderFactory.createTitledBorder("Available Filters");

					for (int i = 0; i < filterBox.size(); i++) {
						Checkbox boxLabel = new Checkbox(filterBox.get(i).getLabel());

						boxLabel.addItemListener(new ItemListener() {
							@Override
							public void itemStateChanged(ItemEvent e) {
								if (e.getStateChange() == ItemEvent.SELECTED) {
									System.out.println("Selected");
									userFilter.add(boxLabel);
								} else if (e.getStateChange() == ItemEvent.DESELECTED) {
									System.out.println("Deselected");
									userFilter.remove(boxLabel);
								}
							}
						});
						selectFilterPanel.add(boxLabel);
					}

					if (filterBox.size() != 0) {
						selectFilterPanel.setBorder(border);
						contentPane = index.this.getContentPane();

						// TODO continue button should list out UI fields
						continueButton = new JButton("Continue");
						continueButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								try {
									continueAction(e, userFilter);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						});

						contentPane.add(continueButton, BorderLayout.PAGE_END);
						contentPane.add(selectFilterPanel, BorderLayout.CENTER);

						index.this.add(selectFilterPanel);
						index.this.pack();
						index.this.setVisible(true);
						isCleared = false;
					}
				} else if (!load && !isCleared) {
					JOptionPane.showMessageDialog(index.this, "Filters already loaded");
				} else {
					JOptionPane.showMessageDialog(index.this, "Please open an Excel file");
				}
				load = false;
			}
		});

		pack();
	}

	// get the file path and send it to excel
	private void openFileAction(ActionEvent e) throws IOException {
		clearFileAction(e);
		filePath = "";
		listOfFilterNames.clear();

		JFileChooser fileChooser = new JFileChooser();
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			filePath = selectedFile.getPath();
			excelFile = new ExcelMod(filePath);
			numberOfFilters = excelFile.getNumberOfFilters();
			for (int i = 0; i < numberOfFilters; i++) {
				this.listOfFilterNames.add(excelFile.getContent(i));
			}
		}
		isCleared = false;
	}

	private void clearFileAction(ActionEvent e) {
		filePath = "";
		listOfFilterNames.clear();
		filterBox.clear();
		try {
			if (isCleared) {
				throw new java.lang.NullPointerException("Panel is empty");
			}
			contentPane.remove(selectFilterPanel);
		} catch (java.lang.NullPointerException nullError) {
			isCleared = true;
			System.out.println("Clear");
			return;
		}

		index.this.remove(continueButton);
		this.revalidate();
		this.repaint();
		if (!isCleared) {
			JOptionPane.showMessageDialog(this, "Window is cleared");
		}
		isCleared = true;
		load = true;
	}

	private void continueAction(ActionEvent e, ArrayList<Checkbox> userFilter) throws IOException {
		ArrayList<String> reorder = new ArrayList<String>();
		String inputFile = filePath;
		File inputWorkBook = new File(inputFile);
		Workbook w;
		int row = 0;
		JFrame window = new JFrame();
		window.repaint();
		window.revalidate();
		try {
			w = Workbook.getWorkbook(inputWorkBook);
			Sheet sheet = w.getSheet(0);
			row = sheet.getRows();

			// colName is ordered based on how the user selected the checkbox
			String[] colName = new String[userFilter.size()];
			for (int i = 0; i < colName.length; i++) {
				colName[i] = userFilter.get(i).getLabel();
			}
			// the following reorders the filters to its original order
			for (int i = 0; i < filterBox.size(); i++) {
				String thisFilter = filterBox.get(i).getLabel();
				for (int j = 0; j < colName.length; j++) {
					if (thisFilter.equals(colName[j])) {
						reorder.add(thisFilter);
					}
				}
			}

			for (int i = 0; i < colName.length; i++) {
				colName[i] = "";
			}

			for (int i = 0; i < colName.length; i++) {
				colName[i] = reorder.get(i);
			}

			for (String c : colName) {
				System.out.println(c);
			}

			defTab = new DefaultTableModel(colName, 0);
			JTable test = new JTable(defTab);

			window.getContentPane().add(new JScrollPane(test), BorderLayout.CENTER);
			window.setSize(1000, 720);
			window.setVisible(true);
			pack();

			int rowCounter = 1;
			for (int i = rowCounter; i < sheet.getRows(); i++) {
				int userIndex = 0;
				int nextCol = 0;
				ArrayList<String> addThisRow = new ArrayList<String>();
				for (int j = 0; j < sheet.getColumns(); j++) {
					if (j > userFilter.size() && addThisRow.size() == userFilter.size()) {
						break;
					}
					String thisFilter = reorder.get(userIndex);
					if (sheet.getCell(nextCol, 0).getContents().equals(thisFilter)) {
						addThisRow.add(sheet.getCell(nextCol, i).getContents());
						userIndex++;
						if (userIndex == userFilter.size()) {
							break;
						}
					} else {
						nextCol++;
						j--;
					}
				}

				Object[] content = new Object[addThisRow.size()];
				for (int k = 0; k < content.length; k++) {
					content[k] = addThisRow.get(k);
				}

				defTab.addRow(content);
			}

		} catch (BiffException e1) {
			e1.printStackTrace();
		}

	}

	private ArrayList<Checkbox> loadFilterAction(ActionEvent e) {
		ArrayList<Checkbox> sendThisList = new ArrayList<Checkbox>();
		if (isCleared) {
			JOptionPane.showMessageDialog(this, "Please open an Excel file");
		} else {
			for (int j = 0; j < listOfFilterNames.size(); j++) {
				Checkbox checkBox = new Checkbox(listOfFilterNames.get(j));
				sendThisList.add(checkBox);
			}
		}
		return sendThisList;
	}

	public static void main(String[] args) {
		// sets up the systems look & feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		// start application
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new index().setVisible(true);
			}
		});
	}

}
