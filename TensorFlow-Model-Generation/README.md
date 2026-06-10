#                  TensorFlow花卉图片分类器模型训练

## 花卉图片分类器：Keras 训练并导出 TFLite

TensorFlow Lite Model Maker由于依赖库与新版本的Python不兼容的问题，我们将方案转为用 TensorFlow/Keras 训练一个花卉图片分类模型，并把训练好的模型转换为 TensorFlow Lite 的 `.tflite` 文件。

它不依赖 tflite-model-maker，因此可以避开 scann、旧版 TensorFlow、旧版 Python 之间常见的安装冲突。整体流程是：下载 /读取图片数据集 -> 构建 Keras 模型 -> 训练与评估 -> 保存 .keras 模型 -> 转换并导出 .tflite 模型 -> 简单测试 TFLite 推理结果。

#### 1、安装依赖

![环境信息](shotscreens\环境信息.png)

#### 2、导入库并设置参数

```python
import tarfile
from pathlib import Path

import numpy as np
import tensorflow as tf

# TensorFlow 官方花卉数据集。第一次运行时会自动下载，之后会复用本地缓存。
FLOWER_URL = "https://storage.googleapis.com/download.tensorflow.org/example_images/flower_photos.tgz"

# 数据集保存位置
DATA_ROOT = Path(
    r"E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验五"
)

print("TensorFlow 版本:", tf.__version__)
```

    TensorFlow 版本: 2.19.1



```python
# 数据目录配置：
# - DATA_DIR = None：自动下载并使用 TensorFlow 官方 flowers 数据集。
# - DATA_DIR = r"D:\path\to\my_images"：使用你自己的图片分类目录。
#
# 自定义图片目录需要按类别分文件夹，例如：
# my_images/
#   daisy/
#     1.jpg
#   roses/
#     2.jpg
DATA_DIR = None

# 导出目录。训练完成后会在这里生成 model.tflite、labels.txt 和 flower_classifier.keras。
EXPORT_DIR = "exported_flower_model"

# 训练参数。教程演示可以先用 3 到 5 个 epoch；如果使用自己的数据，可以适当增加。
EPOCHS = 5
BATCH_SIZE = 32
IMAGE_SIZE = 224
LEARNING_RATE = 1e-3

# TFLite 量化方式：
# - "dynamic"：默认推荐，模型更小，通常最容易成功。
# - "float16"：适合部分支持 float16 的设备。
# - "int8"：体积更小，但需要代表性数据集，转换要求更严格。
# - "none"：不量化，保留浮点模型。
QUANTIZATION = "dynamic"

# 固定随机种子，方便训练/验证划分尽量可复现。
SEED = 123
```

#### 3、读取并划分数据集


```python
def load_flower_datasets(data_dir, image_size, batch_size, seed):
    # 如果没有传入自定义数据目录，就下载 TensorFlow 官方 flower_photos 数据集。
    if data_dir is None:
        archive_path = tf.keras.utils.get_file(
            fname="flower_photos.tgz",
            origin=FLOWER_URL,
            cache_dir=str(DATA_ROOT),
            cache_subdir="",
            extract=False,
        )
        archive_path = Path(archive_path)

        # Keras 可能已经缓存了解压后的目录；先检查常见位置，避免重复解压。
        candidates = [
            archive_path.parent / "flower_photos",
            archive_path.parent / "flower_photos_extracted" / "flower_photos",
        ]
        data_dir = next((path for path in candidates if path.exists()), None)
        if data_dir is None:
            with tarfile.open(archive_path, "r:gz") as tar:
                tar.extractall(archive_path.parent / "flower_photos_extracted")
            data_dir = archive_path.parent / "flower_photos_extracted" / "flower_photos"
    else:
        data_dir = Path(data_dir)

    # 从目录读取图片。目录下的每个子文件夹会被当作一个类别。
    train_ds = tf.keras.utils.image_dataset_from_directory(
        data_dir,
        validation_split=0.2,
        subset="training",
        seed=seed,
        image_size=(image_size, image_size),
        batch_size=batch_size,
    )
    val_ds = tf.keras.utils.image_dataset_from_directory(
        data_dir,
        validation_split=0.2,
        subset="validation",
        seed=seed,
        image_size=(image_size, image_size),
        batch_size=batch_size,
    )
    class_names = train_ds.class_names

    # 原始 validation 部分再拆成验证集和测试集：验证集用于训练过程中观察效果，测试集用于最后评估。
    val_batches = int(tf.data.experimental.cardinality(val_ds).numpy())
    test_ds = val_ds.take(val_batches // 2)
    val_ds = val_ds.skip(val_batches // 2)

    # cache/prefetch 可以减少数据读取等待；shuffle 只用于训练集。
    autotune = tf.data.AUTOTUNE
    train_ds = train_ds.cache().shuffle(1000, seed=seed).prefetch(autotune)
    val_ds = val_ds.cache().prefetch(autotune)
    test_ds = test_ds.cache().prefetch(autotune)
    return train_ds, val_ds, test_ds, class_names
```


