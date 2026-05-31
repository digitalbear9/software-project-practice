### 一、编译运行并成功解决生成项目中的错误

启动finish模块中的MainActivity.kt：

![image-20260531202040076](C:\Users\digetal-bear\AppData\Roaming\Typora\typora-user-images\image-20260531202040076.png)

### 二、完成start模块中的TODO代码项并导入已有的Tensorflow Lite模型

![image-20260531204110853](C:\Users\digetal-bear\AppData\Roaming\Typora\typora-user-images\image-20260531204110853.png)

![image-20260531204154005](C:\Users\digetal-bear\AppData\Roaming\Typora\typora-user-images\image-20260531204154005.png)

![image-20260531204208542](C:\Users\digetal-bear\AppData\Roaming\Typora\typora-user-images\image-20260531204208542.png)

### 三、在真机(OPPO)上运行start模块后的识别花朵的结果

dandelion:

![dandelion](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\dandelion.jpg)

daisy:

![daisy](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\daisy.jpg)

sunflowers：

![sunflowers](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\sunflowers.jpg)

roses:

![roses](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\roses.jpg)

tulips:

![tulips](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\tulips.jpg)

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

![rose_extend](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\rose_extend.jpg)

dandelion:

![dandelion_extend](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\dandelion_extend.jpg)

tulip:

![tulip_extend](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\tulip_extend.jpg)

sunflowers:

![sunflowers_extend](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\sunflowers_extend.jpg)

daisy:

![daisy_extend](E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验四\daisy_extend.jpg)