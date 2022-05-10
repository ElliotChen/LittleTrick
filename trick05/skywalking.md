# Apache Skywalking

## 說明

相較於Elastic APM，Apache Skywalking 算是比較容易設定的，主要是同時提供了OAP Server與UI的image，所以要建一個試用的環境相當方便，但由於此部份主要開發維護者國籍的關係，在應用上可能會有其他的問題。

## 架構

借用https://twitter.com/ASFSkyWalking/上的說明圖片，

![DtPKREQVYAE1U2r](https://picgo.ap-south-1.linodeobjects.com/20220510/150ced97b11305f087e0466e16fe9df3.jpeg)

Agent透過gRPC與http將log回傳以供分析，skywalking提供了公用的ui介面來查詢相關結果。



## Docker Compose

在docker-compose.yml裡主要需求為幾個部份

### .env

```
ELK_VERSION=8.2.0
TIME_ZONE=Asia/Taipei
ES_JAVA_OPTS=-Xmx512m -Xms512m
KAFKA_VERSION=3.1.0-amd64
SKW_VERSION=9.0.0

ELASTIC_PASSWORD=changeme
LICENSE=basic
```



### Elasticsearch

```yaml
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:${ELK_VERSION}
    container_name: elasticsearch
    environment:
      - ES_JAVA_OPTS=${ES_JAVA_OPTS}
      - TZ=${TIME_ZONE}
      - cluster.name=docker-cluster
      - node.name=master
      - cluster.initial_master_nodes=master
      - network.host=0.0.0.0
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
      - "9300:9300"
    healthcheck:
      test: nc -z localhost 9200 || exit 1
      interval: 5s
      timeout: 10s
      retries: 100
    volumes:
      - ./data/elasticsearch:/usr/share/elasticsearch/data
    networks:
      - tracing
```



### OAP Server

```yaml
  oap:
    image: apache/skywalking-oap-server:${SKW_VERSION}
    container_name: oap
    depends_on:
      elasticsearch:
        condition: service_healthy
    links:
      - elasticsearch
    ports:
      - "11800:11800"
      - "12800:12800"
    healthcheck:
      test: [ "CMD-SHELL", "/skywalking/bin/swctl ch" ]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 10s
    environment:
      SW_STORAGE: elasticsearch
      SW_STORAGE_ES_CLUSTER_NODES: elasticsearch:9200
      SW_HEALTH_CHECKER: default
      SW_TELEMETRY: prometheus
      JAVA_OPTS: "-Xms1024m -Xmx1024m"
    networks:
      - tracing

```



### UI

```yaml
  ui:
    image: apache/skywalking-ui:${SKW_VERSION}
    container_name: ui
    depends_on:
      oap:
        condition: service_healthy
    links:
      - oap
    ports:
      - "9080:8080"
    environment:
      SW_OAP_ADDRESS: http://oap:12800
    networks:
      - tracing
```



## Agent

下載可用版本的skywalking-agent.jar後，再以javaagent的方式加在執行方式裡

如果用idea，設定畫面如下

![Screen Shot 2022-05-10 at 10.08.25](https://picgo.ap-south-1.linodeobjects.com/20220510/e8d739d2c5b9617ea198a0e5e6b30c93.png)



## 畫面

實際連入ui後可見相關service相關訊息。

![Screen Shot 2022-05-10 at 10.10.21](https://picgo.ap-south-1.linodeobjects.com/20220510/a0f6e084faf4b10c60b41f18f3069848.png)

也可以分析到程式中各段Service裡使用時間。

![Screen Shot 2022-05-10 at 10.12.02](https://picgo.ap-south-1.linodeobjects.com/20220510/1164d04059e1389f6652952ca13a7285.png)



## 比較

與Elastic APM相較，Skywalking在初始設定的複雜度相當的低，基本上沒有複雜的地方，侵入性也低，唯一的缺點是resource消耗頗高，再者解析時間較長，並不是立即性的反應。