import os
import shutil
import re

# Paths
base_src = r"e:\all backendss\user-module\user-module\src\main\java"
external_dirs = [
    r"e:\all backendss\user-module\service",
    r"e:\all backendss\user-module\dto"
]

def get_package_from_content(content):
    match = re.search(r"package\s+([\w\.]+);", content)
    if match:
        return match.group(1)
    return None

def migrate():
    # 1. Merge external files
    for ext_dir in external_dirs:
        if not os.path.exists(ext_dir): continue
        for root, dirs, files in os.walk(ext_dir):
            for file in files:
                if file.endswith(".java"):
                    path = os.path.join(root, file)
                    with open(path, "r", encoding="utf-8", errors="ignore") as f:
                        content = f.read()
                    
                    pkg = get_package_from_content(content)
                    if pkg:
                        # Normalize package to com.lms.www prefix if needed
                        if pkg.startswith("com.lms.management") and not pkg.startswith("com.lms.www"):
                            pkg = pkg.replace("com.lms.management", "com.lms.www.management")
                        
                        target_dir = os.path.join(base_src, pkg.replace(".", "\\"))
                        os.makedirs(target_dir, exist_ok=True)
                        target_path = os.path.join(target_dir, file)
                        
                        # Update content with new package
                        new_content = re.sub(r"package\s+[\w\.]+;", f"package {pkg};", content)
                        # Re-write to target
                        with open(target_path, "w", encoding="utf-8") as f:
                            f.write(new_content)
                        print(f"Migrated {file} to {pkg}")

    # 2. Update all references in the entire project
    old_p = "com.lms.management"
    new_p = "com.lms.www.management"
    
    for root, dirs, files in os.walk(base_src):
        for file in files:
            if file.endswith(".java") or file.endswith(".xml"):
                path = os.path.join(root, file)
                try:
                    with open(path, "r", encoding="utf-8", errors="ignore") as f:
                        content = f.read()
                    
                    # Update package and import statements
                    new_content = content.replace(f"package {old_p}", f"package {new_p}")
                    new_content = new_content.replace(f"import {old_p}", f"import {new_p}")
                    new_content = new_content.replace(f'"{old_p}', f'"{new_p}') # for XML/Strings
                    
                    if new_content != content:
                        with open(path, "w", encoding="utf-8") as f:
                            f.write(new_content)
                except:
                    pass

if __name__ == "__main__":
    migrate()
