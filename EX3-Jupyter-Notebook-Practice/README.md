# Jupyter Notebook基础教程

## 实验目的

• 进一步熟悉Python的语法

• 熟悉Notebook开发的基本流程

• 熟悉Python中常用库的用法

## 实验内容

• 安装Jupyter Notebook和相关的Python环境，

建议采用Anaconda的安装方式。

• 按照教程完成实验过程，主要包括几个方面：

• 掌握Notebook工具的基本原理

• 学习Python基本语法，完成相关功能

• 完成Python数据分析的例子

• 将上述完成的Jupyter Notebook在Github上进行

共享。

#### 1、Notebook基本概念

• 熟悉Notebook的快捷键

• 掌握Notebook中Cell的两种模式（Edit和Command）

• 理解Notebook中Kernel的概念

cell：

主要包含两种类型的cell：

- 代码cell：包含可被kernel执行的代码，执行之后在下方显示输出。
- Markdown cell：书写Markdown标记语言的cell。


```python
print('Hello World!')
```

    Hello World!

上面代码执行之后，cell左侧的标签从`In [ ]` 变成了 `In [1]`。`In`代表输入，`[]`中的数字代表kernel执行的顺序，而`In [*]`则表示代码cell正在执行代码。以下例子显示了短暂的`In [*]`过程。

```python
import time
time.sleep(3)
```

Kernel：

每个notebook都基于一个内核运行，当执行cell代码时，代码将在内核当中运行，运行的结果会显示在页面上。Kernel中运行的状态在整个文档中是延续的，可以跨越所有的cell。这意思着在一个Notebook某个cell定义的函数或者变量等，在其他cell也可以使用。例如：


```python
import numpy as np
def square(x):
    return x * x
```

执行上述代码cell之后，后续cell可以使用`np`和`square`


```python
x = np.random.randint(1, 10)
y = square(x)
print('%d squared is %d' % (x, y))
```

    7 squared is 49



#### 2、熟悉基本的Python语法

• 掌握Python基本语法并编写选择排序算法

• 定义selection_sort函数执行选择排序功能。

• 定 义 test 函数进行测试 ， 执行数据输入 ， 并 调 用

selection_sort函数进行排序，最后输出结果。


```python
def selection_sort(arr):
    """
    选择排序函数
    对列表 arr 进行升序排序
    """
    n = len(arr)

    for i in range(n - 1):
        min_index = i

        for j in range(i + 1, n):
            if arr[j] < arr[min_index]:
                min_index = j

        arr[i], arr[min_index] = arr[min_index], arr[i]

    return arr


def test():
    """
    测试函数
    输入数据，调用 selection_sort 函数排序，最后输出结果
    """
    data = input("请输入要排序的数据，用空格分隔：")

    nums = list(map(int, data.split()))

    result = selection_sort(nums)

    print("排序后的结果为：", result)


test()
```

    请输入要排序的数据，用空格分隔： 64 25 12 22 11


    排序后的结果为： [11, 12, 22, 25, 64]



#### 3、数据分析

• 使用Pandas库对数据集（财富500强排名）进行分析

• Pandas是一种高效、强大、灵活且易于使用的开源数据分析和操作工具，

它建立在Python之上

• 数据操作包括数据显示、检查数据列属性、数据过滤、属性查询等

• 实验将完成删除“利润”列包含异常值的数据行

• 使用Matplotlib进行数据图形的绘制

• Matplotlib 是一个综合库，用于在 Python 中创建静态、动画和交互式可

视化。

• 基础实验将利润和收入分别绘制，请完成一张图同时画利润和收入。

##### 设置：

导入相关的工具库：


```python
%matplotlib inline
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
```

加载数据集


```python
df = pd.read_csv(r"E:\backup\大学课程文件\2025-2026第二学期\软件项目研发实践\实验三\fortune500.csv")
```

##### 检查数据集：


