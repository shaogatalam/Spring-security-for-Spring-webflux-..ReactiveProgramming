package basePack.repository;
import basePack.DTO.Users_postgres;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;
@EnableR2dbcRepositories
public interface UserRepository extends R2dbcRepository<Users_postgres, Long> {
    Mono<Users_postgres> findByEmail(String email);
}
