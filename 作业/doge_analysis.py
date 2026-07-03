import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# 设置中文字体
plt.rcParams['font.sans-serif'] = ['SimHei', 'Arial Unicode MS', 'DejaVu Sans']
plt.rcParams['axes.unicode_minus'] = False

# (1) 读取数据并转换日期类型
df = pd.read_csv('DOGE-USD.csv')
print("原始数据类型：")
print(df.dtypes)

df['Date'] = pd.to_datetime(df['Date'])
print("\n转换后数据类型：")
print(df.dtypes)

# (2) 处理缺失值
print("\n缺失值统计：")
print(df.isnull().sum())

# 输出缺失值的日期
missing_rows = df[df.isnull().any(axis=1)]
if not missing_rows.empty:
    print("\n存在缺失值的日期：")
    print(missing_rows['Date'])

# 用前一交易日填充
df = df.ffill()
print("\n填充后缺失值统计：")
print(df.isnull().sum())

# (3) 最高价与最低价
max_price = df['High'].max()
min_price = df['Low'].min()
max_date = df.loc[df['High'].idxmax(), 'Date']
min_date = df.loc[df['Low'].idxmin(), 'Date']

print(f"\n最高价格：{max_price}，日期：{max_date}")
print(f"最低价格：{min_price}，日期：{min_date}")

# (4) 最高价格折线图
plt.figure(figsize=(12, 5))
plt.plot(df['Date'], df['High'], linewidth=0.8)
plt.title('狗狗币每天最高价格走势图')
plt.xlabel('日期')
plt.ylabel('最高价格 (USD)')
plt.grid(True, alpha=0.3)
plt.tight_layout()
plt.savefig('doge_high.png')
plt.show()

# (5) 成交量折线图（取对数）
plt.figure(figsize=(12, 5))
plt.plot(df['Date'], np.log10(df['Volume']), linewidth=0.8, color='orange')
plt.title('狗狗币每日成交量走势图（取对数）')
plt.xlabel('日期')
plt.ylabel('成交量 (log10)')
plt.grid(True, alpha=0.3)
plt.tight_layout()
plt.savefig('doge_volume.png')
plt.show()