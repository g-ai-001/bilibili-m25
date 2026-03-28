# bilibili-m25

一个仿哔哩哔哩的本地安卓视频播放和管理应用。

## 功能特性

- 本地视频扫描与管理
- 视频播放（支持多种格式）
- 视频收藏
- 视频搜索
- 播放历史记录
- 响应式布局（支持手机、折叠屏、平板）

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin 2.3.20 |
| UI框架 | Jetpack Compose + Material 3 |
| 架构 | MVVM + Clean Architecture |
| 依赖注入 | Hilt |
| 本地存储 | Room + DataStore |
| 视频播放 | Media3 ExoPlayer |
| 图片加载 | Coil |
| 导航 | Navigation Compose |
| 目标SDK | Android 16 (API 36) |

## 版本历史

### 0.1.6 (当前版本)
- 修复v0.1.4问题反馈
- 添加存储权限申请(READ_MEDIA_VIDEO)
- 修复扫描进度条无法停止问题
- 实现视频列表多列显示
- 实现标题栏与状态栏融合
- 添加视频封面预览图

### 0.1.5
- 重构优化代码质量和可维护性
- 移除无用方法，修复空catch块处理
- 使用Hilt注入优化SettingsScreen
- 动态获取版本号

### 0.1.4 - 0.1.1
- 修复 gradle-wrapper.jar 缺失问题
- 修复 gradlew 执行权限问题
- 修复 Kotlin 编译错误

### 0.1.0
- 初始化基础框架
- 实现视频扫描和本地存储功能
- 实现视频播放功能
- 实现首页、搜索、收藏、设置界面
- 配置GitHub Actions CI/CD

## 构建

项目使用GitHub Actions进行自动构建，每次推送到main分支或创建版本标签时自动构建APK。

## 下载

从GitHub Actions构建产物下载最新APK。

## 隐私说明

本应用为本地单机应用，不包含任何网络功能，所有数据存储在本地设备上。
