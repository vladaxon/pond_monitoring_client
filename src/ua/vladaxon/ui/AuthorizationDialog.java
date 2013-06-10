package ua.vladaxon.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ua.vladaxon.Client;
import ua.vladaxon.CorrectListener;
import ua.vladaxon.UsrAuthDocListener;
import ua.vladaxon.objects.UserData;

/**
 * Класс, конструирующий окно авторизации пользователя.
 * Необходим для получения от пользователя логина и пароля подключения к серверу.
 */
public class AuthorizationDialog extends JDialog{
	
	public AuthorizationDialog(JFrame owner){
		super(owner,true);
		this.owner = owner;
		JLabel loglabel = new JLabel(loginlabel);
		JLabel passlabel = new JLabel(passwordlabel);
		Box labelbox = PanelBuilder.buildVerticalBox(labelstrutsize, loglabel, passlabel);
		loginfield = new ColoredJTextField();
		UsrAuthDocListener loginlistener = new UsrAuthDocListener(loginfield);
		Dimension fielddim = new Dimension(Integer.MAX_VALUE, loginfield.getPreferredSize().height);
		loginfield.setMaximumSize(fielddim);
		passwfield = new ColoredJPasswordField();
		UsrAuthDocListener passwlistener = new UsrAuthDocListener(passwfield);
		passwfield.setMaximumSize(fielddim);
		Box taxtfieldbox = PanelBuilder.buildVerticalBox(textfstrutsize, loginfield, passwfield);
		Box upperbox = PanelBuilder.buildHorizontalBox(labelbox, taxtfieldbox);
		JButton okbtn = new JButton(ClientUI.oklabel, ClientUI.okicon);
		JButton cancbtn = new JButton(ClientUI.cancellabel, ClientUI.cancelicon);
		Box buttonbox = PanelBuilder.buildHorizontalBox(okbtn, cancbtn);
		totallistener = new CorrectListener(okbtn, loginlistener, passwlistener);
		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.Y_AXIS));
		mainpanel.add(upperbox);
		mainpanel.add(buttonbox);
		mainpanel.add(Box.createVerticalStrut(PanelBuilder.strutsize));
		mainpanel.setPreferredSize(new Dimension(250, mainpanel.getPreferredSize().height));
		okbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				login = loginfield.getText();
				password = new String(passwfield.getPassword());
				AuthorizationDialog.result = true;
				AuthorizationDialog.this.setVisible(false);	
			}
		});
		cancbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AuthorizationDialog.this.setVisible(false);
			}
		});
		this.setTitle(titlename);
		this.setIconImage(connicon.getImage());
		this.setContentPane(mainpanel);
		this.pack();
		this.setResizable(false);
	}
	
	/**
	 * Метод, вызывающий отображение диалога.
	 * @param owner Фрейм владелец окна.
	 * @return <b>true</b> - если логин и пароль были введены.
	 */
	public boolean showDialog(){
		result = false;
		this.loginfield.setText("");
		this.passwfield.setText("");
		this.login = null;
		this.password = null;
		totallistener.checkCorrectness();
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
		return result;
	}
	
	/**
	 * Возвращает введенные данные пользователя.
	 * @return Данные пользователя для авторизации.
	 */
	public UserData getUserData(){
		return new UserData(this.login, this.password.hashCode());
	}
	
	/**Слушатель ввода*/
	private CorrectListener totallistener = null;
	/**Фрейм владелец диалогового окна*/
	private JFrame owner = null;
	/**Введенный логин*/
	private String login = null;
	/**Введенный пароль*/
	private String password = null;
	/**Поле ввода логина*/
	private ColoredJTextField loginfield = null;
	/**Поле ввода пароля*/
	private ColoredJPasswordField passwfield = null;
	/**Результат завершения диалога*/
	private static boolean result = false;
	/**Размер распорки диспечера компоновки*/
	public static final int labelstrutsize = 7;
	/**Размер распорки диспечера компоновки*/
	public static final int textfstrutsize = 5;
	/**Заголовок окна*/
	private static final String titlename = "Авторизация";
	/**Метка логина*/
	private static final String loginlabel = "Логин:";
	/**Метка пароля*/
	private static final String passwordlabel = "Пароль:";
	/**Иконка действия*/
	public static final ImageIcon connicon = 
			new ImageIcon(Client.class.getResource("/ua/vladaxon/res/connect.png"));
	private static final long serialVersionUID = 1L;

}