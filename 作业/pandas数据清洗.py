import pandas as pd
import numpy as np
from pandas import Series, DataFrame

# ========== 实验一：计算机语言分数数据 ==========

# (1) 创建 language Series
language = Series(["Python","C","Scala","Java","GO","Scala","SQL","PHP","Python"])
print("=== language Series ===")
print(language)
print()

# (2) 创建随机 score Series
score = Series(np.random.randint(0, 10, size=len(language)))
print("=== score Series ===")
print(score)
print()

# (3) 创建 DataFrame
df = DataFrame({"language": language, "score": score})
print("=== 完整 DataFrame ===")
print(df)
print()

# (4) 前4行
print("=== 前4行 ===")
print(df.head(4))
print()

# (5) language 为 Python 的行
print("=== language 为 Python 的行 ===")
print(df[df['language'] == 'Python'])
print()

# (6) 按 score 升序排序
df_sorted = df.sort_values('score')
print("=== 按 score 升序排序 ===")
print(df_sorted)
print()

# (7) 统计每种语言出现次数
print("=== 语言出现次数统计 ===")
print(df['language'].value_counts())
print()

# ========== 实验二：酒类消费数据（需要 drinks.csv 文件） ==========
# 如果你有 drinks.csv 文件，取消下面的注释
# df_drinks = pd.read_csv('drinks.csv')
# print("=== drinks.csv 前5行 ===")