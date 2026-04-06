@echo off
echo ======================================================
echo LMS PROJECT - HOSTEL REFACTOR FIX
echo ======================================================
echo.
echo Stopping this script if Maven is still running...
echo Please ensure you have pressed Ctrl+C in your other terminals.
echo.
pause

echo.
echo 1. Renaming uppercase Hostel folders to separate them from hostels...
rename "src\main\java\com\lms\www\campus\model\Hostel" "Hostel_REDUNDANT_DELETE"
rename "src\main\java\com\lms\www\campus\repository\Hostel" "Hostel_REDUNDANT_DELETE"

echo.
echo 2. Deleting redundant folders...
rmdir /s /q "src\main\java\com\lms\www\campus\model\Hostel_REDUNDANT_DELETE"
rmdir /s /q "src\main\java\com\lms\www\campus\repository\Hostel_REDUNDANT_DELETE"

echo.
echo 3. Cleaning the target folder...
rmdir /s /q "target"

echo.
echo ======================================================
echo FIX COMPLETE!
echo You can now run: mvn clean spring-boot:run
echo ======================================================
pause
