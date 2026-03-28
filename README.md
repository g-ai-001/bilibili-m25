# bilibili-m25

一个仿哔哩哔哩的本地安卓视频播放和管理应用。

## 功能特性

- 本地视频扫描与管理
- 视频播放（支持多种格式）
- 视频续播功能（记住播放位置）
- 视频收藏
- 视频搜索
- 播放历史记录
- 播放队列功能（支持上一首/下一首）
- 倍速播放功能（支持 0.5x - 2.0x）
- 主题切换（浅色/深色/跟随系统）
- 文件夹浏览模式
- 画中画模式
- 视频详情查看
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

### 0.8.3
- 重构代码质量和可维护性
- 重构formatDuration函数，删除重复定义
- 重构PlaybackManager Intent创建模式，提取通用sendAction方法
- 重构Logger为object单例模式

### 0.8.2
- 重构优化：修复SwipeableVideoCard滑动收藏图标显示错误
- 添加PlaybackSpeedPreferences构造函数注入简化AppModule
- 提取VideoGridContent组件减少HomeScreen和HistoryScreen重复代码

### 0.8.1
- 修复后台播放通知控制问题
- 添加ACTION_RESUME支持视频播放恢复
- 创建通知频道用于后台播放通知

### 0.8.0
- 新增后台音频播放功能，点击后台播放按钮可在后台继续播放音频
- 新增音频播放通知控制，可在通知栏控制播放

### 0.7.0
- 新增视频截图功能，播放视频时可截取当前画面保存到相册
- 新增视频列表滑动操作（向左滑删除，向右滑收藏）

### 早期版本 (0.1.x - 0.6.x)
- 基础框架搭建、视频播放、搜索、管理功能
- 倍速播放、播放队列、主题切换等特性

## 构建

项目使用GitHub Actions进行自动构建，每次推送到main分支或创建版本标签时自动构建APK。

## 下载

从GitHub Actions构建产物下载最新APK。

## 隐私说明

本应用为本地单机应用，不包含任何网络功能，所有数据存储在本地设备上。
