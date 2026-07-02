# 基于TensorFlow Lite实现的Android花卉识别应用

## 实验内容

• 按照教程构建基于TensorFlow Lite的Android花卉识别应

用。

• 查 看 该 应 用 的 代 码 框 架 ， 特 别 注 意 CameraX 库

(AndroidX.camera.*)和数据视图模型的使用。

• 上 传 完 成 既 定 功 能 的 代 码 至 Github ， 并 撰 写 详 细 的

Readme文档。

## 实验步骤

• 首先安装Android Studio 4.1以上的版本

• 下载代码ZIP或者使用git clone克隆代码

• 按照教程完成所有TODO代码项

• 导入已有的Tensorflow Lite模型（finish模块ml文件夹下

的FlowerModel.tflite）

• 真机运行完成的花卉识别应用

#### 一、编译运行并成功解决生成项目中的错误

启动finish模块中的MainActivity.kt：

<img src="shotscreens\compile_and_run.png" alt="compile_and_run" style="zoom:50%;" />

#### 二、完成start模块中的TODO代码项并导入已有的Tensorflow Lite模型

TODO代码项完成步骤：

1. 定位“start”模块**MainActivity.kt**文件的TODO 1，添加初始化训练模型的代码
2. 在CameraX的analyze方法内部，需要将摄像头的输入`ImageProxy`转化为`Bitmap`对象，并进一步转化为`TensorImage` 对象
3. 对图像进行处理并生成结果，主要包含下述操作：
   - 按照属性`score`对识别结果按照概率从高到低排序
   - 列出最高k种可能的结果，k的结果由常量`MAX_RESULT_DISPLAY`定义
4. 将识别的结果加入数据对象`Recognition` 中，包含`label`和`score`两个元素。后续将用于`RecyclerView`的数据显示
5. 将原先用于虚拟显示识别结果的代码注释掉或者删除

<img src="shotscreens\TODO_Code_1.png" alt="TODO_Code_1" style="zoom:50%;" />

<img src="shotscreens\TODO_Code_2.png" alt="TODO_Code_2" style="zoom:50%;" />

添加TensorFlow Lite完成步骤：

1.选择"start"模块

2.右键“start”模块，或者选择File，然后New>Other>TensorFlow Lite Mode

3.选择已经下载的自定义的训练模型。本教程模型训练任务以后完成，这里选择finish模块中ml文件下的FlowerModel.tflite。

4.最终TensorFlow Lite模型被成功导入，并生成摘要信息

<img src="shotscreens\Tensorflow_Lite_Model.png" alt="Tensorflow_Lite_Model" style="zoom:50%;" />

#### 三、在真机(OPPO)上运行start模块后的识别花朵的结果

dandelion:

<img src="shotscreens\dandelion.jpg" alt="dandelion" style="zoom: 33%;" />

daisy:

<img src="shotscreens\daisy.jpg" alt="daisy" style="zoom: 33%;" />

sunflowers：

<img src="shotscreens\sunflowers.jpg" alt="sunflowers" style="zoom: 33%;" />

roses:

<img src="shotscreens\roses.jpg" alt="roses" style="zoom: 33%;" />

tulips:

<img src="shotscreens\roses.jpg" alt="roses" style="zoom: 33%;" />

### 四、扩展实验

改动：

1、把识别结果从 Top3 改成 Top5，

```kotlin
private const val MAX_RESULT_DISPLAY = 3
```

->

```kotlin
private const val MAX_RESULT_DISPLAY = 5	
```

原来界面只显示置信度最高的 3 个花卉类别；
修改后显示置信度最高的 5 个类别，信息更完整。

2、过滤低置信度结果

```kotlin
val outputs = flowerModel.process(tfImage)
    .probabilityAsCategoryList.apply {
        sortByDescending { it.score }
    }.take(MAX_RESULT_DISPLAY)
```

->

```kotlin
val outputs = flowerModel.process(tfImage)
    .probabilityAsCategoryList
    .filter { it.score >= 0.20f }
    .sortedByDescending { it.score }
    .take(MAX_RESULT_DISPLAY)
```

置信度低于 20% 的结果不会显示；
界面上只保留较可信的花卉识别结果。

3、当识别不出来时显示提示

```kotlin
        override fun analyze(imageProxy: ImageProxy) {

            val items = mutableListOf<Recognition>()

            // TODO 2: Convert Image to Bitmap then to TensorImage
            val tfImage = TensorImage.fromBitmap(toBitmap(imageProxy))

            // TODO 3: Process the image using the trained model, sort and pick out the top results
            val outputs = flowerModel.process(tfImage)
                .probabilityAsCategoryList.apply {
                    sortByDescending { it.score } // Sort with highest confidence first
                }.take(MAX_RESULT_DISPLAY) // take the top results



            // TODO 4: Converting the top probability items into a list of recognitions
            for (output in outputs) {
                items.add(Recognition(output.label, output.score))
            }

            // Return the result
            listener(items.toList())

            // Close the image,this tells CameraX to feed the next image to the analyzer
            imageProxy.close()
        }
```

->

```kotlin
override fun analyze(imageProxy: ImageProxy) {
            try {
                val items = mutableListOf<Recognition>()

                // 将摄像头画面转成 Bitmap，如果转换失败就直接返回
                val bitmap = toBitmap(imageProxy) ?: return

                // 将 Bitmap 转成 TensorFlow Lite 可处理的 TensorImage
                val tfImage = TensorImage.fromBitmap(bitmap)

                // 调用模型识别，并过滤掉置信度低于 20% 的结果
                val outputs = flowerModel.process(tfImage)
                    .probabilityAsCategoryList
                    .filter { it.score >= 0.20f }
                    .sortedByDescending { it.score }
                    .take(MAX_RESULT_DISPLAY)

                // 如果没有任何结果达到 20%，就显示提示
                if (outputs.isEmpty()) {
                    items.add(Recognition("未识别到明显花卉", 0.0f))
                } else {
                    for (output in outputs) {
                        items.add(Recognition(output.label, output.score))
                    }
                }

                // 把结果传给界面显示
                listener(items.toList())

            } catch (e: Exception) {
                Log.e(TAG, "Image analysis failed", e)
            } finally {
                // 无论识别成功还是失败，都关闭 imageProxy，避免相机画面卡住
                imageProxy.close()
            }
        }

```

运行效果：

rose:

<img src="shotscreens\rose_extend.jpg" alt="rose_extend" style="zoom: 33%;" />

dandelion:

<img src="shotscreens\dandelion_extend.jpg" alt="dandelion_extend" style="zoom: 33%;" />

tulip:

<img src="shotscreens\tulip_extend.jpg" alt="tulip_extend" style="zoom: 33%;" />

sunflowers:

<img src="shotscreens\sunflowers_extend.jpg" alt="sunflowers_extend" style="zoom: 33%;" />

daisy:

<img src="shotscreens\daisy_extend.jpg" alt="daisy_extend" style="zoom: 33%;" />