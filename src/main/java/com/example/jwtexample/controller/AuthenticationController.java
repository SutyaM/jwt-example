package com.example.jwtexample.controller;

import com.example.jwtexample.config.CustomUserDetailsService;
import com.example.jwtexample.config.JwtUtil;
import com.example.jwtexample.model.AuthenticationRequest;
import com.example.jwtexample.model.AuthenticationResponse;
import com.example.jwtexample.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthenticationController {

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  @Autowired
  private JwtUtil jwtUtil;

  //egy POST amin keresztül autentikálhatjuk a felhasználót a RequestBody -ba beírt felhasználónév és jelszó alapján
  //figyeljünk, hogy a Postman -ben ne felejtsük el beírni ezt a két értéket a body -ba, és hogy megfeleljen az AuthenticationRequest modellnek
  @PostMapping("/authenticate")
  public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
    throws Exception {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
    } catch (DisabledException e) {
      throw new Exception("USER_DISABLED", e);
    } catch (BadCredentialsException e) {
      throw new Exception("INVALID CREDENTIALS", e);
    }
    //a loadUserByUsername segítségével kiszedjük a már titkosított jelszót és a role -t a requestből küldött username alapján
    UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

    //legeneráljuk a tokent a jwtUtil -ban megírt generate segítségével
    String token = jwtUtil.generateToken(userDetails);

    //felküldjük a tokent egy AuthenticateResponse -ban
    //ha a Postman -ben visszakapott tokent betesszük egy online jwt decoderbe megnézhetjük mi is van benne
    return ResponseEntity.ok(new AuthenticationResponse(token));
  }


  //erre a POST -ra hívva tudunk új usert regisztrálni, a Postman -ben 3 értéket kell megadni a Body -ban:
  //  "username": "valamiUser",
  //  "password": "valamiPassword",
  //  "role": "ROLE_ADMIN" vagy "ROLE_USER" (attól függően admin vagy user jogú felhasználót akarunk létrehozni)
  @PostMapping("/register")
  public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
    return ResponseEntity.ok(userDetailsService.saveUser(user));
  }

}
