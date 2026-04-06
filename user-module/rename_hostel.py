import os

def rename_package(root_dir, old_pkg, new_pkg):
    old_path = old_pkg.replace('.', os.sep)
    new_path = new_pkg.replace('.', os.sep)
    
    full_old_path = os.path.join(root_dir, old_path)
    full_new_path = os.path.join(root_dir, new_path)
    
    if os.path.exists(full_old_path):
        if os.path.exists(full_new_path):
             print(f"Target path already exists: {full_new_path}. Skipping rename.")
        else:
             os.rename(full_old_path, full_new_path)
             print(f"Renamed {full_old_path} to {full_new_path}")
    else:
        print(f"Path not found: {full_old_path}")

def update_references(root_dir, old_pkg, new_pkg):
    for root, dirs, files in os.walk(root_dir):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    new_content = content.replace(f"package {old_pkg};", f"package {new_pkg};")
                    new_content = new_content.replace(f"import {old_pkg}.", f"import {new_pkg}.")
                    
                    if new_content != content:
                        with open(file_path, 'w', encoding='utf-8') as f:
                            f.write(new_content)
                        print(f"Updated references in {file_path}")
                except Exception as e:
                    print(f"Error processing {file_path}: {e}")

root_java = r"e:\all backendss\user-module\user-module\src\main\java"

# Update references first while old folders still exist (optional but safer)
update_references(root_java, "com.lms.www.campus.model.Hostel", "com.lms.www.campus.model.hostels")
update_references(root_java, "com.lms.www.campus.repository.Hostel", "com.lms.www.campus.repository.hostels")

# Rename model package
rename_package(root_java, "com.lms.www.campus.model.Hostel", "com.lms.www.campus.model.hostels")

# Rename repository package
rename_package(root_java, "com.lms.www.campus.repository.Hostel", "com.lms.www.campus.repository.hostels")
