# 安装课程所需软件

#### 一、Android Studio安装

##### 1、初始安装Android Studio成果图

<img src="shotscreens/image_1.png" alt="image_1" style="zoom:50%;" />

##### 2、下载Gradle相关的依赖库

1、新建Android项目，选择Empty Activity，设置项目名称为Lab1，文件保存在AndroidWorkSpace中（路径中不要含英文），其他默认

<img src="shotscreens/image_2.png" alt="image_2" style="zoom:50%;" />

<img src="shotscreens/image_3.png" alt="image_3" style="zoom:50%;" />

2、项目创建后，左侧切到Project视图，找到settings.gradle.kts，修改其中的pluginManagement与dependencyResolutionManagement后的settings.gradle.kts如下：

```kotlin
pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/central") }

        google()
        mavenCentral()
    }
}

rootProject.name = "Lab1"
include(":app")
```

再打开gradle/wrapper/gradle-wrapper.properties，修改其中的distributionUrl改为all.zip 镜像，修改后的gradle-wrapper.properties如下：

```properties
#Sat Apr 25 18:08:07 CST 2026
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-9.3.1-all.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

3、点击顶部蓝色提示栏中的Try Again，重新执行 Gradle Sync，并开始下载依赖，Gradle基础依赖下载成功后如下图所示：

<img src="shotscreens/image_4.png" alt="image_4" style="zoom:50%;" />

4、选择顶部菜单栏中的Tools中的Device Manager；进入Device Manager后点击Create Virtual Device，选择Pixel 6这个手机型号

<img src="shotscreens/image_5.png" alt="image_5" style="zoom:50%;" />

选择API 35版本，若未下载，则选中带⭐的下载按钮下载该版本

<img src="shotscreens/image_6.png" alt="image_6" style="zoom:50%;" />

<img src="shotscreens/image_7.png" alt="image_7" style="zoom:50%;" />

创建完成后，在 Device Manager 里找到：Pixel 6，点击右侧三角形：▶，启动模拟器，启动成功后如下：

<img src="shotscreens/image_8.png" alt="image_8" style="zoom:50%;" />

模拟器启动后，回到 Android Studio 主界面。点击绿色运行按钮▶ Run，如在Build中出现BUILD SUCCESSFUL即说明App 已经成功运行到模拟器上，Gradle基础依赖安装完成

<img src="shotscreens/image_9.png" alt="image_9" style="zoom:50%;" />

#### 二、Anaconda安装

1、进入Anaconda官网：https://www.anaconda.com/download/success，选择Windows版本，按照自己操作系统选择对应位数，本机为64位

<img src="shotscreens/image_10.png" alt="image_10" style="zoom:50%;" />

2、安装过程中勾选Just me,选择安装路径，注意路径不要包含中文、空格等，最好仅含英文，勾选Create shortcuts（创建快捷方式）与Clear the package cache upon completion（安装完成后清理缓存），点击安装即可

<img src="shotscreens/image_11.png" alt="image_11" style="zoom:50%;" />

<img src="shotscreens/image_12.png" alt="image_12" style="zoom:50%;" />

<img src="shotscreens/image_13.png" alt="image_13" style="zoom:50%;" />

3、在开始菜单栏中右键点击Anaconda PowerShell Prompt，以管理员身份运行，然后在Anaconda Prompt中输入 conda list， 若输出已经安装的包名和版本号，则说明安装成功(部分输出如下）：

<img src="shotscreens/image_14.png" alt="image_14" style="zoom:50%;" />



#### 三、Jupyter Notebook安装与试用（Anaconda自带）

1、打开 Anaconda Navigator

2、在左侧选择 Home

3、选择Traffic_Flow_Prediction环境

4、在该虚拟环境中安装Jupyter Notebook

<img src="shotscreens/image_15.png" alt="image_15" style="zoom:50%;" />

5、以Traffic_Flow_Prediction环境启动Jupyter Notebook

<img src="shotscreens/image_16.png" alt="image_16" style="zoom:50%;" />

<img src="shotscreens/image_17.png" alt="image_17" style="zoom:50%;" />

6、试用Jupyter Notebook，修改C:\Users\digetal-bear\.jupyter\jupyter_notebook_config.py中的c.NotebookApp.notebook_dir为c.NotebookApp.notebook_dir = 'E:\\'，使得启动Jupyter Notebook时默认加载E盘，在E盘中创建.ipynb文件，写入测试代码,运行结果如下

<img src="shotscreens/image_18.png" alt="image_18" style="zoom:50%;" />

#### 四、Visual Studio Code与对应插件安装

1、VS Code安装后界面

<img src="shotscreens/image_19.png" alt="image_19" style="zoom:50%;" />

2、安装Python插件

<img src="shotscreens/image_20.png" alt="image_20" style="zoom:50%;" />

3、安装Jupyter插件

<img src="shotscreens/image_21.png" alt="image_21" style="zoom:50%;" />

4、安装Jupyter Keymap插件

<img src="shotscreens/image_22.png" alt="image_22" style="zoom:50%;" />

5、在VS code中创建Notebook

创建一个.ipynb文件，即出现一个 Notebook 页面

<img src="shotscreens/image_23.png" alt="image_23" style="zoom:50%;" />

6、点击Select Kernel选择Traffic_Flow_Prediction环境，点击左侧三角按钮运行结果如下：

<img src="shotscreens\image_24.png" alt="image_24" style="zoom:50%;" />
