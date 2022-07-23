package jemu.util.hexeditor;

/**
 * Title:        JEMU by cpc.devilmarkus.de / jemu.winape.net
 * Description:  JAVA emulator for Amstrad Homecomputers
 * Copyright:    Copyright (c) 2002-2008
 * Company:
 * @author       Devilmarkus
 * @version 5.8
 * 
 * 
 */

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.text.NumberFormat;

import javax.swing.*;

import java.awt.event.*;

public class HexEditor
{
    static Frame frame = new Frame();
    static String savename;
    static final int BUFFER_SIZE = 0x10000;
    static final int BYTE_MASK = 255;
    static final String DUMP_WIDTHS[] = {
        "4", "8", "12", "16", "24", "32"
    };
    static final String EMPTY_STATUS = " ";
    static final char FIRST_CHAR = 32;
    static final boolean FREE_VERSION = true;
    static final char HEX_DIGITS[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F'
    };
    static final int HEX_IGNORE = -1;
    static final int HEX_INVALID = -2;
    static final char LAST_CHAR = 126;
    static final char MARKER_CHAR = 124;
    static final String MARKER_STRING = Character.toString('|');
    static final int NIBBLE_MASK = 15;
    static final int NIBBLE_SHIFT = 4;
    static final int OFFSET_DIGITS = 8;
    static final String PROGRAM_TITLE = "JavaCPC Hex Editor";
    static final char REPLACE_CHAR = 46;
    static final Insets TEXT_MARGINS = new Insets(2, 3, 2, 3);
    static String clipString;
    static int dumpWidth;
    static JComboBox dumpWidthDialog;
    static JButton exitButton;
    static String fontName;
    static JComboBox fontNameDialog;
    static NumberFormat formatComma;
    static JFrame mainFrame;
    static JButton menuButton;
    static JMenuItem menuCopyDump;
    static JMenuItem menuCopyHex;
    static JMenuItem menuCopyText;
    static JMenuItem menuDelete;
    static JMenuItem menuFind;
    static JMenuItem menuNext;
    static JMenuItem menuPasteHex;
    static JMenuItem menuPasteText;
    static JMenuItem menuReplace;
    static JMenuItem menuSelect;
    static JPopupMenu menuPopup;
    static int nibbleCount;
    static Data nibbleData;
    static JButton openButton;
    static JCheckBox overDialog;
    static boolean overFlag;
    static JButton saveButton;
    static JCheckBox searchByteBound;
    static JCheckBox searchIgnoreNulls;
    static JButton searchCloseButton;
    static JButton searchFindButton;
    static JButton searchNextButton;
    static JButton searchReplaceButton;
    static JDialog searchDialog;
    static JTextField searchFindText;
    static JTextField searchReplaceText;
    static JRadioButton searchIsHex;
    static JRadioButton searchIsText;
    static JLabel searchStatus;
    static Text textPanel;
    static JScrollBar textScroll;
    static ActionListener userActions;

