package com.utsoft.chain.km5.controller;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.utsoft.chain.km5.pojo.TransactionCallRsp;
/**
 * 回调注册
 * @author hunterfox
 * @date: 2017年8月17日
 * @version 1.0.0
 */
@Controller
public class ReactController {

	@RequestMapping(value = "/feedbackstatus1", method = {RequestMethod.POST,RequestMethod.GET})
	public TransactionCallRsp feedbackstatus1(@RequestParam("user") String user,String txId,Integer forward,String reqId,Long txTime,Long status) {
		 
		System.out.println("user:"+user+"txId"+txId+"forward"+forward+"reqId"+reqId+"txTime:"+txTime+"status:"+status);
		TransactionCallRsp  resp = new TransactionCallRsp();
		resp.setStatus(200);
		resp.setMsg("ok");
		return resp ;
	}
	
	
	@RequestMapping(value = "/feedbackstatus", method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public TransactionCallRsp feedbackstatus(@RequestBody Map<String,Object> body) {
		 
		System.out.println(body);
		TransactionCallRsp  resp = new TransactionCallRsp();
		resp.setStatus(1);
		resp.setMsg("ok");
		return resp ;
	}
	
	
/*	@RequestMapping(value = "/test", method = {RequestMethod.POST,RequestMethod.GET})
	public TransactionCallRsp feedbackstatus(String hello) {
		
		TransactionCallRsp  resp = new TransactionCallRsp();
		resp.setStatus(200);
		resp.setMsg("ok");
		return resp ;
	}*/
}
