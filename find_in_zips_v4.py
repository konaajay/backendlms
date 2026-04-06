import zipfile
import os

zip_files = [
    r"e:\all backendss\user-module\user-module.zip",
    r"e:\all backendss\user-module\service.zip",
    r"e:\all backendss\user-module\dto.zip",
    r"e:\all backendss\user-module\dto (2).zip",
    r"e:\all backendss\user-module (2).zip",
    r"e:\all backendss\user-module (3).zip"
]
search_term = "ParentDashboard"

with open(r"e:\all backendss\user-module\zip_search_v4.txt", "w") as out:
    for z_file in zip_files:
        if os.path.exists(z_file):
            out.write(f"Checking {z_file}...\n")
            try:
                with zipfile.ZipFile(z_file, 'r') as z:
                    for f in z.namelist():
                        if search_term.lower() in f.lower():
                            out.write(f"FOUND {f} in {z_file}\n")
            except Exception as e:
                out.write(f"Error reading {z_file}: {e}\n")