    @SuppressWarnings("unchecked")
	public HexEditor()
    { 
        clipString = null;
        dumpWidth = 16;
        String fileName = "";
        fontName = "Courier";
        boolean maximizeFlag = false;
        nibbleCount = 0;
        nibbleData = new Data(0);
        overFlag = true;
        searchDialog = null;
        formatComma = NumberFormat.getInstance();
        formatComma.setGroupingUsed(true);
        userActions = new User();
        Box panel1 = Box.createVerticalBox();
        panel1.add(Box.createVerticalStrut(15));
        JPanel panel2 = new JPanel(new FlowLayout(1, 40, 0));
        menuButton = new JButton("Edit Menu");
        menuButton.addActionListener(userActions);
        menuButton.setMnemonic(77);
        menuButton.setToolTipText("Copy, delete, find, paste, replace, etc.");
        panel2.add(menuButton);
        openButton = new JButton("Open File...");
        openButton.addActionListener(userActions);
        openButton.setMnemonic(79);
        openButton.setToolTipText("Read data bytes from a file.");
        panel2.add(openButton);
        saveButton = new JButton("Save File...");
        saveButton.addActionListener(userActions);
        saveButton.setMnemonic(83);
        saveButton.setToolTipText("Write data bytes to a file.");
        panel2.add(saveButton);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(userActions);
        exitButton.setMnemonic(88);
        exitButton.setToolTipText("Close this program.");
        panel2.add(exitButton);
        panel1.add(panel2);
        panel1.add(Box.createVerticalStrut(13));
        menuPopup = new JPopupMenu();
        menuCopyDump = new JMenuItem("Copy Dump");
        menuCopyDump.addActionListener(userActions);
        menuPopup.add(menuCopyDump);
        menuCopyHex = new JMenuItem("Copy Hex");
        menuCopyHex.addActionListener(userActions);
        menuPopup.add(menuCopyHex);
        menuCopyText = new JMenuItem("Copy Text");
        menuCopyText.addActionListener(userActions);
        menuPopup.add(menuCopyText);
        menuPasteHex = new JMenuItem("Paste Hex");
        menuPasteHex.addActionListener(userActions);
        menuPopup.add(menuPasteHex);
        menuPasteText = new JMenuItem("Paste Text");
        menuPasteText.addActionListener(userActions);
        menuPopup.add(menuPasteText);
        menuPopup.addSeparator();
        menuFind = new JMenuItem("Find...");
        menuFind.addActionListener(userActions);
        menuFind.setMnemonic(70);
        menuPopup.add(menuFind);
        menuNext = new JMenuItem("Find Next");
        menuNext.addActionListener(userActions);
        menuNext.setMnemonic(78);
        menuPopup.add(menuNext);
        menuReplace = new JMenuItem("Replace");
        menuReplace.addActionListener(userActions);
        menuReplace.setMnemonic(82);
        menuPopup.add(menuReplace);
        menuPopup.addSeparator();
        menuDelete = new JMenuItem("Delete");
        menuDelete.addActionListener(userActions);
        menuDelete.setMnemonic(68);
        menuPopup.add(menuDelete);
        menuSelect = new JMenuItem("Select All");
        menuSelect.addActionListener(userActions);
        menuSelect.setMnemonic(65);
        menuPopup.add(menuSelect);
        JPanel panel3 = new JPanel(new FlowLayout(1, 5, 0));
       // fontNameDialog = new JComboBox(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
       // fontNameDialog.setEditable(false);
       // fontNameDialog.setSelectedItem(fontName);
       // fontNameDialog.setToolTipText("Font name for displayed text.");
       // fontNameDialog.addActionListener(userActions);
       // panel3.add(fontNameDialog);
        panel3.add(Box.createHorizontalStrut(30));
        dumpWidthDialog = new JComboBox(DUMP_WIDTHS);
        dumpWidthDialog.setEditable(false);
        dumpWidthDialog.setSelectedItem(String.valueOf(dumpWidth));
        dumpWidthDialog.setToolTipText("Number of input bytes per dump line.");
        dumpWidthDialog.addActionListener(userActions);
        panel3.add(dumpWidthDialog);
        panel3.add(new JLabel("bytes per line"));
        panel3.add(Box.createHorizontalStrut(20));
        overDialog = new JCheckBox("overwrite mode", overFlag);
        overDialog.addActionListener(userActions);
        overDialog.setEnabled(true);
        overDialog.setToolTipText("Select for overwrite, clear for insert mode.");
        panel3.add(overDialog);
        panel1.add(panel3);
        panel1.add(Box.createVerticalStrut(7));
        JPanel panel4 = new JPanel(new FlowLayout(1, 0, 0));
        panel4.add(panel1);
        textPanel = new Text();
        textScroll = new JScrollBar(1, 0, 1, 0, 1);
        textScroll.setEnabled(true);
        textScroll.getModel().addChangeListener(textPanel);
        mainFrame = new JFrame("JavaCPC HexEditor"){
        
      
      protected void processWindowEvent(WindowEvent we) {
        super.processWindowEvent(we);
        if (we.getID() == WindowEvent.WINDOW_CLOSING) {
          mainFrame.dispose();
        }
      }
    };
        Container panel6 = mainFrame.getContentPane();
        panel6.setLayout(new BorderLayout(5, 5));
        panel6.add(panel4, "North");
        panel6.add(textPanel, "Center");
        panel6.add(textScroll, "East");
        if(maximizeFlag)
        {
            mainFrame.setExtendedState(6);
        }
        mainFrame.setLocation(50, 50);
        mainFrame.setSize(700, 500);
        mainFrame.validate();
        if(fileName.length() > 0)
        {
            openFile(new File(fileName));
        } else
        {
            byte array[] = "JavaCPC Hex Editor".getBytes();
            nibbleCount = array.length * 2;
            nibbleData = new Data(nibbleCount);
            for(int i = 0; i < array.length; i++)
            {
                nibbleData.append(array[i] >> 4 & 0xf);
                nibbleData.append(array[i] & 0xf);
            }

        }
        textPanel.beginFile();
        mainFrame.setVisible(true);
    }

