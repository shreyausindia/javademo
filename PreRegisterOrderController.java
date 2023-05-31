/**
 * Copyright (c) 2022, HAPPY SAIFURU.
 */
package com.saifuru.shpml.preorder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.saifuru.shpml.registration.VisitorRegisterForm;
import com.saifuru.shpml.shpml_common.common.Commons;
import com.saifuru.shpml.shpml_common.common.Constants;
import com.saifuru.shpml.shpml_common.common.JSONWebToken;
import com.saifuru.shpml.shpml_common.common.XsStringChecker;
import com.saifuru.shpml.web.property.MessageManager;
import com.saifuru.shpml.zipdic.ZipdicDTO;
import com.saifuru.shpml.zipdic.ZipdicForm;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;


@RestController
@CrossOrigin("*")
@RequestMapping(value ="/saifuru/preregisterorder")
public class PreRegisterOrderController {

	/** サービス */
	@Autowired
	private PreRegisterOrderService service;

	@Value("${JWTToken.key}")
	String JWTTokenKey;

	@RequestMapping(value="order",produces = "application/json;charset=UTF-8", consumes=MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public @ResponseBody Map<Object, Object> toOrder(@RequestBody PreRegisterOrderForm form,HttpServletResponse response,HttpServletRequest req){
		Map<Object, Object> visitorParamMap = null;
		Map<Object, Object> responseJson = new LinkedHashMap<>();
		Map<Object, Object> responseMessage = new LinkedHashMap<>();
		int registerUserId = 0;
		visitorParamMap = new LinkedHashMap<>();
		if(service.validatePreRegisterOrderData(form,responseMessage)){
			registerUserId = service.insertPreRegisterOrder(form);
			visitorParamMap.put("status", "0");
			visitorParamMap.put("Message", "確認のメールをお送りしましたので必ずご確認ください。 "
					+"<br>"+ "メールが届かない場合は正常に登録が完了していないと考えられますので、 <br>お手数ですが操作をやり直してください。");
			visitorParamMap.put("Id", registerUserId);
			responseJson.put("APISTATUS", visitorParamMap);
		} else {
			visitorParamMap.put("status", "1");
			visitorParamMap.put("Message", "ERR_001");
			responseJson.put("APISTATUS", visitorParamMap);
			responseJson.put("ERROR_MESSAGE", responseMessage);
		}
		return responseJson;
	}

	@RequestMapping(value="show",produces = "application/json", consumes=MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	public @ResponseBody Map<Object, Object> toMeeting(@RequestBody PreRegisterOrderForm form,
			HttpServletResponse response,HttpServletRequest req) throws  ParseException{
		Map<Object, Object> orderParamMap = null;
		Map<Object, Object> responseJson = new LinkedHashMap<>();
		Map<Object, Object> responseMessage = new LinkedHashMap<>();
		orderParamMap = new LinkedHashMap<>();

		if(service.validateData(form)){
			PreRegisterOrderDTO orderData = service.fetchUserDetails(form);
			orderParamMap.put("status", "0");
			orderParamMap.put("Message", "アドレスフェッチに成功しました.");
			responseJson.put("APISTATUS", orderParamMap);
			responseJson.put("USER_DETAILS", orderData);
		} else {
			orderParamMap.put("status", "1");
			orderParamMap.put("Message", "No record found");
			responseJson.put("APISTATUS", orderParamMap);
		}
		return responseJson;
	}
}
