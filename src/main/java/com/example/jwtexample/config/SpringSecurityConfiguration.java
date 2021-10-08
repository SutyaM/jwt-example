package com.example.jwtexample.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration  extends WebSecurityConfigurerAdapter {

  @Autowired
  CustomUserDetailsService userDetailsService;

  @Autowired
  CustomJwtAuthenticationFilter customJwtAuthenticationFilter;

  @Autowired
  JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  //erre majd a CustomUserDetailsService -ben lesz szükségünk
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManager() throws Exception {
    return  super.authenticationManager();
  }

  //beállítjuk, hogy a mi saját userDetailService -ünk legyen használva
  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
            .authorizeRequests()                                               //autorizáljuk a hívásokat
            .antMatchers("/api/hello-admin")                        //ha a meghívott végpont /api/hello-admin
            .hasRole("ADMIN")                                                  //csak akkor engedélyezzük ha a felhasználó role ADMIN
            .antMatchers("/api/hello-user")                         //ha a meghívott végpont /api/hello-user
            .hasAnyRole("USER", "ADMIN")                                //engedélyezzük ha a felhasználó role USER vagy ADMIN
            .antMatchers(
                    "/api/authenticate",
                    "/api/register",
                    "/h2-console/**")      //ha a meghívott végpont /api/authenticate, /api/register vagy /h2-console/
            .permitAll()                //akkor engedélyezzük mindenkinek, hogy meghívhassa (a /h2-console végponton érhetitek el az in-memory db-t amit felhúzunk elindításkor
            .anyRequest().authenticated()
            .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint) //ha exception fordul elő akkor a jwtAuthenticationEntryPoint -ot hívjuk
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterBefore(customJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); //beállítjuk, hogy az általunk írt customJwtAuthenticationFilter minden hívás előtt lefusson

    //erre itt csak azért van szükség, hogy a spring security ne ölje meg a h2-consolet és tudjuk browserből nézni
    http.headers().frameOptions().disable();
  }
}
