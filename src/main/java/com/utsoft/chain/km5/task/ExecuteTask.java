package com.utsoft.chain.km5.task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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
@DependsOn(value={"submitChainService"})
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
	private ConventionChaincode conventionChaincode = new ConventionChaincode();
	private static final String  queueName="Q-5KM-BLOCK-CHAIN";

	@PostConstruct
	public void initial() {
		
		accountProxyClient = new CloudAccount(accessId,accessKey,accountendpoint);
        client = accountProxyClient.getMNSClient(); 
        queue = client.getQueueRef(queueName);
        executor.submit(this);
	}
	
	
	@Scheduled(cron="0/30 1/5 *  * * ? ")   
	public void periodCheckStatus() {
		if (client!=null && !client.isOpen()) {
			  client = accountProxyClient.getMNSClient(); 
		      queue = client.getQueueRef(queueName);
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
				JSONObject obj = JSONObject.parseObject(popMsg.getMessageBodyAsString());
				//日签
				if(obj.get("queueType").equals("DIARY")){
					conventionChaincode.setSubmitJson(popMsg.getMessageBodyAsString());
					conventionChaincode.setReqId(popMsg.getMessageId());
					conventionChaincode.setFrom(((JSONObject)obj.get("msg")).get("from").toString());
					conventionChaincode.setTo(((JSONObject)obj.get("msg")).get("to").toString());
					conventionChaincode.setCmd("dailyMsg");
				}else if(obj.get("queueType").equals("INTEGRAL")){//积分
					conventionChaincode.setSubmitJson(popMsg.getMessageBodyAsString());
					conventionChaincode.setReqId(popMsg.getMessageId());
					conventionChaincode.setFrom(obj.get("user_id").toString());
					conventionChaincode.setTo(obj.get("order_id").toString());
					conventionChaincode.setCmd("integral");
				}
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
