package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class FilmorateApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}

}

