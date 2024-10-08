package basePack.config;

//import com.springboot1.demo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY="6A586E3272357538782F413F4428472B4B6250655367566B5970337336763979";
    private static final String Refresh_SECRET_KEY="68566D597133743677397A24432646294A404D635166546A576E5A7234753778";

    @Autowired
    public ReactiveUserDetailsService reactiveUserDetailsService;

//    public Mono<UserDetails> getUserDetailsFromJwt(String token) {
//        String username = extractUsername_rtoken(token);
//        try {
//            return userDetailsService.findByUsername(username);
////            return userDetailsService.findByUsername(username)
////                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found")));
//        } catch (UsernameNotFoundException e) {
//            // Handle user not found exception
//            return null;
//        }
//    }

    public Mono<UserDetails> getUserDetailsFromJwt(String token) {
        String username = extractUsername_rtoken(token);
        return reactiveUserDetailsService.findByUsername(username)
                .onErrorResume(UsernameNotFoundException.class, e -> Mono.empty());
    }

    public String extractUsername_rtoken(String token) {
        return extractClaim_r(token, Claims::getSubject);
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }
    public Date extractRefreshExpiration(String token) {
        return extractRefreshClaim(token,Claims::getExpiration);
    }


    public <T> T extractRefreshClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims_RToken(token);
        return claimResolver.apply(claims);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public <T> T extractClaim_r(String token, Function<Claims,T> claimResolver){
        final Claims claims = extractAllClaims_RToken(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInkey())
                .build()
                .parseClaimsJws(token)
                //.parseClaimsJwt(token)
                .getBody();
    }

    public Claims extractAllClaims_RToken(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getRefreshSignInkey())
                .build()
                //.parseClaimsJwt(token)
                .parseClaimsJws(token)
                .getBody();
    }

    public Key getSignInkey() {
        byte[] keyByte = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public Key getRefreshSignInkey() {
        byte[] keyByte = Decoders.BASE64.decode(Refresh_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails,new HashMap<>());
    }


    public String generateToken(UserDetails userDetails, Map<String,Object>extraClaims){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                .compact();

    }

    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isRefreshTokenExpired(String refreshToken) {
        return extractRefreshExpiration(refreshToken).before(new Date());
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isRefreshTokenValid(String refreshToken, UserDetails userDetails) {
        final String username = extractUsername_rtoken(refreshToken);
        return StringUtils.hasText(username) && username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(refreshToken);
    }


    public String generateRefreshToken(UserDetails userDetails) {
        return generateRefreshToken(userDetails,new HashMap<>());
    }

    public String generateRefreshToken(UserDetails userDetails,Map<String,Object>extraClaims) {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis()+1000*60*24*365))
            .signWith(getRefreshSignInkey(), SignatureAlgorithm.HS256)
            .compact();
    }

}
