# Docker & Linux 命令速查表

> 定于 2026-09-26
> 定位：**工具型知识**，不系统学，但要**命令熟**
> 用法：日常忘了来查；面试前扫一遍
> 原则：**只收"日常真会用到的"**，不收偏门命令

---

# 第一部分：Docker 命令

## 一、镜像

```bash
# 列表
docker images                          # 列出所有镜像
docker images -a                       # 含中间层镜像

# 拉取 / 构建
docker pull mysql:8.0                  # 从仓库拉镜像
docker build -t 名字:版本 .             # 构建镜像（. = 当前目录找 Dockerfile）
docker build -t 名字:版本 -f 路径 .      # 指定 Dockerfile 路径

# 删除
docker rmi 镜像ID/名字:tag              # 删镜像
docker rmi $(docker images -q)         # 删全部镜像（危险）
docker image prune                     # 删无用镜像（dangling）
docker image prune -a                  # 删所有未被容器使用的镜像

# 查看详情
docker inspect 镜像名                   # 查看镜像/容器详细信息（JSON）
docker history 镜像名                   # 查看镜像分层
```

## 二、容器

```bash
# 运行（最常用！参数要记）
docker run -d --name 名字 -p 8081:8081 镜像名
#           │   │          │
#           │   │          └─ 端口映射（宿主机:容器）
#           │   └─ 容器命名
#           └─ 后台运行（detach）

# run 的其他常用参数
docker run -it 镜像名 bash             # -it = 交互式终端（进容器）
docker run -v 宿主路径:容器路径 镜像    # 挂载卷
docker run -e KEY=VALUE 镜像           # 环境变量
docker run --rm 镜像                   # 容器退出后自动删除
docker run --add-host=host.docker.internal:host-gateway 镜像   # 让容器能连宿主机

# 查看
docker ps                              # 运行中的容器
docker ps -a                           # 所有容器（含已停止）
docker ps -a --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"   # 格式化输出
docker inspect 容器名                   # 详细信息

# 生命周期
docker start 容器名                     # 启动已停止的容器
docker stop 容器名                      # 优雅停止（发 SIGTERM）
docker kill 容器名                      # 强制停止（发 SIGKILL）
docker restart 容器名                   # 重启
docker rm 容器名                        # 删除容器（必须先停止）
docker rm -f 容器名                     # 强制删除（运行中也删）

# 进入容器 / 执行命令
docker exec -it 容器名 bash             # 进入容器（最常用）
docker exec 容器名 ls /app              # 在容器里执行一条命令（不进容器）
docker exec -it 容器名 mysql -uroot -p  # 在容器里执行具体程序

# 日志（排错必备）
docker logs 容器名                      # 查看日志
docker logs -f 容器名                   # 实时跟踪（follow）
docker logs --tail 100 容器名           # 最后 100 行
docker logs --since 10m 容器名          # 最近 10 分钟

# 文件拷贝
docker cp 容器名:/app/x.log ./x.log     # 从容器拷出
docker cp ./x.log 容器名:/app/          # 拷进容器

# 资源查看
docker stats                           # 实时 CPU/内存/网络
docker top 容器名                       # 容器内进程
```

## 三、Docker Compose

```bash
cd 有docker-compose.yml的目录

docker compose up -d                   # 启动全部（-d 后台）
docker compose up -d --build           # 启动前重新构建镜像
docker compose ps                      # 查看本项目的容器
docker compose logs -f                 # 查看全部日志（-f 实时）
docker compose logs -f 服务名           # 看某个服务日志
docker compose restart 服务名           # 重启某个服务
docker compose stop                    # 停止（不删容器）
docker compose start                   # 启动（不重建）
docker compose down                    # 停止 + 删除容器和网络（保留数据卷）
docker compose down -v                 # ⚠️ 连数据卷一起删（数据全没！）
docker compose config                  # 校验并查看最终配置（排错用）
docker compose pull                    # 拉取所有服务的最新镜像
docker compose exec 服务名 bash         # 进入某个服务容器
```

## 四、系统 / 清理

```bash
docker version                         # 客户端 + 服务端版本
docker info                            # 系统信息（含 Registry Mirrors）
docker system df                       # 磁盘占用（镜像/容器/卷各占多少）
docker system prune                    # 清理无用资源（不含卷）
docker system prune -a --volumes       # ⚠️ 彻底清理（含卷，危险）

# 数据卷
docker volume ls                       # 列出卷
docker volume inspect 卷名              # 查看卷详情
docker volume rm 卷名                   # 删除卷
docker network ls                      # 列出网络
```

---

## 五、Docker 命令速记口诀

| 场景 | 命令 |
|---|---|
| **看有什么** | `docker ps -a` / `docker images` |
| **起容器** | `docker run -d --name X -p 宿主:容器 镜像` |
| **进容器** | `docker exec -it X bash` |
| **看日志** | `docker logs -f X` |
| **一键起环境** | `docker compose up -d` |
| **关掉全部** | `docker compose down` |
| **清垃圾** | `docker system prune` |

