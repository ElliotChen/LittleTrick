package tw.elliot.trick02.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * @author elliot
 */
@Service
@Slf4j
public class Service02 {

	@Retryable()
	public String retryMethod02(String job) throws Exception {
		log.info("Execute Method02");
		throw new RemoteAccessException("For Service02");
	}

	@Recover
	public String recover02(Exception rae, String job) {
		log.info("Execute Recover02 for Exception [{}]", job);
		log.error("Recover for Exception ", rae);
		return "Recovered";
	}

	@Recover
	public String recover03(RemoteAccessException rae, String job) {
		log.info("Execute Recover03 for Exception [{}]", job);
		log.error("Recover for Exception", rae);
		return "Recovered";
	}


}
