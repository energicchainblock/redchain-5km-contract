package com.utsoft.chain.km5.task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.utsoft.chain.km5.pojo.ConventionChaincode;
/**
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
@Component
public class ExecuteTask implements Runnable {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	 @Value("${aliyun.accountendpoint}")
	 private String accountendpoint;
	 
	 @Value("${aliyun.accesskey}")
	 private String accessKey;
	
	 @Value("${aliyun.accessId}")
	 private String accessId;
	 
	 @Autowired
	 private SubmitChainService submitChainService; 
	 
	 private boolean isRunning = true;
	
	private  CloudQueue queue;
	
	private CloudAccount accountProxyClient;
	
	private   MNSClient client;
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	 
	@PostConstruct
	public void initial() {
		
		accountProxyClient = new CloudAccount(accessId,accessKey,accountendpoint);
        client = accountProxyClient.getMNSClient(); 
        queue = client.getQueueRef("TestQueue");
        executor.submit(this);
	}
	
	
	@Scheduled(cron="0/30 1/5 *  * * ? ")   
	public void periodCheckStatus() {
		if (client!=null && !client.isOpen()) {
			  client = accountProxyClient.getMNSClient(); 
		      queue = client.getQueueRef("TestQueue");
		}
	}
	
	@PreDestroy
	public void stop() {
	    if (client!=null)  {
			 client.close();
		 }
	    this.isRunning = false;
	}


	@Override
	public void run() {
		while(isRunning) {
			consume();
		}
	}
	
	public void consume() {
		try {
            Message popMsg = queue.popMessage();
            if (popMsg != null) {
            	/**
            	 * 先记录日志
            	 */
            	Object[] args = {popMsg.getReceiptHandle(),popMsg.getMessageBodyAsString(),popMsg.getMessageId(),popMsg.getDequeueCount()};
                logger.info("msghandler-{} body-{} id-{},dequeue count-{}",args);
                
                 ConventionChaincode conventionChaincode = JSON.parseObject(popMsg.getMessageBodyAsString(),ConventionChaincode.class);
                if (submitChainService.handlerChainLogic(popMsg.getMessageId(),conventionChaincode))  {
                	 queue.deleteMessage(popMsg.getReceiptHandle());
                }
             }
             else{
            	 TimeUnit.MILLISECONDS.sleep(1000);
            }
         } catch (ClientException ce) {
        	
        	logger.error("Something wrong with the network connection between client and MNS service."+ "Please check your network and DNS availablity.",ce);
          
         } catch (ServiceException se) {
        	
        	Object[] errors = { se.getRequestId(),se.getErrorCode(),se};
            logger.error("MNS exception requestId: reqId-{} code-{} e-{}",errors);
            
         } catch (Exception e) {
        	  logger.error("Unknown exception happened!",e);
        }
	}
}
