package com.voi.study.canal.cache_control.task;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.voi.study.canal.cache_control.config.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class SimpleCanalClientTask implements Runnable {

    private CanalConnector connector;

    private RedisTemplate<String, String> redisTemplate;

    public SimpleCanalClientTask(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(),
                11111), "example", "", "");
    }

    @Override
    public void run() {
        int batchSize = 100;
        try {
            connector.connect();
            connector.subscribe(".*");
            connector.rollback();
            while (true) {
                Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    try {
                        //如果没有获取到数据，等待5秒继续
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        break;
                    }
                } else {
                    handleEntry(message.getEntries());
                }
                connector.ack(batchId); // 提交确认
            }

        } finally {
            connector.disconnect();
        }
    }

    private void handleEntry(List<Entry> entries) {
        for (Entry entry : entries) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChange;
            try {
                rowChange = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }

            EventType eventType = rowChange.getEventType();

            String schema = entry.getHeader().getSchemaName();
            String table = entry.getHeader().getTableName();

            //如果获取到了指定表的更新操作，则删除缓存，让下一次查询直接查数据库
            if (Constant.CONTROL_SCHEMA.equals(schema) && Constant.CONTROL_TABLE.equals(table)) {
                if (eventType == EventType.DELETE || eventType == EventType.INSERT) {
                    log.info("receive change in book ,delete cache!");
                    redisTemplate.delete(Constant.REDIS_BOOK_COUNT_KEY);
                }
            }

        }
    }

}


