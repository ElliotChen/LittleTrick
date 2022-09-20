package tw.elliot.trick04.web.adv;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tw.elliot.trick04.web.common.ApplicationResult;
import tw.elliot.trick04.web.common.ErrorEnum;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * @author elliot
 */
@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

	final Gson gson;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		if (body instanceof ApplicationResult) {
			return body;
		}

		Object result = ApplicationResult.success(body);

		if (body instanceof String) {
			return gson.toJson(result);
		}

		//returnType.getParameterType();
		return result;
		/*
		if (body instanceof String) {
			return gson.toJson(ApplicationResult.success(body));
		}





		log.info("return type: [{}]", returnType);
		log.info("{}", gson.toJson(result));
		return result;

		 */
	}

	@ExceptionHandler(RuntimeException.class)
	public ApplicationResult handleRuntimeException(RuntimeException e) {
		log.error("Runtime Exception", e);
		return ApplicationResult.create(ErrorEnum.RUNTIME);
	}

	@ExceptionHandler(BindException.class)
	public ApplicationResult handleBindException(BindException e) {
		log.error("Bind Exception", e);
		ApplicationResult result = ApplicationResult.create(ErrorEnum.PARAMETER);
		String message = e.getBindingResult()
				.getAllErrors()
				.stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.joining(";"));
		result.setMessage(message);
		return result;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApplicationResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error("Argument Exception", e);
		ApplicationResult result = ApplicationResult.create(ErrorEnum.PARAMETER);
		String message = e.getAllErrors()
				.stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.joining(";"));
		result.setMessage(message);
		return result;
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ApplicationResult handleConstraintViolationException(ConstraintViolationException e) {
		log.error("Constraint Exception");
		ApplicationResult result = ApplicationResult.create(ErrorEnum.PARAMETER);
		String message = e.getConstraintViolations()
				.stream()
				.map(error -> error.getMessage())
				.collect(Collectors.joining(";"));
		result.setMessage(message);
		return result;
	}
	@ModelAttribute
	public void addAttribute(Model model) {
		model.addAttribute("TestMsg", "all model will see this msg.");
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {

	}
}
