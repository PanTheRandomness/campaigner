# Campaigner

Campaigner is a tool for managing D&D (and other TTRPGs) campaigns, timelines and worlds.
__________________________________________________________________________________________________

<h3>Arviointikriteerit: </h3>
<h4>Arvosana 1</h4>
✅ Yksi entiteetti*: `Event` <br>
✅ Yksinkertainen suodatus yhdelle sarakkeelle: `Events: name` <br>
✅ Globaalien tyylien muuttaminen <br>
✅ SPA-sovellus, jossa on päänäkymä <br>
✅ Security-palikan käyttöönotto <br>

<h4>Arvosana 2</h4>
✅ Toinen entiteetti*, jolle edelliselle toteutettuna `1-1`-relaatio: `EventDuration` <br>
✅ Yksinkertainen suodatus kahden tai useamman sarakkeen mukaan: `Events: name, place` <br>
✅ Tyylien muokkaaminen suoraan yksittäiselle komponentille <br>
✅ Header <br>
✅ Sisäänkirjautumissivun luominen <br>

<h4>Arvosana 3</h4>
✅ Kolmas entiteetti*, sille vähintään toteutettuna `1-m`-relaatio: `Campaign` <br>
✅ Suodatus relaatiossa olevan entiteetin osalta: `Events: area` <br>
✅ Tyylien muokkaaminen näkymässä yksittäiselle komponentille <br>
✅ Toimiva navigointipalkki <br>
✅ Käyttäjäentiteetin luominen ja roolien määrittäminen (Admin/user) <br>

<h4>Arvosana 4</h4>
✅ Neljäs entiteetti* ja toteutus `m-m`-relaatiosta: `User: Campaign (GM), Campaign (player)` <br>
✅ Tee vielä yksi hakuehto suodattimeen: `Events: EventType` <br>
✅ Vaihda käyttöön eri Lumo Utility luokkia <br>
✅ Footer <br>
✅ Toteuta: - Kaikki käyttäjät näkevät päänäkymän: `Home` - User ja Admin käyttäjät näkevät jonkun sivun: `Campaigns` - Sivu pelkästään ADMIN käyttäjille: `Users` <br>

<h4>Arvosana 5</h4>
✅ Kaikille edellä oleville myös muokkaus ja poisto: Kaikille tässä luetelluille toimii! `EventDuration`-muokkaus tapahtuu `Event`-muokkauksen kautta <br>
✅ Tee vielä viides hakuehto suodattimeen: `Events: ReoccurrenceType` <br>
✅ Kustomoidun tyylitiedoston lisääminen ja sen käyttäminen luokkamäärittelyllä: `campaigner-custom.css` <br>
✅ Selkeästi erityyppisiä sisältösivuja vähintään kolme kappaletta: Edelliset pitää toimia näiden kanssa <br>
❓ Kustomoitu virheviesti jos user yrittää admin-sivulle: yritetty<br>


<h4>Lisäpisteet: </h4>
 ✅ Työ julkaistu GIT:iin <br>
 ✅ Salasanojen suojaus Vaadin Securityn Hashauksella <br>
 ✅ Server Push toteutettu sovelluksessa: esim. eventGrid ja userGrid <br>
 ✅ Lokalisointi vähintään yhdellä kielellä: suomi <br>
 ❌ Työstä tehty Docker Image <br>
 ❌ Tietokannan ja sovelluksen määrittäminen Docker Composen avulla <br>
 
❗Docker-toiminnallisuudet toteutan mahdollisesti vasta, kun sovellus on kokonaan/lähes valmis. Nykyisillään se kattaa vain arviointikriteerejä, mutta jatkan sen kehitystä kurssin jälkeenkin!

*sis. `CRUD: Haku & Tallennus` & `Repository UI:lta taustapalvelimen kautta tietokantaan`

## Running the application

Open the project in an IDE. You can download the [IntelliJ community edition](https://www.jetbrains.com/idea/download) if you do not have a suitable IDE already.
Once opened in the IDE, locate the `Application` class and run the main method using "Debug".

For more information on installing in various IDEs, see [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/getting-started/import).

If you install the Vaadin plugin for IntelliJ, you should instead launch the `Application` class using "Debug using HotswapAgent" to see updates in the Java code immediately reflected in the browser.

## Deploying to Production

The project is a standard Maven project. To create a production build, call 

```
./mvnw clean package -Pproduction
```

If you have Maven globally installed, you can replace `./mvnw` with `mvn`.

This will build a JAR file with all the dependencies and front-end resources,ready to be run. The file can be found in the `target` folder after the build completes.
You then launch the application using 
```
java -jar target/campaigner-1.0-SNAPSHOT.jar
```

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `src/main/frontend` contains the client-side JavaScript views of your application.
- `themes` folder in `src/main/frontend` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorial at [vaadin.com/docs/latest/tutorial/overview](https://vaadin.com/docs/latest/tutorial/overview).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/docs/latest/components](https://vaadin.com/docs/latest/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Build any UI without custom CSS by discovering Vaadin's set of [CSS utility classes](https://vaadin.com/docs/styling/lumo/utility-classes). 
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Forum](https://vaadin.com/forum).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).