```python
# 加载数据集并查看类别名称。
train_ds, val_ds, test_ds, class_names = load_flower_datasets(
    DATA_DIR,
    IMAGE_SIZE,
    BATCH_SIZE,
    SEED,
)

print("类别数量:", len(class_names))
print("类别名称:", class_names)
```

    Downloading data from https://storage.googleapis.com/download.tensorflow.org/example_images/flower_photos.tgz
    [1m228813984/228813984[0m [32m━━━━━━━━━━━━━━━━━━━━[0m[37m[0m [1m115s[0m 1us/step
    Found 3670 files belonging to 5 classes.
    Using 2936 files for training.
    Found 3670 files belonging to 5 classes.
    Using 734 files for validation.
    类别数量: 5
    类别名称: ['daisy', 'dandelion', 'roses', 'sunflowers', 'tulips']

#### 4、构建并训练 Keras 模型

```python
def build_model(num_classes, image_size, learning_rate):
    # 输入图片尺寸固定为 IMAGE_SIZE x IMAGE_SIZE x 3。
    inputs = tf.keras.Input(shape=(image_size, image_size, 3), name="image")

    # MobileNetV2 有自己的预处理方式，这里把像素值转换到模型期望的范围。
    x = tf.keras.applications.mobilenet_v2.preprocess_input(inputs)

    # include_top=False 表示不要 ImageNet 原始的 1000 类分类头，只保留特征提取部分。
    base_model = tf.keras.applications.MobileNetV2(
        input_shape=(image_size, image_size, 3),
        include_top=False,
        weights="imagenet",
        pooling="avg",
    )

    # 冻结预训练模型参数，只训练后面的 Dense 分类层。
    base_model.trainable = False
    x = base_model(x, training=False)
    x = tf.keras.layers.Dropout(0.2)(x)

    # 输出维度等于类别数量，softmax 输出每个类别的概率。
    outputs = tf.keras.layers.Dense(num_classes, activation="softmax", name="predictions")(x)
    model = tf.keras.Model(inputs, outputs)

    model.compile(
        optimizer=tf.keras.optimizers.Adam(learning_rate=learning_rate),
        loss=tf.keras.losses.SparseCategoricalCrossentropy(),
        metrics=["accuracy"],
    )
    return model
```


```python
# 创建模型并打印结构。第一次运行会下载 MobileNetV2 的 ImageNet 预训练权重。
model = build_model(len(class_names), IMAGE_SIZE, LEARNING_RATE)
model.summary()
```

    Downloading data from https://storage.googleapis.com/tensorflow/keras-applications/mobilenet_v2/mobilenet_v2_weights_tf_dim_ordering_tf_kernels_1.0_224_no_top.h5
    [1m9406464/9406464[0m [32m━━━━━━━━━━━━━━━━━━━━[0m[37m[0m [1m3s[0m 0us/step



<pre style="white-space:pre;overflow-x:auto;line-height:normal;font-family:Menlo,'DejaVu Sans Mono',consolas,'Courier New',monospace"><span style="font-weight: bold">Model: "functional"</span>
</pre>




<pre style="white-space:pre;overflow-x:auto;line-height:normal;font-family:Menlo,'DejaVu Sans Mono',consolas,'Courier New',monospace">┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━┓
┃<span style="font-weight: bold"> Layer (type)                    </span>┃<span style="font-weight: bold"> Output Shape           </span>┃<span style="font-weight: bold">       Param # </span>┃
┡━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━╇━━━━━━━━━━━━━━━━━━━━━━━━╇━━━━━━━━━━━━━━━┩
│ image (<span style="color: #0087ff; text-decoration-color: #0087ff">InputLayer</span>)              │ (<span style="color: #00d7ff; text-decoration-color: #00d7ff">None</span>, <span style="color: #00af00; text-decoration-color: #00af00">224</span>, <span style="color: #00af00; text-decoration-color: #00af00">224</span>, <span style="color: #00af00; text-decoration-color: #00af00">3</span>)    │             <span style="color: #00af00; text-decoration-color: #00af00">0</span> │
├─────────────────────────────────┼────────────────────────┼───────────────┤
│ true_divide (<span style="color: #0087ff; text-decoration-color: #0087ff">TrueDivide</span>)        │ (<span style="color: #00d7ff; text-decoration-color: #00d7ff">None</span>, <span style="color: #00af00; text-decoration-color: #00af00">224</span>, <span style="color: #00af00; text-decoration-color: #00af00">224</span>, <span style="color: #00af00; text-decoration-color: #00af00">3</span>)    │             <span style="color: #00af00; text-decoration-color: #00af00">0</span> │
├─────────────────────────────────┼────────────────────────┼───────────────┤
│ subtract (<span style="color: #0087ff; text-decoration-color: #0087ff">Subtract</span>)             │ (<span style="color: #00d7ff; text-decoration-color: #00d7ff">None</span>, <span style="color: #00af00; text-decoration-color: #00af00">224</span>, <span style="color: #00af00; text-decoration-color: #00af00">224</span>, <span style="color: #00af00; text-decoration-color: #00af00">3</span>)    │             <span style="color: #00af00; text-decoration-color: #00af00">0</span> │
├─────────────────────────────────┼────────────────────────┼───────────────┤
│ mobilenetv2_1.00_224            │ (<span style="color: #00d7ff; text-decoration-color: #00d7ff">None</span>, <span style="color: #00af00; text-decoration-color: #00af00">1280</span>)           │     <span style="color: #00af00; text-decoration-color: #00af00">2,257,984</span> │
│ (<span style="color: #0087ff; text-decoration-color: #0087ff">Functional</span>)                    │                        │               │
├─────────────────────────────────┼────────────────────────┼───────────────┤
│ dropout (<span style="color: #0087ff; text-decoration-color: #0087ff">Dropout</span>)               │ (<span style="color: #00d7ff; text-decoration-color: #00d7ff">None</span>, <span style="color: #00af00; text-decoration-color: #00af00">1280</span>)           │             <span style="color: #00af00; text-decoration-color: #00af00">0</span> │
├─────────────────────────────────┼────────────────────────┼───────────────┤
│ predictions (<span style="color: #0087ff; text-decoration-color: #0087ff">Dense</span>)             │ (<span style="color: #00d7ff; text-decoration-color: #00d7ff">None</span>, <span style="color: #00af00; text-decoration-color: #00af00">5</span>)              │         <span style="color: #00af00; text-decoration-color: #00af00">6,405</span> │
└─────────────────────────────────┴────────────────────────┴───────────────┘
</pre>




<pre style="white-space:pre;overflow-x:auto;line-height:normal;font-family:Menlo,'DejaVu Sans Mono',consolas,'Courier New',monospace"><span style="font-weight: bold"> Total params: </span><span style="color: #00af00; text-decoration-color: #00af00">2,264,389</span> (8.64 MB)
</pre>




<pre style="white-space:pre;overflow-x:auto;line-height:normal;font-family:Menlo,'DejaVu Sans Mono',consolas,'Courier New',monospace"><span style="font-weight: bold"> Trainable params: </span><span style="color: #00af00; text-decoration-color: #00af00">6,405</span> (25.02 KB)
</pre>




<pre style="white-space:pre;overflow-x:auto;line-height:normal;font-family:Menlo,'DejaVu Sans Mono',consolas,'Courier New',monospace"><span style="font-weight: bold"> Non-trainable params: </span><span style="color: #00af00; text-decoration-color: #00af00">2,257,984</span> (8.61 MB)
</pre>




```python
# 开始训练。history 中会保存每个 epoch 的 loss、accuracy、val_loss、val_accuracy。
history = model.fit(train_ds, validation_data=val_ds, epochs=EPOCHS)
```

    Epoch 1/5
    [1m92/92[0m [32m━━━━━━━━━━━━━━━━━━━━[0m[37m[0m [1m132s[0m 1s/step - accuracy: 0.6672 - loss: 0.8615 - val_accuracy: 0.8743 - val_loss: 0.4273
    Epoch 2/5
    [1m92/92[0m [32m━━━━━━━━━━━━━━━━━━━━[0m[37m[0m [1m119s[0m 1s/step - accuracy: 0.8583 - loss: 0.4112 - val_accuracy: 0.8874 - val_loss: 0.3490
    Epoch 3/5
    [1m92/92[0m [32m━━━━━━━━━━━━━━━━━━━━[0m[37m[0m [1m101s[0m 1s/step - accuracy: 0.8777 - loss: 0.3356 - val_accuracy: 0.8953 - val_loss: 0.2950
    Epoch 4/5
    [1m92/92[0m [32m━━━━━━━━━━━━━━━━━━━━[0m[37m[0m [1m104s[0m 1s/step - accuracy: 0.8995 - loss: 0.2936 - val_accuracy: 0.9084 - val_loss: 0.2762
    Epoch 5/5
    [1m92/92[0m [32m━━━━━━━━━━━━━━━━━━━━[0m[37m[0m [1m136s[0m 1s/step - accuracy: 0.9101 - loss: 0.2536 - val_accuracy: 0.9058 - val_loss: 0.2598



```python
# 使用测试集评估模型。测试集没有参与训练，用于更客观地观察最终效果。
loss, accuracy = model.evaluate(test_ds)
print(f"test_loss={loss:.4f}, test_accuracy={accuracy:.4f}")
```

    [1m11/11[0m [32m━━━━━━━━━━━━━━━━━━━━[0m[37m[0m [1m11s[0m 987ms/step - accuracy: 0.8949 - loss: 0.3363
    test_loss=0.3363, test_accuracy=0.8949

#### 5、转换为 TensorFlow Lite 模型

```python
def convert_to_tflite(model, quantization, representative_ds):
    # 从 Keras 模型创建 TFLite 转换器。
    converter = tf.lite.TFLiteConverter.from_keras_model(model)

    if quantization == "dynamic":
        # 动态范围量化：最常用、最容易成功的压缩方式。
        converter.optimizations = [tf.lite.Optimize.DEFAULT]
    elif quantization == "float16":
        # float16 量化：权重使用半精度浮点数，适合部分移动端/GPU 场景。
        converter.optimizations = [tf.lite.Optimize.DEFAULT]
        converter.target_spec.supported_types = [tf.float16]
    elif quantization == "int8":
        # int8 全整数量化：体积更小，但需要代表性数据集校准输入分布。
        converter.optimizations = [tf.lite.Optimize.DEFAULT]

        def representative_data_gen():
            for images, _ in representative_ds.take(100):
                for image in images:
                    yield [tf.expand_dims(tf.cast(image, tf.float32), 0)]

        converter.representative_dataset = representative_data_gen
        converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS_INT8]
        converter.inference_input_type = tf.uint8
        converter.inference_output_type = tf.uint8
    elif quantization != "none":
        raise ValueError(f"Unsupported quantization mode: {quantization}")

    return converter.convert()
```


```python
# 创建导出目录。
export_dir = Path(EXPORT_DIR)
export_dir.mkdir(parents=True, exist_ok=True)

# 保存标签文件。部署时需要 labels.txt 把模型输出编号映射回类别名称。
labels_path = export_dir / "labels.txt"
labels_path.write_text("\n".join(class_names) + "\n", encoding="utf-8")

# 保存 Keras 原始模型，便于以后继续训练或重新转换。
keras_path = export_dir / "flower_classifier.keras"
model.save(keras_path)

# 转换并保存 TFLite 模型。
tflite_model = convert_to_tflite(model, QUANTIZATION, train_ds)
tflite_path = export_dir / "model.tflite"
tflite_path.write_bytes(tflite_model)

print(f"已保存 Keras 模型: {keras_path}")
print(f"已保存 TFLite 模型: {tflite_path}")
print(f"已保存标签文件: {labels_path}")
```

    INFO:tensorflow:Assets written to: C:\Users\DIGETA~1\AppData\Local\Temp\tmphh_moc2m\assets


    INFO:tensorflow:Assets written to: C:\Users\DIGETA~1\AppData\Local\Temp\tmphh_moc2m\assets


    Saved artifact at 'C:\Users\DIGETA~1\AppData\Local\Temp\tmphh_moc2m'. The following endpoints are available:
    
    * Endpoint 'serve'
      args_0 (POSITIONAL_ONLY): TensorSpec(shape=(None, 224, 224, 3), dtype=tf.float32, name='image')
    Output Type:
      TensorSpec(shape=(None, 5), dtype=tf.float32, name=None)
    Captures:
      1758167934656: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168012528: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168008480: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168010592: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168012176: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168019744: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168018688: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168021152: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168016928: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168019920: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758168020976: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157672112: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157674400: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157670352: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157673168: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157675632: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157678448: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157680736: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157676688: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157679504: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157674576: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157883872: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157886688: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157882816: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157885632: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157889328: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157892144: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157894432: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157890384: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157893200: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157896368: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157949056: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157949584: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157949408: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157949232: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157881760: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157955216: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157957856: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157953456: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157955744: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157961200: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158046656: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158045424: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758157960496: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158046832: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158050176: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158052992: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158055280: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158051232: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158054048: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158047888: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158143904: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158146192: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158146016: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158145664: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158149360: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158152176: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158154464: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158150416: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158153232: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158155696: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158155168: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158157456: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158158864: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158159040: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158059680: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158264400: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158267040: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158262640: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158264928: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158269680: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158272496: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158271264: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158270736: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158273552: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163045360: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163048176: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163050464: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163046416: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163049232: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163056624: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163055568: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163058208: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163053808: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163056096: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163058032: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163227696: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163229984: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163225936: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163228752: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163232624: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163235440: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163237728: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163233680: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163236496: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163053280: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163277376: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163280016: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163275616: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163277904: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163282656: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163285472: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163287760: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163283712: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163286528: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163287584: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163144544: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163146832: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163142784: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163145600: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163152992: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163151936: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163154576: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163150176: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163152464: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163154400: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163358064: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163359120: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163355072: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163357888: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163361760: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163364576: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163366864: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163362816: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163365632: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163359296: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162864080: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162866896: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162863024: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162865840: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162869536: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162872352: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162874640: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162870592: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162873408: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758162877984: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159490032: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159487040: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159487568: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159490208: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758163369680: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159496192: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159498832: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159494432: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159496720: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159502176: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159587632: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159586928: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159588160: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159587808: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159591328: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159594144: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159596432: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159592384: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159595200: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159588864: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159717296: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159716592: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159717120: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159716416: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159721696: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159724512: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159726800: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159722752: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159725568: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159728032: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159727504: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159729088: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159729792: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159731728: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159599248: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159803968: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159806608: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159802208: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159804496: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159809248: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159812064: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159814352: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159810304: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159813120: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159814176: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159869152: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159871440: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159867392: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159870208: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159877600: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159876544: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159879184: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159874784: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159877072: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159879008: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159983136: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159985424: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159981376: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159984192: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159988064: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159990880: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159993168: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159989120: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159991936: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159874256: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160081968: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160084608: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160080208: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160082496: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160087248: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160090064: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160092352: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160088304: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160091120: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160092176: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160178512: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160180800: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160176752: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160179568: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160079680: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160185904: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160188544: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160184144: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160186432: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160188368: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160260960: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160262016: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160258496: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160261136: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160264656: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160267472: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160269760: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160265712: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160268528: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160262192: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159340992: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159343808: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159339760: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159342752: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159346448: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159349264: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159351552: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159347504: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159350320: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758159354896: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158490080: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158487616: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158489376: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158490256: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758160272576: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158496240: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158498880: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158494480: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158496768: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158502224: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158571296: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158569536: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158571648: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158571472: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158573584: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158576400: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158578688: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158574640: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158577456: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158576928: TensorSpec(shape=(), dtype=tf.resource, name=None)
      1758158884352: TensorSpec(shape=(), dtype=tf.resource, name=None)
    已保存 Keras 模型: exported_flower_model\flower_classifier.keras
    已保存 TFLite 模型: exported_flower_model\model.tflite
    已保存标签文件: exported_flower_model\labels.txt

#### 6、简单测试导出的 TFLite 模型

```python
def smoke_test_tflite(tflite_path, test_ds, class_names):
    # 加载 TFLite 模型并分配张量内存。
    interpreter = tf.lite.Interpreter(model_path=str(tflite_path))
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()[0]
    output_details = interpreter.get_output_details()[0]

    # 从测试集中取 8 张图片做快速推理。
    images, labels = next(iter(test_ds.unbatch().batch(8)))
    input_data = tf.cast(images, input_details["dtype"]).numpy()

    # 如果模型是 uint8 输入，需要按照量化参数把图片转换到对应范围。
    if input_details["dtype"] == np.uint8:
        scale, zero_point = input_details["quantization"]
        if scale:
            input_data = images.numpy() / scale + zero_point
            input_data = np.clip(input_data, 0, 255).astype(np.uint8)

    predictions = []
    for image in input_data:
        interpreter.set_tensor(input_details["index"], np.expand_dims(image, 0))
        interpreter.invoke()
        predictions.append(interpreter.get_tensor(output_details["index"])[0])

    predicted_ids = np.argmax(np.asarray(predictions), axis=1)
    for expected, predicted in zip(labels.numpy()[:5], predicted_ids[:5]):
        print(f"真实类别={class_names[expected]}, 预测类别={class_names[predicted]}")
```


```python
# 运行 TFLite 快速测试。
smoke_test_tflite(tflite_path, test_ds, class_names)
```

    F:\conda_envs\Traffic_Flow_Prediction\lib\site-packages\tensorflow\lite\python\interpreter.py:457: UserWarning:     Warning: tf.lite.Interpreter is deprecated and is scheduled for deletion in
        TF 2.20. Please use the LiteRT interpreter from the ai_edge_litert package.
        See the [migration guide](https://ai.google.dev/edge/litert/migration)
        for details.
        
      warnings.warn(_INTERPRETER_DELETION_WARNING)


    真实类别=daisy, 预测类别=daisy
    真实类别=tulips, 预测类别=tulips
    真实类别=sunflowers, 预测类别=sunflowers
    真实类别=daisy, 预测类别=daisy
    真实类别=sunflowers, 预测类别=sunflowers

#### 7、生成的model.tflite实际运行结果

daisy:

<img src="shotscreens\daisy.png" alt="daisy" style="zoom:50%;" />

dandelion:

<img src="shotscreens\dandelion.png" alt="dandelion" style="zoom:50%;" />

roses:

<img src="shotscreens\roses.png" alt="roses" style="zoom:50%;" />

sunflowers:

<img src="shotscreens\sunflowers.png" alt="sunflowers" style="zoom:50%;" />

tulips:

<img src="shotscreens\tulips.png" alt="tulips" style="zoom:50%;" />
