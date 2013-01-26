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


import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;

public class JPing {

    private JFrame frmJping;
    private JTextField txtSiteToPing;
    private JTextField txtTimesToPing;
    private JLabel lblTimes;
    private String cmdLine = "";
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
                    JPing window = new JPing();
                    window.frmJping.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public JPing() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frmJping = new JFrame();
        frmJping.setResizable(false);
        frmJping.setTitle("JPing");
        frmJping.setBounds(100, 100, 721, 491);
        frmJping.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frmJping.getContentPane().setLayout(null);

        JLabel lblPing = new JLabel("Ping");
        lblPing.setBounds(10, 11, 27, 14);
        frmJping.getContentPane().add(lblPing);

        txtSiteToPing = new JTextField();
        txtSiteToPing.setHorizontalAlignment(SwingConstants.CENTER);
        txtSiteToPing.setText("www.google.com");
        txtSiteToPing.setBounds(40, 8, 488, 20);
        frmJping.getContentPane().add(txtSiteToPing);
        txtSiteToPing.setColumns(10);

        txtTimesToPing = new JTextField();
        txtTimesToPing.setHorizontalAlignment(SwingConstants.CENTER);
        txtTimesToPing.setText("25");
        txtTimesToPing.setBounds(574, 8, 71, 20);
        frmJping.getContentPane().add(txtTimesToPing);
        txtTimesToPing.setColumns(10);

        lblTimes = new JLabel("Time(s)");
        lblTimes.setBounds(656, 11, 46, 14);
        frmJping.getContentPane().add(lblTimes);

        JPanel pnlOutput = new JPanel();
        pnlOutput.setBounds(10, 46, 695, 372);
        frmJping.getContentPane().add(pnlOutput);
        pnlOutput.setLayout(null);

        final JTextArea txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setBounds(0, 0, 548, 254);
        pnlOutput.add(txtOutput);
        JScrollPane scrollOutput = new JScrollPane(txtOutput);
        pnlOutput.setLayout(new BorderLayout());
        pnlOutput.add(scrollOutput, BorderLayout.CENTER);

        final JButton btnPing = new JButton("Ping!");
        btnPing.setBounds(140, 429, 147, 23);
        frmJping.getContentPane().add(btnPing);

        final JButton btnCancel = new JButton("Cancel!");
        btnCancel.setEnabled(false);
        btnCancel.setBounds(427, 429, 147, 23);
        frmJping.getContentPane().add(btnCancel);

        btnPing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtOutput.setText("");
                cmdLine = "ping " + txtSiteToPing.getText() + " -n " + txtTimesToPing.getText();

                btnPing.setEnabled(false);
                btnCancel.setEnabled(true);

                //Use a SwingWorker thread to wait for the process completion
                //That way, the Swing GUI won't freeze
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() {
                            Runtime rt = Runtime.getRuntime();

                            try {
                                proc = rt.exec(cmdLine);
                                pRunning = true;

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
                            btnPing.setEnabled(true);
                            btnCancel.setEnabled(false);
                            pRunning = false;
                        }

                     };
                worker.execute();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                proc.destroy();
            }
        });

        frmJping.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                if (pRunning) {
                    int choice = JOptionPane.showConfirmDialog(frmJping,
                            "Do you really wanna exit?",
                            "", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        proc.destroy();
                        frmJping.dispose();
                        System.exit(0);
                    }
                } else {
                    frmJping.dispose();
                    System.exit(0);
                }
            }

        });
    }
}
