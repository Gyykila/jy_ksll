package com.cqjysoft.modules.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cqjysoft.common.security.K3PasswordParse;
import com.cqjysoft.modules.domian.entity.User;
import com.cqjysoft.modules.domian.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value="/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
    @RequestMapping(value="/login",method=RequestMethod.POST)
    @ResponseBody
    public String login(String data) throws ParseException, IOException {
    	ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(data, User.class);
		Map<String, Object> map = new HashMap<String, Object>();
		if(user!=null) {
			User realUser = userRepository.findByUsername(user.getUsername());
			if(realUser!=null) {
				if(K3PasswordParse.validatePassword(user.getPassword(), realUser.getPassword())){
					map.put("code", "SUCCESS");
					map.put("msg", "用户验证成功");
					String token = UUID.randomUUID().toString();
					realUser.setToken(token);
					userRepository.save(realUser);
					user.setToken(token);
					user.setPassword(null);
					map.put("data", user);
				}else {
					map.put("code", "ERROR");
		    		map.put("msg", "密码不正确");
				}
			}else {
				map.put("code", "ERROR");
	    		map.put("msg", "用户不存在");
			}
    	}else {
    		map.put("code", "ERROR");
    		map.put("msg", "上传参数不正确");
    	}
        return mapper.writeValueAsString(map);
    }
    
    @RequestMapping(value="/autologin",method=RequestMethod.POST)
    @ResponseBody
    public String autologin(String data) throws ParseException, IOException {
    	ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(data, User.class);
		Map<String, Object> map = new HashMap<String, Object>();
		if(user!=null) {
			User realUser = userRepository.findByUsername(user.getUsername());
			if(realUser!=null) {
				if(realUser.getToken().equals(user.getToken())){
					map.put("code", "SUCCESS");
					map.put("msg", "用户验证成功");
					String token = UUID.randomUUID().toString();
					realUser.setToken(token);
					userRepository.save(realUser);
					user.setToken(token);
					map.put("data", user);
				}else {
					map.put("code", "ERROR");
		    		map.put("msg", "token验证失败");
				}
			}else {
				map.put("code", "ERROR");
	    		map.put("msg", "用户不存在");
			}
    	}else {
    		map.put("code", "ERROR");
    		map.put("msg", "上传参数不正确");
    	}
        return mapper.writeValueAsString(map);
    }
}
