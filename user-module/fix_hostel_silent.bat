@echo off
rename "src\main\java\com\lms\www\campus\model\Hostel" "Hostel_REDUNDANT_DELETE"
rename "src\main\java\com\lms\www\campus\repository\Hostel" "Hostel_REDUNDANT_DELETE"
rmdir /s /q "src\main\java\com\lms\www\campus\model\Hostel_REDUNDANT_DELETE"
rmdir /s /q "src\main\java\com\lms\www\campus\repository\Hostel_REDUNDANT_DELETE"
rmdir /s /q "target"
echo DONE
