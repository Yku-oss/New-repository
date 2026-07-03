import os
import pandas as pd

# # 如果还没有 drinks.csv，先下载（下载一次后可以注释掉）
# import urllib.request
# url = "https://raw.githubusercontent.com/fivethirtyeight/data/master/alcohol-consumption/drinks.csv"
# urllib.request.urlretrieve(url, "drinks.csv")

# 读取数据
base_dir = os.path.dirname(__file__)
csv_path = os.path.join(base_dir, 'drinks.csv')
drinks = pd.read_csv(csv_path)

# (1) 输出包含缺失值的行
print("包含缺失值的行：")
print(drinks[drinks.isnull().any(axis=1)])
print()

# (2) 将 continent 中的 NaN 替换为 'NA'（两种方式任选一种）
# 方法一：读取时直接处理
# drinks = pd.read_csv('drinks.csv', keep_default_na=False)

# 方法二：读取后替换缺失值
drinks['continent'] = drinks['continent'].fillna('NA')

# (3) 各洲平均消费量（啤酒、烈酒、红酒）
print("各大洲的平均消费量：")
print(drinks.groupby('continent')[['beer_servings', 'spirit_servings', 'wine_servings']].mean())
print()

# (4) 消费量最高的国家
def get_max_country(df, col):
    return df.loc[df[col].idxmax(), 'country']

print("啤酒消费量最高的国家：", get_max_country(drinks, 'beer_servings'))
print("烈酒消费量最高的国家：", get_max_country(drinks, 'spirit_servings'))
print("红酒消费量最高的国家：", get_max_country(drinks, 'wine_servings'))