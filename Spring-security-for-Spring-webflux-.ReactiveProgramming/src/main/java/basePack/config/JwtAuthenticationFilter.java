package basePack.config;
import io.netty.handler.codec.http.cookie.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
//@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private  WebClient.Builder webClientBuilder;

    @Autowired
    public JwtService jwtService;

    @Autowired
    public ReactiveUserDetailsService userDetailsService;

    //    @Override
    //    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    //
    //        String targetEndpoint = exchange.getRequest().getPath().value();
    //        if ( targetEndpoint.equals("/getSpecificUsers") || targetEndpoint.equals("/getUsers") || targetEndpoint.equals("/login") || targetEndpoint.equals("/register") || targetEndpoint.equals("/signup")) {
    //            return chain.filter(exchange);
    //        }
    //
    //        String AccessToken  = "";
    //        String RefreshToken = "";
    //
    //        List<HttpCookie> cookies = exchange.getRequest()
    //                                           .getCookies()
    //                                           .getOrDefault("AccessToken", Collections.emptyList());
    //        if (!cookies.isEmpty()) { AccessToken = cookies.get(0).getValue(); }
    //
    //        List<HttpCookie> rcookies = exchange.getRequest()
    //                                            .getCookies()
    //                                            .getOrDefault("RefreshToken", Collections.emptyList());
    //        if (!rcookies.isEmpty()) { RefreshToken = rcookies.get(0).getValue(); }
    //
    //        String userEmail = null;
    //        if (AccessToken != null) {
    //            try {
    //                userEmail = jwtService.extractUsername(AccessToken);
    //            } catch (Exception e) {
    //                log.error("Invalid JWT: {}", e.getMessage());
    //            }
    //        }
    //
    //        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    //
    //            UserDetails userDetails = userDetailsService.findByUsername(userEmail).block();
    //
    //            if (userDetails != null && jwtService.isTokenValid(AccessToken, userDetails)) {
    //                //UsernamePasswordAuthenticationToken or Authentication
    //                Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    //                //authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    //                SecurityContextHolder.getContext().setAuthentication(authToken);
    //            }
    //        }
    //
    //        if (SecurityContextHolder.getContext().getAuthentication() == null && RefreshToken != null  ) {
    //
    //            UserDetails userDetails = jwtService.getUserDetailsFromJwt(RefreshToken).block();
    //
    //            if (userDetails != null && jwtService.isRefreshTokenValid(RefreshToken, userDetails)) {
    //                //UsernamePasswordAuthenticationToken or Authentication
    //                Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    //                // authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    //                SecurityContextHolder.getContext().setAuthentication(authToken);
    //                String newAccessToken = jwtService.generateToken(userDetails);
    //
    //                exchange.getResponse()
    //                        .addCookie(ResponseCookie.from("AccessToken", newAccessToken)
    //                            .httpOnly(true)
    //                            .path("/")
    //                            .build());
    //
    //                exchange.getResponse()
    //                        .addCookie(ResponseCookie.from("RefreshToken", RefreshToken)
    //                            .httpOnly(true)
    //                            .path("/")
    //                            .build());
    //
    //            }
    //        }
    //        return chain.filter(exchange);
    //    }
    //

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String targetEndpoint = exchange.getRequest().getPath().value();
        if (targetEndpoint.equals("/getSpecificUsers") || targetEndpoint.equals("/getUsers") || targetEndpoint.equals("/login")
                || targetEndpoint.equals("/register") || targetEndpoint.equals("/signup")) {
            return chain.filter(exchange);
        }

        List<HttpCookie> cookies = exchange.getRequest().getCookies().getOrDefault("AccessToken", Collections.emptyList());
        String accessToken = cookies.isEmpty() ? null : cookies.get(0).getValue();

        List<HttpCookie> rcookies = exchange.getRequest().getCookies().getOrDefault("RefreshToken", Collections.emptyList());
        String refreshToken = rcookies.isEmpty() ? null : rcookies.get(0).getValue();

        return Mono.justOrEmpty(accessToken)
            .flatMap(jwtService::getUserDetailsFromJwt)
            .flatMap(userDetails -> {
                if (jwtService.isTokenValid(accessToken, userDetails)) {
                    Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    return chain.filter(exchange);
                }
                return Mono.empty();
            })
            .switchIfEmpty(Mono.defer(() -> {
                if (refreshToken != null) {
                    jwtService.getUserDetailsFromJwt(refreshToken)
                        .flatMap(userDetails -> {
                            if (jwtService.isRefreshTokenValid(refreshToken, userDetails)) {
                                Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(authToken);
                                String newAccessToken = jwtService.generateToken(userDetails);
                                exchange.getResponse().addCookie(ResponseCookie.from("AccessToken", newAccessToken).httpOnly(true).path("/").build());
                                exchange.getResponse().addCookie(ResponseCookie.from("RefreshToken", refreshToken).httpOnly(true).path("/").build());
                                return chain.filter(exchange);
                            }
                            return Mono.empty();
                        });
                }
                return Mono.empty();
            }))
            .then(chain.filter(exchange));
    }

}