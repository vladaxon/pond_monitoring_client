package ua.vladaxon.xml;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import ua.vladaxon.PropManager;
import ua.vladaxon.manager.ControlManager;
import ua.vladaxon.manager.MonitorManager;
import ua.vladaxon.manager.PondManager;
import ua.vladaxon.objects.ControlData;
import ua.vladaxon.objects.Monitor;
import ua.vladaxon.objects.Pond;
import ua.vladaxon.objects.UserData;

/**
 * Класс представителя сервера. Обеспечивает связь клиента с сервером, посредством xml документов.
 */
public class ServerProxy {
	
	public ServerProxy(PropManager props){
		serverurl = props.getServerURL();
		serverport = props.getServerPort();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setValidating(true);
		domFactory.setIgnoringElementContentWhitespace(true);
		try {
			docbuilder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"client.dtd");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		} catch (TransformerFactoryConfigurationError | TransformerConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Производит подключение к серверу и проверку соединения в присланном документе.
	 * @return true - если соединение с сервером установлено.
	 */
	public boolean connect() {
		try {
			serversocket = new Socket(serverurl, serverport);
			in = new XMLInputStream(serversocket.getInputStream());
			out = new XMLOutputStream(serversocket.getOutputStream());
			docstream = new StreamResult(out);
			Document doc = receive();
			isconnected = checkConnection(doc);
			return isconnected;
		} catch (IOException e) {
			try {
				serversocket.close();
			} catch (Exception e1) {}
			return false;
		}
	}
	
	/**
	 * Отправляет уведомление об отключении от сервера и отключает клиент.
	 */
	public void disconnect(){
		if (isconnected) {
			Document discdoc = formDisconnectDocument();
			send(discdoc);
			try {
				serversocket.close();
				serversocket = null;
				in = null;
				out = null;
				docstream = null;
				isconnected = false;
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Метод, отправляющий запрос на получение всех записей ставков.
	 * @param manager Менеджер ставков.
	 */
	public void getAllPonds(final PondManager manager){
		pool.submit(new Runnable() {
			@Override
			public void run() {
				if(!isconnected){
					connect();
				}
				if(isconnected){
					Document pond = formRequest(RequestType.POND);
					send(pond);
					Document result = receive();
					manager.setList(parsePond(result));
				}
			}
		});
	}
	
	/**
	 * Метод, отправляющий запрос на получение всех записей мониторинга.
	 * @param manager Менеджер мониторинга.
	 */
	public void getAllMonitors(final MonitorManager manager){
		pool.submit(new Runnable() {
			@Override
			public void run() {
				if(!isconnected){
					connect();
				}
				if(isconnected){
					Document monitor = formRequest(RequestType.MONITOR);
					send(monitor);
					Document result = receive();
					manager.setList(parseMonitors(result));
				}
			}
		});
	}
	
	/**
	 * Метод, отправляющий запрос на получение данных данного пользователя.
	 * @param manager Менеджер управления.
	 * @param user Данные пользователя.
	 */
	public void getControlData(final ControlManager manager, final UserData user){
		pool.submit(new Runnable() {
			@Override
			public void run() {
				if(!isconnected){
					connect();
				}
				if(isconnected){
					Document idrequest = formAuthorization(user);
					send(idrequest);
					Document idresponse = receive();
					int userid = parseUserID(idresponse);
					if(userid==-1){
						manager.setControlData(null);
					} else {
						Document pondrequest = formRequest(RequestType.POND);
						send(pondrequest);
						Document pondresponse = receive();
						List<Pond> pondlist = parsePond(pondresponse);
						Document monitorrequest = formRequest(RequestType.MONITOR);
						send(monitorrequest);
						Document monitorresponse = receive();
						List<Monitor> monitorlist = parseMonitors(monitorresponse);
						manager.setControlData(new ControlData(userid, pondlist, monitorlist));
					}
				}
				
			}
		});
	}
	
	/**
	 * Метод выполняющий обновление данных пользователя.
	 * @param manager Менеджер управления, для получения уведомления.
	 * @param user Данные пользователя.
	 * @param pond Объект ставка.
	 * @param list Список мониторинга.
	 */
	public void executeUpdate(final ControlManager manager, final UserData user, final Pond pond, final List<Monitor> list){
		pool.submit(new Runnable() {
			@Override
			public void run() {
				if(!isconnected){
					connect();
				}
				if(isconnected){
					Document pondreq = formUpdate(pond, user);
					send(pondreq);
					Document pondresp = receive();
					if(list.size()!=0){
						Document monitorreq = formUpdate(list, user);
						send(monitorreq);
						Document monitorresp = receive();
						manager.updateExecuted(checkResponseState(pondresp) && checkResponseState(monitorresp));
					} else {
						manager.updateExecuted(checkResponseState(pondresp));
					}
				}	
			}
		});
	}
	
	/**
	 * Разбирает документ со списком записей ставков.
	 * @param result Документ содержащий данные.
	 * @return Список записей ставков.
	 */
	private List<Pond> parsePond(Document result) {
		List<Pond> list = new ArrayList<Pond>();
		try {
			if(checkResponseState(result)){
				NodeList items = (NodeList) xpath.evaluate(REQUSETPATH, result, XPathConstants.NODESET);
				for(int i=0; i<items.getLength(); i++){
					try {
						Pond pond = new Pond((Element) items.item(i));
						list.add(pond);
					} catch (Exception e) {}
				}
			}
		} catch (XPathExpressionException e) {
		}
		return list;
	}
	
	/**
	 * Разбирает документ со списком записей мониторинга.
	 * @param result Документ содержащий данные.
	 * @return Список записей мониторинга.
	 */
	private List<Monitor> parseMonitors(Document result) {
		List<Monitor> list = new ArrayList<Monitor>();
		try {
			if(checkResponseState(result)){
				NodeList items = (NodeList) xpath.evaluate(MONITORSPATH, result, XPathConstants.NODESET);
				for(int i=0; i<items.getLength(); i++){
					try {
						Monitor monitor = new Monitor((Element) items.item(i));
						list.add(monitor);
					} catch (Exception e) {}
				}
			}
		} catch (XPathExpressionException e) {
		}
		return list;
	}
	
	/**
	 * Метод, разбирающий документ с номером пользователя.
	 * @param result Документ, содержащий данные.
	 * @return Номер пользователя или -1 в противном случае
	 */
	private int parseUserID(Document result){
		try {
			if(checkResponseState(result)){
				int id = Integer.parseInt(xpath.evaluate(USERIDPATH, result));
				return id;
			}
			return -1;
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * Формирует документ об отключении от севрера.
	 * @return Документ отключения
	 */
	private Document formDisconnectDocument(){
		Document discdoc = docbuilder.newDocument();
		Element rootelem = discdoc.createElement(ROOTNAME);
		discdoc.appendChild(rootelem);
		rootelem.setAttribute(ROOTVERSIONATTR, ROOTVERSION);
		Element dataelement = discdoc.createElement(DATAELEMENT);
		rootelem.appendChild(dataelement);
		Element connectionelem = discdoc.createElement(CONNECTIONELEMENT);
		dataelement.appendChild(connectionelem);
		Text connstatus = discdoc.createTextNode(Response.REJECT.toString());
		connectionelem.appendChild(connstatus);
		return discdoc;
	}
	
	/**
	 * Формирует основу документа запроса и для отправки серверу.
	 * Может формировать готовые запросы для получения списка ставков и мониторинга.
	 * Для формирования запроса на получение номера пользователя или обновлении
	 * нужно выполнить вставку элементов логина и пароля.
	 * @param type Тип запроса
	 * @return Документ для отправки серверу.
	 */
	private Document formRequest(RequestType type){
		Document discdoc = docbuilder.newDocument();
		Element rootelem = discdoc.createElement(ROOTNAME);
		discdoc.appendChild(rootelem);
		rootelem.setAttribute(ROOTVERSIONATTR, ROOTVERSION);
		Element dataelement = discdoc.createElement(DATAELEMENT);
		rootelem.appendChild(dataelement);
		Element connectionelem = discdoc.createElement(CONNECTIONELEMENT);
		dataelement.appendChild(connectionelem);
		Text connstatus = discdoc.createTextNode(Response.ACCEPT.toString());
		connectionelem.appendChild(connstatus);
		Element requestelem = discdoc.createElement(REQUESTELEMENT);
		dataelement.appendChild(requestelem);
		Element subjectelem = discdoc.createElement(SUBJECTELEMENT);
		requestelem.appendChild(subjectelem);
		Text subjtype = discdoc.createTextNode(type.toString());
		subjectelem.appendChild(subjtype);
		return discdoc;
	}
	
	/**
	 * Формирует документ для получения ID пользователя.
	 * @param user Данные пользователя
	 * @return Документ для запроса авторизации.
	 */
	private Document formAuthorization(UserData user){
		Document doc = formRequest(RequestType.AUTHORIZATION);
		return insertAuthorization(doc, user);
	}
	
	/**
	 * Формирует документ для обновления записей ставка.
	 * @param pond Объект ставка.
	 * @param user Данные пользователя.
	 * @return Документ для обновления записи ставка.
	 */
	private Document formUpdate(Pond pond, UserData user){
		Document doc = formRequest(RequestType.UPDATE);
		doc = insertAuthorization(doc, user);
		Element itemselement = doc.createElement(ITEMSELEMENT);
		itemselement.appendChild(pond.getElement(doc));
		Element root;
		try {
			root = (Element) xpath.evaluate(CLIENTROOTPATH, doc, XPathConstants.NODE);
			root.appendChild(itemselement);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * Формирует документ для обновления записей мониторинга.
	 * @param list Список записей мониторинга.
	 * @param user Данные пользователя.
	 * @return Документ для обновления записей мониторинга.
	 */
	private Document formUpdate(List<Monitor> list, UserData user){
		Document doc = formRequest(RequestType.UPDATE);
		doc = insertAuthorization(doc, user);
		Element itemselement = doc.createElement(ITEMSELEMENT);
		for(Monitor m: list){
			itemselement.appendChild(m.getElement(doc));
		}
		Element root;
		try {
			root = (Element) xpath.evaluate(CLIENTROOTPATH, doc, XPathConstants.NODE);
			root.appendChild(itemselement);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * Вставляет в документ данные о пользователе. Метод необходим для формирования
	 * запроса о получении ID пользователя и для обновления данных.
	 * @param doc Документ в который вставляются данные.
	 * @param user Данные пользователя
	 * @return Документ с данными пользователя.
	 */
	private Document insertAuthorization(Document doc, UserData user){
		try {
			Element reqestelement = (Element) xpath.evaluate(REQUESTPATH, doc, XPathConstants.NODE);
			Element usernameel = doc.createElement(USERNAMEELEMENT);
			Text usrnametext = doc.createTextNode(user.getUsername());
			usernameel.appendChild(usrnametext);
			Element passwordel = doc.createElement(PASSWORDELEMENT);
			Text passwrdtext = doc.createTextNode(user.getPassword()+"");
			passwordel.appendChild(passwrdtext);
			reqestelement.appendChild(usernameel);
			reqestelement.appendChild(passwordel);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * Отправляет указанный документ серверу.
	 * @param doc Документ для отправки
	 * @return true - если документ был отправлен успешно
	 */
	private boolean send(Document doc){
		try {
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, docstream);
			out.send();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Принимает документ из входящего потока.
	 * @return Возвращает документ или null если не удалось загрузить документ.
	 */
	private Document receive(){
		try {
			in.receive();
			Document doc = docbuilder.parse(in);
			return doc;
		} catch (IOException | SAXException e) {
			return null;
		}
	}
	
	/**
	 * Проверяет состояние соединения от сервера.
	 * @param doc Документ для считывания состояния
	 * @return true - если сервер поддерживает подключение
	 */
	private boolean checkConnection(Document doc){
		try {
			String connstr = xpath.evaluate(CONNECTIONPATH, doc);
			return connstr.equals(Response.ACCEPT.toString());
		} catch (XPathExpressionException e) {
			return false;
		}
	}
	
	/**
	 * Проверяет состояние выполнения запроса сервером.
	 * Считывает значение state в ответе сервера.
	 * @param response Документ, содержащий ответ
	 * @return true - если запрос выполнен
	 */
	private boolean checkResponseState(Document response){
		try {
			return "true".equals(xpath.evaluate(RESPONSEPATH, response));
		} catch (XPathExpressionException e) {
			return false;
		}
	}
	
	/**Поток документа*/
	private StreamResult docstream;
	/**Пул потока для выполнения запросов к серверу*/
	private ExecutorService pool = Executors.newSingleThreadExecutor();
	/**Сокет соединяющий с сервером*/
	private Socket serversocket;
	/**Строитель документов*/
	private DocumentBuilder docbuilder;
	/**Объект трансформера, для отправки документов*/
	private Transformer transformer;
	/**Входящий поток для получения*/
	private XMLInputStream in;
	/**Исходящий поток для отправки*/
	private XMLOutputStream out;
	/**Объект XPath для обработки документов*/
	private XPath xpath = XPathFactory.newInstance().newXPath();
	/**Адрес сервера*/
	private String serverurl;
	/**Порт сервера*/
	private int serverport;
	/**Флаг соединения*/
	private boolean isconnected = false;
	/**Нумерация типов соединений*/
	public static enum Response {ACCEPT, REJECT};
	public static enum RequestType {POND,MONITOR,UPDATE,AUTHORIZATION};
	//XPath
	private static final String CLIENTROOTPATH = "/client";
	private static final String CONNECTIONPATH = "/server/data/connection";
	private static final String RESPONSEPATH = "/server/data/response/state";
	private static final String MONITORSPATH = "/server/items/monitor";
	private static final String REQUSETPATH = "/server/items/pond";
	private static final String REQUESTPATH = "/client/data/request";
	private static final String USERIDPATH = "/server/data/response/pid";
	//Elements
	private static final String ROOTNAME = "client";
	private static final String ROOTVERSIONATTR = "version";
	private static final String DATAELEMENT = "data";
	private static final String CONNECTIONELEMENT = "connection";
	private static final String REQUESTELEMENT = "request";
	private static final String SUBJECTELEMENT = "subject";
	private static final String USERNAMEELEMENT = "username";
	private static final String PASSWORDELEMENT = "password";
	private static final String ITEMSELEMENT = "items";
	/**Версия обработчика документа*/
	private static String ROOTVERSION = "1";
	
}