import pymysql

try:
    conn = pymysql.connect(
        host="localhost",
        user="root",
        password="123456",   # 改成你的 MySQL 密码
        database="testdb"
    )
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM student")
    for row in cursor.fetchall():
        print(row)
    conn.close()
    print("连接成功")
except Exception as e:
    print("连接失败:", e)