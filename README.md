Wypożyczalnia Sprzętu Sportowego — Projekt Grupowy

Projekt realizowany w ramach przedmiotów:
Aplikacje Wielowarstwowe oraz Metody Dostępu do Danych

Repozytorium zawiera działającą aplikację REST API (SparkJava), moduł serializacji oraz moduł JDBC z SQLite.

1. Wymagania — instalacja i konfiguracja środowiska

Aby uruchomić projekt, każda osoba w zespole musi przejść poniższe kroki.

1.1 Instalacja JDK (wymagane!)

Pobierz JDK z Adoptium:
https://adoptium.net

Zalecana wersja:

JDK 21 lub 17

(projekt kompiluje się na obu)

Po instalacji sprawdź:

java -version

Powinno wyświetlić wersję JDK, np.:

openjdk version "21.0.x"

1.2 Konfiguracja JAVA_HOME i PATH
KROK A: Znajdź folder instalacji JDK

Zwykle:

C:\Program Files\Eclipse Adoptium\jdk-21.x.x

Musi zawierać foldery bin, lib, itd.

KROK B: Ustaw JAVA_HOME

Start → "Edit the system environment variables"

Environment Variables

System variables → New

Name:

JAVA_HOME

Value (przykład):

C:\Program Files\Eclipse Adoptium\jdk-21.0.1

KROK C: Dodaj JDK do PATH

System variables → Path → Edit → New:

%JAVA_HOME%\bin

Zapisz.

KROK D: Sprawdź poprawność

Nowe okno PowerShell/CMD:

echo %JAVA_HOME%
java -version

Jeśli widzisz JDK → wszystko gotowe.

1.3 Instalacja MAVEN (opcjonalnie)
Uwaga:

Projekt można uruchamiać bez instalowania Mavena – poprzez:

.\mvnw.cmd clean package

Jednak jeśli chcesz mieć mvn globalnie:

KROK A: Pobierz Mavena

https://maven.apache.org/download.cgi

Pobierz Binary zip archive (3.9.x)

KROK B: Rozpakuj

Polecana lokalizacja:

C:\Program Files\Maven\apache-maven-3.9.11

Upewnij się, że struktura wygląda tak:

apache-maven-3.9.11/
bin/
lib/
conf/

a NIE:

apache-maven-3.9.11/apache-maven-3.9.11/ ← ŹLE

KROK C: Dodaj Maven do PATH

System variables → Path → New:

C:\Program Files\Maven\apache-maven-3.9.11\bin

KROK D: Sprawdzamy:
mvn -version

1.4 Konfiguracja VS Code

Zainstaluj rozszerzenia:

Extension Pack for Java

Maven for Java

Debugger for Java

Project Manager for Java

Wybór JDK w VS Code

Ctrl + Shift + P

Java: Configure Java Runtime

W sekcji Workspace JDK wybierz zainstalowaną wersję JDK (17/21)

2. Budowanie projektu
   Build:
   mvn clean package

lub (gdy Maven nie jest zainstalowany globalnie):

.\mvnw.cmd clean package

3. Uruchamianie aplikacji
   mvn exec:java

Serwer startuje na:

http://localhost:8080

Endpoint testowy:
http://localhost:8080/hello

4. Struktura projektu
   src/main/java/pl/projekt/sprzet/
   ├─ api/ # Endpointy REST
   ├─ model/ # Modele domenowe i serializacja
   ├─ db/ # Moduł JDBC
   └─ App.java # Punkt startowy aplikacji

5. Autorzy

Piotr Cierpiał

Tomasz Czyż

Joanna Sil