```python
df.head()
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }
    
    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Year</th>
      <th>Rank</th>
      <th>Company</th>
      <th>Revenue (in millions)</th>
      <th>Profit (in millions)</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>0</th>
      <td>1955</td>
      <td>1</td>
      <td>General Motors</td>
      <td>9823.5</td>
      <td>806</td>
    </tr>
    <tr>
      <th>1</th>
      <td>1955</td>
      <td>2</td>
      <td>Exxon Mobil</td>
      <td>5661.4</td>
      <td>584.8</td>
    </tr>
    <tr>
      <th>2</th>
      <td>1955</td>
      <td>3</td>
      <td>U.S. Steel</td>
      <td>3250.4</td>
      <td>195.4</td>
    </tr>
    <tr>
      <th>3</th>
      <td>1955</td>
      <td>4</td>
      <td>General Electric</td>
      <td>2959.1</td>
      <td>212.6</td>
    </tr>
    <tr>
      <th>4</th>
      <td>1955</td>
      <td>5</td>
      <td>Esmark</td>
      <td>2510.8</td>
      <td>19.1</td>
    </tr>
  </tbody>
</table>
</div>




```python
df.tail()
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }
    
    .dataframe thead th {
        text-align: right;
    }
</style>
<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>Year</th>
      <th>Rank</th>
      <th>Company</th>
      <th>Revenue (in millions)</th>
      <th>Profit (in millions)</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>25495</th>
      <td>2005</td>
      <td>496</td>
      <td>Wm. Wrigley Jr.</td>
      <td>3648.6</td>
      <td>493</td>
    </tr>
    <tr>
      <th>25496</th>
      <td>2005</td>
      <td>497</td>
      <td>Peabody Energy</td>
      <td>3631.6</td>
      <td>175.4</td>
    </tr>
    <tr>
      <th>25497</th>
      <td>2005</td>
      <td>498</td>
      <td>Wendy's International</td>
      <td>3630.4</td>
      <td>57.8</td>
    </tr>
    <tr>
      <th>25498</th>
      <td>2005</td>
      <td>499</td>
      <td>Kindred Healthcare</td>
      <td>3616.6</td>
      <td>70.6</td>
    </tr>
    <tr>
      <th>25499</th>
      <td>2005</td>
      <td>500</td>
      <td>Cincinnati Financial</td>
      <td>3614.0</td>
      <td>584</td>
    </tr>
  </tbody>
</table>
</div>

对数据属性列进行重命名，以便在后续访问:


```python
df.columns = ['year', 'rank', 'company', 'revenue', 'profit']
```

检查数据条目是否加载完整:


```python
len(df)
```




    25500

检查属性列的类型


```python
df.dtypes
```




    year         int64
    rank         int64
    company     object
    revenue    float64
    profit      object
    dtype: object

其他属性列都正常，但是对于profit属性，期望的结果是float类型，因此其可能包含非数字的值，利用正则表达式进行检查。


```python
non_numberic_profits = df.profit.str.contains('[^0-9.-]')
df.loc[non_numberic_profits].head()
```




<div>
<style scoped>
    .dataframe tbody tr th:only-of-type {
        vertical-align: middle;
    }

    .dataframe tbody tr th {
        vertical-align: top;
    }
    
    .dataframe thead th {
        text-align: right;
    }
</style>

<table border="1" class="dataframe">
  <thead>
    <tr style="text-align: right;">
      <th></th>
      <th>year</th>
      <th>rank</th>
      <th>company</th>
      <th>revenue</th>
      <th>profit</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>228</th>
      <td>1955</td>
      <td>229</td>
      <td>Norton</td>
      <td>135.0</td>
      <td>N.A.</td>
    </tr>
    <tr>
      <th>290</th>
      <td>1955</td>
      <td>291</td>
      <td>Schlitz Brewing</td>
      <td>100.0</td>
      <td>N.A.</td>
    </tr>
    <tr>
      <th>294</th>
      <td>1955</td>
      <td>295</td>
      <td>Pacific Vegetable Oil</td>
      <td>97.9</td>
      <td>N.A.</td>
    </tr>
    <tr>
      <th>296</th>
      <td>1955</td>
      <td>297</td>
      <td>Liebmann Breweries</td>
      <td>96.0</td>
      <td>N.A.</td>
    </tr>
    <tr>
      <th>352</th>
      <td>1955</td>
      <td>353</td>
      <td>Minneapolis-Moline</td>
      <td>77.4</td>
      <td>N.A.</td>
    </tr>
  </tbody>
