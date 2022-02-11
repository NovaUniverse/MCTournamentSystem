@echo off
echo Warning!
echo You are about to update TournamentSystem
echo Please close all tournamnet servers and then press any key to start the update
pause
java -jar bin\NovaUpdater.jar

if exist updater_temp_data\content\bin\NovaUpdater.jar (
	echo Extracting new updater jar
	copy updater_temp_data\content\bin\NovaUpdater.jar Bin\NovaUpdater.jar /Y
)

if exist updater_temp_data (
	echo Removing temporary files...
	rd /S /Q updater_temp_data
)

echo Done

pause