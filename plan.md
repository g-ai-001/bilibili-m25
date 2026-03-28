# bilibili-m25 项目规划

## 项目概述

- **项目名称**: bilibili-m25
- **包名**: app.bilibili_m25
- **项目类型**: 仿哔哩哔哩安卓本地应用
- **版本号**: 0.1.3 (PATCH版本)
- **目标用户**: 哔哩哔哩用户，需要本地视频管理功能
- **核心功能**: 本地视频播放、本地视频管理、用户界面与主流安卓app一致

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Kotlin 2.3.20 |
| 构建工具 | Gradle 9.4.1 |
| JDK | JDK 25 |
| UI框架 | Jetpack Compose + Material 3 |
| 架构 | MVVM + Clean Architecture |
| 依赖注入 | Hilt |
| 异步处理 | Kotlin Coroutines + Flow |
| 本地存储 | Room (结构化数据) + DataStore (键值对) |
| 图片加载 | Coil |
| 导航 | Navigation Compose |
| 目标SDK | Android 16 (API 36) |
| 最低SDK | Android 16 (API 36) |

## 功能规划

### 短期规划 (0.1.x)

#### 0.1.3 版本 - 修复 Kotlin 编译错误
- [ ] 修复 BilibiliApp 命名冲突
- [ ] 修复 VideoPlayerViewModel catch 参数类型缺失

#### 0.1.2 版本 - 修复 gradle-wrapper.jar 缺失问题
- [x] 修复 gradle-wrapper.jar 缺失导致的 CI 构建失败

#### 0.1.1 版本 - 修复 gradlew 执行权限问题
- [x] 修复 gradlew 缺少执行权限导致的 CI 构建失败

### 0.1.0 版本 - 基础框架搭建
- [x] 脚手架代码搭建项目基础框架
- [x] 日志系统实现 (日志保存在 context.getExternalFilesDir(null))
- [x] 首页界面 (视频列表展示)
- [x] 视频播放界面
- [x] 本地视频管理 (扫描、删除)
- [x] 搜索功能 (标题搜索)
- [x] 用户设置界面
- [x] 折叠屏/平板自适应布局

### 中期规划 (0.2.x - 0.3.x)

- 视频收藏功能
- 视频分类标签管理
- 播放历史记录
- 视频下载管理 (本地)
- 播放列表/播放队列
- 主题切换功能

### 长期规划 (1.0.x)

- 性能优化
- 单元测试和集成测试
- 高级播放控制 (倍速、弹幕样式模拟)
- 用户体验优化

## 已完成版本

### 0.1.3
- 修复 BilibiliApp 命名冲突
- 修复 VideoPlayerViewModel catch 参数类型缺失

### 0.1.2
- 修复 gradle-wrapper.jar 缺失导致的 CI 构建失败

### 0.1.1
- 修复 gradlew 缺少执行权限导致的 CI 构建失败

### 0.1.0
- 首个版本，搭建基础框架
- 实现视频播放核心功能
- 实现本地视频管理

## 开发约束

1. **代码量控制**: 总代码量少于10000行
2. **无网络功能**: 仅支持本地单机使用
3. **界面规范**:
   - 标题栏按钮不超过3个
   - 搜索栏或搜索按钮合并到标题栏
   - 支持简体中文
   - Material 3 theme布局紧凑
4. **构建环境**: GitHub Actions CI/CD

## 版本历史

- **0.1.0** (已完成): 首个版本，基础框架搭建，视频播放核心功能
