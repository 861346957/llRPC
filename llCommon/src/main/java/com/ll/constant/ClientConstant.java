package com.ll.constant;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 14:49
 */
public interface ClientConstant {
    String DEFAULT_HOST_SEPARATOR=",";
    String DEFAULT_PORT_SEPARATOR=":";
    String DEFAULT_PROJECT_SEPARATOR="-";
    String DEFAULT_SEPARATOR=";";
    String DEFAULT_CLASS_SEPARATOR=".";
    String DEFAULT_PROJECT_NAME="project";
    String DEFAULT_ROUTE_CLASS="com.ll.route.PollingRoute";
    String SYNC_STATUS ="sync";
    String ASYNC_STATUS="async";
    String SUCCESS="success";
    String FAIL="fail";
    Integer QUEUE_THREAD_CORE_NUMBER=1;
    Integer READ_WRITE_OVERTIME_COUNT=3;
    Integer CORE_SIZE=Runtime.getRuntime().availableProcessors();
    Integer MAX_CORE_SIZE=CORE_SIZE*2-1;
    Integer DEFAULT_PORT=7272;
    Integer CHANNEL_OPEN=1;
    Integer CHANNEL_CLOSE=2;
    Integer DEFAULT_THRESHOLD=100;
    Long DEFAULT_THREAD_EXPIRE_TIME=10L;
    Integer DEFAULT_READ_WRITE_TIME=60;
    Integer IS_CONFIRM =1;
    Integer NO_CONFIRM=0;
    Integer CONFIRM_OVER_TIME=90;
    Long WAIT_ACK_TIME=15*1000L;
    Long RETRY_MESSAGE_TIME=3000L;
    Long METHOD_WAIT_TIME=1000L*60*60*6;
    //Long METHOD_WAIT_TIME=1000L*5;
}
