# Codex Naturalis

<div>
  <img src="https://studiobombyx.com/assets/Slider-Codex-2-1920x1080.jpg"/>
</div>

![Static Badge](https://img.shields.io/badge/JDK-21-orange)
![Static Badge](https://img.shields.io/badge/AY-2023%2F24-green)

## Project Specifications
This project aims at creating a digital  and distributed version, written in Java, of the board game **Codex Naturalis** by **Cranio Creations**, allowing up to four (4) players to play a game on a local network. 

The original board game can be found, and bought, [here (IT)](https://www.craniocreations.it/prodotto/codex-naturalis) or [here (EN)](https://studiobombyx.com/en/produit/codex-naturalis-precommande/).

In order to play the game correctly and enjoy the game at it's full capability, you should read and learn all the rules, which can be found [here (IT)](https://www.craniocreations.it/storage/media/product_downloads/126/1516/CODEX_ITA_Rules_compressed.pdf) or [here (EN)](https://cdn.1j1ju.com/medias/a7/d7/66-codex-naturalis-rulebook.pdf).

## UML Diagrams
The project's UML diagrams (class diagrams), will be uploaded whenever produced; mainly, there will be an initial diagram, with the basic model of the game, which may contain flaws or lack some elements, and the final version, which will be automatically generated by specific tools.

- [Initial UML](https://github.com/emanuelegreco29/IngSw1_Project/blob/30491dcc3699d568ddcc3111b69df244a055cd85/DELIVERABLES/UML%20diagrams/UML%20-%20am43.pdf)
- [Network Design Sequence Diagram](https://github.com/emanuelegreco29/IngSw1_Project/blob/30491dcc3699d568ddcc3111b69df244a055cd85/DELIVERABLES/COMMUNICATION%20DESIGN/COMMUNICATION%20DESIGN%20-%20AM43.pdf)
- [Final UML - Automatically Generated](https://github.com/emanuelegreco29/IngSw1_Project/blob/30491dcc3699d568ddcc3111b69df244a055cd85/DELIVERABLES/UML%20diagrams/FINAL%20High-level%20view.png)

## Prerequisites
After having read and learnt the rules, which always is the most fundamental requirement to have fun, you need to do other little steps before launching the game. You will need to download and install Java Development Kit 21 (JDK 21) or higher to run the game successfully, you can find the download links here:

- [Windows x64 Installer](https://download.oracle.com/java/21/archive/jdk-21.0.2_windows-x64_bin.exe)
- [MacOS x64 Installer](https://download.oracle.com/java/21/archive/jdk-21.0.2_macos-x64_bin.dmg)
- [MacOS Arm Installer](https://download.oracle.com/java/21/archive/jdk-21.0.2_macos-aarch64_bin.dmg)
- [Linux x64 Archive](https://download.oracle.com/java/21/archive/jdk-21.0.2_linux-x64_bin.tar.gz)
- [Linux Arm Archive](https://download.oracle.com/java/21/archive/jdk-21.0.2_linux-aarch64_bin.tar.gz)

## How to run
To run the Client and/or the Server, simply download the .jar files from the [DELIVERABLES](https://github.com/emanuelegreco29/IngSw1_Project/tree/95a7d9b2d8eef6e2c9f907db86b8b7b3641eb69c/DELIVERABLES) folder, open a terminal window in the same directory where you saved the files and run the command:
```
java -jar CodexNaturalis.jar
```
to run the Client app or
```
java -jar CodexServer.jar
```
to run the Server.

Our game implementation also offers the possibility to reload a match in case the server or a player disconnects. To reload a game, simply choose the option to do so when running the server (which must be run by the same machine that hosted the game previously) and insert the ID of the game to reload. Game files, named by ID, can be found in the user's main directory, under the 'CodexNaturalis' folder. For example, for Windows users the directory will be C:\Users\ [user]\CodexNaturalis.

## Troubleshooting
If you are encountering issues when connecting to the server, please make sure to be on the same network as the machine hosting the server, better if a small network with only the server and the clients who wants to play connected. The user starting the server should also make sure to run the server on its local IP and NOT on localhost, otherwise the other machines will not be able to see the server as available. When playing the game from Textual User Interface (TUI), typing while waiting for others players' turns will not show any character typed, do not panic! Characters are registered correctly and, when sent, if the input inserted is a valid command, the command will be run by the application successfully.


## Implemented Functionalities
| Functionality | Status |
|:-----------------------|:------------------------------------:|
| Basic rules | ✅ |
| Complete rules | ✅ |
| TUI | ✅ |
| GUI | ✅ |
| RMI | ✅ |
| Socket | ✅ |
| Multiple games | ⛔ |
| Persistance | ✅ |
| Disconnection Resilience | ⛔ |
| Chat | ✅ |

#### Legend
⛔ - Not Implemented; ⚠️ - Work In Progress; ✅ - Implemented

## Group Members
- [Matteo Badialetti](https://github.com/mbadialetti)
- [Simone Colecchia](https://github.com/Colsim01)
- [Emanuele Greco](https://github.com/emanuelegreco29)
- [Francesco Mario Inzerillo](https://github.com/francinzepolimi)

## Disclaimer
IT: **Codex Naturalis** è un gioco da tavolo sviluppato ed edito da **Cranio Creations Srl**. I contenuti grafici di questo progetto riconducibili al prodotto editoriale da tavolo sono utilizzati previa approvazione di Cranio Creations Srl **a solo scopo didattico**. È severamente vietata la distribuzione, la copia o la riproduzione dei contenuti e immagini in qualsiasi forma al di fuori del progetto, così come la redistribuzione e la pubblicazione dei contenuti e immagini a fini diversi da quello sopracitato. È inoltre vietato l'utilizzo commerciale di suddetti contenuti.

EN: **Codex Naturalis** is a board game developed and published by **Cranio Creations Srl**. The graphic contents of this project related to the editorial board game product are used with the approval of Cranio Creations Srl **for educational purposes only**. Distribution, copying, or reproduction of the contents and images in any form outside of this project is prohibited, as is the redistribution and publication of the contents and images for purposes other than the aforementioned. Additionally, commercial use of said contents is prohibited.
