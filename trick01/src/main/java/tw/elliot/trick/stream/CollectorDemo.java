package tw.elliot.trick.stream;

import tw.elliot.trick.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectorDemo {

	public Map<String, User> mapById(List<User> users) {
		return users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
	}

	public Map<Integer, List<User>> groupByAge(List<User> users) {
		return users.stream().collect(Collectors.groupingBy(User::getAge));
	}

	public List<User> collectToList(User user1, User user2, User... users) {
		List<User> collect = Arrays.stream(users).collect(Collectors.toList());
		return Stream.of(user1, user2).collect(Collectors.toList());
	}

	public Map<Integer, Long> countByAge(List<User> users) {
		return users.stream().collect(Collectors.groupingBy(User::getAge, Collectors.counting()));
	}
}
