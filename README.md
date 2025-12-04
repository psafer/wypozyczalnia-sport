# Wypożyczalnia Sprzętu Sportowego — Projekt Grupowy

Projekt realizowany w ramach przedmiotów **Aplikacje Wielowarstwowe** oraz **Metody Dostępu do Danych**.  
Repozytorium zawiera działającą aplikację REST API (SparkJava), moduł serializacji oraz moduł JDBC z SQLite.

---

## 1. Wymagania — instalacja i konfiguracja środowiska

Aby uruchomić projekt, **każda osoba w zespole** musi wykonać poniższe kroki.

---

### 1.1 Instalacja JDK (wymagane)

Pobierz JDK z Adoptium:  
https://adoptium.net

Zalecana wersja: **JDK 21** (działa także JDK 17)

Sprawdzenie instalacji:

```bash
java -version
```

---

### 1.2 Konfiguracja JAVA_HOME i PATH

#### KROK A — znajdź folder instalacji JDK

Przykład:

```
C:\Program Files\Eclipse Adoptium\jdk-21.x.x
```

#### KROK B — ustaw JAVA_HOME

1. Start → *Edit the system environment variables*
2. *Environment Variables*
3. *System variables* → **New**
4. Name: `JAVA_HOME`
5. Value:
```
C:\Program Files\Eclipse Adoptium\jdk-21.0.1
```

#### KROK C — dodaj JDK do PATH

System variables → *Path* → *Edit* → *New*:

```
%JAVA_HOME%\bin
```

#### KROK D — test poprawności

```bash
echo %JAVA_HOME%
java -version
```

---

### 1.3 Instalacja Maven (opcjonalnie)

Projekt można uruchamiać bez instalacji Maven, używając wrappera:

```bash
.\mvnw.cmd clean package
```

Jeśli chcesz korzystać z globalnego `mvn`:

1. Pobierz Maven z: https://maven.apache.org/download.cgi  
2. Rozpakuj do:
```
C:\Program Files\Maven\apache-maven-3.9.11
```
3. Upewnij się, że struktura wygląda tak:
```
apache-maven-3.9.11/
    bin/
    lib/
    conf/
```
4. Dodaj do PATH:

```
C:\Program Files\Maven\apache-maven-3.9.11\bin
```

Test:

```bash
mvn -version
```

---

### 1.4 Konfiguracja Visual Studio Code

Zainstaluj rozszerzenia:

- Extension Pack for Java  
- Maven for Java  
- Debugger for Java  
- Project Manager for Java  

Wybór JDK w VS Code:

1. `Ctrl + Shift + P`
2. Wpisz: **Java: Configure Java Runtime**
3. W sekcji *Workspace JDK* wybierz zainstalowaną wersję JDK

---

## 2. Budowanie projektu

Standardowe uruchomienie:

```bash
mvn clean package
```

Jeśli Maven nie jest zainstalowany globalnie:

```bash
.\mvnw.cmd clean package
```

---

## 3. Uruchamianie aplikacji

```bash
mvn exec:java
```

Serwer startuje pod adresem:

```
http://localhost:8080
```

Testowy endpoint:

```
http://localhost:8080/hello
```

---

## 4. Struktura projektu

```
src/main/java/pl/projekt/sprzet/
 ├─ api/       # Endpointy REST
 ├─ model/     # Modele domenowe i serializacja
 ├─ db/        # Moduł JDBC
 └─ App.java   # Punkt startowy aplikacji
```

---

## 5. Autorzy

- **Piotr Cierpiał**  
- **Tomasz Czyż**  
- **Joanna Sil**
