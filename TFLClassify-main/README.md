# 基于TensorFlow Lite实现的Android花卉识别应用

#### 一、编译运行并成功解决生成项目中的错误

启动finish模块中的MainActivity.kt：

<img src="shotscreens\compile_and_run.png" alt="compile_and_run" style="zoom:50%;" />

#### 二、完成start模块中的TODO代码项并导入已有的Tensorflow Lite模型

<img src="shotscreens\TODO_Code_1.png" alt="TODO_Code_1" style="zoom:50%;" />

<img src="shotscreens\TODO_Code_2.png" alt="TODO_Code_2" style="zoom:50%;" />

<img src="shotscreens\Tensorflow_Lite_Model.png" alt="Tensorflow_Lite_Model" style="zoom:50%;" />

### 三、在真机(OPPO)上运行start模块后的识别花朵的结果

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