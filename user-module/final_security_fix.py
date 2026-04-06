import sys

file_path = r'e:\just now ceated\user-module (2) (1)\user-module (3)\user-module\user-module\src\main\java\com\lms\www\config\SecurityConfig.java'

with open(file_path, 'r', encoding='utf-8') as f:
    text = f.read()

target = '.anyRequest().authenticated()'
# Add Topics (GET) and Leads (POST) before anyRequest
replacement = (
    '.requestMatchers(org.springframework.http.HttpMethod.GET, "/api/topics/course/**", "/api/topic-contents/topic/**").permitAll()\n'
    '                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/public/leads").permitAll()\n'
    '                    .anyRequest().authenticated()'
)

if target in text:
    updated_text = text.replace(target, replacement)
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(updated_text)
    print("SUCCESS")
else:
    print("TARGET NOT FOUND")
