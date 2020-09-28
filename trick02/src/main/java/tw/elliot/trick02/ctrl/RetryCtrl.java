package tw.elliot.trick02.ctrl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.elliot.trick02.service.Service01;
import tw.elliot.trick02.service.Service02;
import tw.elliot.trick02.service.Service03;

@RestController
@RequestMapping("/retry")
@Slf4j
public class RetryCtrl {
	@Autowired
	Service01 service01;

	@Autowired
	Service02 service02;

	@Autowired
	Service03 service03;

	@RequestMapping("/r01")
	public String retry01() {
		String result = "";
		try {
			result = this.service01.retryMethod01("task01");

			if (StringUtils.isEmpty(result)) {
				result = "Failed.";
			}
		} catch (Exception rae) {
			log.error("RAE", rae);
			result = "Failed";
		}

		return result;
	}

	@RequestMapping("/r02")
	public String retry02() {
		String result = "";
		try {
			result = this.service02.retryMethod02("task02");

			if (StringUtils.isEmpty(result)) {
				result = "Failed.";
			}
		} catch (Exception rae) {
			log.error("RAE", rae);
			result = "Failed";
		}

		return result;
	}

	@RequestMapping("/r03")
	public String retry03() {
		String result = "";
		try {
			result = this.service03.retryMethod03("task03");

			if (StringUtils.isEmpty(result)) {
				result = "Failed.";
			}
		} catch (Exception rae) {
			log.error("RAE", rae);
			result = "Failed";
		}

		return result;
	}
}