    static boolean canWriteFile(File givenFile)
    {
        boolean result;
        if(givenFile.isDirectory())
        {
            JOptionPane.showMessageDialog(mainFrame, givenFile.getName() + " is a directory or folder.\nPlease select a normal file.");
            result = false;
        } else
        if(givenFile.isHidden())
        {
            JOptionPane.showMessageDialog(mainFrame, givenFile.getName() + " is a hidden or protected file.\nPlease select a normal file.");
            result = false;
        } else
        if(!givenFile.isFile())
        {
            result = true;
        } else
        if(givenFile.canWrite())
        {
            result = true;
            //result = JOptionPane.showConfirmDialog(mainFrame, givenFile.getName() + " already exists.\nDo you want to replace this with a new file?") == 0;
        } else
        {
            JOptionPane.showMessageDialog(mainFrame, givenFile.getName() + " is locked or write protected.\nCan't write to this file.");
            result = false;
        }
        return result;
    }

    static int charHexValue(char ch)
    {
        int result;
        if(ch >= '0' && ch <= '9')
        {
            result = ch - 48;
        } else
        if(ch >= 'A' && ch <= 'F')
        {
            result = (ch - 65) + 10;
        } else
        if(ch >= 'a' && ch <= 'f')
        {
            result = (ch - 97) + 10;
        } else
        if(ch == ',' || ch == '.' || ch == ':')
        {
            result = -1;
        } else
        if(Character.isWhitespace(ch))
        {
            result = -1;
        } else
        {
            result = -2;
        }
        return result;
    }

    static void copyDump()
    {
        int beginIndex = Math.min(textPanel.cursorDot, textPanel.cursorMark);
        int endIndex = Math.max(textPanel.cursorDot, textPanel.cursorMark);
        if(beginIndex < endIndex)
        {
            int lineLength = 8 + 4 * dumpWidth + 5;
            StringBuffer lineBuffer = new StringBuffer(lineLength + 1);
            lineBuffer.setLength(lineLength + 1);
            lineBuffer.setCharAt(lineLength, '\n');
            int lineNibbles = 2 * dumpWidth;
            int lineUsed = -1;
            int nextText;
            int nextHex = nextText = -1;
            String result = "";
            for(int thisIndex = beginIndex; thisIndex < endIndex; thisIndex++)
            {
                if(lineUsed >= lineNibbles)
                {
                    result = result + lineBuffer.toString();
                    lineUsed = -1;
                }
                if(lineUsed < 0)
                {
                    for(int i = 0; i < lineLength; i++)
                    {
                        lineBuffer.setCharAt(i, ' ');
                    }

                    lineBuffer.setCharAt(lineLength - dumpWidth - 2, '|');
                    lineBuffer.setCharAt(lineLength - 1, '|');
                    int shiftedOffset = (thisIndex / lineNibbles) * dumpWidth;
                    for(int i = 7; i >= 0; i--)
                    {
                        lineBuffer.setCharAt(i, HEX_DIGITS[shiftedOffset & 0xf]);
                        shiftedOffset >>= 4;
                    }

                    lineUsed = thisIndex % lineNibbles;
                    nextHex = lineUsed + lineUsed / 2 + 8 + 2;
                    nextText = (lineLength - dumpWidth - 1) + lineUsed / 2;
                }
                lineBuffer.setCharAt(nextHex++, HEX_DIGITS[nibbleData.get(thisIndex)]);
                nextHex += thisIndex % 2;
                lineUsed++;
                if(thisIndex % 2 == 1)
                {
                    int byteValue;
                    if(thisIndex - 1 < beginIndex)
                    {
                        byteValue = 46;
                    } else
                    {
                        byteValue = nibbleData.get(thisIndex - 1) << 4 | nibbleData.get(thisIndex);
                        if(byteValue < 32 || byteValue > 126)
                        {
                            byteValue = 46;
                        }
                    }
                    lineBuffer.setCharAt(nextText++, (char)byteValue);
                    continue;
                }
                if(thisIndex + 1 >= endIndex)
                {
                    lineBuffer.setCharAt(nextText++, '.');
                }
            }

            result = result + lineBuffer.toString();
            setClipboard(result);
        }
    }

