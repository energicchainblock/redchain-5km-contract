package com.utsoft.chain.km5.dao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.utsoft.chain.km5.pojo.PricipalUserPo;
/**
 * 
 * @author hunterfox
 * @date: 2017年8月14日
 * @version 1.0.0
 */
@Repository
public interface PricipalUserRepository  extends CrudRepository<PricipalUserPo, Long>, JpaSpecificationExecutor<Long> {


	 @Query("from PricipalUserPo u where u.userName=:name")
	 PricipalUserPo findUser(@Param("name") String userName);
	
}
