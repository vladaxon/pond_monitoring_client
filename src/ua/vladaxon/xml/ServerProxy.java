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
 * ����� ������������� �������. ������������ ����� ������� � ��������, ����������� xml ����������.
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
	 * ���������� ����������� � ������� � �������� ���������� � ���������� ���������.
	 * @return true - ���� ���������� � �������� �����������.
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
	 * ���������� ����������� �� ���������� �� ������� � ��������� ������.
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
	 * �����, ������������ ������ �� ��������� ���� ������� �������.
	 * @param manager �������� �������.
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
	 * �����, ������������ ������ �� ��������� ���� ������� �����������.
	 * @param manager �������� �����������.
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
	 * �����, ������������ ������ �� ��������� ������ ������� ������������.
	 * @param manager �������� ����������.
	 * @param user ������ ������������.
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
	 * ����� ����������� ���������� ������ ������������.
	 * @param manager �������� ����������, ��� ��������� �����������.
	 * @param user ������ ������������.
	 * @param pond ������ ������.
	 * @param list ������ �����������.
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
	 * ��������� �������� �� ������� ������� �������.
	 * @param result �������� ���������� ������.
	 * @return ������ ������� �������.
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
	 * ��������� �������� �� ������� ������� �����������.
	 * @param result �������� ���������� ������.
	 * @return ������ ������� �����������.
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
	 * �����, ����������� �������� � ������� ������������.
	 * @param result ��������, ���������� ������.
	 * @return ����� ������������ ��� -1 � ��������� ������
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
	 * ��������� �������� �� ���������� �� �������.
	 * @return �������� ����������
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
	 * ��������� ������ ��������� ������� � ��� �������� �������.
	 * ����� ����������� ������� ������� ��� ��������� ������ ������� � �����������.
	 * ��� ������������ ������� �� ��������� ������ ������������ ��� ����������
	 * ����� ��������� ������� ��������� ������ � ������.
	 * @param type ��� �������
	 * @return �������� ��� �������� �������.
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
	 * ��������� �������� ��� ��������� ID ������������.
	 * @param user ������ ������������
	 * @return �������� ��� ������� �����������.
	 */
	private Document formAuthorization(UserData user){
		Document doc = formRequest(RequestType.AUTHORIZATION);
		return insertAuthorization(doc, user);
	}
	
	/**
	 * ��������� �������� ��� ���������� ������� ������.
	 * @param pond ������ ������.
	 * @param user ������ ������������.
	 * @return �������� ��� ���������� ������ ������.
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
	 * ��������� �������� ��� ���������� ������� �����������.
	 * @param list ������ ������� �����������.
	 * @param user ������ ������������.
	 * @return �������� ��� ���������� ������� �����������.
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
	 * ��������� � �������� ������ � ������������. ����� ��������� ��� ������������
	 * ������� � ��������� ID ������������ � ��� ���������� ������.
	 * @param doc �������� � ������� ����������� ������.
	 * @param user ������ ������������
	 * @return �������� � ������� ������������.
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
	 * ���������� ��������� �������� �������.
	 * @param doc �������� ��� ��������
	 * @return true - ���� �������� ��� ��������� �������
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
	 * ��������� �������� �� ��������� ������.
	 * @return ���������� �������� ��� null ���� �� ������� ��������� ��������.
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
	 * ��������� ��������� ���������� �� �������.
	 * @param doc �������� ��� ���������� ���������
	 * @return true - ���� ������ ������������ �����������
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
	 * ��������� ��������� ���������� ������� ��������.
	 * ��������� �������� state � ������ �������.
	 * @param response ��������, ���������� �����
	 * @return true - ���� ������ ��������
	 */
	private boolean checkResponseState(Document response){
		try {
			return "true".equals(xpath.evaluate(RESPONSEPATH, response));
		} catch (XPathExpressionException e) {
			return false;
		}
	}
	
	/**����� ���������*/
	private StreamResult docstream;
	/**��� ������ ��� ���������� �������� � �������*/
	private ExecutorService pool = Executors.newSingleThreadExecutor();
	/**����� ����������� � ��������*/
	private Socket serversocket;
	/**��������� ����������*/
	private DocumentBuilder docbuilder;
	/**������ ������������, ��� �������� ����������*/
	private Transformer transformer;
	/**�������� ����� ��� ���������*/
	private XMLInputStream in;
	/**��������� ����� ��� ��������*/
	private XMLOutputStream out;
	/**������ XPath ��� ��������� ����������*/
	private XPath xpath = XPathFactory.newInstance().newXPath();
	/**����� �������*/
	private String serverurl;
	/**���� �������*/
	private int serverport;
	/**���� ����������*/
	private boolean isconnected = false;
	/**��������� ����� ����������*/
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
	/**������ ����������� ���������*/
	private static String ROOTVERSION = "1";
	
}