**三个高频参数**：
- **`-d`** = 后台
- **`-p 宿主:容器`** = 端口映射
- **`-v 宿主:容器`** = 挂载

---

# 第二部分：Linux 命令

> 场景：服务器排查、看日志、看资源、部署
> **重点**：`grep / tail / ps / top / df / du / chmod / netstat` 这几个必须熟

## 一、文件与目录

```bash
ls -l          # 详细列表（权限/大小/时间）
ls -la         # 含隐藏文件
ls -lh         # 大小人性化显示（KB/MB）
cd /path       # 切换目录
cd ..          # 上级目录
cd ~           # 家目录
pwd            # 当前路径
mkdir -p a/b/c # 递归创建目录
cp -r src dst  # 递归复制
mv src dst     # 移动 / 重命名
rm file        # 删文件
rm -rf dir     # ⚠️ 递归强制删除（危险！）
touch file     # 创建空文件
cat file       # 查看文件全部内容
less file      # 分页查看（q 退出）
head -n 20 f   # 前 20 行
tail -n 20 f   # 后 20 行
```

## 二、查找与过滤（⭐ 服务器排错必用）

```bash
# 搜索文件
find /path -name "*.log"               # 按名字找
find /path -name "*.log" -mtime -7     # 7 天内修改的
find /path -type d -name "logs"        # 只找目录

# 搜索内容（最常用）
grep "关键字" file.log                  # 在文件里搜
grep -i "error" file.log               # 忽略大小写
grep -r "关键字" /path                 # 递归搜目录
grep -n "关键字" file.log              # 显示行号
grep -v "关键字" file.log              # 反选（不含关键字的行）
grep -c "关键字" file.log              # 统计匹配行数
grep -A 3 -B 2 "Exception" file.log    # 显示匹配行的前后 3/2 行

# 管道组合（威力最大）
grep "ERROR" app.log | tail -20                    # 先搜后取后 20 行
ps -ef | grep java                                 # 找 java 进程
netstat -anp | grep 8081                           # 找占用 8081 的进程
cat app.log | awk '{print $1}' | sort | uniq -c    # 统计第一列出现次数
```

## 三、日志查看（⭐ 看日志是日常）

```bash
tail -f app.log                        # 实时跟踪（最常用！）
tail -f app.log | grep ERROR           # 实时跟踪 + 只看 ERROR
tail -100f app.log                     # 实时跟踪，从最后 100 行开始
tail -n 500 app.log                    # 最后 500 行
head -n 100 app.log                    # 前 100 行
wc -l app.log                          # 统计行数
```

## 四、进程与资源（⭐ 排查性能问题）

```bash
ps -ef                                 # 所有进程（全格式）
ps -ef | grep java                     # 找 java 进程
ps aux                                 # 另一种格式（含 CPU/内存）
top                                    # 实时资源监控（q 退出）
top -p PID                             # 只看某进程
kill PID                               # 优雅结束进程
kill -9 PID                            # ⚠️ 强制杀死
pkill java                             # 按名字杀进程
jobs / bg / fg                         # 后台任务管理
```

## 五、磁盘与内存

```bash
df -h                                  # 磁盘使用（-h 人性化）
du -sh /path                           # 目录总大小
du -sh /path/* | sort -rh | head -10   # 找最大的 10 个（排错神器）
free -h                                # 内存使用
free -m                                # 以 MB 显示
```

## 六、网络

```bash
ip addr / ifconfig                     # 查看网卡 IP
ping host                              # 测试连通
curl -X POST http://host:port/api      # 发 HTTP 请求
curl -i http://host                    # 含响应头
curl -s http://host                    # 静默（只要 body，脚本常用）
wget url                               # 下载文件
netstat -anp | grep 8081               # 查看端口占用（老命令）
ss -tunlp | grep 8081                  # 查看端口占用（新命令，推荐）
lsof -i:8081                           # 查看端口被哪个进程占
telnet host port                       # 测试端口通不通
```

## 七、权限

```bash
chmod 755 file                         # 设置权限（rwxr-xr-x）
chmod +x script.sh                     # 加可执行权限
chown user:group file                  # 改所有者
ls -l                                  # 查看权限
# 权限数字：r=4 w=2 x=1，三组 = 所有者/组/其他人
# 755 = rwxr-xr-x   644 = rw-r--r--
```

## 八、压缩与传输

```bash
tar -czvf x.tar.gz dir/                # 打包压缩（c=创建 z=gzip v=显示 f=文件）
tar -xzvf x.tar.gz                     # 解压
zip -r x.zip dir/                      # zip 压缩
unzip x.zip                            # 解压
scp file user@host:/path               # 远程复制（上传）
scp user@host:/path/file .             # 远程复制（下载）
```

## 九、其他常用

