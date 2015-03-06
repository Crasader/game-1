package com.netty.server;

import java.util.Date;

/**
 * ��ʱ���������
 * 
 * 
 */
public interface Scheduler {
	/**
	 * ������ʾ
	 */
	public static final int ONE_SECOND_MILLISECOND = 1000;
	/**
	 * �ֺ����ʾ
	 */
	public static final int ONE_MINUTE_MILLISECOND = 60 * ONE_SECOND_MILLISECOND;
	/**
	 * ʱ�����ʾ
	 */
	public static final int ONE_HOUR_MILLISECOND = 60 * ONE_MINUTE_MILLISECOND;
	/**
	 * ������ʾ
	 */
	public static final int ONE_DAY_MILLISECOND = 24 * ONE_HOUR_MILLISECOND;

	/**
	 * ����ִ������
	 * 
	 * @param task
	 *            ��������
	 * @param taskId
	 *            ����ID
	 */
	void submit(String taskId, final Runnable task);

	/**
	 * �ύ��ʱ����
	 * 
	 * @param taskId
	 *            ��ʱ����ID
	 * @param delay
	 *            �ӳ�ʱ��(��λΪ����)
	 * @param interval
	 *            ���ʱ��(����)
	 * @param task
	 *            ��������
	 */
	void submit(String taskId, long delay, long interval, final Runnable task);

	/**
	 * �ύ��ʱ����
	 * 
	 * @param taskId
	 *            ��ʱ����ID
	 * @param delay
	 *            �ӳ�ʱ��(��λΪ����)
	 * @param task
	 *            ��������
	 */
	void submit(String taskId, long delay, final Runnable task);

	/**
	 * �ύ��ʱ����
	 * 
	 * @param time
	 *            ����ʱ��
	 * @param interval
	 *            ���ʱ��(MS)
	 * @param task
	 *            ��������
	 * @param taskId
	 *            ��ʱ����ID
	 */
	void submit(String taskId, Date time, long interval, final Runnable task);

	/**
	 * �ύ��ʱ����
	 * 
	 * @param time
	 *            ����ʱ��
	 * @param task
	 *            ��������
	 * @param taskId
	 *            ��ʱ����ID
	 */
	void submit(String taskId, Date time, final Runnable task);

	/**
	 * ����ִ������
	 * 
	 * @param task
	 *            ��������
	 * @return ����ID
	 */
	String submit(final Runnable task);

	/**
	 * �ύ��ʱ����
	 * 
	 * @param delay
	 *            �ӳ�ʱ��(��λΪ����)
	 * @param interval
	 *            ���ʱ��(MS)
	 * @param task
	 *            ��������
	 * @return ��ʱ����ID
	 */
	String submit(long delay, long interval, final Runnable task);

	/**
	 * �ύ��ʱ����
	 * 
	 * @param delay
	 *            �ӳ�ʱ��(��λΪ����)
	 * @param task
	 *            ��������
	 * @return ��ʱ����ID
	 */
	String submit(long delay, final Runnable task);

	/**
	 * �ύ��ʱ����
	 * 
	 * @param time
	 *            ִ�е�ʱ��
	 * @param interval
	 *            ���ʱ��(MS)
	 * @param task
	 *            ��������
	 * @return ��ʱ����ID
	 */
	String submit(Date time, long interval, final Runnable task);

	/**
	 * �ύ��ʱ����
	 * 
	 * @param time
	 *            ִ�е�ʱ��
	 * @param task
	 *            ��������
	 * @return ��ʱ����ID
	 */
	String submit(Date time, final Runnable task);

	/**
	 * ������ʱ����
	 * 
	 * @param taskId
	 *            ����ID
	 */
	void cancel(String taskId);

	/**
	 * ��ȡ��ʱ����ľ�����һ��ִ�е�ʱ��
	 * 
	 * @param taskId
	 *            ����ID
	 * @return ʱ���(����)
	 */
	long getDelay(String taskId);
}
