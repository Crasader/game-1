package com.business.server.impl;

import com.business.model.Role;
import com.business.model.Room;
import com.business.model.User;
import com.business.server.BusinessServer;
import com.common.util.BusinessUtil;
import com.common.util.FormatterUtil;
import com.common.util.Log;
import com.netty.exception.BusinessException;
import com.netty.model.Message;
import com.netty.model.MessageType;
import com.server.model.Server;

public class LoginGameAction extends BusinessServer {

	@Override
	public void doMsg(User user, Message msg) throws BusinessException {

		if (user.getRole() != null) {
			throw new BusinessException("��������");
		}

		String roomId = get(msg, "gameType");

		Room room = null;
		if (roomId != null) {
			room = Server.allRoom.get(roomId);
		}

		if (room == null || !room.isOpen()) {
			throw new BusinessException("���䲻���ڻ�δ����!");
		}

		Integer uid = FormatterUtil.toInteger(get(msg, "uid"));

		if (uid == null) {
			throw new BusinessException("�û�IDΪ�գ���¼ʧ��");
		}

		BusinessUtil.loginCheck(uid, get(msg, "pass"));

		if (Server.blackListID.contains(uid)) {
			throw new BusinessException("��Ǹ������˺��ѱ����");
		}

		user.setUid(uid);

		User oldUser = Server.allUser.get(uid);

		if (oldUser != null) {
			oldUser.copy(oldUser, user);
		}

		if (Server.allRole.get(user.getUid()) != null) {

			Role role = Server.allRole.get(user.getUid());
			role.setUser(user);
			user.setRole(role);

		}

		if (user.getRole() == null) {
			user.installRole();
		}

		Server.allUser.put(uid, user);
		Server.allRole.put(uid, user.getRole());
		room.addUser(user);
		user.setRoom(room);
		user.getRole().setRoomId(roomId);
		user.write(user.getRole().getInfo(MessageType.loginGameAction));
		Log.info("���û�����,��ǰ��������:  " + Server.allUser.size());

	}

}
