package com.bookstore.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@EntityScan({"com.bookstore.entity", "com.bookstore.admin.user"})
public class BookstoreAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookstoreAdminApplication.class, args);
	}

}
