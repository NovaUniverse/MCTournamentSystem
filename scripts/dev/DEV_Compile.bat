@echo off

set old_cd=%cd%

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

REM ==== PROXY ====
echo Copying plugins for Proxy
copy G:\github\NovaCore\NovaCore-Bungeecord.jar %cd%\Servers\proxy\plugins\NovaCore.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Bungeecord\target\MCTournamentSystem-Bungeecord-1.0.0-SNAPSHOT.jar %cd%\Servers\proxy\plugins\TournamentSystem.jar /Y

REM ==== LOBBY ====
echo Copying plugins for Lobby
copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\Servers\lobby\plugins\NovaCore.jar /Y
copy G:\github\NovaCore\NovaUtils.jar %cd%\Servers\lobby\plugins\NovaUtils.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Spigot\target\MCTournamentSystem-Spigot-1.0.0-SNAPSHOT.jar %cd%\Servers\lobby\plugins\TournamentSystem.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Lobby\target\MCTournamentSystem-Lobby-1.0.0-SNAPSHOT.jar %cd%\Servers\lobby\plugins\TournamentSystemLobby.jar /Y

REM ==== SURVIVAL GAMES ====
echo Copying plugins for Survivalgames
copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\Servers\survivalgames\plugins\NovaCore.jar /Y
copy G:\github\NovaCore\NovaUtils.jar %cd%\Servers\survivalgames\plugins\NovaUtils.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Spigot\target\MCTournamentSystem-Spigot-1.0.0-SNAPSHOT.jar %cd%\Servers\survivalgames\plugins\TournamentSystem.jar /Y

copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\Servers\survivalgames\plugins\NovaCore-GameEngine.jar /Y
copy G:\github\NovaSurvivalGames\target\NovaSurvivalGames-1.0.0-SNAPSHOT.jar %cd%\Servers\survivalgames\plugins\NovaSurvivalGames.jar /Y

REM ==== SPLEEF =====
echo Copying plugins for Spleef
copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\Servers\spleef\plugins\NovaCore.jar /Y
copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\Servers\spleef\plugins\NovaCore-GameEngine.jar /Y
copy G:\github\NovaCore\NovaUtils.jar %cd%\Servers\spleef\plugins\NovaUtils.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Spigot\target\MCTournamentSystem-Spigot-1.0.0-SNAPSHOT.jar %cd%\Servers\spleef\plugins\TournamentSystem.jar /Y
copy G:\github\NovaSpleef\target\NovaSpleef-1.0.0-SNAPSHOT.jar %cd%\Servers\spleef\plugins\NovaSpleef.jar /Y

REM ==== BINGO =====
echo Copying plugins for Bingo
copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\Servers\bingo\plugins\NovaCore.jar /Y
copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\Servers\bingo\plugins\NovaCore-GameEngine.jar /Y
copy G:\github\NovaCore\NovaUtils.jar %cd%\Servers\bingo\plugins\NovaUtils.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Spigot\target\MCTournamentSystem-Spigot-1.0.0-SNAPSHOT.jar %cd%\Servers\bingo\plugins\TournamentSystem.jar /Y
copy G:\github\NovaBingo\target\NovaBingo-1.0.0-SNAPSHOT.jar %cd%\Servers\bingo\plugins\NovaBingo.jar /Y

REM ==== SKYWARS ====
echo Copying plugins for Skywars
copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\Servers\skywars\plugins\NovaCore.jar /Y
copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\Servers\skywars\plugins\NovaCore-GameEngine.jar /Y
copy G:\github\NovaCore\NovaUtils.jar %cd%\Servers\skywars\plugins\NovaUtils.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Spigot\target\MCTournamentSystem-Spigot-1.0.0-SNAPSHOT.jar %cd%\Servers\skywars\plugins\TournamentSystem.jar /Y
copy G:\github\NovaSkywars\target\NovaSkywars-1.0.0-SNAPSHOT.jar %cd%\Servers\skywars\plugins\NovaSkywars.jar /Y

REM ==== MISSILEWARS ====
echo Copying plugins for Missilewars
copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\Servers\missilewars\plugins\NovaCore.jar /Y
copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\Servers\missilewars\plugins\NovaCore-GameEngine.jar /Y
copy G:\github\NovaCore\NovaUtils.jar %cd%\Servers\missilewars\plugins\NovaUtils.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-MissilewarsLobby\target\MCTournamentSystem-MissilewarsLobby-1.0.0-SNAPSHOT.jar %cd%\Servers\missilewars\plugins\MCTournamentSystem-MissilewarsLobby-1.0.0-SNAPSHOT.jar /Y
copy G:\github\NovaMissileWars\target\NovaMissileWars-1.0.0-SNAPSHOT.jar %cd%\Servers\missilewars\plugins\NovaMissileWars-1.0.0-SNAPSHOT.jar /Y

