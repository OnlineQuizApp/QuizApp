package com.example.project_module6.config;

import com.example.project_module6.model.Users;
import com.example.project_module6.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {
    private IUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public AdminInitializer(IUserRepository userRepository,PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(userRepository.findByUsername("admin").isEmpty()){
            Users admin = new Users();
            admin.setUsername("admin");
            String rawPassword = "Dat2503@";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            admin.setPassword(encodedPassword);
            admin.setRole(Users.Role.ADMIN);
            admin.setEmail("khucvylinh1998@outlook.com");
            userRepository.save(admin);
            System.out.println("Admin đã được thêm vào database");
        }
    }
}
