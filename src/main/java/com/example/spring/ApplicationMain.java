package com.example.spring; // Ensure this package matches your project structure
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EntityScan(basePackages = "com.example.spring.model")
@EnableCaching
public class ApplicationMain {
    public static void main(String[] args) {

        SpringApplication.run(ApplicationMain.class, args);

    }

}

// git config --global user.name "sudharsanan123"
// git config --global user.email "sanansudhar7@gmail.com"

