package tw.elliot.trick02.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * @author elliot
 */
@Service
@Slf4j
public class Service01 {

	@Retryable()
	public String retryMethod01(String job) throws Exception {
		log.info("Execute Method01");
		throw new RemoteAccessException("For Service01");
	}

	@Recover
	public String recover00(Exception rae, String job) {
		log.info("Execute Recover00 for Exception [{}]", job);
		log.error("Recover for Exception ", rae);
		return "Recovered";
	}

	@Recover
	public String recover01(RemoteAccessException rae, String job) {
		log.info("Execute Recover01 for Exception [{}]", job);
		log.error("Recover for Exception", rae);
		return "Recovered";
	}


}
