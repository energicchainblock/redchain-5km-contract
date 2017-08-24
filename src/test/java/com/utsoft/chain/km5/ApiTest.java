package com.utsoft.chain.km5;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.ServiceApplyCodeReqMode;
import com.utsoft.blockchain.api.pojo.TkcQueryDetailRspVo;
import com.utsoft.blockchain.api.pojo.TkcSubmitRspVo;
import com.utsoft.blockchain.api.pojo.TkcTransactionBlockInfoVo;
import com.utsoft.blockchain.api.pojo.TransactionVarModel;
import com.utsoft.blockchain.api.pojo.UserInfoRequstModel;
import com.utsoft.blockchain.api.pojo.UserInfoRspModel;
import com.utsoft.blockchain.api.proivder.ITkcAccountStoreExportService;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportServiceAsync;
import com.utsoft.blockchain.api.security.FamilySecCrypto;
import com.utsoft.blockchain.api.util.SdkUtil;
import com.utsoft.blockchain.api.util.SignaturePlayload;
import com.weibo.api.motan.rpc.Future;
import com.weibo.api.motan.rpc.FutureListener;
import com.weibo.api.motan.rpc.ResponseFuture;

import junit.framework.Assert;
@RunWith(SpringJUnit4ClassRunner.class)     
@ContextConfiguration(initializers={ConfigFileApplicationContextInitializer.class})
@SpringBootTest(classes=KmContractApplication.class)
public class ApiTest {

	
	@Value("${apply.category}")
	private String applyCategory;
	 
	/**
	 * 支持异常处理
	 */
	@Autowired
	private ITkcTransactionExportServiceAsync tkcTransactionExportService;
	
	@Autowired
	private ITkcAccountStoreExportService tkcAccountStoreExportService;
	
	 private String privateKey;
	 
	 @Value("${user.username}")
	 private String username;
	 
	 @Value("${user.password}")
	 private String password ;
	 
	 @Value("${user.toUser}")
	 private String toUser;

	 @Value("${user.token}")
	 private String token ; 
	 
	 private static String txId ="018029b55ff861140fbb19dba2f8478c1725f01580fedb81b960d25200440e74";
	
	 @Before
	 public void setup() {
		
		 /**
		  * 查询账号存在不存在
		  */
		BaseResponseModel<UserInfoRspModel> userPrivateKeyAccess = tkcAccountStoreExportService.getIndividualAccout(username,token);
	    if (userPrivateKeyAccess.isSuccess()) {
		    privateKey = userPrivateKeyAccess.getData().getPrivateKey();
	    } else   {
	    	 String  created = SdkUtil.generateId();
		    
	    	 UserInfoRequstModel requestModel = new UserInfoRequstModel();
			 requestModel.setUserName(username);
			 requestModel.setPassword(password);
			 requestModel.setCreated(created);
			
			 /**
			  * 注册
			  */
			BaseResponseModel<UserInfoRspModel> baseResponse = tkcAccountStoreExportService.register(requestModel);
			if (baseResponse.getData()!=null) {
				String publicKey = baseResponse.getData().getPrivateKey();
				token =baseResponse.getData().getToken();
				System.out.println(password+":"+publicKey);
			}
			userPrivateKeyAccess = tkcAccountStoreExportService.getIndividualAccout(username,token);
			if (!userPrivateKeyAccess.isSuccess()) {
				fail("user not exists");
		    }
			//保存私钥，后续加密使用
			 privateKey = userPrivateKeyAccess.getData().getPrivateKey();
	   }
	}
	 
	 /**
	  * 注册回调地址，注册回调地址后，之后交易信息，接入服务器通知上次<code>created</code> 交易是否成功与否
	  * <code>created</code> 由用户保证唯一
	  * 回调内容：
	  *{
			 "reqId": "String", #请求id=created
		     "txId": "String" ,#"区块链交易ID,后面可以通过它查询bolckinfo 去查询blockinfo"
			 "txTime": "String", #yyyy-MM-DD:mm:hh:ss
			 "status": true|false,
			  "forward": 0|1,0 转出，1 转进
			  "user":  用户
		 }
		 
		 服务器回调之后返回回调内容
		 {
		    "status": true|false
		 }
	  */
	 @Test
	 public void testRegisterCallback() {
		   
		     ServiceApplyCodeReqMode serviceApplyCodeReqMode = new ServiceApplyCodeReqMode();
		    /**
		     * 业务代码
		     */
		     serviceApplyCodeReqMode.setApplyCode("5kmcode");
		     /**
		      * 回调地址
		      */
		     serviceApplyCodeReqMode.setCallbackUrl("http://host/servicecode/callback");
			 BaseResponseModel<Integer> registerCallBackModel = tkcAccountStoreExportService.applyService(token,serviceApplyCodeReqMode);
	         if (registerCallBackModel.getCode()==200)  {
	        	 System.out.println("注册成功");
	         }
	     	 assertEquals(registerCallBackModel.getCode(),"200");
	 }
	
