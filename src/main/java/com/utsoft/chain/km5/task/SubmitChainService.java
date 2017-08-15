package com.utsoft.chain.km5.task;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.utsoft.blockchain.api.exception.CryptionException;
import com.utsoft.blockchain.api.exception.ServiceProcessException;
import com.utsoft.blockchain.api.pojo.BaseResponseModel;
import com.utsoft.blockchain.api.pojo.TkcSubmitRspVo;
import com.utsoft.blockchain.api.pojo.TransactionVarModel;
import com.utsoft.blockchain.api.pojo.UserInfoRequstModel;
import com.utsoft.blockchain.api.pojo.UserInfoRspModel;
import com.utsoft.blockchain.api.proivder.ITkcAccountStoreExportService;
import com.utsoft.blockchain.api.proivder.ITkcTransactionExportService;
import com.utsoft.blockchain.api.security.FamilySecCrypto;
import com.utsoft.blockchain.api.util.Constants;
import com.utsoft.blockchain.api.util.SdkUtil;
import com.utsoft.blockchain.api.util.SignaturePlayload;
import com.utsoft.chain.km5.dao.PricipalUserRepository;
import com.utsoft.chain.km5.dao.TransactionResultRepository;
import com.utsoft.chain.km5.pojo.ConventionChaincode;
import com.utsoft.chain.km5.pojo.PricipalUserPo;
import com.utsoft.chain.km5.pojo.TransactionResultPo;
/**
 * 处理逻辑
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
@Service
public class SubmitChainService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	 @Value("${apply.category}")
	 private String category;
	 
	 @Value("${user.password}")
	 private String password ;
	 
	
	 @Autowired 
     @Lazy(true)
	 ITkcTransactionExportService tkcTransactionExportService;
		
	 @Autowired
	 private ITkcAccountStoreExportService tkcAccountStoreExportService;
	 
	 @Autowired
	 private PricipalUserRepository poricipUserRepository;
	 
	 @Autowired
	 private  TransactionResultRepository tansactionResultRepository;
	 
	 /**
	  * 交易逻辑
	  * @param username
	  * @param to
	  * @return
	  */
	 public boolean handlerChainLogic(String reqId,ConventionChaincode conventionChaincode)  {
		 
		 PricipalUserPo userPo = poricipUserRepository.findUser(conventionChaincode.getFrom());
		 if (userPo==null) {
			 if(!register(conventionChaincode.getFrom(),password))
				throw new ServiceProcessException(Constants.EXECUTE_FAIL_ERROR,"user"+conventionChaincode.getFrom()+" register fail");
			   userPo = poricipUserRepository.findUser(conventionChaincode.getFrom());
		 }
		 if (userPo!=null) {
			return doTransaction(userPo,conventionChaincode);
		 }
		 return false;
	 }
	 
	 
	 public boolean register(String accountId,String password)  {
		   
    	 String  created = SdkUtil.generateId();
    	 UserInfoRequstModel requestModel = new UserInfoRequstModel();
		 requestModel.setUserName(accountId);
		 requestModel.setPassword(password);
		 requestModel.setCreated(created);
		 
		 BaseResponseModel<UserInfoRspModel> baseResponse = tkcAccountStoreExportService.register(requestModel);
		 if (baseResponse.getData()!=null) {
			String publicKey = baseResponse.getData().getPrivateKey();
			
			PricipalUserPo pricipalUserPo = new PricipalUserPo();
			pricipalUserPo.setToken(baseResponse.getData().getToken());
			pricipalUserPo.setGmtCreate(new Date());
			pricipalUserPo.setPrivateKey(publicKey);
			pricipalUserPo.setUserName(accountId);
			poricipUserRepository.save(pricipalUserPo);
			return true;
		 }
		 return  false;
	 }
	 
	 /**
	  * @param reqId
	  * @param from
	  * @param privateKey
	  * @param to
	  * @param submitJson
	  */
	 public boolean doTransaction( PricipalUserPo userPo,ConventionChaincode conventionChaincode) {
		
			String created = SdkUtil.generateId();
			TransactionVarModel model = new TransactionVarModel(category,conventionChaincode.getCmd());
			model.setCreated(created);
			model.setFrom(userPo.getUserName());
			model.setTo(conventionChaincode.getTo());
			model.setSubmitJson(conventionChaincode.getSubmitJson());
			FamilySecCrypto familyCrypto = FamilySecCrypto.Factory.getCryptoSuite();
			SignaturePlayload signaturePlayload = new SignaturePlayload(familyCrypto);
			
			/**
			 * md5(applyCategory=1&from=2&to=3&cmd=4&submitJson=5&created=xxx)
			 * 注意顺序
			 */
			signaturePlayload.addPlayload(category);
			signaturePlayload.addPlayload(userPo.getUserName());
			signaturePlayload.addPlayload(conventionChaincode.getTo());
			signaturePlayload.addPlayload(conventionChaincode.getCmd());
			signaturePlayload.addPlayload(conventionChaincode.getSubmitJson());
			signaturePlayload.addPlayload(created);
			String sign;
			try {
				sign = signaturePlayload.doSignature(userPo.getPrivateKey());
			  } catch (CryptionException e) {
				  logger.error("not sign success ",e);
				return  false;
			}
			 BaseResponseModel<TkcSubmitRspVo> baseResponse = tkcTransactionExportService.tranfer(model,sign);
			 if (baseResponse.isSuccess()) {
				
				 TkcSubmitRspVo rspVo = baseResponse.getData();
				 TransactionResultPo  entities = new TransactionResultPo();
				 entities.setReqId(conventionChaincode.getSubmitJson());
				 entities.setTxId(rspVo.getTxId());
				 entities.setSubmitId(created);
				 entities.setStatus(rspVo.isStatus()?1 : 0);
				 entities.setGmtCreate(new Date());
				 tansactionResultRepository.save(entities);
				 return true;
		} 
	    return false;
	 } 
}
