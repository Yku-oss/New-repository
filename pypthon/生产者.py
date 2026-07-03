import json
import pymysql
from kafka import KafkaProducer

# 连接 MySQL
conn = pymysql.connect(
    host="localhost",
    user="root",
    password="123456",   # 改成你自己的密码
    database="testdb"
)
cursor = conn.cursor()
cursor.execute("SELECT * FROM student")

# 连接 Kafka（如果 Kafka 在虚拟机，把 localhost 换成虚拟机 IP）
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# 发送每行数据
for row in cursor.fetchall():
    data = {
        "sno": row[0],
        "sname": row[1],
        "ssex": row[2],
        "sage": row[3]
    }
    producer.send('student-topic', value=data)
    print(f"Sent: {data}")

producer.flush()
cursor.close()
conn.close()