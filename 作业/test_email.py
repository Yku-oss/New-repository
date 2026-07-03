import smtplib
from email.mime.text import MIMEText

from_addr = '2078478787@qq.com'
password = 'srfrkuierbeljaai'  # 授权码
to_addr = '2078478787@qq.com'   # 先发给自己

msg = MIMEText('这是一封测试邮件', 'plain', 'utf-8')
msg['From'] = from_addr
msg['To'] = to_addr
msg['Subject'] = '测试邮件'

try:
    smtp = smtplib.SMTP_SSL('smtp.qq.com', 465)
    smtp.login(from_addr, password)
    smtp.sendmail(from_addr, [to_addr], msg.as_string())
    smtp.quit()
    print("邮件发送成功")
except Exception as e:
    print(f"发送失败：{e}")