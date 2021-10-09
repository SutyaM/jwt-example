package com.example.jwtexample.config;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JwtUtil {

  private String secret;
  private int jwtExpirationInMs;

  //betöltjük az application.properties fájlban deklarált jwt.secret értékét
  @Value("${jwt.secret}")
  public void setSecret(String secret) {
    this.secret = secret;
  }

  //betöltjük az application.properties fájlban deklarált jwt.expirationInMs értékét, ami ha tudok számolni jelenleg 30 perc
  @Value("${jwt.expirationDateInMs}")
  public void setJwtExpirationInMs(int jwtExpirationInMs) {
    this.jwtExpirationInMs = jwtExpirationInMs;
  }

  //token generálás
  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    //kiszedjük a userDetails -ből a user role -ját
    Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

    //ellenőrizzük, hogy a roles -ban szerepel-e az ADMIN szerepkör
    if (roles.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
      //ha a felhasználó szerepköreiben szerepel az ADMIN akkor hozzáadunk a claims -hez egy isAdmin-true kulcs-érték párt
      claims.put("isAdmin", true);
    }
    //megtesszük ugyenezt, de most a USER szerepkörrel
    if (roles.contains(new SimpleGrantedAuthority("ROLE_USER"))) {
      claims.put("isUser", true);
    }
    return doGenerateToken(claims, userDetails.getUsername());
  }

  private String doGenerateToken(Map<String, Object> claims, String userName) {
    return Jwts.builder()                   //meghívjuk a JWT buildert
            .setClaims(claims)              //setteljük a claim -et amit a generateToken() -ben hoztunk létre és töltöttünk fel adatokkal
            .setSubject(userName)           //setteljük a userName -t amit a userDetails -ből nyertünk ki a generateToken() -ben
            .setIssuedAt(new Date(System.currentTimeMillis())) //setteljük, hogy mikor generáltuk a tokent, ez a jelenlegi idő lesz ezredmásodpercben
            .setExpiration(new Date(System.currentTimeMillis()+jwtExpirationInMs)) //setteljük, hogy mikor járjon le a token ez a jelenlegi idő plusz az application.properties -ben deklarált jwt.expirationInMs értéke lesz
            .signWith(SignatureAlgorithm.HS512, secret) //lekreáljuk a signature -t ami az application.properties -ben deklarált jwt.secret -et használja majd
            .compact();
  }

  //token validálás, megnézzük, hogy nem járt-e le, megfelelő-e a signature, stb.
  public boolean validateToken(String authToken) {
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
      return true;
    } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
      throw new BadCredentialsException("INVALID_CREDENTIALS", e);
    } catch (ExpiredJwtException e) {
      throw e;
    }
  }

  //kiszedjük a felhasználónevet a tokenből
  public String getUserNameFromToken(String token) {
    Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

    //azért return -öljük a claims subjectjét mert ebbe helyeztük bele a username -t a fentebbi doGenerateToken() -ben
    //a token létrehozásakor
    return claims.getSubject();
  }

  //kiszedjük a role -okat a tokenből
  public List<SimpleGrantedAuthority> getRolesFromToken(String token) {
    Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

    List<SimpleGrantedAuthority> roles = null;

    //megnézzük, hogy a claims tartalmazza-e az isAdmin illetve az isUser értékeket
    //ezeket az értékeket a fentebbi generateToken() -ben helyeztük bele a claims -be
    Boolean isAdmin = claims.get("isAdmin", Boolean.class);
    Boolean isUser = claims.get("isUser", Boolean.class);

    //ellenőrizzük, hogy az isAdmin érték true -e, és ha igen behelyezzük a ROLE_ADMIN -t a roles-ba
    if (isAdmin != null && isAdmin) {
      roles = Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    //ugyanez az isUserrel és ROLE_USER -rel
    if (isUser != null && isUser) {
      roles = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    return roles;
  }
}
