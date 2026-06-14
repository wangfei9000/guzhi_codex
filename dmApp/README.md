# Welcome to your Expo app 👋

This is an [Expo](https://expo.dev) project created with [`create-expo-app`](https://www.npmjs.com/package/create-expo-app).

## Get started

1. Install dependencies

   ```bash
   npm install
   ```

2. Start the app

   ```bash
   npx expo start
   ```

In the output, you'll find options to open the app in a

- [development build](https://docs.expo.dev/develop/development-builds/introduction/)
- [Android emulator](https://docs.expo.dev/workflow/android-studio-emulator/)
- [iOS simulator](https://docs.expo.dev/workflow/ios-simulator/)
- [Expo Go](https://expo.dev/go), a limited sandbox for trying out app development with Expo

You can start developing by editing the files inside the **app** directory. This project uses [file-based routing](https://docs.expo.dev/router/introduction).

## Get a fresh project

When you're ready, run:

```bash
npm run reset-project
```

This command will move the starter code to the **app-example** directory and create a blank **app** directory where you can start developing.

### Other setup steps

- To set up ESLint for linting, run `npx expo lint`, or follow our guide on ["Using ESLint and Prettier"](https://docs.expo.dev/guides/using-eslint/)
- If you'd like to set up unit testing, follow our guide on ["Unit Testing with Jest"](https://docs.expo.dev/develop/unit-testing/)
- Learn more about the TypeScript setup in this template in our guide on ["Using TypeScript"](https://docs.expo.dev/guides/typescript/)

## Learn more

To learn more about developing your project with Expo, look at the following resources:

- [Expo documentation](https://docs.expo.dev/): Learn fundamentals, or go into advanced topics with our [guides](https://docs.expo.dev/guides).
- [Learn Expo tutorial](https://docs.expo.dev/tutorial/introduction/): Follow a step-by-step tutorial where you'll create a project that runs on Android, iOS, and the web.

## Join the community

Join our community of developers creating universal apps.

- [Expo on GitHub](https://github.com/expo/expo): View our open source platform and contribute.
- [Discord community](https://chat.expo.dev): Chat with Expo users and ask questions.




⏺ Android 应用开发完成！项目已通过 Expo 编译验证（1541 模块，无错误）。以下是完整总结：

  ---
  📱 dmApp — 房地产估价外勘录入 Android 应用

  项目路径： /Users/edy/Downloads/duming/react/dmApp/

  对照 SurveyMobilePage.tsx 实现的功能

  ┌─────────────────┬────────────────────────────────┬────────────────────────────────┐
  │      功能       │       原 Web 版 (React)        │  新 Android 版 (React Native)  │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 输入勘查码查询  │         Input + Button         │  TextInput + TouchableOpacity  │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 项目信息展示    │              Card              │   卡片展示项目编号/名称/地址   │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 勘查码大字显示  │        Text strong 20px        │ fontSize: 24, letterSpacing: 6 │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 勘查状态切换    │     Switch (已查勘/未查勘)     │       Switch + 状态文字        │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 勘查人 (禁用)   │         Input disabled         │    TextInput editable=false    │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 接待人/电话输入 │             Input              │           TextInput            │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 勘查日期选择    │           DatePicker           │  DateTimePicker (mode="date")  │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 开始/结束时间   │           TimePicker           │  DateTimePicker (mode="time")  │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 验看房产证      │             Switch             │             Switch             │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 权属争议/备注   │        TextArea rows=2         │      TextInput multiline       │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 保存按钮        │      Button block loading      │     绿色全宽按钮 + loading     │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 选择照片上传    │     Upload + customRequest     │ expo-image-picker（拍照/相册） │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 照片网格展示    │    2列 grid + Image + 预览     │   FlatList 2列 + Modal 预览    │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 照片删除        │ Popconfirm → deleteSurveyPhoto │        长按弹窗确认删除        │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 加载/空状态     │        Spin / 提示文字         │ ActivityIndicator / 空状态提示 │
  ├─────────────────┼────────────────────────────────┼────────────────────────────────┤
  │ 登录认证        │       路由守卫 + 登录页        │  Auth Context + AsyncStorage   │
  └─────────────────┴────────────────────────────────┴────────────────────────────────┘

  新增/修改的文件

  ┌─────────────────────────┬────────────────────────────────────────────┐
  │          文件           │                    说明                    │
  ├─────────────────────────┼────────────────────────────────────────────┤
  │ src/app/login.tsx       │ 新建 — 登录页面，JWT 认证                  │
  ├─────────────────────────┼────────────────────────────────────────────┤
  │ src/app/index.tsx       │ 重写 — 外勘录入主页面（核心）              │
  ├─────────────────────────┼────────────────────────────────────────────┤
  │ src/app/_layout.tsx     │ 重写 — 根布局，登录/主页面路由切换         │
  ├─────────────────────────┼────────────────────────────────────────────┤
  │ src/api/client.ts       │ 新建 — Axios 实例，Token 自动附加          │
  ├─────────────────────────┼────────────────────────────────────────────┤
  │ src/api/survey.ts       │ 新建 — 勘查 API（查询/保存/照片/文件上传） │
  ├─────────────────────────┼────────────────────────────────────────────┤
  │ src/api/types.ts        │ 新建 — TypeScript 类型定义                 │
  ├─────────────────────────┼────────────────────────────────────────────┤
  │ src/store/authStore.tsx │ 新建 — Auth Context + AsyncStorage 持久化  │
  ├─────────────────────────┼────────────────────────────────────────────┤
  │ src/utils/constants.ts  │ 新建 — API 地址、存储 Key                  │
  ├─────────────────────────┼────────────────────────────────────────────┤
  │ app.json                │ 修改 — 添加相机/相册权限、Android 包名     │
  └─────────────────────────┴────────────────────────────────────────────┘

  如何构建 APK

  cd /Users/edy/Downloads/duming/react/dmApp

  # 方式1：直接连安卓手机/模拟器运行
  npx expo run:android

  # 方式2：使用 EAS 构建独立 APK
  npx eas build -p android --profile preview

  # 方式3：开发模式预览
  npm start
  # 然后扫码或按 'a' 启动 Android 模拟器

  注意事项

  - API 地址配置在 src/utils/constants.ts，当前指向 http://121.40.244.102:8080/api
  - Android 真机访问服务器需确保网络互通（使用服务器公网 IP）
  - 首次运行需要在手机端登录（默认账号 admin/admin123），Token 会自动保存


