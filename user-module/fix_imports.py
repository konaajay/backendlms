import os

root_dir = r"E:\all backendss\user-module\user-module\src\main\java"

modified_files = []

for subdir, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith(".java"):
            filepath = os.path.join(subdir, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
                
            new_content = content.replace("import com.lms.management.", "import com.lms.www.management.")
            new_content = new_content.replace("package com.lms.management.", "package com.lms.www.management.")
            
            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                modified_files.append(filepath)

print("Modified files count:", len(modified_files))
for f in modified_files:
    print(f)
