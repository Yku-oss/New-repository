import json
import mysql.connector
from kafka import KafkaProducer

conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="你的密码",
    database="testdb"
)
cursor = conn.cursor()
cursor.execute("SELECT * FROM student")

producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

for row in cursor.fetchall():
    data = {"sno": row[0], "sname": row[1], "ssex": row[2], "sage": row[3]}
    producer.send('student-topic', value=data)
    print(f"Sent: {data}")

producer.flush()
cursor.close()
conn.close()