#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
#

# 跨平台的 Gradle 启动脚本（PowerShell 版），By 拾贰光尘。

$ErrorActionPreference = 'Stop'

# 根据操作系统选择 Java 可执行文件的名称：
# 在 Windows 上为 java.exe，在 Linux / macOS 上为 java
$IsWindows = ($env:OS -eq 'Windows_NT')
$javaExeName = if ($IsWindows) { 'java.exe' } else { 'java' }

# 项目根目录，即本脚本所在目录
$APP_HOME = $PSScriptRoot
if (-not $APP_HOME) {
    $APP_HOME = (Get-Location).Path
}

# 默认 JVM 选项，可通过 JAVA_OPTS 或 GRADLE_OPTS 环境变量追加
$DEFAULT_JVM_OPTS = @('-Xmx64m', '-Xms64m')

# 查找 Java：优先使用 JAVA_HOME，其次从 PATH 中查找
$JAVA_EXE = $null
if ($env:JAVA_HOME) {
    $JAVA_EXE = Join-Path $env:JAVA_HOME (Join-Path 'bin' $javaExeName)
    if (-not (Test-Path -LiteralPath $JAVA_EXE)) {
        Write-Error "JAVA_HOME is set to an invalid directory: $env:JAVA_HOME"
        exit 1
    }
} else {
    $javaCommand = Get-Command $javaExeName -ErrorAction SilentlyContinue
    if ($javaCommand) {
        $JAVA_EXE = $javaCommand.Source
    }
    if (-not $JAVA_EXE) {
        Write-Error "JAVA_HOME is not set and no '$javaExeName' command could be found in your PATH."
        exit 1
    }
}

# 组合全部 JVM 选项
$JVM_OPTS = @($DEFAULT_JVM_OPTS)
if ($env:JAVA_OPTS) {
    $JVM_OPTS += $env:JAVA_OPTS -split '\s+'
}
if ($env:GRADLE_OPTS) {
    $JVM_OPTS += $env:GRADLE_OPTS -split '\s+'
}

$APP_BASE_NAME = 'gradlew'
$CLASSPATH = ''

# 执行 Gradle Wrapper
# 使用 Join-Path 逐级拼接路径，以兼容不同平台的路径分隔符
$wrapper = Join-Path $APP_HOME (Join-Path 'gradle' (Join-Path 'wrapper' 'gradle-wrapper.jar'))
$launchArgs = @("-Dorg.gradle.appname=$APP_BASE_NAME", '-jar', $wrapper)
if ($CLASSPATH) {
    $launchArgs = @('-classpath', $CLASSPATH) + $launchArgs
}
& $JAVA_EXE @JVM_OPTS @launchArgs @args

exit $LASTEXITCODE
