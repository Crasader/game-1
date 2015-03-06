package com.business.model;

import static com.server.model.Server.scheduler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.common.model.CallBack;
import com.common.util.FactoryUtil;
import com.common.util.Log;
import com.common.util.LogicHandler;
import com.common.util.MathUtil;
import com.netty.model.Message;
import com.netty.model.MessageType;
import com.netty.server.Scheduler;
import com.server.model.Server;

public class Room implements CallBack {

	private boolean open; // 开放状态

	private String roomId; // 房间ID

	private List<Role> roleList; // 角色列表

	private List<User> userlist; // 用户列表

	private int second = 0; // 秒

	private String taskId; // 主任务ID

	private String saveTaskId; // 保存任务Id

	private String addTaskId; // 用户登陆任务id

	private boolean closing = false;

	private String name; // 房间名

	private Map<Object, String[]> saveList = FactoryUtil.newMap();

	private boolean save = false;

	private List<User> loginUsers = FactoryUtil.newList(); // 等待加入房间的用户队列;

	private int useThread = 0; // 使用用户列表的线程数

	public Room(String roomId, String name, boolean open) {

		this.name = name;
		this.roomId = roomId;
		if (open) {
			open();
		} else if (isOpen()) {
			close();
		}
	}

	public void open() {
		if (open) {
			if (closing) {
				Log.info("取消关闭房间" + name);
				closing = false;
			}
			return;
		}
		this.open = true;
		if (taskId == null) {
			taskId = new StringBuffer().append("roomOneSencondTask")
					.append(roomId).toString();
		}
		if (saveTaskId == null) {
			saveTaskId = new StringBuffer().append("roomSaveTask")
					.append(roomId).toString();
		}
		if (addTaskId == null) {
			addTaskId = new StringBuffer().append("roomAddTask").append(roomId)
					.toString();
		}
		StartThreads();
		Server.allRoom.put(roomId, this);
	}

	public void close() {
		if (!open)
			return;
		if (open && closing) {
			return;
		}
		Log.info(new StringBuffer("请求关闭房间").append(name));
		closing = true;
	}

	private void StartThreads() {

		scheduler.submit(taskId, Scheduler.ONE_SECOND_MILLISECOND,
				Scheduler.ONE_SECOND_MILLISECOND, new Runnable() {
					@Override
					public void run() {
						updateOneSecond();
					}
				});

		scheduler.submit(saveTaskId, Scheduler.ONE_SECOND_MILLISECOND,
				Scheduler.ONE_SECOND_MILLISECOND, new Runnable() {
					@Override
					public void run() {
						saveAndInstallTop();
					}
				});

		scheduler.submit(addTaskId, Scheduler.ONE_SECOND_MILLISECOND,
				Scheduler.ONE_SECOND_MILLISECOND, new Runnable() {
					@Override
					public void run() {
						addUserTask();
					}
				});

	}

	private void addUserTask() {

		int temp = second;

		if (useThread == 0 && loginUsers.size() > 0) {

			Iterator<User> iter = loginUsers.iterator();
			while (iter.hasNext()) {
				if (temp < second || useThread > 0) {
					break;
				}
				User user = iter.next();
				Server.allUser.put(user.getUid(), user);
				Server.allRole.put(user.getUid(), user.getRole());
				userlist.add(user);
				roleList.add(user.getRole());
				iter.remove();
			}

		}

	}

	private void updateOneSecond() {
		try {
			if (Server.Closing && !closing) {
				closing = true;
			}
			// TODO
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
	}

	private void saveAndInstallTop() {

		if (save) {

			save = false;

			if (closing) {

				boolean err = false;

				for (String[] str : saveList.values()) {

					if (!Arrays.asList(str).contains(Role.saveAll)) {
						err = true;
					}

					break;

				}

				if (err) {
					saveList.clear();
					saveAll();
				}

				scheduler.cancel(taskId);
				Log.info("room: " + name + "主线程终止...");
			}

			for (Object object : saveList.keySet()) {
				try {
					String[] methods = saveList.get(object);
					Class<?> c = object.getClass();
					for (String method : methods) {
						Method m = c.getMethod(method, new Class[] {});
						m.invoke(object, new Object[] {});
					}
				} catch (Exception e) {
					Log.error("保存信息失败" + e.getMessage());
				}
			}

			saveList.clear();

			if (closing) {
				open = false;
				Log.info("room:" + name + "数据保存完毕");
				Log.info("room:  " + name + " is closed");
				Server.allRoom.remove(roomId);
				scheduler.cancel(addTaskId);
				scheduler.cancel(saveTaskId);
			}

		}

	}

	private void saveAll() {

		String[] saveMethods = null;

		if (closing || (MathUtil.getRandom(1, 100) > 50)) { // TODO
			saveMethods = new String[] { Role.saveAll ,Role.clear};
		} else if (MathUtil.getRandom(1, 100) > 50) {
			saveMethods = new String[] { Role.updateGameTop,Role.clear };
		} else {
			saveMethods = new String[] {Role.clear};
		}

		if (MathUtil.getRandom(1, 100) > 20) { // TODO
			Iterator<User> useritor = userlist();
			while (useritor.hasNext()) {
				User user = useritor.next();
				if (user.getRole().getIsMachine() == 1)
					continue;
				if (!user.isOpen()) {
					useritor.remove();
					Server.allUser.remove(user);
				}

			}
		}

		lock();

		for (Role role : roleList) {
			if (role.getIsMachine() == 1 || role.getUser().isOpen()) {
				saveList.put(role, saveMethods);
			} else {
				saveList.put(role, new String[] { Role.saveAll, Role.exitRoom });
			}

		}

		unlock();

		save = true;

	}

	private void roomEnd() {
		try {

			saveAll();
			Iterator<User> iterlist = userlist();
			while (iterlist.hasNext()) {
				User user = iterlist.next();
				user.write(user.getRole().getInfo(MessageType.return_userInfo));
			}
			second = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public List<User> getUserlist() {
		return userlist;
	}

	public void setUserlist(List<User> userlist) {
		this.userlist = userlist;
	}

	public boolean isClosing() {
		return closing;
	}

	public void setClosing(boolean closing) {
		this.closing = closing;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public List<Role> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<Role> roleList) {
		this.roleList = roleList;
	}

	public Map<Object, String[]> getSaveList() {
		return saveList;
	}

	public void addUser(User user) {
		loginUsers.add(user);
	}

	public void lock() {
		useThread++;
	}

	public void unlock() {
		useThread--;
	}

	@SuppressWarnings("unchecked")
	public Iterator<User> userlist() {

		lock();

		return (Iterator<User>) new LogicHandler(userlist.iterator(), this)
				.newProxyInstance();

	}

	public void writeAll(Message msg) {

		Iterator<User> userlist = userlist();

		while (userlist.hasNext()) {
			userlist.next().write(msg);
		}

	}

	@Override
	public void callBack(Method method, Object returnValue) {

		if (!method.getName().equals("hasNext")) {
			return;
		}

		if ((boolean) returnValue == false) {
			unlock();
		}

	}

}
