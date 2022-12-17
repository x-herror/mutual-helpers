## SE homework1
[主文件](https://github.com/gxherror/MutualHelpers/blob/main/app/src/main/java/top/xherror/mutualhelpers/MainActivity.kt)
[APP](https://github.com/gxherror/MutualHelpers/blob/main/app/release/app-release.apk)
[说明](https://xherror.top/post/se/mutual-helpers/)

《第一行代码 第三版》，

- 安卓部分
  - 整体框架使用Activity套Fragment，后续考虑换成更流行的Viewpager2加Fragment
  - UI采用NestedScrollView嵌套RecycleView实现滑动与列表实现
  - UI模仿柠檬的布局，白绿粉蓝配色
- 数据库部分
  - item的储存采用原生的关系数据库SQLite加ORM模型Room
  - person与其他metadata采用原生的KV数据库SharedPreferences
  - 后序将person的储存也换成关系数据库方便进行DA
  - 使用Gson实现储存的Json格式与运行时ArrayList转换
- 网络部分(未完成)
  - 三层架构，即client-server-database
  - 使用Gin框架实现RESTFUL API，做为前端服务器
  - 使用Glide实现远端图片的获取，图片的异步加载与本地缓存


- 特点
  - UI设计较为简单，更多重点在后端逻辑部分
  - 实现了留言功能，方便双方的交流
  - 动态类别修改，分类搜索，简单的模糊搜索(水壶->水杯)
  - 密码采用SHA256加密储存，实现记住密码自动登入功能




- Android
 - total framework design use Activity together with Fragment, would change to Viewpager2 together with Fragment in future 
 - UI design uses NestedScrollView nests RecycleView to implement screen scroll and list show
 - UI design imitates SJTU lemon design style,use white-green-pink-blue color scheme
- Database
 - item storage use native ralational database SQLite with native ORM model Room
 - person and other metadata storage use native key-value database SharedPreference
 - hope to change person storage model to relational database for the sake of data analysis in future
 - use Gson to convert between storage format Json and runtime format ArrayList
- Network(unfinished)
 - three-tier architecture,namely client-server-database
 - use Gin framework to implement RESTFUL API,work as frontend server
 - use Glide to get remote images,asynchronous load and local cache

- Features 
  - UI design is relative simple，attach more importance on backend logic design 
  - implement comment utility，make communication easier 
  - dynamic category edit,classified saerch,simple huzzy search
  - encrypt password use SHA256 when storage, implement remember password and auto login in utility 
