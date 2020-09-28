package tw.elliot.trick02.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Service03 {

	@Autowired
	private RetryTemplate retryTemplate;

	public String retryMethod03(String job) throws Exception {
		Service03Retry task = new Service03Retry();
		task.setTask(job);
		this.retryTemplate.execute(task, task);
		throw new RemoteAccessException("For RetryTemplate");
	}
}

@Slf4j
@Data
class Service03Retry implements RetryCallback<String, RemoteAccessException>, RecoveryCallback<String> {

	private String task;

	@Override
	public String doWithRetry(RetryContext retryContext) throws RemoteAccessException {
		log.info("Execute doWithRetry");
		throw new RemoteAccessException("For Service03");
	}

	@Override
	public String recover(RetryContext retryContext) throws RemoteAccessException {
		log.info("Execute Recover for Exception [{}]", task);
		log.error("Recover for Exception", retryContext.getLastThrowable());
		return "Recovered";
	}
}