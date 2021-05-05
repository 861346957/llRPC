package com.ll.thread;

import com.ll.Utils.StringCustomUtils;
import com.ll.constant.ClientConstant;
import com.ll.entity.BeanInfo;
import com.ll.entity.ResultInfo;
import com.ll.serve.ChannelContext;
import com.ll.serve.ServeResultContext;
import com.ll.utils.SpringBootBeanUtil;
import com.ll.utils.TaskExe;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 已过时
 * @author liang.liu
 * @date createTime：2021/5/1 16:24
 */
@Deprecated
public class ThreadExecute {
    private static ThreadExecute threadExecute;
    private static Logger logger= LoggerFactory.getLogger(ThreadExecute.class);
    private Set<String> runThreadPool;
    private ThreadExecute() {
        runThreadPool=new HashSet<>();
    }
    public static ThreadExecute getInstance(){
        if(threadExecute==null){
            synchronized (ThreadExecute.class){
                if(threadExecute==null){
                    threadExecute=new ThreadExecute();
                }
            }
        }
        return threadExecute;
    }
    public  void executeAsync(ChannelHandlerContext channelHandlerContext,Integer coreNumber){
        new Thread(new ExecutePool(channelHandlerContext,coreNumber)).start();
    }
    private void execute(ChannelHandlerContext channelHandlerContext,Integer coreNumber){
        try {
            String key = ChannelContext.getKey(channelHandlerContext);
            if(runThreadPool.contains(key)){
                return;
            }
            ThreadPoolExecutor pool = new ThreadPool(coreNumber,key) .getPool();
            LinkedBlockingQueue<BeanInfo> queue = ServeResultContext.getInstance().getQueue(key);
            for (;;){
                BeanInfo beanInfo = queue.poll();
                while(beanInfo==null){
                    sleep(1L);
                    beanInfo = queue.poll();
                }
                pool.execute(new ExecuteTask(beanInfo,key));
            }
        } catch (InterruptedException e) {
           logger.info("thread is interrupt");
        }
    }
    private void sleep(Long time) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1L);
    }
    class ExecuteTask implements Runnable{
        private BeanInfo beanInfo;
        private String key;
        public ExecuteTask(BeanInfo beanInfo,String key ) {
            this.beanInfo = beanInfo;
            this.key=key;
        }

        @Override
        public void run() {
            TaskExe.executeMethod(beanInfo,key);
        }
    }
    class ExecutePool implements Runnable{
        private ChannelHandlerContext channelHandlerContext;
        private Integer coreNumber;

        public ExecutePool(ChannelHandlerContext channelHandlerContext, Integer coreNumber) {
            this.channelHandlerContext = channelHandlerContext;
            this.coreNumber = coreNumber;
        }

        @Override
        public void run() {
            execute(channelHandlerContext,coreNumber);
        }
    }
}