```bash
history                                # 历史命令
history | grep docker                  # 搜历史命令
!!                                     # 执行上一条命令
sudo 命令                               # 以 root 执行
su - user                              # 切换用户
whoami                                 # 当前用户
date                                   # 当前时间
pwd                                    # 当前目录
echo $PATH                             # 查看环境变量
export VAR=value                       # 设置环境变量
env                                    # 列出所有环境变量
nohup java -jar app.jar &              # 后台运行（关终端不退出）
```

---

## 十、Linux 速记口诀

| 场景 | 命令 |
|---|---|
| **看日志** | `tail -f app.log` + `grep` |
| **找进程** | `ps -ef \| grep xxx` |
| **找端口占用** | `ss -tunlp \| grep 8081` 或 `lsof -i:8081` |
| **看资源** | `top` / `free -h` / `df -h` |
| **找大文件** | `du -sh * \| sort -rh \| head` |
| **杀进程** | `kill -9 PID` |
| **搜内容** | `grep -rn "关键字" ./` |
| **发请求** | `curl -X POST url` |

---

# 第三部分：组合场景（面试/实战常用）

## 场景1：服务器上查"应用为什么挂了"

```bash
# 1. 进程还在吗？
ps -ef | grep java

# 2. 端口通吗？
ss -tunlp | grep 8081

# 3. 看日志找异常
tail -200 app.log | grep -i "error\|exception"

# 4. 磁盘满了吗？（常见原因）
df -h

# 5. 内存够吗？
free -h
```

## 场景2：Docker 里排错

```bash
# 1. 容器活着吗？
docker ps -a

# 2. 看日志
docker logs --tail 200 seckill-app

# 3. 进容器看
docker exec -it seckill-app bash
#   → 里面再用 Linux 命令排查

# 4. 看资源占用
docker stats

# 5. 容器里连不到外部？
docker exec seckill-app ping host.docker.internal
```

## 场景3：本机端口被占用

```bash
# Windows (PowerShell)
netstat -ano | findstr 8081
Stop-Process -Id PID -Force

# Linux
lsof -i:8081
kill -9 PID
```

## 场景4：数据库容器操作

```bash
# 进 MySQL 容器执行 SQL
docker exec -it seckill-mysql mysql -uroot -p123456 seckill -e "SELECT * FROM seckill_goods;"

# 进 Redis 容器
docker exec -it seckill-redis redis-cli
#   → GET seckill:stock:1

# 备份数据库
docker exec seckill-mysql mysqldump -uroot -p123456 seckill > backup.sql

# 恢复
docker exec -i seckill-mysql mysql -uroot -p123456 seckill < backup.sql
```

---

# 第四部分：易混点

| 易混 | 区分 |
|---|---|
| **`docker stop` vs `kill`** | stop 优雅（SIGTERM）、kill 强制（SIGKILL） |
| **`docker rm` vs `rmi`** | rm 删容器、rmi 删镜像 |
| **`docker exec` vs `run`** | exec 进**已运行**的容器、run 创建**新**容器 |
| **`compose down` vs `down -v`** | down 保留数据卷、**down -v 删卷** |
| **`cp` vs `mv`** | cp 复制（原文件在）、mv 移动（原文件没了） |
| **`>` vs `>>`** | `>` 覆盖、`>>` 追加 |
| **`find` vs `grep`** | find 找**文件**、grep 找**文件内容** |
| **`df` vs `du`** | df 看**文件系统**总用量、du 看**目录**占用 |
| **`kill` vs `kill -9`** | 普通 = SIGTERM（可捕获）、-9 = SIGKILL（强杀） |
| **`netstat` vs `ss`** | 功能一样，ss 更快更现代（推荐） |

---

# 第五部分：面试可能问的（Docker 相关命令）

1. **怎么查看容器日志？**
   → `docker logs -f 容器名`（-f 实时跟踪）

2. **怎么进容器排查？**
   → `docker exec -it 容器名 bash`

3. **怎么查看镜像分层？**
   → `docker history 镜像名`

4. **怎么清理 Docker 占的磁盘？**
   → `docker system df` 看占用 → `docker system prune` 清理

5. **`docker stop` 和 `docker kill` 区别？**
   → stop 发 SIGTERM（优雅退出，等应用收尾）；kill 发 SIGKILL（直接杀）

6. **Linux 怎么看端口被哪个进程占用？**
   → `lsof -i:8081` 或 `ss -tunlp | grep 8081`

7. **Linux 怎么找大文件？**
   → `du -sh /* | sort -rh | head -10`

---

# 使用建议

**不要背** —— **用的时候来查**。
**但下面这些必须"肌肉记忆"**（面试/日常高频）：
- `docker ps -a` / `docker logs -f` / `docker exec -it`
- `docker compose up -d` / `down`
- `tail -f` / `grep` / `ps -ef` / `df -h` / `free -h`
- `ss -tunlp | grep 端口`
