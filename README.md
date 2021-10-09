# jwt-example

Csináltam egy példát a jwt használatára. Ha a jóisten is úgy akarja sikeresen beindul majd nálatok és tudjátok nézegetni.
H2 in-memory adatbázist használ amibe NEM töltünk adatot induláskor. Ha valaki nem bír magával nyugodtan írja meg magának az adat betöltést.

Nem tudom h2-consolet használtatok-e már, de ha nem akkor íme:
miután beindítjátok a cuccot a böngészőben(ne a Postmanben) hívjatok rá a localhost:8080/h2-console -ra, itt meg fog jelenni a console.
Írjátok át a JDBC URL -t "jdbc:h2:mem:jwt_example_db" -re. user=user, password=password. Ezeket az application.properties -ben át tudjátok írni, ha szeretnétek
átemelhetitek az összes itteni értéket egy application.yml -be, de akkor arra figyeljetek, hogy a properties -ből töröljétek mert az application.properties felül-
írja az application.yml -t és ha csak a yml -be változtattok majd mehet a fejfogás, hogy miért nem működik.
Ha sikerült belépni akkor itt láthatjátok az in-memory DB-t táblákkal mindennel. Tudtok SQL commandokat is írni. Jelenleg csak a user mentés van végpontra kivezetve
a többi (getById, getAll, update, delete) nincs, de ebben a példában nem is ezek a lényegesek. Gyakorlásképp hozzáadhatjátok.

A működés röviden:
/api/register végpontra POST -olunk. Létrehozzuk az új felhasználót.
Példa felhasználók:
{
  "username": "Alpha",
  "password": "alpha123",
  "role": "ROLE_ADMIN"
}
{
  "username": "Bravo",
  "password": "bravo456",
  "role": "ROLE_USER"
}

A role "ROLE_ADMIN" vagy "ROLE_USER" lehet csak.

Ha létrejött a felhasználó hívjunk rá a POST /api/authenticate végpontra. A Body -ban legyen egy username és egy password.
Pl:
{
  "username": "Alpha",
  "password": "alpha123"
}
Ha sikeres a hívás vissza fogunk kapni egy TOKEN -t ami tárolja a felhasználónk nevét és role -ját. Ezt copyzzuk is ki.

Na itt jön a csoda. A fenti végpontokon kívül van még egy /api/hello-user és egy /api/hello-admin végpont. Mind a kettő GET.
Ha ezeket akarjuk meghívni akkor a Postman-ben az Authorization fülön át kell állítanunk a Type -ot Bearer Token -re,
és jobb oldalt a Token mezőbe be kell másolnunk a /api/authenticate hívásból kapott tokent.
Ha a token egy ROLE_USER felhasználóhoz tartozik akkor ő el tudja értni a /api/hello-user végpontot és visszakap egy Hello-t, 
viszont a /api/hello-admin végpont már nem fog köszönni neki. Ha a token ROLE_ADMIN felhasználóhoz tartozik akkor mindkét végpontot vígan hivogathatja.
Ha nincs bent token, vagy hibás, vagy lejárt akkor nem tudjuk elérni egyik végpontot sem.

DEBUG módban indítsátok el, de előtte szórjátok tele breakpointokkal az egészet, hogy akadjon minden functionben. Ugye ha debug módban megakasztottuk a kódot valahol
akkor az F8 -al tudunk egyet előre lépni és az F9 -el továbberesztjük a kódot egészen a következő breakpointig. Azért örülnék ha így csinálnátok mert írtam elég sok
kommentet a kódba, hogy mit miért mikor csinálunk, és ha debug módban csak lépegettek akkor ezeket sorrendben tudjátok majd olvasni és hátha jobban összeáll a kép.

Ha van kérdésetek írjatok, megpróbálok válaszolni amint tudok.
Remélem ez segít valamennyit a jwt tisztázásában.
