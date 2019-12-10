package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.ItripUser;
import cn.itrip.beans.vo.userinfo.ItripUserVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import cn.itrip.common.MD5;
import cn.itrip.service.SmsService;
import cn.itrip.service.TokenService;
import cn.itrip.service.UserService;
import cn.itrip.service.mailService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.regex.Pattern;

@RequestMapping("/api")
@Controller
public class Usercontroller {

   @Resource
    private TokenService TokenService;

   @Resource
   private UserService UserService;

   @Resource
   private SmsService SmsService;

    @Resource
    private mailService mailService;



    @ApiOperation(value="邮箱注册用户激活",httpMethod = "PUT",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="邮箱激活")
    @RequestMapping(value="/activate",method=RequestMethod.PUT,produces= "application/json")
    public @ResponseBody Dto activate(
            @ApiParam(name="user",value="注册邮箱地址",defaultValue="test@bdqn.cn")
            @RequestParam String user,
            @ApiParam(name="code",value="激活码",defaultValue="018f9a8b2381839ee6f40ab2207c0cfe")
            @RequestParam String code){
        try {
            if(UserService.activate(user, code))
            {
                return DtoUtil.returnSuccess("激活成功");
            }else{
                return DtoUtil.returnSuccess("激活失败");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return DtoUtil.returnFail("激活失败", ErrorCode.AUTH_ACTIVATE_FAILED);
        }
    }






    /**
     * 检查用户是否已注册
     * @param name
     * @return
     */
    @ApiOperation(value="用户名验证",httpMethod = "GET",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="验证是否已存在该用户名")
    @RequestMapping(value="/ckusr",method=RequestMethod.GET,produces= "application/json")
    public @ResponseBody
    Dto checkUser(
            @ApiParam(name="name",value="被检查的用户名",defaultValue="test@bdqn.cn")
            @RequestParam String name) {
        try {

            if (null == UserService.finduserBuuserCode(name))
            {
                return DtoUtil.returnSuccess("用户名可用");
            }
            else
            {
                return DtoUtil.returnFail("用户已存在，注册失败", ErrorCode.AUTH_USER_ALREADY_EXISTS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
        }
    }


   //手机短信验证
   /* @RequestMapping(value="/validatephone",method=RequestMethod.POST,produces= "application/json")
    public @ResponseBody Dto validatePhone(
            @RequestParam String user,
            @RequestParam String code){
        try {
            if(UserService.validatePhone(user,code))
            {
                return DtoUtil.returnSuccess("验证成功");
            }else{
                return DtoUtil.returnSuccess("验证失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("验证失败", ErrorCode.AUTH_ACTIVATE_FAILED);
        }
    }*/

    @ApiOperation(value="手机注册用户短信验证",httpMethod = "PUT",
            protocols = "HTTP", produces = "application/json",
            response = Dto.class,notes="手机注册短信验证")
    @RequestMapping(value="/validatephone",method=RequestMethod.PUT,produces= "application/json")
    public @ResponseBody Dto validatePhone(
            @ApiParam(name="user",value="手机号码",defaultValue="13811565189")
            @RequestParam String user,
            @ApiParam(name="code",value="验证码",defaultValue="8888")
            @RequestParam String code){
        try {
            if(UserService.validatePhone(user, code))
            {
                return DtoUtil.returnSuccess("验证成功");
            }else{
                return DtoUtil.returnSuccess("验证失败");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return DtoUtil.returnFail("验证失败", ErrorCode.AUTH_ACTIVATE_FAILED);
        }
    }

//邮箱注册  redustermail

  @RequestMapping(value = "/doregister",method = RequestMethod.POST,produces = "application/json")
    @ResponseBody
  public Dto doredisBymail(@RequestBody ItripUserVO vo){
        if(!this.validEmail(vo.getUserCode())){
            return DtoUtil.returnFail("请输入正确的邮箱格式",ErrorCode.AUTH_ILLEGAL_USERCODE);
        }else{
            ItripUser user=new ItripUser();
            user.setUserCode(vo.getUserCode());
            user.setUserName(vo.getUserName());
            try {
                if (null ==UserService.finduserBuuserCode(vo.getUserCode())) {
                    user.setUserPassword(MD5.getMd5(vo.getUserPassword(), 32));
                    UserService.itriptxCreateUser(user);
                    return DtoUtil.returnSuccess();
                }else
                {
                    return DtoUtil.returnFail("用户已存在，注册失败", ErrorCode.AUTH_USER_ALREADY_EXISTS);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
                  return  null;
  }




    //手机注册

   @RequestMapping(value = "/registerbyphone",method = RequestMethod.POST,produces = "application/json")
   @ResponseBody
    public Dto doredisByphone(@RequestBody ItripUserVO vo){
            //手机号 验证

       if(!validPhone(vo.getUserCode())){
            return DtoUtil.returnFail("请输入正确的手机号码", ErrorCode.AUTH_ILLEGAL_USERCODE);
       }else{
           ItripUser user=new ItripUser();
           user.setUserCode(vo.getUserCode());
           user.setUserName(vo.getUserName());

           try {
               if (null ==UserService.finduserBuuserCode(vo.getUserCode())) {
                   user.setUserPassword(MD5.getMd5(vo.getUserPassword(), 32));
                   UserService.itripxCreateUserByPhone(user);
                   return DtoUtil.returnSuccess();
               }else
               {
                   return DtoUtil.returnFail("用户已存在，注册失败", ErrorCode.AUTH_USER_ALREADY_EXISTS);
               }



           }catch (Exception e){
                 e.printStackTrace();
           }

       }
          //调用具体的业务层方法

       return null;
   }






    /**			 *
     * 合法E-mail地址：
     * 1. 必须包含一个并且只有一个符号“@”
     * 2. 第一个字符不得是“@”或者“.”
     * 3. 不允许出现“@.”或者.@
     * 4. 结尾不得是字符“@”或者“.”
     * 5. 允许“@”前的字符中出现“＋”
     * 6. 不允许“＋”在最前面，或者“＋@”
     */
    private boolean validEmail(String email){

        String regex="^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"  ;
        return Pattern.compile(regex).matcher(email).find();
    }

    /**
     * 验证是否合法的手机号
     * @param phone
     * @return
     */
    private boolean validPhone(String phone) {
        String regex="^1[3578]{1}\\d{9}$";
        return Pattern.compile(regex).matcher(phone).find();
    }




}
