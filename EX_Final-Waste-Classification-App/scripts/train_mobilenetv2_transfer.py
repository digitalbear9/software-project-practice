from pathlib import Path
import json

import numpy as np
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from sklearn.metrics import classification_report, confusion_matrix


# =========================
# 一、路径设置
# =========================

PROJECT_DIR = Path(__file__).resolve().parents[1]

DATASET_DIR = PROJECT_DIR / "dataset_v1_split"
TRAIN_DIR = DATASET_DIR / "train"
VAL_DIR = DATASET_DIR / "val"
TEST_DIR = DATASET_DIR / "test"

LABELS_PATH = PROJECT_DIR / "labels.txt"

MODEL_DIR = PROJECT_DIR / "models"
MODEL_DIR.mkdir(parents=True, exist_ok=True)

KERAS_MODEL_PATH = MODEL_DIR / "waste_classification_mobilenetv2_v1.keras"
TFLITE_FLOAT_PATH = MODEL_DIR / "waste_classification_mobilenetv2_v1_float32.tflite"
TFLITE_DYNAMIC_PATH = MODEL_DIR / "waste_classification_mobilenetv2_v1_dynamic_range.tflite"

CLASS_NAMES_JSON = MODEL_DIR / "class_names.json"
REPORT_PATH = MODEL_DIR / "test_report.txt"
CONFUSION_MATRIX_PATH = MODEL_DIR / "confusion_matrix.txt"


# =========================
# 二、训练参数
# =========================

IMG_SIZE = 224
BATCH_SIZE = 32
SEED = 42

NUM_CLASSES = 7

INITIAL_EPOCHS = 15
FINE_TUNE_EPOCHS = 10


# =========================
# 三、读取 labels.txt
# =========================

with open(LABELS_PATH, "r", encoding="utf-8") as f:
    CLASS_NAMES = [line.strip() for line in f if line.strip()]

if len(CLASS_NAMES) != NUM_CLASSES:
    raise ValueError(f"labels.txt 中应该有 {NUM_CLASSES} 类，但当前是 {len(CLASS_NAMES)} 类")

print("类别顺序：")
for index, name in enumerate(CLASS_NAMES):
    print(index, name)


# =========================
# 四、加载数据集
# =========================

train_ds = tf.keras.utils.image_dataset_from_directory(
    TRAIN_DIR,
    labels="inferred",
    label_mode="int",
    class_names=CLASS_NAMES,
    image_size=(IMG_SIZE, IMG_SIZE),
    batch_size=BATCH_SIZE,
    shuffle=True,
    seed=SEED,
)

val_ds = tf.keras.utils.image_dataset_from_directory(
    VAL_DIR,
    labels="inferred",
    label_mode="int",
    class_names=CLASS_NAMES,
    image_size=(IMG_SIZE, IMG_SIZE),
    batch_size=BATCH_SIZE,
    shuffle=False,
)

test_ds = tf.keras.utils.image_dataset_from_directory(
    TEST_DIR,
    labels="inferred",
    label_mode="int",
    class_names=CLASS_NAMES,
    image_size=(IMG_SIZE, IMG_SIZE),
    batch_size=BATCH_SIZE,
    shuffle=False,
)

AUTOTUNE = tf.data.AUTOTUNE

train_ds = train_ds.prefetch(AUTOTUNE)
val_ds = val_ds.prefetch(AUTOTUNE)
test_ds = test_ds.prefetch(AUTOTUNE)


# =========================
# 五、数据增强
# =========================

data_augmentation = keras.Sequential(
    [
        layers.RandomFlip("horizontal"),
        layers.RandomRotation(0.08),
        layers.RandomZoom(0.10),
        layers.RandomTranslation(0.08, 0.08),
        layers.RandomContrast(0.10),
    ],
    name="data_augmentation",
)


# =========================
# 六、构建 MobileNetV2 迁移学习模型
# =========================

base_model = tf.keras.applications.MobileNetV2(
    input_shape=(IMG_SIZE, IMG_SIZE, 3),
    include_top=False,
    weights="imagenet",
)

# 第一阶段：冻结 MobileNetV2 主干，只训练后面的分类头
base_model.trainable = False

inputs = keras.Input(shape=(IMG_SIZE, IMG_SIZE, 3), name="input_image")

x = data_augmentation(inputs)

# MobileNetV2 官方预处理要求输入缩放到 [-1, 1] 区间。
# 由于你的图片读入后是 0-255，这里加 Rescaling 层。
x = layers.Rescaling(1.0 / 127.5, offset=-1.0, name="mobilenetv2_rescale")(x)

x = base_model(x, training=False)
x = layers.GlobalAveragePooling2D(name="global_average_pooling")(x)
x = layers.Dropout(0.3, name="dropout")(x)

outputs = layers.Dense(NUM_CLASSES, activation="softmax", name="predictions")(x)

model = keras.Model(inputs, outputs, name="Waste_Classification_MobileNetV2_V1")

