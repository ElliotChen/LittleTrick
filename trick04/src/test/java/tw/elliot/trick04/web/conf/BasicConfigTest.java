package tw.elliot.trick04.web.conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import tw.elliot.trick04.web.common.ApplicationResult;
import tw.elliot.trick04.web.common.ErrorEnum;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class BasicConfigTest {

	@Test
	public void test01() {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();

		ApplicationResult applicationResult = ApplicationResult.create(ErrorEnum.RUNTIME);

		String s = gson.toJson(applicationResult);

		System.out.println(s);
	}
}