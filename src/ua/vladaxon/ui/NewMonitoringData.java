package ua.vladaxon.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ua.vladaxon.CorrectListener;
import ua.vladaxon.DateFormatListener;
import ua.vladaxon.NumberInputListener;
import ua.vladaxon.manager.ControlManager;
import ua.vladaxon.objects.BasicItem.Flag;
import ua.vladaxon.objects.Monitor;
import ua.vladaxon.ui.ColoredJTextField;
import ua.vladaxon.ui.PanelBuilder;

/**
 * Диалог создания новой записи мониторинга.
 */
public class NewMonitoringData extends JDialog{
	
	public NewMonitoringData(JFrame owner, final ControlManager manager){
		super(owner,true);
		this.owner = owner;
		JLabel mondatel = new JLabel(Monitor.getHeader(1));
		JLabel montempl = new JLabel(Monitor.getHeader(2));
		JLabel monlevl = new JLabel(Monitor.getHeader(3));
		Box labelbox = PanelBuilder.buildVerticalBox(11, mondatel, montempl, monlevl);
		day = new JComboBox<Integer>();
		month = new JComboBox<String>();
		year = new JComboBox<Integer>();
		datelistener = new DateFormatListener(day, month, year);
		Box datebox = Box.createHorizontalBox();
		datebox.add(day);
		datebox.add(Box.createHorizontalStrut(5));
		datebox.add(month);
		datebox.add(Box.createHorizontalStrut(5));
		datebox.add(year);
		Dimension combodim = new Dimension(Integer.MAX_VALUE, day.getPreferredSize().height-3);
		day.setMaximumSize(combodim);
		month.setMaximumSize(combodim);
		year.setMaximumSize(combodim);
		wtemp = new ColoredJTextField();
		monlevel = new ColoredJTextField();
		NumberInputListener nonneg = new NumberInputListener(monlevel, false, false);
		NumberInputListener tempinput = new NumberInputListener(wtemp, false, true);
		Dimension fielddim = new Dimension(Integer.MAX_VALUE, monlevl.getPreferredSize().height);
		wtemp.setMaximumSize(fielddim);
		monlevel.setMaximumSize(fielddim);
		Box textbox = PanelBuilder.buildVerticalBox(6,datebox,wtemp,monlevel);
		JButton okbtn = new JButton(ClientUI.oklabel, ClientUI.okicon);
		JButton cancelbtn = new JButton(ClientUI.cancellabel, ClientUI.cancelicon);
		totallistener = new CorrectListener(okbtn, nonneg, tempinput);
		Box buttonbox = PanelBuilder.buildHorizontalBox(okbtn, cancelbtn);
		Box upperbox = PanelBuilder.buildHorizontalBox(labelbox, textbox);
		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.Y_AXIS));
		mainpanel.add(upperbox);
		mainpanel.add(buttonbox);
		mainpanel.add(Box.createVerticalStrut(PanelBuilder.strutsize));
		mainpanel.setPreferredSize(new Dimension(320, mainpanel.getPreferredSize().height));
		okbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				createdmonitor = new Monitor(manager.getClientID(), datelistener.getDate(), 
						Integer.parseInt(wtemp.getText()), Integer.parseInt(monlevel.getText()), Flag.ADDED);
				NewMonitoringData.this.result = true;
				NewMonitoringData.this.setVisible(false);
			}
		});
		cancelbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NewMonitoringData.this.setVisible(false);	
			}
		});
		setTitle(titlename);
		setIconImage(ClientUI.monicon.getImage());
		setContentPane(mainpanel);
		pack();
		setResizable(false);
	}

	/**
	 * Отображает диалог создания записи.
	 * @return true - если пользователь нажал кнопку Готово(успешное завершение).
	 */
	public boolean showDialog() {
		result = false;
		monlevel.setText("");
		monlevel.setDefault();
		wtemp.setText("");
		wtemp.setDefault();
		createdmonitor = null;
		setLocationRelativeTo(owner);
		totallistener.checkCorrectness();
		setVisible(true);
		return result;
	}
	
	/**
	 * Возвращает созданный объект.
	 * @return Объект мониторинга.
	 */
	public Monitor getItem() {
		return createdmonitor;
	}
	
	/**Фрейм владелец диалогово окна*/
	private JFrame owner = null;
	/**Список для выбора дня*/
	private JComboBox<Integer> day = null;
	/**Список для выбора месяца*/
	private JComboBox<String> month = null;
	/**Список для выбора года*/
	private JComboBox<Integer> year = null;
	/**Поле для ввода температуры*/
	private ColoredJTextField wtemp = null;
	/**Поле для ввода уровня воды*/
	private ColoredJTextField monlevel = null;
	/**Объект созданной записи*/
	private Monitor createdmonitor = null;
	/**Слушатель ввода даты*/
	private DateFormatListener datelistener = null;
	/**Слушатель ввода*/
	private CorrectListener totallistener = null;
	/**Флаг результата завершения диалога*/
	private boolean result = false;
	/**Заголовок окна*/
	private static final String titlename = "Новые данные";
	private static final long serialVersionUID = 1L;

}