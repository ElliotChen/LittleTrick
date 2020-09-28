package tw.elliot.trick02;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tw.elliot.trick02.service.Service01;
import tw.elliot.trick02.service.Service02;
import tw.elliot.trick02.service.Service03;

@SpringBootTest
class Trick02ApplicationTests {

	@Autowired
	private Service01 service01;

	@Autowired
	private Service02 service02;

	@Autowired
	private Service03 service03;
	@Test
	void retryMethod01() {
		try {
			this.service01.retryMethod01("t1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void retryMethod02() {
		try {
			this.service02.retryMethod02("t2");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	void retryMethod03() {
		try {
			this.service03.retryMethod03("t3");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
