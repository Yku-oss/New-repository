import requests
from bs4 import BeautifulSoup
import time

# 请求头，模拟浏览器访问
headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/91.0.4472.124 Safari/537.36'
}

# 存储所有电影信息
movies = []

session = requests.Session()
session.headers.update(headers)

# 豆瓣 Top250 共 10 页，每页 25 条
for start in range(0, 250, 25):
    url = f'https://movie.douban.com/top250?start={start}'
    
    # 发送请求
    response = session.get(url, timeout=15)
    if response.status_code != 200:
        print(f'请求失败: {response.status_code} -> {url}')
        break
    if '验证码' in response.text or '请先登录' in response.text:
        print('可能被豆瓣反爬，返回了验证或登录页面，请稍后再试。')
        break
    soup = BeautifulSoup(response.text, 'html.parser')
    
    # 获取所有电影条目
    items = soup.select('.grid_view li')
    
    for item in items:
        # 排名
        rank = item.select_one('.pic em').text
        # 中文片名
        title = item.select_one('.title').text
        # 评分
        rating = item.select_one('.rating_num').text
        # 链接
        link = item.select_one('a')['href']
        
        movies.append({
            'rank': rank,
            'title': title,
            'rating': rating,
            'link': link
        })
    
    # 礼貌性延时，避免请求过快
    time.sleep(2)

# 输出结果
print("排名-中文片名-评分-链接")
print("-" * 60)
for movie in movies:
    print(f"{movie['rank']}-{movie['title']}-{movie['rating']}-{movie['link']}")