</table>
</div>

确实存在这样的记录，profit这一列为字符串，统计一下到底存在多少条这样的记录。


```python
len(df.profit[non_numberic_profits])
```




    369

使用直方图显示一下按照年份的分布情况


```python
bin_sizes, _, _ = plt.hist(df.year[non_numberic_profits], bins=range(1955, 2006))
```


<img src="shotscreens\output_1.png" alt="output_1"  />
​    

可见，单独年份这样的记录数都少于25条，即少于4%的比例。这在可以接受的范围内，因此删除这些记录。

```python
df = df.loc[~non_numberic_profits]
df.profit = df.profit.apply(pd.to_numeric)
```

再次检查数据记录的条目数


```python
len(df)
```




    25131




```python
df.dtypes
```




    year         int64
    rank         int64
    company     object
    revenue    float64
    profit     float64
    dtype: object



#### 4、数据图形绘制

以年分组绘制平均利润和收入:


```python
group_by_year = df.loc[:, ['year', 'revenue', 'profit']].groupby('year')
avgs = group_by_year.mean()
x = avgs.index
y1 = avgs.profit
def plot(x, y, ax, title, y_label):
    ax.set_title(title)
    ax.set_ylabel(y_label)
    ax.plot(x, y)
    ax.margins(x=0, y=0)
```


```python
fig, ax = plt.subplots()
plot(x, y1, ax, 'Increase in mean Fortune 500 company profits from 1955 to 2005', 'Profit (millions)')
```

<img src="shotscreens\output_2.png" alt="output_2"  />
    

收入曲线:

```python
y2 = avgs.revenue
fig, ax = plt.subplots()
plot(x, y2, ax, 'Increase in mean Fortune 500 company revenues from 1955 to 2005', 'Revenue (millions)')
```

<img src="shotscreens\output_3.png" alt="output_3"  />
    

对数据结果进行标准差处理:

```python
def plot_with_std(x, y, stds, ax, title, y_label):
    ax.fill_between(x, y - stds, y + stds, alpha=0.2)
    plot(x, y, ax, title, y_label)
fig, (ax1, ax2) = plt.subplots(ncols=2)
title = 'Increase in mean and std Fortune 500 company %s from 1955 to 2005'
stds1 = group_by_year.std().profit.values
stds2 = group_by_year.std().revenue.values
plot_with_std(x, y1.values, stds1, ax1, title % 'profits', 'Profit (millions)')
plot_with_std(x, y2.values, stds2, ax2, title % 'revenues', 'Revenue (millions)')
fig.set_size_inches(14, 4)
fig.tight_layout()
```

<img src="shotscreens\output_4.png" alt="output_4"  />
    

一张图同时画利润和收入:

```python
fig, ax1 = plt.subplots(figsize=(8, 5))

# 左侧 y 轴：利润
line1 = ax1.plot(
    x, y1,
    label='Profit',
    color='tab:blue'
)

ax1.set_ylabel('Profit (millions)', color='tab:blue')
ax1.tick_params(axis='y', labelcolor='tab:blue')

# 右侧 y 轴：收入
ax2 = ax1.twinx()

line2 = ax2.plot(
    x, y2,
    label='Revenue',
    color='tab:orange'
)

ax2.set_ylabel('Revenue (millions)', color='tab:orange')
ax2.tick_params(axis='y', labelcolor='tab:orange')

# 设置标题
ax1.set_title(
    'Increase in mean Fortune 500 company profits and revenues from 1955 to 2005'
)

# 合并图例
lines = line1 + line2
labels = [line.get_label() for line in lines]
ax1.legend(lines, labels, loc='upper left')

# 去掉左右空白
ax1.margins(x=0)
ax2.margins(x=0)

plt.show()
```


<img src="shotscreens\output_5.png" alt="output_5"  />
    

