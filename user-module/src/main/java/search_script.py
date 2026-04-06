import os

search_dir = "."
search_str = "com.lms.management"
results_file = "search_results.txt"

files_found = []
for root, dirs, files in os.walk(search_dir):
    for file in files:
        if file.endswith(".java"):
            file_path = os.path.join(root, file)
            try:
                with open(file_path, "r", encoding="utf-8") as java_file:
                    content = java_file.read()
                    if search_str in content:
                        files_found.append(file_path)
            except Exception as e:
                print(f"Error reading {file_path}: {e}")

with open(results_file, "w") as f:
    for path in files_found:
        f.write(path + "\n")
print(f"Done. Found {len(files_found)} files.")
