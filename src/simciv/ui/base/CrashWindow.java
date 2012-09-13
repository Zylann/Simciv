package simciv.ui.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This is a window displayed when a game crashes.
 * It says "sorry" and displays debug info for developpers.
 * @author Marc Gilleron
 *
 */
public class CrashWindow extends JFrame implements ClipboardOwner
{
	private static final long serialVersionUID = 1L;
	
	private JLabel label;
	private JTextArea errorDetails;
	private JButton quitButton;
	private JButton copyToClipboardButton;
	private JFrame gameWindowRef;
	
	public CrashWindow(JFrame gameWindowRef)
	{
		// Window options
		this.setTitle("The game has crashed :(");
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Content panel
        
        JPanel content = new JPanel();
        BorderLayout contentLayout = new BorderLayout();
        contentLayout.setHgap(10);
        contentLayout.setVgap(10);
        content.setLayout(contentLayout);
        content.setBackground(Color.lightGray);
        content.setBackground(Color.darkGray);
        setContentPane(content);
        
        // Text
		
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.LINE_AXIS));
        label = new JLabel();
		label.setText(
				"<html>Ooops ! It seems that the game has crashed. We apologize for the inconvenience :( " +
				" Please send us the following bug report and describe clearly what you where doing at the same time." +
				" This will help us to fix bugs :) </html>");
		label.setForeground(Color.white);
		label.setOpaque(true);
		label.setBackground(Color.gray);
		textPanel.add(label);
		content.add(textPanel, BorderLayout.NORTH);
		
		// Spacers
		
		JPanel leftSpacer = new JPanel();
		JPanel rightSpacer = new JPanel();
		leftSpacer.setBackground(Color.darkGray);
		rightSpacer.setBackground(Color.darkGray);
		content.add(leftSpacer, BorderLayout.EAST);		
		content.add(rightSpacer, BorderLayout.WEST);
		
		// Log
		
		errorDetails = new JTextArea();
		errorDetails.setEditable(false);
		errorDetails.setAlignmentX(LEFT_ALIGNMENT);
		errorDetails.setAlignmentY(TOP_ALIGNMENT);
		errorDetails.setLineWrap(true);
		JScrollPane scrollPane = new JScrollPane(errorDetails);
		content.add(scrollPane, BorderLayout.CENTER);
		
		// Buttons
		
		JPanel buttons = new JPanel();
		
		copyToClipboardButton = new JButton("Copy contents to clipoard");
		copyToClipboardButton.addActionListener(new CopyLogToClipboardAction());
		buttons.add(copyToClipboardButton);
		
		quitButton = new JButton("Close");
		quitButton.addActionListener(new CloseAction());		
		buttons.add(quitButton);
		
		buttons.setBackground(Color.darkGray);
		content.add(buttons, BorderLayout.SOUTH);

		// Test
		String sampleText = "";
		for(int i = 0; i < 50; i++)
		{
			sampleText += "This is a sample text with a counter at " + i + "\n";
		}
		setErrorDetails(sampleText);
	}
	
	public void setErrorDetails(String str)
	{
		errorDetails.setText(str);
	}
	
	private void copyLogToClipboard()
	{
		StringSelection stringSel = new StringSelection(errorDetails.getText());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSel, this);
	}
	
	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1)
	{
		// Nothing (inherited from ClipboardOwner)
	}
	
	class CopyLogToClipboardAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			copyLogToClipboard();
		}
	}
	
	class CloseAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
			if(gameWindowRef != null)
				gameWindowRef.dispose();
		}		
	}

}


