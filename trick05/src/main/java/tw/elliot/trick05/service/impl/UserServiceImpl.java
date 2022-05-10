package tw.elliot.trick05.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.elliot.trick05.model.User;
import tw.elliot.trick05.repo.UserRepo;
import tw.elliot.trick05.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Override
	public List<User> findAllUsers() {
		return userRepo.findAll();
	}
}