    static void copyHex()
    {
        int beginIndex = Math.min(textPanel.cursorDot, textPanel.cursorMark);
        int endIndex = Math.max(textPanel.cursorDot, textPanel.cursorMark);
        if(beginIndex < endIndex)
        {
            String result = "";
            for(int thisIndex = beginIndex; thisIndex < endIndex; thisIndex++)
            {
                result = result + HEX_DIGITS[nibbleData.get(thisIndex)];
            }

            setClipboard(result);
        }
    }

    static void copyText()
    {
        int beginIndex = Math.min(textPanel.cursorDot, textPanel.cursorMark);
        int endIndex = Math.max(textPanel.cursorDot, textPanel.cursorMark);
        if(beginIndex < endIndex)
        {
            byte array[] = new byte[((endIndex - beginIndex) + 1) / 2];
            int thisIndex = beginIndex;
            for(int i = 0; i < array.length; i++)
            {
                int byteValue = nibbleData.get(thisIndex++) << 4;
                if(thisIndex < endIndex)
                {
                    byteValue |= nibbleData.get(thisIndex++);
                }
                array[i] = (byte)byteValue;
            }

            setClipboard(new String(array));
        }
    }

    static void deleteSelected()
    {
        int beginIndex = Math.min(textPanel.cursorDot, textPanel.cursorMark);
        int endIndex = Math.max(textPanel.cursorDot, textPanel.cursorMark);
        if(beginIndex < endIndex)
        {
            for(int thisIndex = beginIndex; thisIndex < endIndex; thisIndex++)
            {
                nibbleData.delete(beginIndex);
            }

            textPanel.cursorDot = textPanel.cursorMark = beginIndex;
            textPanel.limitCursorRange();
            textPanel.makeVisible(textPanel.cursorDot);
            textPanel.adjustScrollBar();
            textPanel.repaint();
        }
    }

    static String getClipboard()
    {
        String result;
        try
        {
            result = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
        }
        catch(IllegalStateException ise)
        {
            result = "";
        }
        catch(IOException ioe)
        {
            result = "";
        }
        catch(UnsupportedFlavorException ufe)
        {
            result = "";
        }
        return result;
    }

