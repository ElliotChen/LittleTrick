# Elastic APM And Skywalking

[toc]

為了在Debug時能再加上proformance的訊息，試了兩種侵入性較低的工具，分別做了點重點筆記，最後也比較兩種的結果

## Elastic APM

### 說明

Elastic 8.0開始推薦使用Fleet Server + Elastic Agent，先前的各種Beats將會逐漸退場，不過目前Beats還有不少功能是Elastic Agent缺乏的，例如Elastic Agent的輸出，目前看來僅有支援Elasticsearch與Logstash，無法送到如Kafka等Message Queue。

### 架構

#### Elastic Agent

Elastic Agent則如同之前的Beats，裝在單機中，可用來讀取Log檔案，或接收TCP, Socket之類的資料流，再傳至Elasticsearch。

#### Fleet

Fleet server is an elastic-agent running in a server mode。

Fleet Sever也是一個Elastic Agent，僅是以Server mode的狀態運行，Fleet的存在是為透過Policy管理分散的Elastic Agent，並不是做為接收Elastic Agent的資料，這是要特別注意的。

下圖為Elastic Agent -> Fleet Sever -> Elasticsearch的架構。

![20210727174241218](https://picgo.ap-south-1.linodeobjects.com/20220503/d303897d965e4760490383e8bd4b942d.png)

在Elastic 8.0版本，要使用APM integrations (Fleet) 的話，一定要將security選項開啟。
![Screen Shot 2022-05-03 at 08.49.09](https://picgo.ap-south-1.linodeobjects.com/20220503/c37e9372c7284c46423651299d6ff689.png)



[Environment config](https://www.elastic.co/guide/en/fleet/current/agent-environment-variables.html)

### .Env

利用`.env`來指定共用的參數是個好習慣。

```
ELK_VERSION=8.2.0
TIME_ZONE=Asia/Taipei
ES_JAVA_OPTS=-Xmx512m -Xms512m
KAFKA_VERSION=3.1.0-amd64
SKW_VERSION=9.0.0

ELASTIC_PASSWORD=changeme
LICENSE=basic
```

在此指定了elastic的版本與root account: elastic的密碼

### Elasticsearch

docker-compose.yml

```yaml
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:${ELK_VERSION}
    container_name: elasticsearch
    environment:
      - ES_JAVA_OPTS=${ES_JAVA_OPTS}
      - TZ=${TIME_ZONE}
      - ELASTIC_PASSWORD=${ELASTIC_PASSWORD:-}
      - cluster.name=docker-cluster
      - discovery.type=single-node
      - network.host=0.0.0.0
      - http.host=0.0.0.0
      - xpack.security.enabled=true
      - xpack.security.authc.api_key.enabled=true
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

其中的

```
      - xpack.security.enabled=true
      - xpack.security.authc.api_key.enabled=true
```

一定要開，之後的Fleet Server才能正常接入，但這也會造成要使用kibana時需要登入。

### Kibana

docker-compose.yml

```yaml
  kibana:
    image: docker.elastic.co/kibana/kibana:${ELK_VERSION}
    container_name: kibana
    environment:
      - ES_JAVA_OPTS=${ES_JAVA_OPTS}
      - TZ=${TIME_ZONE}
    ports:
      - "5601:5601"
    volumes:
      - ./config/kibana/kibana.yml:/usr/share/kibana/config/kibana.yml
      - ./data/kibana:/usr/share/kibana/data
    healthcheck:
      test: ["CMD-SHELL", "curl -u elastic:changeme -s http://localhost:5601/api/status"]
      interval: 5s
      timeout: 10s
      retries: 120
    networks:
      - tracing
    depends_on:
      elasticsearch:
        condition: service_healthy
```



kibana.yml

```yaml
server.name: kibana
server.host: 0.0.0.0
elasticsearch.hosts: [ "http://elasticsearch:9200" ]
monitoring.ui.container.elasticsearch.enabled: true

## X-Pack security credentials
#
elasticsearch.username: kibana_system
elasticsearch.password: changeme
```

在此預先將`kibana_system`的帳密寫好，稍待下一部份Security設定後即可使用

### Security

#### Set User Password

在`docker-compose.yaml`中已透過`- ELASTIC_PASSWORD=${ELASTIC_PASSWORD:changeme}`輸入elasticsearch的root 使用者`elastic`的密碼為`changeme`，所以之後的curl操作皆可使用`elastic:changeme`做為驗証的帳密資料。

若未完成下列的操作，會連Kibana的登入畫面都無法顯示，所以在container啟動後第一步要做的就是將kibana預設的帳號啟用。

##### Get name of build-in users

```bash
curl http://elastic:changeme@localhost:9200/_security/user
```

可以收到如下的結果，可知預設的user有`apm_system` `beats_system` `elastic` `kibana` `kibana_system` `logstash_system` `remote_monitoring_user`

```json
{
    "apm_system": {
        ...
        "username": "apm_system"
    },
    "beats_system": {
        ...
        "username": "beats_system"
    },
    "elastic": {
        ...
        "username": "elastic"
    },
    "kibana": {
        ...
        "username": "kibana"
    },
    "kibana_system": {
       ....
        "username": "kibana_system"
    },
    "logstash_system": {
        ....
        "username": "logstash_system"
    },
    "remote_monitoring_user": {
        ....
        "username": "remote_monitoring_user"
    }
}
```

##### Update password of build-in users

[Changes the passwords of users in the native realm and built-in users.](https://www.elastic.co/guide/en/elasticsearch/reference/current/security-api-change-password.html)

```bash
curl -X POST -H "Content-Type: application/json" -d '{"password" : "changeme"}' http://elastic:changeme@localhost:9200/_security/user/kibana/_password 

curl -X POST -H "Content-Type: application/json" -d '{"password" : "changeme"}' http://elastic:changeme@localhost:9200/_security/user/kibana_system/_password 
```

用以上的範例，將`kibana`與 `kibana_system`的密碼設定為`changeme`

此時再用browser連到`http://localhost:5601`時就可看到要求輸入帳密的畫面，如下圖所示

![Screen Shot 2022-05-09 at 16.22.24](https://picgo.ap-south-1.linodeobjects.com/2022059/354332a402e39b92cac7d5970a37f9ea.png)

### Fleet Server

#### docker-compose.yml

```yaml
  fleet-server:
    image: docker.elastic.co/beats/elastic-agent:${ELK_VERSION}
    container_name: fleet-server
    user: root
    environment:
      - FLEET_SERVER_ENABLE=1
      - FLEET_SERVER_ELASTICSEARCH_HOST=http://elasticsearch:9200
      - FLEET_SERVER_SERVICE_TOKEN= #must edit
      - FLEET_SERVER_POLICY_ID=fleet-server-policy
      - FLEET_SERVER_INSECURE_HTTP=true
    ports:
      - "8220:8220"
    networks:
      - tracing
    healthcheck:
      test: [ "CMD-SHELL", "curl -f http://127.0.0.1:8220/api/status | grep HEALTHY 2>&1 >/dev/null" ]
      interval: 5s
      timeout: 10s
      retries: 120
    depends_on:
      kibana:
        condition: service_healthy
```

這部份的`FLEET_SERVER_SERVICE_TOKEN`設定在稍後操作會產生，再回頭補上。

#### kibana

登入Kibana後可以在menu找到Fleet的選項

<img src="https://picgo.ap-south-1.linodeobjects.com/2022059/4b76b7916e367f7955c0b61674980952.png" alt="Screen Shot 2022-05-09 at 16.43.46" style="zoom:50%;" />



按下後就開啟了一連串的設定

#### 1. Create policy

第一步是要建立Policy，按下畫面中的`Create policy`按鈕，這個操作需要等待一段時間。

![Screen Shot 2022-05-09 at 16.45.44](https://picgo.ap-south-1.linodeobjects.com/2022059/c55832009fdf94b8ea5cd6b7f60c613b.png)

完成後會提示已成功建立一個Policy

![Screen Shot 2022-05-09 at 16.48.18](https://picgo.ap-south-1.linodeobjects.com/2022059/2d8b8aad7595f8c1a0258455c409e4df.png)

#### 2. Create Fleet Sevrver Instance

這一步可以略過，主要是在docker-compose.yml裡已經產生了一個fleet server 的container等在那了。

![Screen Shot 2022-05-09 at 16.49.12](https://picgo.ap-south-1.linodeobjects.com/2022059/fe758d98d97e051da9f1fda9850b9705.png)



#### 3. Choose a deployment mode

在此使用簡易模式也就是不走https的方式連線

![Screen Shot 2022-05-09 at 16.54.12](https://picgo.ap-south-1.linodeobjects.com/2022059/094316704695b1287e3f56bb925397de.png)

#### 4. Add Fleet Server host

這個部份需要讓稍後新增的agent能連到fleet server，所以相關值要考慮在container裡的network。在這是用http://fleet-server:8220

![Screen Shot 2022-05-09 at 16.56.03 ](https://picgo.ap-south-1.linodeobjects.com/2022059/6d83e751653c4deb3f1dc4878507c7ca.png)

此步驟可先至Settings裡設定，將Fleet server hosts 與Outpus的server加入

![Screen Shot 2022-05-10 at 08.37.43](https://picgo.ap-south-1.linodeobjects.com/20220510/fb2d0b65da8426092ee32483c9dbd862.png)

#### 5. Generate a service token

![Screen Shot 2022-05-10 at 00.54.51](https://picgo.ap-south-1.linodeobjects.com/20220510/b5f247eff873e99d7121893c40b6e6eb.png)

將此token貼回docker-compose.yml裡的`FLEET_SERVER_SERVICE_TOKEN`中

再重啟整個docker-compose即可。

登入kibana再去檢視fleet應該可以看到下列的畫面，已經有一個可用的Fleet Server連上了。

![Screen Shot 2022-05-10 at 01.19.54](https://picgo.ap-south-1.linodeobjects.com/20220510/78d43fd17697f6eb64b323b635e07c6c.png)

### APM Agent

再來就是加入一個APM Agent，用以接受稍後的APM Log，並傳送到Elasticsearch。

#### docker-compose

```yaml
  agent01:
    image: docker.elastic.co/beats/elastic-agent:${ELK_VERSION}
    container_name: agent01
    user: root
    environment:
      - FLEET_ENROLLMENT_TOKEN= #must edit
      - FLEET_ENROLL=1
      - FLEET_URL=http://fleet-server:8220
      - FLEET_INSECURE=true
    ports:
      - "8200:8200"
    networks:
      - tracing
    depends_on:
      fleet-server:
        condition: service_healthy
```

先將docker-compose內容準備好，稍後填入`FLEET_ENROLLMENT_TOKEN`即可運作

#### 1.  Add Elastic APM

在Menu中選取Integraton
![Screen Shot 2022-05-10 at 01.27.16](https://picgo.ap-south-1.linodeobjects.com/20220510/8de5942e5349c4f25d9324d48e4590ef.png)

再選取Elastic APM
![Screen Shot 2022-05-10 at 01.27.34](https://picgo.ap-south-1.linodeobjects.com/20220510/7bdb54b3b1daf2f8d83246dd708a6b82.png)

#### 2. Config APM Server

Elastic 8前的版本，的確有一個app名為APM Server，但在Elastic 8之後用Elastic Agent來取代，與Fleet Server用的是同樣的image，差別在於Fleet Server是執行為Server模式，而APM Server則僅單單做為Agent使用，這部份不要混淆。

按下APM Integration按鈕。

![Screen Shot 2022-05-10 at 01.34.48](https://picgo.ap-south-1.linodeobjects.com/20220510/38dc045bead0ec5da7c6b6223d54d894.png)

再按下Add Elastic APM

![Screen Shot 2022-05-10 at 01.37.05](https://picgo.ap-south-1.linodeobjects.com/20220510/e9b8a1f91589beb2c8565fb20a44e3cd.png)



由於我使用了docker container的方式，務必要修改Host裡的值為`0.0.0.0:8200`，不然會收不到container外的資料

![Screen Shot 2022-05-10 at 09.00.03](https://picgo.ap-south-1.linodeobjects.com/20220510/ec67f424debf6e4c64094d41dc203a81.png)



儲存按鈕在畫面的右下角，不要找不到了

![Screen Shot 2022-05-10 at 01.41.33](https://picgo.ap-south-1.linodeobjects.com/20220510/53d18453ca7ff3237bb2f27b8efa7850.png)

再回去檢視APM的新增畫面，應該可以看到各項都準備完成。

![Screen Shot 2022-05-10 at 01.44.05](https://picgo.ap-south-1.linodeobjects.com/20220510/eb03190ef9a2fb7797e2019f4c1aadc9.png)



#### 3. Enrollment Token

再來要到Fleet的畫面取得Enrollment Token

![Screen Shot 2022-05-10 at 01.48.04](https://picgo.ap-south-1.linodeobjects.com/20220510/a19ea8e03df6902edb32ca8e0b061dc9.png)

將此token填回`FLEET_ENROLLMENT_TOKEN`中，重起container即可。再回到Fleet畫面就可以看到多了一個Agent

![Screen Shot 2022-05-10 at 02.01.00](https://picgo.ap-south-1.linodeobjects.com/20220510/68eef2c3c6eded453c19d31957954b6d.png)

### Java Agent Client

再來就是在我們要監控的程式裡加入javaagent的設定。

可以參考Kibana中的說明，先下載對應版本的jar，再加上參數。
![Screen Shot 2022-05-10 at 02.05.43](https://picgo.ap-south-1.linodeobjects.com/20220510/8df80ef2bb88eac192cb7bf2d6abee14.png)



若是使用idea的話，在Run/Debug Configuration裡設定即可，例如下列這樣。

![Screen Shot 2022-05-10 at 02.07.41](https://picgo.ap-south-1.linodeobjects.com/20220510/e2e760e037efff8df9395c36e6893443.png)



再執行程式，回到kibana裡就能看到相關的APM資訊

例如下圖，可以分析程式在各個Service裡所使用的時間，以便找出可能的問題。

![Screen Shot 2022-05-10 at 08.53.50](https://picgo.ap-south-1.linodeobjects.com/20220510/c663f6cfdee65885e77c60f512e9477d.png)

### Step



```
> docker ps
CONTAINER ID   IMAGE                                                 COMMAND                  CREATED         STATUS                            PORTS                                            NAMES
a4e2da8dd2c8   docker.elastic.co/kibana/kibana:8.1.3                 "/bin/tini -- /usr/l…"   4 minutes ago   Up 3 minutes (health: starting)   0.0.0.0:5601->5601/tcp                           kibana
e799a6e91bd6   docker.elastic.co/elasticsearch/elasticsearch:8.1.3   "/bin/tini -- /usr/l…"   4 minutes ago   Up 4 minutes (healthy)            0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp   elasticsearch
```



```
docker exec -u root -it e799a6e91bd6 /bin/bash
root@4b3620a383c0:/usr/share/elasticsearch# ./bin/elasticsearch-setup-passwords interactive
******************************************************************************
Note: The 'elasticsearch-setup-passwords' tool has been deprecated. This       command will be removed in a future release.
******************************************************************************

Initiating the setup of passwords for reserved users elastic,apm_system,kibana,kibana_system,logstash_system,beats_system,remote_monitoring_user.
You will be prompted to enter passwords as the process progresses.
Please confirm that you would like to continue [y/N]y


Enter password for [elastic]:
Reenter password for [elastic]:
Enter password for [apm_system]:
Reenter password for [apm_system]:
Enter password for [kibana_system]:
Reenter password for [kibana_system]:
Enter password for [logstash_system]:
Reenter password for [logstash_system]:
Enter password for [beats_system]:
Reenter password for [beats_system]:
Enter password for [remote_monitoring_user]:
Reenter password for [remote_monitoring_user]:
Changed password for user [apm_system]
Changed password for user [kibana_system]
Changed password for user [kibana]
Changed password for user [logstash_system]
Changed password for user [beats_system]
Changed password for user [remote_monitoring_user]
Changed password for user [elastic]
```



```
> docker exec -u root -it a4e2da8dd2c8 /bin/bash

> ./bin/kibana-encryption-keys generate
## Kibana Encryption Key Generation Utility

The 'generate' command guides you through the process of setting encryption keys for:

xpack.encryptedSavedObjects.encryptionKey
    Used to encrypt stored objects such as dashboards and visualizations
    https://www.elastic.co/guide/en/kibana/current/xpack-security-secure-saved-objects.html#xpack-security-secure-saved-objects

xpack.reporting.encryptionKey
    Used to encrypt saved reports
    https://www.elastic.co/guide/en/kibana/current/reporting-settings-kb.html#general-reporting-settings

xpack.security.encryptionKey
    Used to encrypt session information
    https://www.elastic.co/guide/en/kibana/current/security-settings-kb.html#security-session-and-cookie-settings


Already defined settings are ignored and can be regenerated using the --force flag.  Check the documentation links for instructions on how to rotate encryption keys.
Definitions should be set in the kibana.yml used configure Kibana.

Settings:
xpack.encryptedSavedObjects.encryptionKey: 99caf1848a8fac9e9061adeac2b4f199
xpack.reporting.encryptionKey: 72f386ba60c8ba4154d40ad4b91495ca
xpack.security.encryptionKey: ea22c0fc5d7bd506399c1b75f13adca8
```

![Screen Shot 2022-05-03 at 15.06.40](https://picgo.ap-south-1.linodeobjects.com/20220503/d51b4222322721917c5ac2c8ba06572b.png)