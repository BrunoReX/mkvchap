import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.*;

public class JInnounp {

	private JFrame frmJInnounp;
	private JTextField txtExeFile;
	private JTextField txtExtractDir;
	private JTextField txtPassword;
	private String finalCmdLine = "";
	private Process proc = null;
	private boolean pRunning = false;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Use native theme for GUI
					JInnounp window = new JInnounp();
					window.frmJInnounp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JInnounp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmJInnounp = new JFrame();
		frmJInnounp.setResizable(false);
		frmJInnounp.setTitle("JInnounp");
		frmJInnounp.setBounds(100, 100, 628, 407);
		frmJInnounp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmJInnounp.getContentPane().setLayout(null);
		
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 11, 602, 337);
		frmJInnounp.getContentPane().add(tabbedPane);
		
		JPanel pnlUnpack = new JPanel();
		tabbedPane.addTab("Unpack", null, pnlUnpack, null);
		pnlUnpack.setLayout(null);
		
		JLabel lblExecutableToUnpack = new JLabel("Executable to unpack");
		lblExecutableToUnpack.setBounds(22, 48, 103, 14);
		pnlUnpack.add(lblExecutableToUnpack);
		
		txtExeFile = new JTextField();
		txtExeFile.setBounds(21, 64, 553, 20);
		pnlUnpack.add(txtExeFile);
		txtExeFile.setEditable(false);
		txtExeFile.setColumns(10);
		
		JButton btnBrowseFile = new JButton("Browse...");
		btnBrowseFile.setBounds(495, 89, 79, 23);
		pnlUnpack.add(btnBrowseFile);
		
		JPanel pnlOptions = new JPanel();
		tabbedPane.addTab("Options", null, pnlOptions, null);
		pnlOptions.setLayout(null);
		
		JLabel lblHelp1 = new JLabel("?");
		lblHelp1.setForeground(Color.BLUE);
		lblHelp1.setBounds(177, 47, 13, 14);
		lblHelp1.setToolTipText("Will not prompt for password or disk changes");
		pnlOptions.add(lblHelp1);
		
		final JCheckBox chckbxExtractFilesWithout = new JCheckBox("Extract files without paths");
		chckbxExtractFilesWithout.setBounds(15, 17, 161, 23);
		pnlOptions.add(chckbxExtractFilesWithout);
		
		final JCheckBox chckbxBatchnoninteractiveMode = new JCheckBox("Batch (non-interactive) mode");
		chckbxBatchnoninteractiveMode.setSelected(true);
		chckbxBatchnoninteractiveMode.setEnabled(false);
		chckbxBatchnoninteractiveMode.setBounds(15, 43, 165, 23);
		pnlOptions.add(chckbxBatchnoninteractiveMode);
		
		final JCheckBox chckbxProcessInternalEmbedded = new JCheckBox("Process internal embedded files (such as license and uninstall.exe)");
		chckbxProcessInternalEmbedded.setBounds(15, 69, 343, 23);
		pnlOptions.add(chckbxProcessInternalEmbedded);
		
		final JCheckBox chckbxPasswordForExtraction = new JCheckBox("Password for extraction");
		chckbxPasswordForExtraction.setBounds(15, 207, 144, 23);
		pnlOptions.add(chckbxPasswordForExtraction);
		
		txtPassword = new JTextField();
		txtPassword.setEnabled(false);
		txtPassword.setBounds(19, 237, 557, 20);
		pnlOptions.add(txtPassword);
		txtPassword.setColumns(10);
				
		final JComboBox cbPasswordType = new JComboBox();
		cbPasswordType.setEnabled(false);
		cbPasswordType.addItem("Type password");
		cbPasswordType.addItem("Password from file");
		cbPasswordType.setBounds(164, 207, 135, 20);
		pnlOptions.add(cbPasswordType);
				
		final JButton btnBrowsePassword = new JButton("Browse...");
		btnBrowsePassword.setEnabled(false);
		btnBrowsePassword.setBounds(497, 262, 79, 23);
		pnlOptions.add(btnBrowsePassword);
		btnBrowsePassword.setVisible(false);
		
		final JCheckBox chckbxProcessAllCopies = new JCheckBox("Process all copies of duplicate files");
		chckbxProcessAllCopies.setSelected(true);
		chckbxProcessAllCopies.setBounds(15, 93, 195, 23);
		pnlOptions.add(chckbxProcessAllCopies);
		
		final JCheckBox chckbxAssumeYesOn = new JCheckBox("Assume Yes on all queries (e.g. overwrite files)");
		chckbxAssumeYesOn.setEnabled(false);
		chckbxAssumeYesOn.setSelected(true);
		chckbxAssumeYesOn.setBounds(15, 119, 257, 23);
		pnlOptions.add(chckbxAssumeYesOn);
		
		final JButton btnBrowseDir = new JButton("Browse...");
		btnBrowseDir.setBounds(495, 230, 79, 23);
		pnlUnpack.add(btnBrowseDir);
		
		final JLabel lblExtractTo = new JLabel("Extract to");
		lblExtractTo.setBounds(22, 187, 53, 23);
		pnlUnpack.add(lblExtractTo);
		
		txtExtractDir = new JTextField();
		txtExtractDir.setEditable(false);
		txtExtractDir.setBounds(21, 205, 553, 20);
		pnlUnpack.add(txtExtractDir);
		txtExtractDir.setColumns(10);
		
		JPanel pnlOutput = new JPanel();
		tabbedPane.addTab("Output", null, pnlOutput, null);
		pnlOutput.setLayout(null);
		pnlOutput.setLayout(new BorderLayout(0, 0));
		
		final JTextArea txtOutput = new JTextArea();
		txtOutput.setLineWrap(true);
		txtOutput.setEditable(false);
		pnlOutput.add(txtOutput);
		JScrollPane scrollOutput = new JScrollPane(txtOutput);
		pnlOutput.add(scrollOutput);
		
		
		final JButton btnUnpack = new JButton("Unpack");
		btnUnpack.setBounds(162, 351, 68, 23);
		frmJInnounp.getContentPane().add(btnUnpack);
		
		final JButton btnCancel = new JButton("Cancel");
		btnCancel.setEnabled(false);
		btnCancel.setBounds(392, 351, 68, 23);
		frmJInnounp.getContentPane().add(btnCancel);
		
		
		final JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		chooser.setFileHidingEnabled(true);
		chooser.setAcceptAllFileFilterUsed(false);
				
		btnBrowseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setDialogTitle("Select Executable for unpacking");
				FileFilter filter = new FileNameExtensionFilter("SetupLdr Files (.0)", "0");
				FileFilter filter2 = new FileNameExtensionFilter("Executable Files (.exe)", "exe");
				chooser.resetChoosableFileFilters();
				chooser.setFileFilter(filter);				
				chooser.setFileFilter(filter2);
								
				int open = chooser.showOpenDialog(frmJInnounp);
				
				if (open == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (file.exists()) {
						txtExeFile.setText(file.getAbsolutePath());
						txtExtractDir.setText(file.getParent() + "\\" + file.getName().replaceFirst("[.][^.]+$", ""));
					}
				}			
			}
		});
		
		btnBrowseDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogTitle("Select directory for unpacking");
				chooser.resetChoosableFileFilters();
								
				int open = chooser.showOpenDialog(frmJInnounp);
				
				if (open == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (file.exists()) {
						txtExtractDir.setText(file.getAbsolutePath());
					}
				}			
			}
		});
		
		chckbxPasswordForExtraction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chckbxPasswordForExtraction.isSelected()) {
					txtPassword.setEnabled(true);
					cbPasswordType.setEnabled(true);
					if (cbPasswordType.getSelectedIndex() == 1) {
						btnBrowsePassword.setVisible(true);
						btnBrowsePassword.setEnabled(true);
						txtPassword.setEnabled(false);
					}
				} else {
					txtPassword.setEnabled(false);
					cbPasswordType.setEnabled(false);
					txtPassword.setText("");
					if (cbPasswordType.getSelectedIndex() == 1) {
						btnBrowsePassword.setVisible(true);
						btnBrowsePassword.setEnabled(false);
					}
				}
			}
		});
		
		
		cbPasswordType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cbPasswordType.getSelectedIndex() == 0) {
					btnBrowsePassword.setEnabled(false);
					btnBrowsePassword.setVisible(false);
					txtPassword.setText("");
					txtPassword.setEnabled(true);
				} else {
					btnBrowsePassword.setEnabled(true);
					btnBrowsePassword.setVisible(true);
					txtPassword.setText("");
					txtPassword.setEnabled(false);
				}
			}
		});
		
		btnBrowsePassword.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setDialogTitle("Select text file with the password");
				FileFilter filter = new FileNameExtensionFilter("Plain text files (.txt)", "txt");
				chooser.resetChoosableFileFilters();
				chooser.setFileFilter(filter);
								
				int open = chooser.showOpenDialog(frmJInnounp);
				
				if (open == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					if (file.exists()) {
						txtPassword.setText(file.getAbsolutePath());
					}
				}	
			}
		});
		
		btnUnpack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File innounpExe = new File("innounp.exe");
				
				if (txtExeFile.getText().equals("") && txtExtractDir.getText().equals("")) {
					JOptionPane.showMessageDialog(frmJInnounp,
							"Both executable and directory fields are empty!",
							"", JOptionPane.ERROR_MESSAGE);
				} else if (txtExeFile.getText().equals("") || txtExtractDir.getText().equals("")) {
					JOptionPane.showMessageDialog(frmJInnounp,
							"Executable or directory field is empty!",
							"", JOptionPane.ERROR_MESSAGE);
				} else if (!innounpExe.exists()) {
					JOptionPane.showMessageDialog(frmJInnounp,
							"Inno Setup Unpacker executable not found!" +
							"\nPlease copy innounp.exe to the working folder.",
							"", JOptionPane.ERROR_MESSAGE);
				} else {
					String[] cmdLine = new String[9];
					cmdLine[0] = "innounp.exe";
					cmdLine[1] = chckbxExtractFilesWithout.isSelected() ? " -e" : " -x";
					cmdLine[2] = " -d\"" + txtExtractDir.getText() + "\"";
					cmdLine[3] = chckbxProcessAllCopies.isSelected() ? " -a" : "";
					cmdLine[4] = chckbxProcessInternalEmbedded.isSelected() ? " -m" : "";
					cmdLine[5] = chckbxBatchnoninteractiveMode.isSelected() ? " -b" : "";
					cmdLine[6] = chckbxAssumeYesOn.isSelected() ? " -y" : "";
					
					if (chckbxPasswordForExtraction.isSelected()) {
						if (txtPassword.getText().equals("")) {
							cmdLine[7] = "";
						} else if (cbPasswordType.getSelectedIndex() == 0) {
							cmdLine[7] = " -p\"" + txtPassword.getText() + "\"";
						} else {
							cmdLine[7] = " -f\"" + txtPassword.getText() + "\"";
						}
					} else {
						cmdLine[7] = "";
					}
					
					cmdLine[8] = " \"" + txtExeFile.getText() + "\"";
					
					finalCmdLine = "";
					
					if (cmdLine[7].equals("") && chckbxPasswordForExtraction.isSelected()) {
						JOptionPane.showMessageDialog(frmJInnounp,
								"Password option is checked but the field is empty!",
								"", JOptionPane.ERROR_MESSAGE);
					} else {
						for (int i = 0; i < cmdLine.length; i++) {
							finalCmdLine += cmdLine[i];
						}
						
						tabbedPane.setSelectedIndex(2);
						txtOutput.setText("");
						btnUnpack.setEnabled(false);
						btnCancel.setEnabled(true);
						
						SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
							@Override
							public Void doInBackground() {
						    	Runtime rt = Runtime.getRuntime();
						    	pRunning = true;
						    	
								try {
									proc = rt.exec(finalCmdLine);
									StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), txtOutput);
									StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), txtOutput);
									
									outputGobbler.start();
									errorGobbler.start();
									proc.waitFor();
								} catch (IOException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

								return null;
						    }
						    
						    @Override
						    protected void done() {
						    	btnUnpack.setEnabled(true);
						    	btnCancel.setEnabled(false);
						    	pRunning = false;
						    }						   
						 };
					worker.execute();
					}
				}
			} 
		});
		
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				proc.destroy();
			}
		});
		
		frmJInnounp.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (pRunning) {
					int choice = JOptionPane.showConfirmDialog(frmJInnounp,
							"Do you really wanna exit?",
							"", JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.YES_OPTION) {
						proc.destroy();
						frmJInnounp.dispose();
						System.exit(0);
					}
				} else {
					frmJInnounp.dispose();
					System.exit(0);
				}
			}
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				File innounpExe = new File("innounp.exe");
				
				if (!innounpExe.exists()) {
					JOptionPane.showMessageDialog(frmJInnounp,
							"Inno Setup Unpacker executable not found!" +
							"\nPlease copy innounp.exe to the working folder.",
							"Inno Setup Unpacker not found",
							JOptionPane.ERROR_MESSAGE);
					frmJInnounp.dispose();
					System.exit(0);
				}
			}
		});
	}
}