    static void openFile(File givenFile)
    {
        nibbleCount = 0;
        File inputFile;
        if(givenFile == null)
        {
        FileDialog filedia = new FileDialog((Frame) frame, "Open file...", FileDialog.LOAD);
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename == null) {
                refreshDataSize();
                return;
        } else {
           filename =  filedia.getDirectory() + filedia.getFile();
            savename =filename;
            }
            inputFile = new File(savename);
        } else
        {
            inputFile = givenFile;
        }
        long inputSize = inputFile.length();
        if(inputSize > 0x3fff0000L)
        {
            JOptionPane.showMessageDialog(mainFrame, "This program can't open files larger than one gigabyte.\n" + inputFile.getName() + " has " + formatComma.format(inputSize) + " bytes.");
            refreshDataSize();
            return;
        }
        if(inputSize > 0x5f5e0ffL && JOptionPane.showConfirmDialog(mainFrame, "Files larger than 100 megabytes may be slow.\n" + inputFile.getName() + " has " + formatComma.format(inputSize) + " bytes.\nDo you want to open this file anyway?", "Large File Warning", 1) != 0)
        {
            refreshDataSize();
            return;
        }
        try
        {
            byte buffer[] = new byte[0x10000];
            FileInputStream inputStream = new FileInputStream(inputFile);
            nibbleData = new Data(2 * (int)inputSize);
            int length;
            while((length = inputStream.read(buffer, 0, 0x10000)) > 0) 
            {
                int i = 0;
                while(i < length) 
                {
                    nibbleData.append(buffer[i] >> 4 & 0xf);
                    nibbleData.append(buffer[i] & 0xf);
                    i++;
                }
            }
            inputStream.close();
        }
        catch(IOException ioe)
        {
            nibbleData = new Data(0);
            JOptionPane.showMessageDialog(mainFrame, "Can't read from input file:\n" + ioe.getMessage());
        }
        catch(OutOfMemoryError oome)
        {
            nibbleData = new Data(0);
            JOptionPane.showMessageDialog(mainFrame, "Not enough memory to open this file.\n" + inputFile.getName() + " has " + formatComma.format(inputSize) + " bytes.\nTry increasing the Java heap size with the -Xmx option.");
        }
        refreshDataSize();
    }

    static void pasteHex()
    {
        String text = getClipboard();
        int length = text.length();
        int nibbles[] = new int[length * 2];
        int used = 0;
        for(int i = 0; i < length; i++)
        {
            char ch = text.charAt(i);
            int hexValue = charHexValue(ch);
            if(hexValue >= 0)
            {
                nibbles[used++] = hexValue;
                continue;
            }
            if(hexValue != -1)
            {
                JOptionPane.showMessageDialog(mainFrame, "Clipboard string must be hexadecimal digits or spaces; found " + (Character.isISOControl(ch) ? "" : "\"" + ch + "\" or ") + "0x" + Integer.toHexString(ch).toUpperCase() + ".");
                return;
            }
        }

        pasteNibbles(nibbles, used);
    }

    static void pasteNibbles(int array[], int used)
    {
        if(used <= 0)
        {
            return;
        }
        if(overFlag)
        {
            int beginIndex = Math.min(textPanel.cursorDot, textPanel.cursorMark);
            int endIndex = Math.max(textPanel.cursorDot, textPanel.cursorMark);
            if(beginIndex < endIndex && used != endIndex - beginIndex)
            {
                JOptionPane.showMessageDialog(mainFrame, "Overwrite selection (" + (endIndex - beginIndex) + ") and clipboard (" + used + ") have different sizes.");
                return;
            }
            textPanel.cursorDot = beginIndex;
            for(int i = 0; i < used; i++)
            {
                nibbleData.put(textPanel.cursorDot++, array[i]);
            }

        } else
        {
            deleteSelected();
            for(int i = 0; i < used; i++)
            {
                nibbleData.insert(textPanel.cursorDot++, array[i]);
            }

        }
        textPanel.cursorMark = textPanel.cursorDot;
        textPanel.limitCursorRange();
        textPanel.makeVisible(textPanel.cursorDot);
        textPanel.adjustScrollBar();
        textPanel.repaint();
    }

    static void pasteText()
    {
        byte bytes[] = getClipboard().getBytes();
        int nibbles[] = new int[bytes.length * 2];
        int used = 0;
        for(int i = 0; i < bytes.length; i++)
        {
            nibbles[used++] = bytes[i] >> 4 & 0xf;
            nibbles[used++] = bytes[i] & 0xf;
        }

        pasteNibbles(nibbles, used);
    }

    static void refreshDataSize()
    {
        nibbleCount = nibbleData.size();
    }

    static void saveFile()
    {
        FileDialog filedia = new FileDialog((Frame) frame, "Save file...", FileDialog.SAVE);
        filedia.setVisible(true);
        String filename = filedia.getFile();
        if (filename == null) {
            return;
        } else {
           filename =  filedia.getDirectory() + filedia.getFile();
            savename =filename;
        }
        File outputFile = new File(savename);
        try
        {
            if(canWriteFile(outputFile))
            {
                byte buffer[] = new byte[0x10000];
                int length = 0;
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                refreshDataSize();
                for(int i = 0; i < nibbleCount;)
                {
                    if(length >= 0x10000)
                    {
                        outputStream.write(buffer);
                        length = 0;
                    }
                    buffer[length] = (byte)(nibbleData.get(i++) << 4);
                    if(i < nibbleCount)
                    {
                        buffer[length] |= (byte)nibbleData.get(i++);
                    }
                    length++;
                }

                if(length > 0)
                {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
            }
        }
        catch(IOException ioe)
        {
            JOptionPane.showMessageDialog(mainFrame, "Can't write to output file:\n" + ioe.getMessage());
        }
    }

    static int[] searchConvertNibbles(String text, String type)
    {
        int result[];
        if(searchIsHex.isSelected())
        {
            boolean error = false;
            int length = text.length();
            int nibbles[] = new int[length * 2];
            int used = 0;
            for(int i = 0; i < length; i++)
            {
                char ch = text.charAt(i);
                int hexValue = charHexValue(ch);
                if(hexValue >= 0)
                {
                    nibbles[used++] = hexValue;
                    continue;
                }
                if(hexValue == -1)
                {
                    continue;
                }
                showSearchMessage("Invalid hex digit in " + type + " string; found " + (Character.isISOControl(ch) ? "" : "\"" + ch + "\" or ") + "0x" + Integer.toHexString(ch).toUpperCase() + ".");
                error = true;
                break;
            }

            if(error)
            {
                used = 0;
            } else
            if(used == 0)
            {
                showSearchMessage("No valid hex digits found in " + type + " string.");
            }
            result = new int[used];
            for(int i = 0; i < used; i++)
            {
                result[i] = nibbles[i];
            }

        } else
        {
            byte bytes[] = text.getBytes();
            result = new int[bytes.length * 2];
            int used = 0;
            for(int i = 0; i < bytes.length; i++)
            {
                result[used++] = bytes[i] >> 4 & 0xf;
                result[used++] = bytes[i] & 0xf;
            }

        }
        return result;
    }

    static void searchFindFirst()
    {
        searchFindNext(0);
    }

    static void searchFindNext()
    {
        searchFindNext(Math.max(textPanel.cursorDot, textPanel.cursorMark));
    }

    static void searchFindNext(int givenStart)
    {
        if(searchDialog == null)
        {
            showSearchMessage("There is no search string, no search dialog.");
            return;
        }
        String text = searchFindText.getText();
        if(text.length() == 0)
        {
            showSearchMessage("Empty strings are found everywhere.  (Joke.)");
            return;
        }
        int nibbles[] = searchConvertNibbles(text, "search");
        if(nibbles.length == 0)
        {
            return;
        }
        searchStatus.setText(" ");
        boolean byteFlag = searchByteBound.isSelected();
        boolean matchFlag = false;
        boolean nullFlag = searchIgnoreNulls.isSelected() && nibbles.length % 2 == 0;
        int start = givenStart;
        if(byteFlag)
        {
            start += start % 2;
        }
label0:
        for(; nibbleCount - start >= nibbles.length; start += byteFlag ? 2 : 1)
        {
            boolean differFlag;
            int dataIndex;
            int findIndex;
label1:
            {
                differFlag = false;
                int i = 0;
                do
                {
                    if(i >= nibbles.length)
                    {
                        break;
                    }
                    if(nibbles[i] != nibbleData.get(start + i))
                    {
                        differFlag = true;
                        break;
                    }
                    i++;
                } while(true);
                if(!differFlag)
                {
                    matchFlag = true;
                    textPanel.cursorMark = start;
                    textPanel.cursorDot = start + nibbles.length;
                    break label0;
                }
                if(!nullFlag || start % 2 != 0)
                {
                    continue;
                }
                dataIndex = start;
                differFlag = false;
                findIndex = 0;
                do
                {
                    if(dataIndex >= nibbleCount - 1 || findIndex >= nibbles.length - 1)
                    {
                        break label1;
                    }
                    int dataByte = nibbleData.get(dataIndex) << 4 | nibbleData.get(dataIndex + 1);
                    int findByte = nibbles[findIndex] << 4 | nibbles[findIndex + 1];
                    if(dataByte == findByte)
                    {
                        dataIndex += 2;
                        findIndex += 2;
                        continue;
                    }
                    if(dataByte != 0 || findIndex <= 0)
                    {
                        break;
                    }
                    dataIndex += 2;
                } while(true);
                differFlag = true;
            }
            if(differFlag || findIndex != nibbles.length)
            {
                continue;
            }
            matchFlag = true;
            textPanel.cursorMark = start;
            textPanel.cursorDot = dataIndex;
            break;
        }

        if(matchFlag)
        {
            textPanel.makeVisible(textPanel.cursorMark);
            textPanel.makeVisible(textPanel.cursorDot);
            textPanel.adjustScrollBar();
            textPanel.repaint();
        } else
        {
            showSearchMessage("Search string not found.");
        }
    }

    static void searchReplaceThis()
    {
        if(textPanel.cursorDot == textPanel.cursorMark)
        {
            showSearchMessage("There is no selection to replace.");
            return;
        }
        if(searchDialog == null)
        {
            showSearchMessage("There is no replacement string, no search dialog.");
            return;
        }
        String text = searchReplaceText.getText();
        if(text.length() == 0)
        {
            showSearchMessage("Replacement with an empty string is not supported.");
            return;
        }
        int nibbles[] = searchConvertNibbles(text, "replace");
        if(nibbles.length == 0)
        {
            return;
        } else
        {
            searchStatus.setText(" ");
            boolean oldFlag = overFlag;
            overFlag = false;
            pasteNibbles(nibbles, nibbles.length);
            overFlag = oldFlag;
            return;
        }
    }

    static void selectAll()
    {
        textPanel.cursorDot = nibbleCount;
        textPanel.cursorMark = 0;
        textPanel.repaint();
    }

    static void setClipboard(String text)
    {
        clipString = text;
        try
        {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents((Transferable)userActions, null);
        }
        catch(IllegalStateException ise)
        {
            JOptionPane.showMessageDialog(mainFrame, "Can't put text on clipboard:\n" + ise.getMessage());
        }
    }

    static void showEditMenu(Component invoker, int x, int y)
    {
        boolean content = nibbleCount > 0;
        boolean selection = textPanel.cursorDot != textPanel.cursorMark;
        menuCopyDump.setEnabled(selection);
        menuCopyHex.setEnabled(selection);
        menuCopyText.setEnabled(selection);
        menuDelete.setEnabled(selection);
        menuFind.setEnabled(content);
        menuNext.setEnabled(content);
        menuReplace.setEnabled(selection);
        menuSelect.setEnabled(content);
        menuPopup.show(invoker, x, y);
    }


    static void showSearchDialog()
    {
        if(searchDialog == null)
        {
            JPanel panel1 = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = 13;
            gbc.fill = 0;
            gbc.gridwidth = 1;
            panel1.add(new JLabel("Search for:"), gbc);
            panel1.add(Box.createHorizontalStrut(10), gbc);
            searchFindText = new JTextField("", 20);
            searchFindText.addActionListener(userActions);
            searchFindText.setMargin(TEXT_MARGINS);
            gbc.anchor = 17;
            gbc.fill = 2;
            gbc.gridwidth = 0;
            panel1.add(searchFindText, gbc);
            panel1.add(Box.createVerticalStrut(10), gbc);
            gbc.anchor = 13;
            gbc.fill = 0;
            gbc.gridwidth = 1;
            panel1.add(new JLabel("Replace with:"), gbc);
            panel1.add(Box.createHorizontalStrut(10), gbc);
            searchReplaceText = new JTextField("", 20);
            searchReplaceText.addActionListener(userActions);
            searchReplaceText.setMargin(TEXT_MARGINS);
            gbc.anchor = 17;
            gbc.fill = 2;
            gbc.gridwidth = 0;
            panel1.add(searchReplaceText, gbc);
            panel1.add(Box.createVerticalStrut(10), gbc);
            ButtonGroup group1 = new ButtonGroup();
            JPanel panel3 = new JPanel(new FlowLayout(1, 10, 0));
            searchIsHex = new JRadioButton("hex string", true);
            searchIsHex.setMnemonic(72);
            searchIsHex.setToolTipText("Search and replace strings are hexadecimal digits.");
            group1.add(searchIsHex);
            panel3.add(searchIsHex);
            searchIsText = new JRadioButton("text string", false);
            searchIsText.setMnemonic(84);
            searchIsText.addActionListener(userActions);
            searchIsText.setToolTipText("Search and replace strings are regular text.");
            group1.add(searchIsText);
            panel3.add(searchIsText);
            searchByteBound = new JCheckBox("byte boundary", false);
            searchByteBound.setMnemonic(66);
            searchByteBound.setToolTipText("Search starts on byte boundary.  Does not apply to replace.");
            panel3.add(searchByteBound);
            searchIgnoreNulls = new JCheckBox("ignore nulls", false);
            searchIgnoreNulls.setMnemonic(73);
            searchIgnoreNulls.setToolTipText("Ignore null data bytes between search bytes.");
            panel3.add(searchIgnoreNulls);
            gbc.anchor = 10;
            gbc.fill = 2;
            gbc.gridwidth = 0;
            panel1.add(panel3, gbc);
            panel1.add(Box.createVerticalStrut(10), gbc);
            searchStatus = new JLabel(" ", 0);
            gbc.anchor = 10;
            gbc.fill = 2;
            gbc.gridwidth = 0;
            panel1.add(searchStatus, gbc);
            panel1.add(Box.createVerticalStrut(16), gbc);
            JPanel panel4 = new JPanel(new FlowLayout(1, 25, 0));
            searchFindButton = new JButton("Find First");
            searchFindButton.addActionListener(userActions);
            searchFindButton.setMnemonic(70);
            searchFindButton.setToolTipText("Find first occurrence of search string in file.");
            panel4.add(searchFindButton);
            searchNextButton = new JButton("Find Next");
            searchNextButton.addActionListener(userActions);
            searchNextButton.setMnemonic(78);
            searchNextButton.setToolTipText("Find next occurrence of search string.");
            panel4.add(searchNextButton);
            searchReplaceButton = new JButton("Replace");
            searchReplaceButton.addActionListener(userActions);
            searchReplaceButton.setMnemonic(82);
            searchReplaceButton.setToolTipText("Replace current selection or previously found string.");
            panel4.add(searchReplaceButton);
            searchCloseButton = new JButton("Close");
            searchCloseButton.addActionListener(userActions);
            searchCloseButton.setMnemonic(67);
            searchCloseButton.setToolTipText("Close this dialog box.");
            panel4.add(searchCloseButton);
            gbc.anchor = 10;
            gbc.fill = 2;
            gbc.gridwidth = 0;
            panel1.add(panel4, gbc);
            JPanel panel5 = new JPanel(new FlowLayout(1, 0, 0));
            panel5.add(panel1);
            Box panel6 = Box.createVerticalBox();
            panel6.add(Box.createGlue());
            panel6.add(panel5);
            searchDialog = new JDialog((Frame)null, "Find or Replace");
            searchDialog.getContentPane().add(panel6, "Center");
            searchDialog.setLocation(200, 250);
            searchDialog.setSize(500, 250);
            searchDialog.validate();
        }
        searchStatus.setText(" ");
        if(!searchDialog.isVisible())
        {
            searchFindText.requestFocusInWindow();
        }
        searchDialog.setVisible(true);
    }

    static void showSearchMessage(String text)
    {
        if(searchDialog != null && searchDialog.isVisible())
        {
            searchStatus.setText(text);
            searchDialog.setVisible(true);
        } else
        {
            JOptionPane.showMessageDialog(mainFrame, text);
        }
    }

    static void userButton(ActionEvent event)
    {
        Object source = event.getSource();
        if(source == dumpWidthDialog)
        {
            dumpWidth = Integer.parseInt((String)dumpWidthDialog.getSelectedItem());
            textPanel.repaint();
        } else
        if(source == exitButton)
        {
        mainFrame.setVisible(false);
        } else
        if(source == fontNameDialog)
        {
            fontName = (String)fontNameDialog.getSelectedItem();
            textPanel.repaint();
        } else
        if(source == menuButton)
        {
            showEditMenu(menuButton, 0, menuButton.getHeight());
        } else
        if(source == menuCopyDump)
        {
            copyDump();
        } else
        if(source == menuCopyHex)
        {
            copyHex();
        } else
        if(source == menuCopyText)
        {
            copyText();
        } else
        if(source == menuDelete)
        {
            deleteSelected();
        } else
        if(source == menuFind)
        {
            showSearchDialog();
        } else
        if(source == menuNext)
        {
            searchFindNext();
        } else
        if(source == menuPasteHex)
        {
            pasteHex();
        } else
        if(source == menuPasteText)
        {
            pasteText();
        } else
        if(source == menuReplace)
        {
            searchReplaceThis();
        } else
        if(source == menuSelect)
        {
            selectAll();
        } else
        if(source == openButton)
        {
            openFile(null);
            textPanel.beginFile();
        } else
        if(source == overDialog)
        {
            overFlag = overDialog.isSelected();
            textPanel.repaint();
        } else
        if(source == saveButton)
        {
            saveFile();
        } else
        if(source == searchCloseButton)
        {
            searchDialog.setVisible(false);
        } else
        if(source == searchFindButton)
        {
            searchFindFirst();
        } else
        if(source == searchFindText)
        {
            searchFindNext();
        } else
        if(source == searchIsText)
        {
            if(searchIsText.isSelected())
            {
                searchByteBound.setSelected(true);
            }
        } else
        if(source == searchNextButton)
        {
            searchFindNext();
        } else
        if(source == searchReplaceButton)
        {
            searchReplaceThis();
        } else
        if(source == searchReplaceText)
        {
            searchReplaceThis();
        } else
        {
            System.err.println("Error in HexEdit1 userButton(): ActionEvent not recognized: " + event);
        }
    }

}
