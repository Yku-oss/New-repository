# 爬虫相关模块
import requests
from bs4 import BeautifulSoup

# 发送邮箱相关模块
import smtplib
from email.mime.text import MIMEText
from email.header import Header

# 定时模块
import schedule
import time


# 请求网页
def page_request(url, header):
    response = requests.get(url=url, headers=header)
    html = response.content.decode("UTF-8")
    return html


# 解析网页
def page_parse(html):
    soup = BeautifulSoup(html, 'lxml')
    news = []
    # 处理热搜前50
    urls_title = soup.select('#pl_top_realtimehot > table > tbody > tr > td.td-02 > a')
    hotness = soup.select('#pl_top_realtimehot > table > tbody > tr > td.td-02 > span')
    for i in range(len(urls_title)):
        new = {}
        title = urls_title[i].get_text()
        url = urls_title[i].get('href')
        # 个别链接会出现异常
        if url == 'javascript:void(0);':
            url = urls_title[i].get('href_to')
        # 热搜top没有显示热度
        if i == 0:
            hot = 'top'
        else:
            hot = hotness[i - 1].get_text()

        new['title'] = title
        new['url'] = "https://s.weibo.com" + url
        new['hot'] = hot
        news.append(new)
    print(len(news))
    for element in news:
        print(element['title'] + '\t' + element['hot'] + '\t' + element['url'])
    # 发送邮件
    sendMail(news)


# 将获取到的热搜信息发送到邮箱
def sendMail(news):
    import smtplib
    from email.mime.text import MIMEText
    
    from_addr = '2078478787@qq.com'
    password = 'rlpnrwfyxrtofiib'  # 授权码
    to_addr = '2078478787@qq.com'
    
    # 构建内容
    content = ''
    for i in range(min(len(news), 30)):
        content += f"{i+1}、{news[i]['title']} 热度：{news[i]['hot']}\n链接：{news[i]['url']}\n\n"
    get_time = time.strftime('%Y-%m-%d %X', time.localtime(time.time()))
    content += f'获取时间：{get_time}'
    
    msg = MIMEText(content, 'plain', 'utf-8')
    msg['From'] = from_addr
    msg['To'] = to_addr
    msg['Subject'] = '微博热搜'
    
    try:
        smtp = smtplib.SMTP_SSL('smtp.qq.com', 465)
        smtp.set_debuglevel(1)
        smtp.login(from_addr, password)
        smtp.sendmail(from_addr, [to_addr], msg.as_string())
        smtp.quit()
        print('succeed sending')
        return True
    except Exception as e:
        print(f'邮件发送失败：{e}')
        return False

def job():
    print('**************开始爬取微博热搜**************')
    header = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.60 Safari/537.36',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
        'Accept-Language': 'zh-CN,zh-Hans;q=0.9',
        'Accept-Encoding': 'gzip, deflate, br',
        'Cookie': 'SUB=_2AkMdX4CHf8NxqwFRm_0QymzkaolxzQDEieKrA3FcJRMxHRl-yT9kqm0FtRB6Nt-uaF7s09sFmx5D0iX7g3quv5ushDOa; SUBP=0033WrSXqPxfM72-Ws9jqgMF55529P9D9W5.fXzzJWw91kkBJ_2G.ygs; _s_tentry=passport.weibo.com; Apache=4756046656166.246.1778585521476; SINAGLOBAL=4756046656166.246.1778585521476; ULV=1778585521477:1:1:1:4756046656166.246.1778585521476:'   # 请替换成你自己的Cookie
    }
    url = 'https://s.weibo.com/top/summary'
    html = page_request(url=url, header=header)
    page_parse(html)


if __name__ == "__main__":
    # 仅执行一次，便于测试邮件发送是否成功
    job()