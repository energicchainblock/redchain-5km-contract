package com.utsoft.chain.km5.dao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.utsoft.chain.km5.pojo.TransactionResultPo;
/**
 * 交易信息查询
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
@Repository
public interface TransactionResultRepository  extends CrudRepository<TransactionResultPo, String>, JpaSpecificationExecutor<String> {

	 @Query("from TransactionResultPo u where u.reqId=:id")
	 TransactionResultPo findByReqId(@Param("id") String reqId);
}
