/*
 * Copyright (c) 2012-2013 Bruno Barbieri
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package io.github.brunorex;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.*;

public class MKVChap {
    private JFrame frmMKVChap;
    private JTabbedPane tabbedPane;
    private DefaultListModel<String> modelFiles = new DefaultListModel<String>();
    private JList<String> listFiles;
    private JTextArea txtOutput;
    private JButton btnSaveToFile;
    private JButton btnCreateChaptersFile;
    private JButton btnCancel;

    private static TimeInterval time;
    private static MediaInfo MI;

    private ArrayList<String> durations = new ArrayList<String>();

    private static SwingWorker<Void, Void> worker = null;

    private static final FileFilter TXT_EXT_FILTER =
            new FileNameExtensionFilter("Plain text files (*.txt)", "txt");

    private JFileChooser openDialog = new JFileChooser(System.getProperty("user.home")) {
        private static final long serialVersionUID = 1L;

        @Override
        public int showOpenDialog(Component parent) {
            super.setSelectedFile(new File(""));

            return super.showOpenDialog(parent);
        }

        @Override
        public void approveSelection() {
            if (!super.isMultiSelectionEnabled() || super.getSelectedFiles().length == 1) {
                if (!this.getSelectedFile().exists()) {
                    return;
                }
            }

            super.approveSelection();
        }
    };

    private JFileChooser saveDialog = new JFileChooser(System.getProperty("user.home")) {
        private static final long serialVersionUID = 1L;

        @Override
        public int showSaveDialog(Component parent) {
            super.setSelectedFile(new File(""));

            return super.showSaveDialog(parent);
        }

        @Override
        public void approveSelection() {
            if (super.getSelectedFile().exists()) {
                int option = JOptionPane.showConfirmDialog(frmMKVChap, "Overwrite file?");

                if (option == JOptionPane.NO_OPTION || option == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            super.approveSelection();
        }
    };


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    MKVChap window = new MKVChap();
                    window.frmMKVChap.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MKVChap() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmMKVChap = new JFrame();
        frmMKVChap.setTitle("MKVChap 0.4");
        frmMKVChap.setBounds(100, 100, 688, 438);
        frmMKVChap.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        openDialog.setDialogType(JFileChooser.OPEN_DIALOG);
        openDialog.setFileHidingEnabled(true);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frmMKVChap.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JPanel pnlInput = new JPanel();
        pnlInput.setBorder(new EmptyBorder(10, 10, 10, 0));
        pnlInput.setLayout(new BorderLayout(0, 0));
        tabbedPane.add(pnlInput, BorderLayout.CENTER);
        tabbedPane.setTitleAt(0, "Input");

        JScrollPane spInput = new JScrollPane();
        pnlInput.add(spInput, BorderLayout.CENTER);

        listFiles = new JList<String>();
        listFiles.setModel(modelFiles);
        spInput.setViewportView(listFiles);

        JPanel pnlListToolbar = new JPanel();
        pnlListToolbar.setBorder(new EmptyBorder(0, 5, 0, 5));
        pnlInput.add(pnlListToolbar, BorderLayout.EAST);
        pnlListToolbar.setLayout(new BoxLayout(pnlListToolbar, BoxLayout.Y_AXIS));

        JButton btnAddFiles = new JButton("");
        btnAddFiles.setIcon(new ImageIcon(MKVChap.class.getResource("/res/list-add.png")));
        btnAddFiles.setMargin(new Insets(0, 0, 0, 0));
        btnAddFiles.setBorderPainted(false);
        btnAddFiles.setContentAreaFilled(false);
        btnAddFiles.setFocusPainted(false);
        btnAddFiles.setOpaque(false);
        pnlListToolbar.add(btnAddFiles);

        Component verticalStrut1 = Box.createVerticalStrut(10);
        pnlListToolbar.add(verticalStrut1);

        JButton btnRemoveFiles = new JButton("");
        btnRemoveFiles.setIcon(new ImageIcon(MKVChap.class.getResource("/res/list-remove.png")));
        btnRemoveFiles.setMargin(new Insets(0, 0, 0, 0));
        btnRemoveFiles.setBorderPainted(false);
        btnRemoveFiles.setContentAreaFilled(false);
        btnRemoveFiles.setFocusPainted(false);
        btnRemoveFiles.setOpaque(false);
        pnlListToolbar.add(btnRemoveFiles);

        Component verticalStrut2 = Box.createVerticalStrut(10);
        pnlListToolbar.add(verticalStrut2);

        JButton btnTopFiles = new JButton("");
        btnTopFiles.setIcon(new ImageIcon(MKVChap.class.getResource("/res/go-top.png")));
        btnTopFiles.setMargin(new Insets(0, 0, 0, 0));
        btnTopFiles.setBorderPainted(false);
        btnTopFiles.setContentAreaFilled(false);
        btnTopFiles.setFocusPainted(false);
        btnTopFiles.setOpaque(false);
        pnlListToolbar.add(btnTopFiles);

        Component verticalStrut3 = Box.createVerticalStrut(10);
        pnlListToolbar.add(verticalStrut3);

        JButton btnUpFiles = new JButton("");
        btnUpFiles.setIcon(new ImageIcon(MKVChap.class.getResource("/res/go-up.png")));
        btnUpFiles.setMargin(new Insets(0, 0, 0, 0));
        btnUpFiles.setBorderPainted(false);
        btnUpFiles.setContentAreaFilled(false);
        btnUpFiles.setFocusPainted(false);
        btnUpFiles.setOpaque(false);
        pnlListToolbar.add(btnUpFiles);

        Component verticalStrut4 = Box.createVerticalStrut(10);
        pnlListToolbar.add(verticalStrut4);

        JButton btnDownFiles = new JButton("");
        btnDownFiles.setIcon(new ImageIcon(MKVChap.class.getResource("/res/go-down.png")));
        btnDownFiles.setMargin(new Insets(0, 0, 0, 0));
        btnDownFiles.setBorderPainted(false);
        btnDownFiles.setContentAreaFilled(false);
        btnDownFiles.setFocusPainted(false);
        btnDownFiles.setOpaque(false);
        pnlListToolbar.add(btnDownFiles);

        Component verticalStrut5 = Box.createVerticalStrut(10);
        pnlListToolbar.add(verticalStrut5);

        JButton btnBottomFiles = new JButton("");
        btnBottomFiles.setIcon(new ImageIcon(MKVChap.class.getResource("/res/go-bottom.png")));
        btnBottomFiles.setMargin(new Insets(0, 0, 0, 0));
        btnBottomFiles.setBorderPainted(false);
        btnBottomFiles.setContentAreaFilled(false);
        btnBottomFiles.setFocusPainted(false);
        btnBottomFiles.setOpaque(false);
        pnlListToolbar.add(btnBottomFiles);

        Component verticalStrut6 = Box.createVerticalStrut(10);
        pnlListToolbar.add(verticalStrut6);

        JButton btnClearFiles = new JButton("");
        btnClearFiles.setIcon(new ImageIcon(MKVChap.class.getResource("/res/edit-clear.png")));
        btnClearFiles.setMargin(new Insets(0, 0, 0, 0));
        btnClearFiles.setBorderPainted(false);
        btnClearFiles.setContentAreaFilled(false);
        btnClearFiles.setFocusPainted(false);
        btnClearFiles.setOpaque(false);
        pnlListToolbar.add(btnClearFiles);


        JPanel pnlOutput = new JPanel();
        pnlOutput.setBorder(new EmptyBorder(10, 10, 0, 10));
        tabbedPane.addTab("Output", null, pnlOutput, null);
        pnlOutput.setLayout(new BorderLayout(0, 0));

        JScrollPane spOutput = new JScrollPane();
        pnlOutput.add(spOutput, BorderLayout.CENTER);

        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        spOutput.setViewportView(txtOutput);
        Utils.addRCMenuMouseListener(txtOutput);

        JPanel pnlSaveOutput = new JPanel();
        pnlOutput.add(pnlSaveOutput, BorderLayout.SOUTH);

        btnSaveToFile = new JButton("Save to file");
        pnlSaveOutput.add(btnSaveToFile);

        JPanel pnlBottom = new JPanel();
        frmMKVChap.getContentPane().add(pnlBottom, BorderLayout.SOUTH);

        btnCreateChaptersFile = new JButton("Create chapters file");
        pnlBottom.add(btnCreateChaptersFile);

        btnCancel = new JButton("Cancel");
        btnCancel.setEnabled(false);
        pnlBottom.add(btnCancel);

        frmMKVChap.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                try {
                    MI = new MediaInfo();
                } catch (UnsatisfiedLinkError e1) {
                    JOptionPane.showMessageDialog(frmMKVChap,
                            "Couldn't load MediaInfo library!",
                            "", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });

        new FileDrop(listFiles, new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {
                for (int i = 0; i < files.length; i++) {
                    try {
                        if (!modelFiles.contains(files[i].getCanonicalPath()) && !files[i].isDirectory()) {
                            modelFiles.add(modelFiles.getSize(), files[i].getCanonicalPath());
                        }
                    } catch(java.io.IOException e) {
                    }
                }
            }
        });

        btnAddFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File[] files = null;

                openDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
                openDialog.setDialogTitle("Select media files");
                openDialog.setAcceptAllFileFilterUsed(true);
                openDialog.setMultiSelectionEnabled(true);
                openDialog.resetChoosableFileFilters();

                int open = openDialog.showOpenDialog(frmMKVChap);

                if (open == JFileChooser.APPROVE_OPTION) {
                    files = openDialog.getSelectedFiles();
                    for (int i = 0; i < files.length; i++) {
                            try {
                                if (!modelFiles.contains(files[i].getCanonicalPath()) && files[i].exists()) {
                                    modelFiles.add(modelFiles.getSize(), files[i].getCanonicalPath());
                                }
                            } catch (IOException e1) {
                            }
                    }
                }

            }
        });

        btnRemoveFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (modelFiles.getSize() > 0) {
                    while (listFiles.getSelectedIndex() != -1) {
                        int[] idx = listFiles.getSelectedIndices();
                        modelFiles.remove(idx[0]);
                    }
                }
            }
        });

        btnClearFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                modelFiles.removeAllElements();
            }
        });

        btnTopFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] idx = listFiles.getSelectedIndices();

                for (int i = 0; i < idx.length; i++) {
                    int pos = idx[i];

                    if (pos > 0) {
                        String temp = (String)modelFiles.remove(pos);
                        modelFiles.add(i, temp);
                        listFiles.ensureIndexIsVisible(0);
                        idx[i] = i;
                    }
                }

                listFiles.setSelectedIndices(idx);
            }
        });

        btnUpFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] idx = listFiles.getSelectedIndices();

                for (int i = 0; i < idx.length; i++) {
                    int pos = idx[i];

                    if (pos > 0 && listFiles.getMinSelectionIndex() != 0) {
                        String temp = (String)modelFiles.remove(pos);
                        modelFiles.add(pos-1, temp);
                        listFiles.ensureIndexIsVisible(pos-1);
                        idx[i]--;
                    }
                }

                listFiles.setSelectedIndices(idx);
            }
        });

        btnDownFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] idx = listFiles.getSelectedIndices();

                for (int i = idx.length-1; i > -1; i--) {
                    int pos = idx[i];

                    if (pos < modelFiles.getSize()-1 && listFiles.getMaxSelectionIndex() != modelFiles.getSize()-1) {
                        String temp = (String)modelFiles.remove(pos);
                        modelFiles.add(pos+1, temp);
                        listFiles.ensureIndexIsVisible(pos+1);
                        idx[i]++;
                    }
                }

                listFiles.setSelectedIndices(idx);
            }
        });

        btnBottomFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] idx = listFiles.getSelectedIndices();
                int j = 0;

                for (int i = idx.length-1; i > -1; i--) {
                    int pos = idx[i];

                    if (pos < modelFiles.getSize()) {
                        String temp = (String)modelFiles.remove(pos);
                        modelFiles.add(modelFiles.getSize()-j, temp);
                        j++;
                        listFiles.ensureIndexIsVisible(modelFiles.getSize()-1);
                        idx[i] = modelFiles.getSize()-j;
                    }
                }

                listFiles.setSelectedIndices(idx);
            }
        });

        btnSaveToFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (txtOutput.getText().trim().isEmpty()) {
                    return;
                }

                saveDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
                saveDialog.setDialogTitle("Save chapter file");
                saveDialog.setMultiSelectionEnabled(false);
                saveDialog.resetChoosableFileFilters();
                saveDialog.setFileFilter(TXT_EXT_FILTER);
                saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);

                int save = saveDialog.showSaveDialog(frmMKVChap);

                if (save == JFileChooser.APPROVE_OPTION) {

                    try {
                        File file = saveDialog.getSelectedFile();

                        if (saveDialog.getFileFilter() == TXT_EXT_FILTER && !TXT_EXT_FILTER.accept(file)) {
                            file = new File(file.getCanonicalPath() + ".txt");
                        }

                        FileWriter fw = new FileWriter(file);
                        fw.write(txtOutput.getText());
                        fw.close();
                    } catch (IOException e1) {
                    }

                }


            }
        });

        btnCreateChaptersFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (modelFiles.size() == 0) {
                    JOptionPane.showMessageDialog(frmMKVChap,
                            "Nothing to do!",
                            "", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    createChapters();
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                worker.cancel(true);
            }
        });
    }

    public void createChapters() {
        worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {
                txtOutput.setText("");
                btnCancel.setEnabled(true);
                btnCreateChaptersFile.setEnabled(false);
                btnSaveToFile.setEnabled(false);
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-1);
                tabbedPane.setEnabled(false);
                durations = new ArrayList<String>();

                String total = "";
                int size = String.valueOf(modelFiles.size()).length();
                for (int i = 0; i < modelFiles.size(); i++) {
                    try {
                        if (MI.Open((String) modelFiles.get(i)) > 0) {
                            MI.Option("Inform", "General;%Duration/String3%");
                            durations.add(MI.Inform());
                            MI.Close();
                        } else {
                            break;
                        }

                        String time = "00:00:00.000";

                        if (i > 0) {
                            if (i == 1) {
                                time = durations.get(i-1);
                            } else {
                                time = sumTime(total, durations.get(i-1));
                            }

                            total = time;
                        }

                        String num = Utils.padNumber(size, i+1);
                        String name = Utils.getFileNameWithoutExt((String) modelFiles.get(i));

                        txtOutput.append("CHAPTER" + num + "=" + time + "\n");
                        txtOutput.append("CHAPTER" + num + "NAME=" + name + "\n");

                        txtOutput.setCaretPosition(txtOutput.getText().length());

                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                btnCancel.setEnabled(false);
                btnCreateChaptersFile.setEnabled(true);
                tabbedPane.setEnabled(true);
                btnSaveToFile.setEnabled(true);
            }
         };

         worker.execute();
    }


    public String sumTime(String date1, String date2) {
        time = new TimeInterval(date1);
        time = time.add(new TimeInterval(date2));

        return time.toString();
    }
}
