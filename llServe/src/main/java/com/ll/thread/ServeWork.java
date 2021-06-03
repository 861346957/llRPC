package com.ll.thread;

import com.ll.Utils.ExpireCacheUtils;
import com.ll.Utils.PollingUtils;
import com.ll.entity.BeanInfo;
import com.ll.entity.Count;
import com.ll.entity.TaskQueue;
import com.ll.serve.ServeResultContext;
import com.ll.utils.TaskExe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/2 10:19
 */
public class ServeWork extends Thread {
    private static Count count = new Count();
    private Thread thread;
    private ThreadFactory threadFactory;
    private static Logger logger = LoggerFactory.getLogger(ServeWork.class);
    private static Random random=new Random();
    private static Integer interval=5;
    public ServeWork(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        thread = initThread();
    }

    @Override
    public void run() {
        try {
            logger.info("thread is start");
            for (; ; ) {
                TaskBean task = getTask();
                if (task == null) {
                    ThreadContext.getInstance().compareAndSet(System.currentTimeMillis());
                    if (ThreadContext.getInstance().isOverTime() && ThreadContext.getInstance().isClose()) {
                        break;
                    }
                    Thread.sleep(random.nextInt(getRandomNumber()));
                    continue;
                }
                ThreadContext.getInstance().setFreeTime(0);
                TaskExe.executeMethod(task.getBeanInfo(), task.getKey());
            }

        } catch (InterruptedException e) {
            logger.info("thread is interrupt");
        }

    }
    private Integer getRandomNumber(){
        return ExpireCacheUtils.getData("getRandomNumber", new ExpireCacheUtils.Load<Integer>() {
            @Override
            public Integer loadData() {
                return ThreadContext.getInstance().getRunThreadSize()*interval;
            }
        },1);
    }
    private Thread initThread() {
        return threadFactory.newThread(this);
    }

    public Thread getThread() {
        return thread;
    }

    private TaskBean getTask() {
        BeanInfo poll = null;
        String key = null;
        List<TaskQueue> taskQueueList = ServeResultContext.getInstance().getTaskQueueList();
        //Map<String, LinkedBlockingQueue<BeanInfo>> resultMap = ServeResultContext.getInstance().getResultMap();
        synchronized (ServeWork.class) {
            for (int i = 0; i < taskQueueList.size(); i++) {
                Integer polling = PollingUtils.getPolling(count, taskQueueList.size(), count);
                TaskQueue taskQueue = taskQueueList.get(polling);
                poll = taskQueue.getQueue().poll();
                if (poll == null) {
                    continue;
                }
                key = taskQueue.getKey();
            }
        }
        if (poll == null) {
            return null;
        }
        return new TaskBean(key, poll);


    }

    class TaskBean {
        private String key;
        private BeanInfo beanInfo;

        public String getKey() {
            return key;
        }

        public BeanInfo getBeanInfo() {
            return beanInfo;
        }

        public TaskBean(String key, BeanInfo beanInfo) {
            this.key = key;
            this.beanInfo = beanInfo;
        }
    }
}
