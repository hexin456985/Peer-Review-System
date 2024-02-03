package com.micro.userserve;

import com.micro.userserve.proto.UserService;
import com.micro.userserve.sys.service.impl.UserGrpcServiceImpl;
import com.micro.userserve.sys.service.impl.UserServiceImpl;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;

@SpringBootApplication
@EnableSwagger2
@MapperScan("com.micro.userserve.*.mapper")
@ComponentScan(basePackages = {"com.micro"})
@EnableEurekaClient
public class UserServeApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(UserServeApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}

