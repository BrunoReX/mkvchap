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

import java.awt.event.*;
import java.io.File;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;

public class Utils {
    /* Start of right-click menu code */

    private static void showRCMenu(JTextComponent text, MouseEvent e) {
        JPopupMenu rightClickMenu = new JPopupMenu();
        JMenuItem copyMenuItem = new JMenuItem(text.getActionMap().get(DefaultEditorKit.copyAction));
        JMenuItem cutMenuItem = new JMenuItem(text.getActionMap().get(DefaultEditorKit.cutAction));
        JMenuItem pasteMenuItem = new JMenuItem(text.getActionMap().get(DefaultEditorKit.pasteAction));
        JMenuItem selectAllMenuItem = new JMenuItem(text.getActionMap().get(DefaultEditorKit.selectAllAction));

        copyMenuItem.setText("Copy");
        cutMenuItem.setText("Cut");
        pasteMenuItem.setText("Paste");
        selectAllMenuItem.setText("Select All");

        rightClickMenu.add(copyMenuItem);
        rightClickMenu.add(cutMenuItem);
        rightClickMenu.add(pasteMenuItem);
        rightClickMenu.addSeparator();
        rightClickMenu.add(selectAllMenuItem);

        if (text.getText().isEmpty()) {
            copyMenuItem.setEnabled(false);
            selectAllMenuItem.setEnabled(false);
            cutMenuItem.setEnabled(false);
        }

        if (text.getSelectionStart() == text.getSelectionEnd()) {
            copyMenuItem.setEnabled(false);
            cutMenuItem.setEnabled(false);
        }

        if ((text.getSelectionStart()+text.getSelectionEnd()) == text.getText().length()) {
            selectAllMenuItem.setEnabled(false);
        }

        if (!text.isEditable()) {
            cutMenuItem.setEnabled(false);
            pasteMenuItem.setEnabled(false);
        }

        rightClickMenu.show(text, e.getX(), e.getY());
    }

    public static void addRCMenuMouseListener(final JTextComponent text) {
        text.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isMetaDown() && text.isEnabled()) {
                    text.requestFocus();
                    showRCMenu(text, e);
                }
            }
        });
    }

    /* End of right-click menu code */


    public static String padNumber(int pad, int number) {
        NumberFormat formatter = new DecimalFormat("00");

        if (pad > 1) {
            String n = "";
            for (int i = 0; i < pad; i++) {
                n += 0;
            }
            formatter = new DecimalFormat(n);
        }

        return formatter.format(number);
    }

    public static String getFileNameWithoutExt(String file) {
        File f = new File(file);

        file = f.getName();

        int dotIndex = file.lastIndexOf(".");
        if (dotIndex != -1) {
            return file.substring(0, dotIndex);
        } else {
            return file;
        }
    }
}