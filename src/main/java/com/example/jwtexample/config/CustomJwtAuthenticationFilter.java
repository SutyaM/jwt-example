package com.example.jwtexample.config;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomJwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
    String jwtToken = extractJwtFromRequest(request);

    try {
      //ellenőrizzük, hogy a token valid-e a jwtUtil -ban megírt validateToken() -nel
      if (StringUtils.hasText(jwtToken) && jwtUtil.validateToken(jwtToken)) {
        //kiszedjük a tokenből a felhasználónevet és a roles -t a jwtUtilban megírt functionjeink segítségével
        //és létrehozunk belőlük egy új UserDetails objectet
        UserDetails userDetails = new User(jwtUtil.getUserNameFromToken(jwtToken), "", jwtUtil.getRolesFromToken(jwtToken));

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, "", jwtUtil.getRolesFromToken(jwtToken));

        //eltároljuk a usernamePasswordAuthenticationToken -t a securityContextHolder -ben
        //itt közöljük a SprinSecurity -vel, hogy ez a felhasználó autorizálva lett
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      } else {
        //az ilyesmiket általában egy Logger -rel szoktuk kiírni, de nem tudom ismeritek-e és most nem akarom ezzel még bonyolítani
        System.out.println("Security context cannot be set.");
      }
    } catch (ExpiredJwtException e) {
      //eltároljuk az exceptiont a requestben, hogy aztán a JwtAuthenticationEntryPoint -ban kiszedhessük és a felhasználó arcába dobjuk
      request.setAttribute("exception", e);
    } catch (BadCredentialsException e) {
      request.setAttribute("exception", e);
    }
    //tovább engedjük a többi filternek de jelen helyzetben innentől már a kutyát sem érdekli itt mi történik
    filterChain.doFilter(request, response);
  }

  private String extractJwtFromRequest(HttpServletRequest request) {
    //kiszedjük a tokent a request headerjéből
    String bearerToken = request.getHeader("Authorization");

    //ellenőrizzük, hogy a bearerToken nem üres illetve, hogy "Bearer " -el kezdődik-e
    if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      //a substring -elésre azért van szükség mert mikor leküldjük a tokenünket
      //mindig befog elé kerülni egy "Bearer " string, ezért az első 7 karaktert mindig
      //levágjuk, így csak a saját tokenünk marad
      return bearerToken.substring(7);
    }
    return null;
  }
}
