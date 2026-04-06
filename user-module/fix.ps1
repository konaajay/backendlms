$RootDir = "E:\all backendss\user-module\user-module\src\main\java"

Get-ChildItem -Path $RootDir -Filter *.java -Recurse | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $newContent = $content -replace "import com\.lms\.management\.", "import com.lms.www.management."
    $newContent = $newContent -replace "package com\.lms\.management\.", "package com.lms.www.management."
    
    if ($newContent -cne $content) {
        Set-Content -Path $_.FullName -Value $newContent -Encoding UTF8
        Write-Host "Updated: $($_.FullName)"
    }
}
Write-Host "Done"
