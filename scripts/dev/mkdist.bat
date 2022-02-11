@echo off
if exist tmp_dist rd /S /Q tmp_dist
if exist release.zip del release.zip
mkdir tmp_dist

set old_cd=%cd%

title Compiling
G:

cd G:\github\NovaCore

call build.bat

cd G:\github\MCTournamentSystem

call mvn clean package

cd G:\github\NovaSkywars

call mvn clean package

cd G:\github\NovaSurvivalGames

call mvn clean package

cd G:\github\NovaSpleef

call mvn clean package

cd G:\github\NovaMissileWars

call mvn clean package

cd G:\github\NovaBingo

call mvn clean package

C:

cd %old_cd%

title Creating release

echo Creating folders
mkdir tmp_dist\content
mkdir tmp_dist\content\bungee
mkdir tmp_dist\content\spigot
mkdir tmp_dist\content\bin
mkdir tmp_dist\content\www_app

echo Copy manifest
copy %cd%\update_manifest.json %cd%\tmp_dist\content\update_manifest.json /Y

echo Copy web app
xcopy G:\github\MCTournamentSystem\www_app tmp_dist\content\www_app /s /e

echo Copy bungee plugins
REM Bungee plugins
copy G:\github\NovaCore\NovaCore-Bungeecord.jar %cd%\tmp_dist\content\bungee\NovaCore.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Bungeecord\target\MCTournamentSystem-Bungeecord-1.0.0-SNAPSHOT.jar %cd%\tmp_dist\content\bungee\TournamentSystem.jar /Y

echo Copy spigot plugins
REM Nova core and utils
copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\tmp_dist\content\spigot\NovaCore.jar /Y
copy G:\github\NovaCore\NovaUtils.jar %cd%\tmp_dist\content\spigot\NovaUtils.jar /Y

REM Game engine
copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\tmp_dist\content\spigot\NovaCore-GameEngine.jar /Y
copy G:\github\NovaSurvivalGames\target\NovaSurvivalGames-1.0.0-SNAPSHOT.jar %cd%\tmp_dist\content\spigot\NovaSurvivalGames.jar /Y

REM Tournament system
copy G:\github\MCTournamentSystem\MCTournamentSystem-Spigot\target\MCTournamentSystem-Spigot-1.0.0-SNAPSHOT.jar %cd%\tmp_dist\content\spigot\TournamentSystem.jar /Y

REM Lobby
copy G:\github\MCTournamentSystem\MCTournamentSystem-Lobby\target\MCTournamentSystem-Lobby-1.0.0-SNAPSHOT.jar %cd%\tmp_dist\content\spigot\TournamentSystemLobby.jar /Y

REM Games
copy G:\github\NovaSurvivalGames\target\NovaSurvivalGames-1.0.0-SNAPSHOT.jar  %cd%\tmp_dist\content\spigot\NovaSurvivalGames.jar /Y
copy G:\github\NovaSpleef\target\NovaSpleef-1.0.0-SNAPSHOT.jar  %cd%\tmp_dist\content\spigot\NovaSpleef.jar /Y
copy G:\github\NovaBingo\target\NovaBingo-1.0.0-SNAPSHOT.jar  %cd%\tmp_dist\content\spigot\NovaBingo.jar /Y
copy G:\github\NovaSkywars\target\NovaSkywars-1.0.0-SNAPSHOT.jar  %cd%\tmp_dist\content\spigot\NovaSkywars.jar /Y
copy G:\github\NovaMissileWars\target\NovaMissileWars-1.0.0-SNAPSHOT.jar %cd%\tmp_dist\content\spigot\NovaMissileWars-1.0.0-SNAPSHOT.jar /Y

echo Copy binaries
copy G:\github\MCTournamentSystem\MCTournamentSystem-WebUiLauncher\target\WebUILauncher-1.0.0-SNAPSHOT.jar %cd%\tmp_dist\content\bin\OpenWebUI.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Updater\target\MCTournamentSystem-Updater-1.0.0-SNAPSHOT.jar %cd%\tmp_dist\content\bin\NovaUpdater.jar /Y

REM 

echo Creating zip file

powershell Compress-Archive -LiteralPath 'tmp_dist/content' -DestinationPath "tmp_dist\release.zip"

copy tmp_dist\release.zip .\release.zip

rd /S /Q tmp_dist

title Done

pause