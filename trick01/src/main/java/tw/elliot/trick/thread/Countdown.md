# Countdown

在設計系統時經常會有遇到下列幾個情形要同時滿足的

1. 多執行緒增加效率。
2. 多工作業必需全部完成後再繼續之後的操作。

以下為假設場景：

例如使用者產生一筆新的訂單，我們必需通知存貨，集貨，財會，客服等系統，讓各系統去各自準備要處理的事。
如果用同一執行緒，一個通知完再通知下一個，假設送出通知的時間要0.5秒，等對方系統回應需要1.5秒，那四個系統總共需要8秒；
但如果多工處理，同時發出4個通知，狀況好的話只需要2秒就能完成。

當然現在可以用MQ來達成，但總要假設一下....


## 初步想法

因為要多工，所以要做的事需要寫成一個Runnable的Task，然後要有一個Watcher，去看所有的task是否執行完。

反過來想，告訴Task，在事情做完後，需要跟某個Watcher回報，這樣Watcher就容易寫的多，不用一直去確認，
但還是需知道有多task需要回報完成。而Task只要知道要跟哪一個Watcher回報，複雜度也增加不多。

簡單實體化一下

```
class Task implements Runnable {
    private Watcher watcher;

    public void run() {
        //do our job....
        watcher.report();
    }
}

class Watcher {
    
    private int taskCount;
    private int finished;

    public void startWatching() {
        while (finished != taskCount) {
            Thread.sleep(1000);
        }
    }

    public void report() {
        finished++;
    }
}
```

Task跟Watcher回報，Watcher開始監測後每秒看看回報時跟預期的數量是不是一致，不一致就等下去。

## Java實踐

在Java裡的實踐，我們可能需要下列幾個角色來共同完成。

1. ThreadPool
2. Task
3. CountDownLatch

### CountDownLatch

擔任Watcher的角色，
初始化時先聲明有多少工作量，每次被呼叫```countDown()```會減少工作量，到了工作量為0時就結束；
await()後的程式碼才會被執行。

```
CountDownLatch latch = new CountDownLatch(size);

latch.countDown();

//CountDownLatch利用await等待所有Task執行完
latch.await();
```


### ThreadPool

有效地利用Thread，
ExecutorService的shutdown()可以讓Pool不再收新的task，只等現存在queue裡的task執行完。

```
ExecutorService pool = Executors.newFixedThreadPool(size);
pool.submit(task);
pool.shutdown();
```

### Task

程式最後要呼叫latch.countDown()回報本身工作已完成。

```
Runnable() {
    @Override
	public void run() {
	        //do job!!!!
		    latch.countDown();
		}
```