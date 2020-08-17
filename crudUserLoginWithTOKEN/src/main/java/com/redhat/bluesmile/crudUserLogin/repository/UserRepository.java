package com.redhat.bluesmile.crudUserLogin.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.redhat.bluesmile.crudUserLogin.model.UserModel;

@Repository
public interface UserRepository extends MongoRepository<UserModel, Long> {
	
	UserModel findByEmail(String email);

}