	 /**
	  * 查询本账号当前内容
	  */
	@Test
	public void testQueryAccoutInfo() {
		
		FamilySecCrypto familyCrypto = FamilySecCrypto.Factory.getCryptoSuite();
		SignaturePlayload signaturePlayload = new SignaturePlayload(familyCrypto);
	
		String created = SdkUtil.generateId();
		String from = username;
		
		/**
		 * 注意顺序
		 */
		signaturePlayload.addPlayload(applyCategory);
		signaturePlayload.addPlayload(from);
		signaturePlayload.addPlayload(created);
		
		/**
		 * 签名
		 */
		String sign;
		 try {
			sign = signaturePlayload.doSignature(privateKey);
		  } catch (CryptionException e) {
			e.printStackTrace();
			fail("not sign success ");
			return ;
		}
		
		BaseResponseModel<TkcQueryDetailRspVo> baseResponse = tkcTransactionExportService.getAccountDetail(applyCategory, from, created, sign);
		System.out.println(JSON.toJSON(baseResponse.getData()));
		assertEquals(baseResponse.getCode(),"200");
	}
	
	
	/**
	 * 注册用户
	 */
	@Test
	public void testRegister() {
		
		/**
		 * created，必须填写，后面防止重复提交
		 */
		String  created = SdkUtil.generateId();
		UserInfoRequstModel requestModel = new UserInfoRequstModel();
		requestModel.setUserName(username);
		requestModel.setPassword(password);
		requestModel.setCreated(created);
		BaseResponseModel<UserInfoRspModel> baseResponse = tkcAccountStoreExportService.register(requestModel);
		if (baseResponse.getData()!=null) {
			String  privateKey = baseResponse.getData().getPrivateKey();
			 token = baseResponse.getData().getToken();
			System.out.println(token+":"+privateKey);
		}
	}
	
	/**
	 * 根据token 获取私钥，用于签名
	 */
	@Test
	public void testGetPublicKey() {
		
		BaseResponseModel<UserInfoRspModel> baseResponse = tkcAccountStoreExportService.getIndividualAccout(username,token);
		if (baseResponse.getData()!=null){
			String publicKey = baseResponse.getData().getPrivateKey();
			String token1 = baseResponse.getData().getToken();
			System.out.println(token1+":"+publicKey);
		}
		assertEquals(baseResponse.getCode(),"200");
	}	
	
	
	/**
	 * 支付，当前例子是转账
	 */
	@Test
	public void testMoveAToB() {
		
		String to = toUser;
		String submitJson ="10";
		String created = SdkUtil.generateId();
		
		TransactionVarModel model = new TransactionVarModel(applyCategory,"move");
		model.setCreated(created);
		model.setFrom(username);
		model.setTo(to);
		model.setSubmitJson(submitJson);
		
		FamilySecCrypto familyCrypto = FamilySecCrypto.Factory.getCryptoSuite();
		SignaturePlayload signaturePlayload = new SignaturePlayload(familyCrypto);
		String from = username;
		/**
		 * md5(applyCategory=1&from=2&to=3&cmd=4&submitJson=5&created=xxx)
		 * 注意顺序
		 */
		signaturePlayload.addPlayload(applyCategory);
		signaturePlayload.addPlayload(from);
		signaturePlayload.addPlayload(to);
		signaturePlayload.addPlayload("move");
		signaturePlayload.addPlayload(submitJson);
		signaturePlayload.addPlayload(created);
		//签名
		String sign;
		 try {
			sign = signaturePlayload.doSignature(privateKey);
		  } catch (CryptionException e) {
			e.printStackTrace();
			fail("not sign success ");
			return ;
		}
		 /**
		  * 提交转账请求
		  */
		 BaseResponseModel<TkcSubmitRspVo> baseResponse = tkcTransactionExportService.tranfer(model,sign);
		 assertEquals(baseResponse.getCode(),200);
		 if (baseResponse.isSuccess()) {
			 txId = baseResponse.getData().getTxId(); //区块链txID
			 System.out.println(JSON.toJSON(baseResponse.getData()));
		 } 
	}
	
	/**
	 * 查询区块链事务bockinfo 信息
	 */
	@Test
	public void queryTxBlockInfo() {
	
		FamilySecCrypto familyCrypto = FamilySecCrypto.Factory.getCryptoSuite();
		SignaturePlayload signaturePlayload = new SignaturePlayload(familyCrypto);
	
		String created = SdkUtil.generateId();
		String from = username;
		
		/**
		 * 注意顺序
		 */
		signaturePlayload.addPlayload(applyCategory);
		signaturePlayload.addPlayload(from);
		signaturePlayload.addPlayload(txId);
		signaturePlayload.addPlayload(created);
		
		String sign;
		 try {
			sign = signaturePlayload.doSignature(privateKey);
		  } catch (CryptionException e) {
			e.printStackTrace();
			fail("not sign success ");
			return ;
		}
		 
		 ResponseFuture baseResponseFuture = tkcTransactionExportService.listStockChangesAsync(applyCategory,from, txId, created, sign);
		
		 FutureListener listener = new FutureListener() {  
	       
         public void operationComplete(Future future) throws Exception {
            	 
            	 System.out.println("async call "+ (future.isSuccess() ? "sucess": "fail exception:"+future.getException().getMessage()));
            	 
            	 if (future.isSuccess()) {
            		
            		 BaseResponseModel<TkcTransactionBlockInfoVo> baseResponse = (BaseResponseModel<TkcTransactionBlockInfoVo>) future.getValue();
                	 assertEquals(baseResponse.getCode(),200);
             	     if (baseResponse.isSuccess()) {
             		   System.out.println(JSON.toJSON(baseResponse.getData()));
             	    }
            	 } else 
            	assertTrue(future.isSuccess());
             }
         };
         baseResponseFuture.addListener(listener);
         try {
             Thread.sleep(120000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
	}		
}
