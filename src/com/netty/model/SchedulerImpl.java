package com.netty.model;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netty.server.Scheduler;
;


/**
 * ��ʱ���������
 * 
 */

public class SchedulerImpl implements Scheduler {

	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * ��������
	 */
	private Map<String, Future<?>> tasks = new ConcurrentHashMap<String, Future<?>>();

	/**
	 * ��ʱ�����̳߳�
	 */
	private static ScheduledExecutorService executors;

	 public SchedulerImpl() {
		init();
	}
	
	@PostConstruct
	public void init() {
		// CPU����
		int availableProcessors = Runtime.getRuntime().availableProcessors() * 3;
		executors = Executors.newScheduledThreadPool(availableProcessors,
				new ThreadFactory() {
					AtomicInteger sn = new AtomicInteger();

					public Thread newThread(Runnable r) {
						SecurityManager s = System.getSecurityManager();
						ThreadGroup group = (s != null) ? s.getThreadGroup()
								: Thread.currentThread().getThreadGroup();
						Thread t = new Thread(group, r);
						t.setName("�����߳� - " + sn.incrementAndGet());
						return t;
					}
				});
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				executors.shutdown();
			}
		}));
	}

	private Runnable newTask(final String taskId, final Runnable task,
			final boolean removeAfterExecute) {
		return new Runnable() {
			public void run() {
				try {
					task.run();
				} catch (Throwable e) {
					log.error("SchedulerTask", e);
				}
				if (removeAfterExecute) {
					tasks.remove(taskId);
				}
			}
		};
	}

	@Override
	public void submit(final String taskId, final long delay,
			final Runnable task) {
		cancel(taskId);
		ScheduledFuture<?> future = executors.schedule(newTask(taskId, task,
				true), delay, TimeUnit.MILLISECONDS);
		if (!future.isDone() && !future.isCancelled()) {
			tasks.put(taskId, future);
		}

	}

	@Override
	public void submit(final String taskId, final Runnable task) {
		cancel(taskId);
		Future<?> future = executors.submit(newTask(taskId, task, true));
		if (!future.isDone() && !future.isCancelled()) {
			tasks.put(taskId, future);
		}
	}

	public String submit(final Runnable task) {
		String taskId = null;
		executors.submit(newTask(taskId, task, true));
		return taskId;
	}

	/**
	 * �ύ��ʱ����
	 * 
	 * @param delay
	 *            �ӳ�ʱ�䣬��λΪ����
	 * @param task
	 *            ��������
	 * @return ��ʱ����ID
	 */
	public String submit(long delay, final Runnable task) {
		final String taskId = UUID.randomUUID().toString();
		submit(taskId, delay, task);
		return taskId;
	}

	@Override
	public String submit(Date time, final Runnable task) {
		final String taskId = UUID.randomUUID().toString();
		submit(taskId, time, task);
		return taskId;
	}

	@Override
	public void submit(final String taskId, Date time, final Runnable task) {
		long delay = 0;
		Date now = new Date();
		if (now.before(time)) {
			delay = time.getTime() - now.getTime();

		}
		submit(taskId, delay, task);
	}

	@Override
	public String submit(Date time, long interval, Runnable task) {
		long delay = 0;
		Date now = new Date();
		if (now.before(time)) {
			delay = time.getTime() - now.getTime();
		}
		return submit(delay, task);
	}

	@Override
	public String submit(long delay, long interval, Runnable task) {
		final String taskId = UUID.randomUUID().toString();
		submit(taskId, delay, interval, task);
		return taskId;
	}

	@Override
	public void submit(String taskId, Date time, long interval, Runnable task) {
		long delay = 0;
		Date now = new Date();
		if (now.before(time)) {
			delay = time.getTime() - now.getTime();
		}
		submit(taskId, delay, interval, task);
	}

	@Override
	public void submit(final String taskId, long delay, long interval,
			final Runnable task) {
		cancel(taskId);
		ScheduledFuture<?> future = executors.scheduleWithFixedDelay(newTask(
				taskId, task, false), delay, interval, TimeUnit.MILLISECONDS);
		if (!future.isDone() && !future.isCancelled()) {
			tasks.put(taskId, future);
		}

	}

	public void cancel(String taskId) {
		if (taskId != null) {
			if (tasks.containsKey(taskId)) {
				Future<?> future = tasks.get(taskId);
				future.cancel(false);
				log.error("���� [{}] ��ȡ�� !", taskId);
				tasks.remove(taskId);
			}
		}
	}

	public long getDelay(String taskId) {
		if (taskId != null) {
			if (tasks.containsKey(taskId)) {
				Future<?> future = tasks.get(taskId);
				if (future instanceof ScheduledFuture<?>) {
					return ((ScheduledFuture<?>) future)
							.getDelay(TimeUnit.MILLISECONDS);
				}
			}
		}
		return -1;
	}

}
