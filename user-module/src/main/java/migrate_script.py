import os
import shutil

base_dir = r"e:\all backendss\user-module\user-module\src\main\java"
src_pkg_dir = os.path.join(base_dir, "com", "lms", "management")
dest_pkg_dir = os.path.join(base_dir, "com", "lms", "www", "management")

# 1. Move files
if os.path.exists(src_pkg_dir):
    print(f"Moving contents from {src_pkg_dir} to {dest_pkg_dir}...")
    for item in os.listdir(src_pkg_dir):
        s = os.path.join(src_pkg_dir, item)
        d = os.path.join(dest_pkg_dir, item)
        if os.path.isdir(s):
            if os.path.exists(d):
                shutil.copytree(s, d, dirs_exist_ok=True)
                shutil.rmtree(s)
            else:
                shutil.move(s, d)
        else:
            shutil.move(s, d)
    # Check if empty and remove
    if not os.listdir(src_pkg_dir):
        os.rmdir(src_pkg_dir)
else:
    print(f"Source directory {src_pkg_dir} does not exist.")

# 2. Update code references
old_package_prefix = "com.lms.management"
new_package_prefix = "com.lms.www.management"

modified_files = []

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".java"):
            file_path = os.path.join(root, file)
            try:
                with open(file_path, "r", encoding="utf-8") as f:
                    content = f.read()
                
                new_content = content.replace(f"package {old_package_prefix}", f"package {new_package_prefix}")
                new_content = new_content.replace(f"import {old_package_prefix}", f"import {new_package_prefix}")
                
                if new_content != content:
                    with open(file_path, "w", encoding="utf-8") as f:
                        f.write(new_content)
                    modified_files.append(file_path)
            except Exception as e:
                print(f"Error processing {file_path}: {e}")

print(f"Migration complete. Modified {len(modified_files)} files.")
for f in modified_files:
    print(f"Modified: {f}")
