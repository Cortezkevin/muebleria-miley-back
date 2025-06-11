package com.furniture.miley.security.jwt;

import com.furniture.miley.model.MainUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Integer expiration;

    private Key key;

    @PostConstruct
    private void init(){
        byte[] secretBytes = Decoders.BASE64.decode( secret );
        this.key = Keys.hmacShaKeyFor( secretBytes );
    }

    public String generateToken( MainUser mainUser ){
        Map<String, Object> claims = new HashMap<>();
        claims.put( "roles", mainUser.getRoles() );
        claims.put( "sub", mainUser.getUsername() );
        return Jwts.builder()
                .setSubject( mainUser.getUsername() )
                .setClaims( claims )
                .setIssuedAt( new Date() )
                .setExpiration( new Date( new Date().getTime() + ( expiration * 100) ) )
                .signWith( key, SignatureAlgorithm.HS256 )
                .compact();
    }

    public Claims getAllClaimsFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getUsernameFromToken( String token ){
        return this.getClaimFromToken( token, Claims::getSubject );
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey( key ).build().parseClaimsJws(token);
            return true;
        }catch (MalformedJwtException e) {
            log.error("token mal formado");
        } catch (UnsupportedJwtException e) {
            log.error("token no soportado");
        } catch (ExpiredJwtException e) {
            log.error("token expirado");
        } catch (IllegalArgumentException e) {
            log.error("token vacío");
        } catch (SignatureException e) {
            log.error("fail en la firma");
        }
        return false;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
}
