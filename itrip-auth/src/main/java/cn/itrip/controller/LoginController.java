package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.ErrorCode;
import cn.itrip.service.TokenService;
import cn.itrip.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

@Controller
@RequestMapping("/api")
public class LoginController {

    @Resource
    private TokenService TokenService;
    @Resource
    private UserService UserService;


    /**
     *   api  用户登陆
     * @param name
     * @param password
     * @param request
     * @return
     */
    @ApiOperation(value = "用户登录",httpMethod = "POST",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="根据用户名、密码进行统一认证")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="form",required=true,value="用户名",name="name",defaultValue="itrip@163.com"),
            @ApiImplicitParam(paramType="form",required=true,value="密码",name="password",defaultValue="123456"),
    })
    @RequestMapping(value = "/dologin", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    Dto login(@RequestParam String name, @RequestParam String password, HttpServletRequest request) {
        try {
            ItripUser user = UserService.login(name, password);
            //判断对象是否为空
            if (EmptyUtils.isNotEmpty(user)) {
                String userAgen = request.getHeader("user-agent");
                String token = TokenService.genarateToken(request.getHeader("user-agent"), user);
                System.out.println("token"+token+"userAgen"+userAgen);
                TokenService.sace(token, user);
                ItripTokenVO vo = new ItripTokenVO(token, Calendar.getInstance().getTimeInMillis() + 2 * 60 * 60 * 100, Calendar.getInstance().getTimeInMillis());
                return DtoUtil.returnDataSuccess(vo);
            } else {
                return DtoUtil.returnFail("用户名密码错误", ErrorCode.AUTH_ACTIVATE_FAILED);//用户认证失败
            }

        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(),ErrorCode.AUTH_ACTIVATE_FAILED);
        }
    }


    //用户注销

    /**
     * 用户注销
     * @param request
     * @return
     * @throws Exception
     */

    @ApiOperation(value = "用户注销",httpMethod = "GET",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="客户端需在header中发送token")
    @ApiImplicitParam(paramType="header",required=true,name="token",value="用户认证凭据",defaultValue="token:PC-ed6e201becad0e79ae04178e519fd13b-29-201911261526010-2894fb")
    @RequestMapping(value="/logout",method=RequestMethod.GET,produces="application/json",headers="token")
    public @ResponseBody Dto logout(HttpServletRequest request) throws Exception {
        //验证token
        String token = request.getHeader("token");
        if(!TokenService.validate(request.getHeader("user-agent"),token))
            return DtoUtil.returnFail("token无效", ErrorCode.AUTH_TOKEN_INVALID);
        //删除token和信息
        try {
            TokenService.detele(token);
            return DtoUtil.returnSuccess("注销成功");
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("注销失败", ErrorCode.AUTH_UNKNOWN);
        }

    }


}

