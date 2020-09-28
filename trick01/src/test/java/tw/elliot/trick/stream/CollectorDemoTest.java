package tw.elliot.trick.stream;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tw.elliot.trick.entity.City;
import tw.elliot.trick.entity.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CollectorDemoTest {
	List<User> users;
	CollectorDemo collector = new CollectorDemo();

	@Before
	public void init() {
		City city1 = new City("123", "Taipei");
		City city2 = new City("234", "Tainan");
		users = new ArrayList<>();

		users.add(new User("Adam", 10, "T1234", city1));
		users.add(new User("Bob", 20, "A1234", city2));
		users.add(new User("Cindy", 10, "G1234", city1));
		users.add(new User("David", 15, "Y1234", city2));
		users.add(new User("Eden", 21, "I1234", city1));
	}

	@Test
	public void testMapById() {
		Map<String, User> userMap = collector.mapById(this.users);

		userMap.forEach((name, user) -> log.info("key is [" + name + "] - value is [" + user.toString() + "]"));

		Assert.assertEquals(5, userMap.keySet().size());
	}

	@Test
	public void testGroupByAge() {
		Map<Integer, List<User>> map = collector.groupByAge(this.users);

		map.forEach((age, lists) -> log.info("key is [" + age + "] - size is [" + lists.size() + "]"));

		Assert.assertEquals(4, map.keySet().size());
	}

	@Test
	public void testStreamMap() {
		//Function<User, City> func = (user) -> {return user.getCity();};

		/*
		Set<City> cities = users.stream()
				.map((user) -> {return user.getCity();})
				.collect(Collectors.toSet());
		 */
		Set<City> cities = users.stream()
				.map(User::getCity)
				.collect(Collectors.toSet());

		cities.forEach((city) -> log.info("find city : [{}]", city));

		Assert.assertEquals(2, cities.size());
	}

	@Test
	public void testMatch() {
		boolean allMatch = users.stream().allMatch((user) -> {
			return user.getCity().getZip() == "123";
		});
		boolean anyMatch = users.stream().anyMatch((user) -> {
			return user.getCity().getZip() == "123";
		});

		log.info(" all match [{}], any match [{}]", allMatch, anyMatch);

		Assert.assertFalse(allMatch);
		Assert.assertTrue(anyMatch);
	}

	@Test
	public void testFindFirst() {
		Optional<User> first = users.stream().filter((user) -> {
			return user.getCity().getZip() == "123";
		}).findFirst();

		Assert.assertFalse(first.isEmpty());


	}

}