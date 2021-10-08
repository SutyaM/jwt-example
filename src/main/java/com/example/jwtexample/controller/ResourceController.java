package com.example.jwtexample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ResourceController {

  //ennél a két GET hívásnál szükség lesz a jwTokenre
  //először indítanunk kell egy POST -ot a /api/authentication végpontra
  //úgy, hogy a Body -ba írjuk bele a username és password értékeket (JSON)
  //innen vissza fogunk kapni egy tokent, amit utána a Postman -en belül
  //az Authorization fül alá kell bemásolnunk, itt bal oldalt
  //állítsuk át a Type -ot Bearer Tokenre, jobb oldalt pedig másoljuk be a tokent

  //user és admin is hívhatja
  @GetMapping("/hello-user")
  public String getUser() {
    return "Hello User!";
  }

  //itt viszont csak admin szerepkörű felhasználót hellózunk le
  @GetMapping("/hello-admin")
  public String getAdmin() {
    return "Hello Admin!";
  }

}
