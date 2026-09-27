"""秒杀并发压测脚本（演示超卖用）
用法：python seckill/test_concurrent.py [接口路径] [并发数]
  例：python seckill/test_concurrent.py bug 50     -> 打 /api/seckill/bug（先查后改，预期超卖）
      python seckill/test_concurrent.py do 50      -> 打 /api/seckill/do（正确版，预期不超卖）
"""
import sys
import requests
from concurrent.futures import ThreadPoolExecutor

BASE = "http://localhost:8081/api/seckill"
endpoint = sys.argv[1] if len(sys.argv) > 1 else "bug"
concurrency = int(sys.argv[2]) if len(sys.argv) > 2 else 50


def hit(i):
    try:
        r = requests.post(f"{BASE}/{endpoint}", params={"userId": i, "goodsId": 1}, timeout=10)
        body = r.json()
        return body.get("code"), body.get("message")
    except Exception as e:
        return 0, str(e)


with ThreadPoolExecutor(max_workers=concurrency) as ex:
    results = list(ex.map(hit, range(1, concurrency + 1)))

ok = sum(1 for c, _ in results if c == 200)
failed = sum(1 for c, _ in results if c != 200)
print(f"接口: /{endpoint}  并发: {concurrency}")
print(f"成功(200): {ok}   失败(非200): {failed}")
# 打印前几个非 200 的原因
shown = 0
for c, m in results:
    if c != 200 and shown < 3:
        print(f"  -> code={c} msg={m}")
        shown += 1
