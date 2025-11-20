package io.github.jiangood;


import io.admin.Build;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@Slf4j
@ComponentScan(basePackageClasses = {Build.class, DockerAdminBootApplication.class})
@EntityScan(basePackageClasses =  {Build.class, DockerAdminBootApplication.class})
public class DockerAdminBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerAdminBootApplication.class, args);
    }

}