model.compile(
    optimizer=keras.optimizers.Adam(learning_rate=1e-3),
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"],
)

model.summary()


# =========================
# 七、回调函数
# =========================

callbacks = [
    keras.callbacks.ModelCheckpoint(
        filepath=KERAS_MODEL_PATH,
        monitor="val_accuracy",
        save_best_only=True,
        verbose=1,
    ),
    keras.callbacks.EarlyStopping(
        monitor="val_accuracy",
        patience=5,
        restore_best_weights=True,
        verbose=1,
    ),
    keras.callbacks.ReduceLROnPlateau(
        monitor="val_loss",
        factor=0.3,
        patience=3,
        min_lr=1e-6,
        verbose=1,
    ),
]


# =========================
# 八、第一阶段训练：冻结主干
# =========================

print("\n========== 第一阶段：冻结 MobileNetV2 主干，只训练分类头 ==========")

history_1 = model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=INITIAL_EPOCHS,
    callbacks=callbacks,
)


# =========================
# 九、第二阶段训练：微调后 40 层
# =========================

print("\n========== 第二阶段：微调 MobileNetV2 后 40 层 ==========")

base_model.trainable = True

fine_tune_at = len(base_model.layers) - 40

for layer in base_model.layers[:fine_tune_at]:
    layer.trainable = False

# 微调时继续冻结 BatchNormalization 层，训练更稳定
for layer in base_model.layers:
    if isinstance(layer, layers.BatchNormalization):
        layer.trainable = False

model.compile(
    optimizer=keras.optimizers.Adam(learning_rate=1e-5),
    loss="sparse_categorical_crossentropy",
    metrics=["accuracy"],
)

history_2 = model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=INITIAL_EPOCHS + FINE_TUNE_EPOCHS,
    initial_epoch=len(history_1.epoch),
    callbacks=callbacks,
)


# =========================
# 十、测试集评估
# =========================

print("\n========== 测试集评估 ==========")

best_model = keras.models.load_model(KERAS_MODEL_PATH)

test_loss, test_acc = best_model.evaluate(test_ds, verbose=1)

print(f"Test loss: {test_loss:.4f}")
print(f"Test accuracy: {test_acc:.4f}")


# =========================
# 十一、分类报告和混淆矩阵
# =========================

y_true = []
y_pred = []

for images, labels in test_ds:
    probs = best_model.predict(images, verbose=0)
    preds = np.argmax(probs, axis=1)

    y_true.extend(labels.numpy().tolist())
    y_pred.extend(preds.tolist())

report = classification_report(
    y_true,
    y_pred,
    target_names=CLASS_NAMES,
    digits=4,
)

cm = confusion_matrix(y_true, y_pred)

print("\n分类报告：")
print(report)

print("\n混淆矩阵：")
print(cm)

with open(REPORT_PATH, "w", encoding="utf-8") as f:
    f.write(report)

np.savetxt(CONFUSION_MATRIX_PATH, cm, fmt="%d")

with open(CLASS_NAMES_JSON, "w", encoding="utf-8") as f:
    json.dump(CLASS_NAMES, f, ensure_ascii=False, indent=2)


# =========================
# 十二、导出 float32 TFLite
# =========================

print("\n========== 导出 float32 TFLite ==========")

converter = tf.lite.TFLiteConverter.from_keras_model(best_model)
tflite_float_model = converter.convert()

with open(TFLITE_FLOAT_PATH, "wb") as f:
    f.write(tflite_float_model)

print("float32 TFLite 已保存：", TFLITE_FLOAT_PATH)


# =========================
# 十三、导出动态范围量化 TFLite
# =========================

print("\n========== 导出动态范围量化 TFLite ==========")

converter = tf.lite.TFLiteConverter.from_keras_model(best_model)

# 动态范围量化：转换时量化权重，通常能减小模型体积
converter.optimizations = [tf.lite.Optimize.DEFAULT]

tflite_dynamic_model = converter.convert()

with open(TFLITE_DYNAMIC_PATH, "wb") as f:
    f.write(tflite_dynamic_model)

print("动态范围量化 TFLite 已保存：", TFLITE_DYNAMIC_PATH)


# =========================
# 十四、输出文件大小
# =========================

def file_size_mb(path: Path):
    return path.stat().st_size / 1024 / 1024


print("\n========== Waste-Classification-App 模型训练与导出完成 ==========")
print("Keras 模型：", KERAS_MODEL_PATH)
print("float32 TFLite：", TFLITE_FLOAT_PATH, f"{file_size_mb(TFLITE_FLOAT_PATH):.2f} MB")
print("动态范围量化 TFLite：", TFLITE_DYNAMIC_PATH, f"{file_size_mb(TFLITE_DYNAMIC_PATH):.2f} MB")
print("分类报告：", REPORT_PATH)
print("混淆矩阵：", CONFUSION_MATRIX_PATH)
print("类别顺序文件：", CLASS_NAMES_JSON)