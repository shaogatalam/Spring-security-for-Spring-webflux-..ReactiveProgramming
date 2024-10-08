package basePack.service;

import basePack.DTO.SignUpDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserCreate {

    final DatabaseClient databaseClient;
    final PasswordEncoder passwordEncoder;

    public Mono<Void> insertData(SignUpDTO signUpDTO){

        String insertSql = "INSERT INTO users_postgres (firstname, lastname, email, role, password) VALUES (:firstname, :lastname, :email, :role, :password)";
        System.out.println(insertSql);
        return databaseClient.sql(insertSql)
            .bind("firstname", signUpDTO.getFirstname())
            .bind("lastname", signUpDTO.getLastname())
            .bind("email", signUpDTO.getEmail())
            .bind("role", signUpDTO.getRole().name())
            .bind("password", passwordEncoder.encode(signUpDTO.getPassword()))  // Ensure you hash the password
            .then();
    }
}
