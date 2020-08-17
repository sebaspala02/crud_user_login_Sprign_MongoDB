package com.redhat.bluesmile.crudUserLogin.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.redhat.bluesmile.crudUserLogin.exception.ResourceNotFoundException;
import com.redhat.bluesmile.crudUserLogin.model.Noti;
import com.redhat.bluesmile.crudUserLogin.model.UserModel;
//import com.redhat.bluesmile.crudUserLogin.repository.ApplicationUserRepository;
import com.redhat.bluesmile.crudUserLogin.repository.UserRepository;
import com.redhat.bluesmile.crudUserLogin.service.SequenceGeneratorService;

@RestController
@CrossOrigin(allowedHeaders = "x-auth-token", exposedHeaders = "x-auth-token", origins = "*", methods = {
		RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RequestMapping("/api")
public class UserController {

//	@Autowired
//	private ApplicationUserRepository applicationUserRepository;

	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SequenceGeneratorService sequenceGeneratorService;

	@GetMapping("/user")
	public List<UserModel> getAllUsers() {

		return userRepository.findAll();

	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<UserModel> getCart(@PathVariable Long userId) throws ResourceNotFoundException {

		UserModel userModel = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("El User not found for this id :: " + userId));
		;
		return ResponseEntity.ok().body(userModel);
	}

	@PostMapping("/user")
	public UserModel createUser(@Validated @RequestBody UserModel userModel) {

		userModel.setId(sequenceGeneratorService.generateSequence(UserModel.SEQUENCE_NAME));
		userModel.setPassword(bCryptPasswordEncoder.encode(userModel.getPassword()));
		userModel.setCreatedAt(new Date());

		storeNotification(0, userModel);

		return userRepository.save(userModel);

	}

	@PostMapping("http://notifications-app-notifications-app.apps.na311.openshift.opentlc.com/api/notifications/")
	public void storeNotification(@PathVariable int typeMsj, @RequestBody UserModel userDetails) {

		Gson g = new Gson();
		String notification = "";

		int typeUser = 0;
		String email = userDetails.getEmail();
		if (typeMsj == 0) {
			notification = "user " + email + " has been created";
		} else if (typeMsj == 1) {
			notification = "user " + email + " has been modified";
		} else {
			notification = "user " + email + " has been deleted";
		}
		Noti noti = new Noti(typeUser, email, notification);

		System.out.println(g.toJson(noti));

		String url = "http://notifications-app-notifications-app.apps.na311.openshift.opentlc.com/api/notifications/";
		Map<String, String> params = new HashMap<>(); // put here your params.

		RestTemplate template = new RestTemplate();
		template.postForLocation(url, noti, params);

	}

	@PutMapping("/user/{userId}")
	public ResponseEntity<UserModel> updateUser(@PathVariable Long userId,
			@Validated @RequestBody UserModel userDetails) throws ResourceNotFoundException {

		UserModel userModel = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("El User not found for this id :: " + userId));

		userModel.setName(userDetails.getName());
		userModel.setLastname(userDetails.getLastname());
		userModel.setEmail(userDetails.getEmail());
		userModel.setPassword(userDetails.getPassword());
		userModel.setCreatedAt(new Date());
		final UserModel updatedUser = userRepository.save(userModel);
		storeNotification(1, userModel);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/user/{userId}")
	public Map<String, Boolean> delete(@PathVariable Long userId) throws ResourceNotFoundException {

		UserModel userModel = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("El User not found for this id :: " + userId));

		userRepository.delete(userModel);

		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		storeNotification(2, userModel);
		return response;
	}

	public UserController(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@PostMapping("/sign-up")
	public UserModel signUp(@Validated @RequestBody UserModel userModel) {
		userModel.setId(sequenceGeneratorService.generateSequence(UserModel.SEQUENCE_NAME));
		userModel.setPassword(bCryptPasswordEncoder.encode(userModel.getPassword()));
		userModel.setCreatedAt(new Date());
		storeNotification(0, userModel);

		return userRepository.save(userModel);
	}

}