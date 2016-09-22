package com.base.engine.editor;

import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InputWithInfo{
	
	private JLabel label;
	private JTextField textField;
	
	public InputWithInfo(String labelText, int textFieldColumns){
		label = new JLabel(labelText);
		textField = new JTextField("", textFieldColumns);
	}
	
	public void enable(){
		label.setVisible(true);
		textField.setVisible(true);
	}
	
	public void disable(){
		label.setVisible(false);
		textField.setVisible(false);
	}
	
	public void setLabelBounds(int x, int y){
		label.setBounds(x, y, 120, 20);
	}
	
	public void setTextFieldBounds(int x, int y){
		textField.setBounds(x, y, 80, 20);
	}
	
	public void setLabelText(String text){
		label.setText(text);
	}
	
	public void addActionListener(ActionListener al){
		textField.addActionListener(al);
	}
	
	public JTextField getTextField(){
		return textField;
	}
	
	public void clear(){
		textField.setText("");
	}
	
	public void add(JPanel panel){
		panel.add(label);
		panel.add(textField);
	}
}
