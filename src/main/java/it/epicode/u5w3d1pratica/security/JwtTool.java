package it.epicode.u5w3d1pratica.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.epicode.u5w3d1pratica.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTool {
    @Value("${jwt.duration}")
    private Long duration;

    @Value("${jwt.secret}")
    private String secret;

    public String createToken(User user){
        //per generare il token avremo bisogno della data di generazione del token, della durata e dell'id
        //dell'utente per il quale stiamo creando il token. Avremo inoltre bisogno anche della chiave segreta
        //per poter crittografare il token

        return Jwts.builder().issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + duration)).
                subject(String.valueOf(user.getId())).
                signWith(Keys.hmacShaKeyFor(secret.getBytes())).
                compact();
    }

  //  metodo per la verifica della validità del token
    public void validateToken(String token){
        Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).
                build().parse(token);
    }

    public String refreshToken(String oldToken) {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(oldToken);

               Claims claims = claimsJws.getBody();

        return Jwts.builder()
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + duration))
                .subject(claims.getSubject())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
}

