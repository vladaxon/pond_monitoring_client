package ua.vladaxon.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import ua.vladaxon.CorrectListener;
import ua.vladaxon.NumberInputListener;
import ua.vladaxon.manager.ControlManager;
import ua.vladaxon.objects.Pond;

/**
 * ������� ����������
 */
public class ControlTab extends JPanel{

	public ControlTab(ControlManager manager){
		this.manager = manager;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		tab = new JTable(manager.getTableModel());
		JScrollPane scroll = new JScrollPane(tab);
		add(scroll);
		add(createRightPanel());
	}
	
	/**
	 * ��������� �������.
	 */
	public void refreshTab(){
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				tab.revalidate();
				tab.repaint();
				Pond p = manager.getPond();
				for(int i=0; i<fields.length; i++){
					fields[i].setText(p.getValue(i).toString());
				}
			}
		});
	}
	
	/**
	 * ������� ������� �� ������.
	 */
	public void clearTab(){
		tab.revalidate();
		tab.repaint();
		for(JTextField field: fields){
			field.setText("");
		}
	}
	
	/**
	 * ���������� �������� �����.
	 * @param index ������ ����.
	 * @return �������� � ���� �����.
	 */
	public String getFieldText(int index){
		return fields[index].getText();
	}
	
	/**
	 * ������������ ������ ����� �������.
	 * @return ������ � ������ ������ � ��������.
	 */
	private JPanel createRightPanel(){
		JPanel rightpanel = new JPanel();
		rightpanel.setLayout(new BoxLayout(rightpanel, BoxLayout.Y_AXIS));
		//����� ��������� � ���������� �������
		JLabel[] labels = new JLabel[Pond.getColumnCount()];
		for(int i=0; i<Pond.getColumnCount(); i++){
			labels[i] = new JLabel(Pond.getHeader(i)+":");
		}
		Box labelbox = PanelBuilder.buildVerticalBox(9, labels);
		//��������� ����
		fields[0] = new JTextField();
		fields[1] = new JTextField();
		fields[2] = new ColoredJTextField();
		fields[3] = new JTextField();
		fields[4] = new ColoredJTextField();
		fields[5] = new ColoredJTextField();
		fields[6] = new JTextField();
		fields[7] = new JTextField();
		fields[8] = new JTextField();
		fields[9] = new JTextField();
		Dimension fielddimension = new Dimension(Integer.MAX_VALUE, fields[0].getPreferredSize().height);
		for(JTextField field: fields){
			field.setMaximumSize(fielddimension);
		}
		fields[0].setEditable(false);
		fields[8].setEditable(false);
		fields[9].setEditable(false);
		Box textfieldbox = PanelBuilder.buildVerticalBox(5,fields);
		//������� ������
		Box upperbox = PanelBuilder.buildHorizontalBox(labelbox, textfieldbox);
		upperbox.setAlignmentY(Component.TOP_ALIGNMENT);
		//������ � ��������� ������
		JButton savebtn = new JButton("���������", ClientUI.okicon);
		JButton addbtn = new JButton("�������� ������", ClientUI.addicon);
		addbtn.addActionListener(manager.getAddlistener());
		savebtn.addActionListener(manager.getSavelistener());
		Box buttonbox = PanelBuilder.buildHorizontalBox(addbtn, savebtn);
		//���������
		NumberInputListener areainput = 
				new NumberInputListener((ColoredJTextField) fields[2], true, false);
		NumberInputListener fishspotinput = 
				new NumberInputListener((ColoredJTextField) fields[4], true, false);
		NumberInputListener fishcostinput = 
				new NumberInputListener((ColoredJTextField) fields[5], true, false);
		new CorrectListener(savebtn, areainput, fishspotinput, fishcostinput);
		//������� ������
		rightpanel.add(upperbox);
		rightpanel.add(buttonbox);
		rightpanel.setPreferredSize(new Dimension(300,rightpanel.getPreferredSize().height));
		return rightpanel;
	}
	
	/**������ ����� �����*/
	private JTextField[] fields = new JTextField[Pond.getColumnCount()];
	/**������� ������� �����������*/
	private JTable tab;
	/**�������� ����������*/
	private ControlManager manager;
	private static final long serialVersionUID = 1L;

}