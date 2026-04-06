import os

def update_refs(root_java, old_pkg, new_pkg):
    for root, dirs, files in os.walk(root_java):
        for file in files:
            if file.endswith(".java"):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    # Update imports and package declarations
                    new_content = content.replace(f"import {old_pkg}.", f"import {new_pkg}.")
                    new_content = new_content.replace(f"package {old_pkg};", f"package {new_pkg};")
                    
                    if new_content != content:
                        with open(file_path, 'w', encoding='utf-8') as f:
                            f.write(new_content)
                        print(f"Updated {file_path}")
                except Exception as e:
                    print(f"Error {file_path}: {e}")

root_java = r"e:\all backendss\user-module\user-module\src\main\java"

# Update for Hostel
update_refs(root_java, "com.lms.www.campus.model.Hostel", "com.lms.www.campus.model.hostels")
update_refs(root_java, "com.lms.www.campus.repository.Hostel", "com.lms.www.campus.repository.hostels")

# Empty old ones if they exist at exact old paths
old_model = os.path.join(root_java, "com", "lms", "www", "campus", "model", "Hostel", "Hostel.java")
old_repo = os.path.join(root_java, "com", "lms", "www", "campus", "repository", "Hostel", "HostelRepository.java")

for path in [old_model, old_repo]:
    if os.path.exists(path):
        with open(path, 'w', encoding='utf-8') as f:
            f.write("// Moved to hostels package\n")
        print(f"Emptied {path}")
