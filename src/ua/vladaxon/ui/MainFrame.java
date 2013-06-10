package ua.vladaxon.ui;

import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Класс главного окна клиентского приложения.
 */
public class MainFrame extends JFrame{
	
	public MainFrame(JTabbedPane pane, WindowListener listener){
		super(title);
		setContentPane(pane);
		addWindowListener(listener);
		setSize(900, 400);
		setResizable(false);
		setLocationRelativeTo(null);
	}

	/**Заголовок окна*/
	private static String title = "Pond Monitoring Client";
	private static final long serialVersionUID = 1L;

}