echo Copying bin files
copy G:\github\MCTournamentSystem\MCTournamentSystem-WebUiLauncher\target\WebUILauncher-1.0.0-SNAPSHOT.jar %cd%\Bin\OpenWebUI.jar /Y
copy G:\github\MCTournamentSystem\MCTournamentSystem-Updater\target\MCTournamentSystem-Updater-1.0.0-SNAPSHOT.jar %cd%\Bin\NovaUpdater.jar /Y

REM copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\survivalgames\plugins\NovaCore.jar /Y
REM copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\skywars\plugins\NovaCore.jar /Y
REM copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\uhc\plugins\NovaCore.jar /Y
REM copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\bingo\plugins\NovaCore.jar /Y
REM copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\lobby\plugins\NovaCore.jar /Y
REM copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\spleef\plugins\NovaCore.jar /Y
REM copy G:\github\NovaCore\NovaCore-Spigot.jar %cd%\deathswap\plugins\NovaCore.jar /Y

REM copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\survivalgames\plugins\NovaCore-GameEngine.jar /Y
REM copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\skywars\plugins\NovaCore-GameEngine.jar /Y
REM copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\uhc\plugins\NovaCore-GameEngine.jar /Y
REM copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\bingo\plugins\NovaCore-GameEngine.jar /Y
REM copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\spleef\plugins\NovaCore-GameEngine.jar /Y
REM copy G:\github\NovaCore\NovaCore-GameEngine.jar %cd%\deathswap\plugins\NovaCore-GameEngine.jar /Y

REM copy G:\github\NovaCore\NovaUtils.jar %cd%\survivalgames\plugins\NovaUtils.jar /Y
REM copy G:\github\NovaCore\NovaUtils.jar %cd%\skywars\plugins\NovaUtils.jar /Y
REM copy G:\github\NovaCore\NovaUtils.jar %cd%\spleef\plugins\NovaUtils.jar /Y

REM copy G:\github\TournamentCore\TournamentCore-Spigot\target\tournamentcore-spigot-1.0.0-SNAPSHOT.jar %cd%\survivalgames\plugins\TournamentCore.jar /Y
REM copy G:\github\TournamentCore\TournamentCore-Spigot\target\tournamentcore-spigot-1.0.0-SNAPSHOT.jar %cd%\skywars\plugins\TournamentCore.jar /Y
REM copy G:\github\TournamentCore\TournamentCore-Spigot\target\tournamentcore-spigot-1.0.0-SNAPSHOT.jar %cd%\uhc\plugins\TournamentCore.jar /Y
REM copy G:\github\TournamentCore\TournamentCore-Spigot\target\tournamentcore-spigot-1.0.0-SNAPSHOT.jar %cd%\bingo\plugins\TournamentCore.jar /Y
REM copy G:\github\TournamentCore\TournamentCore-Spigot\target\tournamentcore-spigot-1.0.0-SNAPSHOT.jar %cd%\lobby\plugins\TournamentCore.jar /Y
REM copy G:\github\TournamentCore\TournamentCore-Spigot\target\tournamentcore-spigot-1.0.0-SNAPSHOT.jar %cd%\spleef\plugins\TournamentCore.jar /Y
REM copy G:\github\TournamentCore\TournamentCore-Spigot\target\tournamentcore-spigot-1.0.0-SNAPSHOT.jar %cd%\deathswap\plugins\TournamentCore.jar /Y

REM copy G:\github\NovaUniverseGames\NovaBingo\target\NovaBingo-1.0.0-SNAPSHOT.jar %cd%\bingo\plugins\NovaBingo.jar /Y
REM copy G:\github\NovaSurvivalGames\target\NovaSurvivalGames-1.0.0-SNAPSHOT.jar %cd%\survivalgames\plugins\NovaSurvivalGames.jar /Y
REM copy G:\github\NovaSkywars\target\NovaSkywars-1.0.0-SNAPSHOT.jar %cd%\skywars\plugins\NovaSkywars.jar /Y
REM copy G:\github\NovaSpleef\target\NovaSpleef-1.0.0-SNAPSHOT.jar %cd%\spleef\plugins\NovaSpleef.jar /Y
REM copy G:\github\NovaUniverseGames\NovaUHC\target\NovaUHC-1.0.0-SNAPSHOT.jar %cd%\uhc\plugins\NovaUHC.jar /Y
REM copy G:\github\NovaUniverseGames\NovaDeathSwap\target\NovaDeathSwap-1.0.0-SNAPSHOT.jar %cd%\deathswap\plugins\NovaDeathSwap.jar /Y

pause