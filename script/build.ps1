# build.ps1

Write-Host "=== 开始构建检查 ===" -ForegroundColor Green

# 检查Java
Write-Host "`n1. 检查Java环境..." -ForegroundColor Yellow
try {
    $javaVersionOutput = java -version 2>&1 | Out-String
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Java版本信息:" -ForegroundColor Green
        Write-Host $javaVersionOutput.Trim()

        # 提取Java版本号
        if ($javaVersionOutput -match 'version "(\d+)\.') {
            $javaMajorVersion = [int]$matches[1]
            if ($javaMajorVersion -lt 25) {
                Write-Host "错误: 当前Java $javaMajorVersion 版本较低，请安装Java 25" -ForegroundColor Red
                exit 1
            } else {
                Write-Host "Java版本检查通过 (版本: $javaMajorVersion)" -ForegroundColor Green
            }
        } else {
            Write-Host "警告: 无法解析Java版本号" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "错误: 未检测到Java，请安装Java 25" -ForegroundColor Red
    exit 1
}

# 检查MySQL
Write-Host "`n2. 检查MySQL环境..." -ForegroundColor Yellow
try {
    $mysqlVersionOutput = mysql --version 2>&1 | Out-String
    if ($LASTEXITCODE -eq 0) {
        Write-Host "MySQL版本信息:" -ForegroundColor Green
        Write-Host $mysqlVersionOutput.Trim()

        # 提取MySQL版本号
        if ($mysqlVersionOutput -match 'mysql\s+Ver\s+(\d+)\.') {
            $mysqlMajorVersion = [int]$matches[1]
            if ($mysqlMajorVersion -lt 9) {
                Write-Host "错误: MySQL版本 $mysqlMajorVersion 较低，请安装MySQL 9或更高版本" -ForegroundColor Red
                exit 1
            } else {
                Write-Host "MySQL版本检查通过 (版本: $mysqlMajorVersion)" -ForegroundColor Green
            }
        } else {
            Write-Host "警告: 无法解析MySQL版本号" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "错误: 未检测到MySQL，请安装MySQL 9或更高版本" -ForegroundColor Red
    exit 1
}

# 检查Maven
Write-Host "`n3. 检查Maven环境..." -ForegroundColor Yellow
try {
    $mavenVersionOutput = mvn --version 2>&1 | Out-String
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Maven版本信息:" -ForegroundColor Green
        Write-Host $mavenVersionOutput.Trim()

        # 提取Maven版本号
        if ($mavenVersionOutput -match 'Apache Maven\s+([\d.]+)') {
            $mavenVersion = $matches[1]
            $requiredVersion = [version]"4.0.0"
            $currentVersion = [version]$mavenVersion

            if ($currentVersion -lt $requiredVersion) {
                Write-Host "错误: Maven版本 $mavenVersion 较低，请安装Maven 4.0.0或更高版本" -ForegroundColor Red
                exit 1
            } else {
                Write-Host "Maven版本检查通过 (版本: $mavenVersion)" -ForegroundColor Green
            }
        } else {
            Write-Host "警告: 无法解析Maven版本号" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "错误: 未检测到Maven，请安装Maven 4.0.0或更高版本" -ForegroundColor Red
    exit 1
}

# 检查pom.xml
Write-Host "`n4. 检查pom.xml文件..." -ForegroundColor Yellow
$parentDir = ".."
$pomPath = Join-Path $parentDir "pom.xml"

if (-not (Test-Path $pomPath)) {
    Write-Host "错误: 在上级目录中未找到pom.xml文件" -ForegroundColor Red
    Write-Host "按任意键退出..." -ForegroundColor Yellow
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
    exit 1
} else {
    Write-Host "找到pom.xml文件: $pomPath" -ForegroundColor Green
}

# 构建项目
Write-Host "`n5. 开始构建项目..." -ForegroundColor Yellow
Set-Location $parentDir
Write-Host "当前目录: $(Get-Location)" -ForegroundColor Cyan

$mvnResult = mvn clean package
if ($LASTEXITCODE -ne 0) {
    Write-Host "错误: Maven构建失败" -ForegroundColor Red
    Write-Host $mvnResult
    exit 1
} else {
    Write-Host "构建成功完成!" -ForegroundColor Green
}

# 尝试运行
Write-Host "`n6. 尝试运行应用程序..." -ForegroundColor Yellow
$jarPath = "target/SMS-0.0.1-SNAPSHOT.jar"

if (Test-Path $jarPath) {
    Write-Host "找到JAR文件: $jarPath" -ForegroundColor Green
    Write-Host "启动应用程序..." -ForegroundColor Cyan
    java -jar $jarPath
} else {
    Write-Host "错误: 未找到JAR文件: $jarPath" -ForegroundColor Red
    exit 1
}