package com.demo.test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import cn.hutool.core.date.DateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JucTest {

    static int n = 1_000;

    public static void semaphoreTest() {
        Semaphore semaphore = new Semaphore(3);

        for (int i = 0; i < 10; i++) {
            try {
                semaphore.acquire();
                new Thread(() -> {
                    log.debug("{} START", Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.debug("{} END", Thread.currentThread().getName());
                    semaphore.release();
                }).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    public static void main(String[] args) {
        // semaphoreTest();
        // stampedLockTest();
        // percentageTest();
        // cyclicBarrierTest();
        // transferTest();
        // wait1();
        park2();
        // reentrantLockTest();
    }

    public static void stampedLockTest() {
        StampedLock stampedLock = new StampedLock();
        new Thread(() -> stampedRead(stampedLock), "read").start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> stampedWrite(stampedLock), "write").start();
    }

    public static void countDownLauchTest() {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        String[] arr = new String[10];
        Random rd = new Random();
        ExecutorService pool = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            int k = i;
            pool.execute(() -> {
                for (int j = 1; j <= 100; j++) {
                    arr[k] = j + "%";

                    try {
                        Thread.sleep(rd.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.print("\r" + Arrays.toString(arr));
                }
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
            System.out.println();
            log.debug("Go, count -> {}", countDownLatch.getCount());
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void percentageTest() {
        ArrayList<Callable<String>> list = new ArrayList<>();
        String[] arr = new String[10];
        Random rd = new Random();
        ExecutorService pool = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 10; i++) {
            int k = i;
            list.add(() -> {
                for (int j = 1; j <= 100; j++) {
                    arr[k] = j + "%";

                    try {
                        Thread.sleep(rd.nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.print("\r" + Arrays.toString(arr));
                }
                return null;
            });
        }

        try {
            List<Future<String>> invokeAll = pool.invokeAll(list);

            invokeAll.forEach(o -> {
                try {
                    o.get(1, TimeUnit.SECONDS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            pool.shutdown();
            System.out.println();
            log.debug("GO");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void stampedWrite(StampedLock stampedLock) {
        long ts = stampedLock.tryWriteLock();

        log.debug("ts -> {}, n -> {}", ts, n);

        stampedLock.unlockWrite(ts);
    }

    public static void stampedRead(StampedLock stampedLock) {
        long ts = stampedLock.tryOptimisticRead();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (stampedLock.validate(ts)) {
            log.debug("ts -> {}, n -> {}", ts, n);
            return;
        }

        log.debug("upgrade {}", ts);
        ts = stampedLock.tryReadLock();
        stampedLock.unlockRead(ts);
        log.debug("ts -> {}, n -> {}", ts, n = 9);
    }

    public static void cyclicBarrierTest() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CyclicBarrier c = new CyclicBarrier(2, () -> {
            log.debug("OK");
            pool.shutdown();
        });

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int k = j + i + 1;

                pool.submit(() -> {
                    try {
                        Thread.sleep(k * 1000);
                        log.debug("wait -> {}", c.getNumberWaiting());
                        c.await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public static void transferTest() {
        Transfer<String> transfer = new Transfer<>();

        new Thread(() -> transfer.get(1000)).start();

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(() -> transfer.complete("ok")).start();
    }

    public static void wait1() {
        Object object = new Object();
        HashMap<String, Integer> map = new HashMap<>();
        String key = "0";

        new Thread(() -> {
            synchronized (object) {
                while (Objects.isNull(map.get(key))) {
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(1);
            }
        }).start();

        new Thread(() -> {
            synchronized (object) {
                map.putIfAbsent(key, 1);
                System.out.println(2);
                object.notify();
            }
        }).start();
    }

    public static void park() {
        Thread t1 = new Thread(() -> {
            LockSupport.park();
            System.out.println(1);
        });

        Thread t2 = new Thread(() -> {
            System.out.println(2);
            LockSupport.unpark(t1);
        });

        t1.start();
        t2.start();

    }

    public static void park2() {
        HashMap<String, Thread> map = new HashMap<>();
        Park park = new Park(2);
        String[][] arr = { { "t1", "t2" }, { "t2", "t3" }, { "t3", "t1" } };

        Arrays.asList(arr).forEach(o -> {
            Thread t = park.print(() -> map.get(o[1]));
            map.putIfAbsent(o[0], t);
            t.start();
        });

        LockSupport.unpark(map.get(arr[0][0]));
    }

    public static void reentrantLockTest() {
        RetrantTest reentrantLock = new RetrantTest(2);
        Condition cond1 = reentrantLock.newCondition();
        Condition cond2 = reentrantLock.newCondition();
        Condition cond3 = reentrantLock.newCondition();

        reentrantLock.print(cond1, cond2);
        reentrantLock.print(cond2, cond3);
        reentrantLock.print(cond3, cond1);

        reentrantLock.lock();
        cond1.signal();
        reentrantLock.unlock();
    }

    @Test
    public void mapComputeTest() {
        HashMap<Object, Object> map = new HashMap<>();
        String key = "map";

        map.computeIfAbsent(key, k -> 99);
        log.debug("{}", map);
        map.compute(key, (k, v) -> Objects.isNull(v) ? 0 : (int) v - 10);
        log.debug("{}", map);
        map.computeIfPresent(key, (k, v) -> (int) v + 3);
        log.debug("{}", map);
    }

    @Test
    public void time() {
        System.out.println(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8)));
        System.out.println(new DateTime(LocalDateTime.now().toInstant(ZoneOffset.ofHours(8))));
        System.out.println(new Date(LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8))));
        System.out.println(LocalDateTime.now());
    }
}

@Slf4j
class Transfer<R> {
    R response;

    public R get() {
        return get(0);
    }

    public synchronized R get(long timeout) {
        long startTs = System.currentTimeMillis();
        long passTs = 0;

        log.debug("start");

        while (Objects.isNull(response)) {
            try {
                if (timeout == 0) {
                    wait();
                } else {
                    long delayTs = timeout - passTs;

                    if (delayTs <= 0) {
                        break;
                    }

                    wait(delayTs);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            passTs = System.currentTimeMillis() - startTs;
        }

        log.debug("response -> {}", response);
        return response;
    }

    public synchronized void complete(R response) {
        this.response = response;
        notifyAll();
    }

}

class Park {
    int count;

    public Park(int count) {
        this.count = count;
    }

    public Thread print(Supplier<Thread> method) {
        Thread t = new Thread(() -> {
            for (int i = 0; i < count; i++) {
                LockSupport.park();
                System.out.println(Thread.currentThread().getName());
                LockSupport.unpark(method.get());
            }
        });
        return t;
    }
}

class RetrantTest extends ReentrantLock {
    int count;

    public RetrantTest(int count) {
        this.count = count;
    }

    public void print(Condition currCond, Condition nextCond) {
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                lock();
                try {
                    currCond.await();
                    System.out.println(Thread.currentThread().getName());
                    nextCond.signal();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    unlock();
                }
            }
        }).start();
    }
}
