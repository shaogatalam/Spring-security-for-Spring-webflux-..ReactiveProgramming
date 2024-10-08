package basePack.controller;

import basePack.DTO.*;
import basePack.config.JwtService;
import basePack.repository.UserRepository;
import basePack.service.UserCreate;
import basePack.service.UserFetch;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public UserCreate userCreate;

    @Autowired
    public UserFetch userFetch;

    @Autowired
    public JwtService jwtService;

    @Autowired
    ReactiveAuthenticationManager reactiveAuthenticationManager;

    @PostMapping("/login")
    public Mono<Object> login(@RequestBody SignInDTO signInDTO, ServerWebExchange exchange) {
        return reactiveAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDTO.getUsername(), signInDTO.getPassword()))
            .flatMap(authentication -> {
                if (authentication.isAuthenticated()) {
                    return userRepository.findByEmail(signInDTO.getUsername())
                        .flatMap(user -> {
                            if (user instanceof UserDetails) {
                                String jwtToken = jwtService.generateToken((UserDetails) user);
                                String refreshToken = jwtService.generateRefreshToken((UserDetails) user);
                                exchange.getResponse().addCookie(ResponseCookie.from("AccessToken", jwtToken)
                                    .httpOnly(true)
                                    .secure(true)
                                    .path("/")
                                    .build());

                                exchange.getResponse().addCookie(ResponseCookie.from("RefreshToken", refreshToken)
                                    .httpOnly(true)
                                    .secure(true)
                                    .path("/")
                                    .build());
                                return Mono.empty();
                            }
                            return Mono.error(new UsernameNotFoundException("User not found"));
                        });
                } else {
                    return Mono.error(new BadCredentialsException("Invalid credentials"));
                }
            })
            .onErrorResume(e -> {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return Mono.empty();
            });
    }

    //    @PostMapping("/login")
//    public Mono<Void> login(@RequestBody SignInDTO signInDTO, ServerWebExchange exchange) {
//
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInDTO.getUsername(),signInDTO.getPassword()))
//            .cast(Authentication.class)
//            .flatMap(authentication -> userRepository.findByEmail(signInDTO.getUsername())
//                .flatMap(user -> {
//                    if (user instanceof UserDetails) {
//                        String jwtToken        = jwtService.generateToken((UserDetails)user);
//                        String refreshToken    = jwtService.generateRefreshToken((UserDetails) user);
//                        exchange.getResponse().addCookie(ResponseCookie.from("AccessToken", jwtToken)
//                            .httpOnly(true)
//                            .secure(true)
//                            .path("/")
//                            .build());
//
//                        exchange.getResponse().addCookie(ResponseCookie.from("RefreshToken", refreshToken)
//                            .httpOnly(true)
//                            .secure(true)
//                            .path("/")
//                            .build());
//                        return Mono.just("token");
//                    }
//                    return null;
//                })
//        )
//        .onErrorResume(e -> Mono.just(String.valueOf(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())));

        //        if(authentication.isAuthenticated()) {
//            userRepository.findByEmail(signInDTO.getUsername())
//            .flatMap(user -> {
//                if (user instanceof UserDetails) {
//                    String jwtToken        = jwtService.generateToken((UserDetails)user);
//                    String refreshToken    = jwtService.generateRefreshToken((UserDetails) user);
//                    exchange.getResponse().addCookie(ResponseCookie.from("AccessToken", jwtToken)
//                        .httpOnly(true)
//                        .secure(true)
//                        .path("/")
//                        .build());
//
//                    exchange.getResponse().addCookie(ResponseCookie.from("RefreshToken", refreshToken)
//                        .httpOnly(true)
//                        .secure(true)
//                        .path("/")
//                        .build());
//                    return Mono.just("token");
//                } else {
//                    return Mono.error(new IllegalArgumentException("User does not implement UserDetails"));
//                }
//            });
//        }

//        return null;
//    }


    @PostMapping("/signup")
    public Mono<Object> signUp(@RequestBody SignUpDTO signUpDTO,ServerWebExchange exchange) {
        return userRepository.findByEmail(signUpDTO.getEmail())
            .flatMap(userObj -> {
                return Mono.error(new RuntimeException("User already exists"));
            })
            .switchIfEmpty(
                userCreate.insertData(signUpDTO).then(Mono.just(ResponseEntity.ok("User created successfully!")))
            )
            .onErrorResume(e ->
                Mono.just(ResponseEntity.badRequest().body("Error: " + e.getMessage()))
            );
    }

    @PostMapping("/inventoryUpdate")
    public void inventoryUpdate(@RequestBody SignUpDTO signUpDTO, ServerHttpResponse response) {
        return;
    }

    @GetMapping("/getUsers")
    public Flux<UserDTO> fetchUser(){
        return userFetch.fetchAllUser();
    }

    @PostMapping("/getSpecificUsers")
    public Mono<UserDTO> fetchSpecificUser(@RequestBody fetchSingleUserDTO singleUserDTO){
        return userFetch.fetchSpecificUser(singleUserDTO.getEmail());
    }

}
