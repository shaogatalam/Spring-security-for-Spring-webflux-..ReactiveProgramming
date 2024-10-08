package basePack.service;

import basePack.DTO.UserDTO;
import basePack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserFetch {

    final public DatabaseClient databaseClient;
    @Autowired
    public UserRepository userRepository;

    public Flux<UserDTO> fetchAllUser(){

        return databaseClient.sql("SELECT * FROM users_postgres")
            .map((row, rowMetadata) -> new UserDTO(
                row.get("firstname", String.class),
                row.get("lastname", String.class),
                row.get("email", String.class),
                row.get("role", String.class)
            ))
            .all();
    }

    public Mono<UserDTO> fetchSpecificUser(String email) {
        return databaseClient.sql("SELECT * FROM users_postgres WHERE email = :email")
            .bind("email", email)
            .map((row, rowMetadata) -> new UserDTO(
                row.get("firstname", String.class),
                row.get("lastname", String.class),
                row.get("email", String.class),
                row.get("role", String.class)
            ))
            .one();
    }

    //    public Mono<CustomerDTO> getCustomerWithOrders(@PathVariable Long customerId) {
    //
    //        //-- Using flatMap
    //        return findCustomerById(customerId)
    //            .flatMap(customer -> orderRepository.findAllByCustomerId(customer.getId()).collectList()
    //                .map(orders -> new CustomerDTO(
    //                    customer.getId(),
    //                    customer.getName(),
    //                    customer.getEmail(),
    //                    orders
    //                ))
    //            );
    //
    //        //-- Using zipWith
    //        return findCustomerById(customerId)
    //            .zipWith(orderRepository.findAllByCustomerId(customerId).collectList()) // Combine the customer and orders Mono
    //            .map(result -> {
    //                Customer customer = result.getT1();
    //                List<Order> orders = result.getT2();
    //                return new CustomerDTO(
    //                    customer.getId(),
    //                    customer.getName(),
    //                    customer.getEmail(),
    //                    orders
    //                );
    //            });
    //
    //
    //    }

}
