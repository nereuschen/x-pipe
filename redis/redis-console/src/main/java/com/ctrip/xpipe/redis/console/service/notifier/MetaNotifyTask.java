package com.ctrip.xpipe.redis.console.service.notifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.xpipe.api.retry.RetryPolicy;
import com.ctrip.xpipe.command.AbstractCommand;
import com.ctrip.xpipe.retry.RetryNTimes;

/**
 * @author shyin
 *
 *         Oct 26, 2016
 */
public abstract class MetaNotifyTask implements Runnable {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private String taskName;
	private int retryTimes;
	private RetryPolicy retryPolicy;

	public MetaNotifyTask(String taskName, int retryTimes, RetryPolicy retryPolicy) {
		this.taskName = taskName;
		this.retryTimes = retryTimes;
		this.retryPolicy = retryPolicy;
	}

	@Override
	public void run() {
		try {
			logger.info("[{}][construct]",taskName);
			new RetryNTimes<>(retryTimes, retryPolicy).execute(new AbstractCommand<Object>() {
				@Override
				public String getName() {
					return taskName;
				}

				@Override
				protected void doExecute() throws Exception {
					doNotify();
				}

				@Override
				protected void doReset() {
				}

			});
			logger.info("[{}][success]",taskName);
		} catch (Exception e) {
			logger.error("[{}][failed][rootCause]{}",taskName, e);
		}
	}

	public abstract void doNotify();
}
