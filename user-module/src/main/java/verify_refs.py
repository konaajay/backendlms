import os

base_dir = r"e:\all backendss\user-module\user-module\src\main\java"
new_package = "com.lms.www.management"
old_package = "com.lms.management" # Not as a substring of new_package

files_with_new = []
files_with_old = []

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".java"):
            file_path = os.path.join(root, file)
            try:
                with open(file_path, "r", encoding="utf-8", errors='ignore') as f:
                    content = f.read()
                
                # Check for old package but NOT if it's already part of the new one
                # A simple way: check for "com.lms.management" but NOT "com.lms.www.management"
                if new_package in content:
                    files_with_new.append(file_path)
                
                # To find truly 'old' ones without 'www'
                if old_package in content and new_package not in content:
                    files_with_old.append(file_path)
            except Exception as e:
                print(f"Error checking {file_path}: {e}")

print(f"Verification Results:")
print(f"Files with '{new_package}': {len(files_with_new)}")
print(f"Files with '{old_package}' (without www): {len(files_with_old)}")

if files_with_old:
    print("WARNING: Found files with old package prefix!")
    for f in files_with_old:
        print(f"  {f}")
