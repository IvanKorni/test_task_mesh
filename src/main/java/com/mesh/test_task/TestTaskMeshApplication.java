package com.mesh.test_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TestTaskMeshApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestTaskMeshApplication.class, args);
	}

}
