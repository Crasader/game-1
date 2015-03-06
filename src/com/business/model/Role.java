package com.business.model;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import net.sf.json.JSONString;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.common.util.FactoryUtil;
import com.common.util.Log;
import com.common.util.SQLUtils;
import com.netty.config.ConnectionLevel;
import com.netty.exception.BusinessException;
import com.netty.model.Message;
import com.netty.model.MessageType;
import com.server.model.Server;

/**
 * 
 * @author mc ��ɫ
 */
public class Role implements Comparable<Role>, JSONString {

	private static List<String> names;

	public static final String saveAll = "saveAll";

	public static final String clear = "clear";

	public static final String updateGameTop = "updateGameTop";

	public static final String exitRoom = "exitRoom";

	@SuppressWarnings("unchecked")
	public static void loadNames() throws BusinessException {
		String xmlName = "name";
		String xmlPath = System.getProperty("user.dir") + "/res/" + xmlName
				+ ".xml";
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(new File(xmlPath));
			Element rootElt = doc.getRootElement();
			List<Element> names = rootElt.elements("e");
			Role.names = FactoryUtil.newList(names.size());
			for (Element e : names) {
				Role.names.add(e.attributeValue("name"));
			}
		} catch (Exception e) {
			throw new BusinessException("��ȡ����������ʧ��");
		}
		Log.info("��ȡ���������֣� " + names.size());
	}

	private User user;

	private Integer uid;

	private String name; // ����

	private int xinNum; // ����

	private int huaNum; // �ʻ�

	private String roomId; // ����ID

	private int isMachine = 0; // �Ƿ��ǻ�����

	private int isFengHao = 0; // 1���

	private int isJinYan = 0; // 1����

	private int isAdmin = 0; // 1����Ա

	public Role(User user) throws BusinessException {
		uid = user.getUid();
		init();
		this.user = user;
	}

	public void init() throws BusinessException {

		CachedRowSet result = null;

		try {
			result = SQLUtils
					.executeQuery(
							new StringBuffer(
									"select name,xinNum,huaNum,isadmin,isJinYan,isFengHao from role where uid=")
									.append(uid), ConnectionLevel.now);

			boolean find = false;

			while (result.next()) {
				find = true;
				this.name = result.getString("name");
				this.xinNum = result.getInt("xinNum");
				this.huaNum = result.getInt("huaNum");
				this.isAdmin = result.getInt("isAdmin");
				this.isJinYan = result.getInt("isJinYan");
				this.isFengHao = result.getInt("isFengHao");
			}

			if (!find) {
				throw new BusinessException("��ȡ��ɫ��Ϣʧ��");
			}
			if (this.isFengHao == 1) {
				Server.blackListID.add(uid);
				throw new BusinessException("�����˺��ѱ������");
			}

		} catch (Exception e) {
			if (e instanceof BusinessException) {
				throw (BusinessException) e;
			}
			throw new BusinessException("��ȡ��ɫ��Ϣʧ��");
		}

	}

	/**
	 * ���½�ɫ����������Ϣ
	 */
	public void updateGameTop() {
		// TODO
	}

	/**
	 * ���½�ɫ������Ϣ��DB
	 */
	public void saveAll() {

		// TODO
	}
	
	public void clear(){
		//TODO
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public int getXinNum() {
		return xinNum;
	}

	public void setXinNum(int xinNum) {
		this.xinNum = xinNum;
	}

	public int getHuaNum() {
		return huaNum;
	}

	public void setHuaNum(int huaNum) {
		this.huaNum = huaNum;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public int getIsFengHao() {
		return isFengHao;
	}

	public void setIsFengHao(int isFengHao) {
		this.isFengHao = isFengHao;
	}

	public int getIsJinYan() {
		return isJinYan;
	}

	public void setIsJinYan(int isJinYan) {
		this.isJinYan = isJinYan;
	}

	public int getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int compareTo(Role role) {

		return 0;// TODO
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Message getInfo(MessageType type) {

		return null;// TODO

	}

	public int getIsMachine() {
		return isMachine;
	}

	public void setIsMachine(int isMachine) {
		this.isMachine = isMachine;
	}

	@Override
	public String toJSONString() {
		return null;// TODO
	}

	public void exitRoom() {
		if (user == null || user.getRoom() == null)
			return;
		Iterator<Role> rolelist = user.getRoom().getRoleList().iterator();
		while (rolelist.hasNext()) {
			if (rolelist.next() == this)
				rolelist.remove();
		}
		Server.allRole.remove(this.getUid());
	}

}
