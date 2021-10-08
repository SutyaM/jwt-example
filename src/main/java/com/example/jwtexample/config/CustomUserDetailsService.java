package com.example.jwtexample.config;

import com.example.jwtexample.model.DAOUser;
import com.example.jwtexample.model.UserDTO;
import com.example.jwtexample.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  //a SpringSecurityConfiguration -ben létrehozott Bean a save() -ben használjuk majd az új user jelszavának titkosítására
  @Autowired
  private PasswordEncoder bcryptEncoder;

  //megkeressük a usert username alapján, ha nem null akkor visszaadunk egy new User() ojjektumok a szükséges adatokkal
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    List<SimpleGrantedAuthority> roles = null;

    //előkaparjuk a usert a felhasználóneve alapján
    DAOUser user = userRepository.findByUsername(username);
    if (user != null) {
      //ha a user nem null akkor kiszedjük a role -ját egy List -be amit utána tovább tudunk adni a Spring security saját User osztályának
      roles = Arrays.asList(new SimpleGrantedAuthority(user.getRole()));
      return new User(user.getUsername(), user.getPassword(), roles);
    }
    //ha nincs ilyen felhasználónevű userünk eldobjuk az egészet
    throw new UsernameNotFoundException("User not found with the name " + username);
  }

  //itt mentjük el a usert a registrate végpontra való sikeres hívás után
  public DAOUser saveUser(UserDTO user) {
    DAOUser newUser = new DAOUser();
    newUser.setUsername(user.getUsername());
    newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
    newUser.setRole(user.getRole());
    return userRepository.save(newUser);
  }
}
