package io.github.mxvc;


import io.tmgg.BasePackage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@Slf4j
@ComponentScan(basePackageClasses = {BasePackage.class, DockerAdminBootApplication.class})
@EntityScan(basePackageClasses =  {BasePackage.class, DockerAdminBootApplication.class})
public class DockerAdminBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerAdminBootApplication.class, args);
    }

}
