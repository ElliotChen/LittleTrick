package tw.elliot.trick.entity;

import lombok.Data;

import java.util.List;

@Data
public class Shop {
	private String name;
	private List<User> users;
}
