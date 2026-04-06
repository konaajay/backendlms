import os
import re

target_dir = r"src\main\java\com\lms\www\management\controller"

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    pattern = r"@PreAuthorize\(\"hasAuthority\('([^']+)'\)\"\)"
    replacement = r"@PreAuthorize(\"hasAnyAuthority('\1', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')\")"
    
    new_content = re.sub(pattern, replacement, content)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Updated {os.path.basename(filepath)}")

for filename in os.listdir(target_dir):
    if filename.startswith("Exam") or filename.startswith("Question"):
        if filename.endswith("Controller.java"):
            process_file(os.path.join(target_dir, filename))
        
print("Replacement script finished.")
