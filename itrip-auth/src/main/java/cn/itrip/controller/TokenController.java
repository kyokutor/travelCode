package cn.itrip.controller;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.vo.ItripTokenVO;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import cn.itrip.service.TokenService;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.logging.Logger;

@RequestMapping("/api")
@Controller
public class TokenController {

    private Logger logger = Logger.getLogger(String.valueOf(TokenController.class));

    @Resource
    private TokenService TokenService;

    /**
     * tooken验证
     * @param request
     * @return
     */
    @ApiOperation(value = "token验证",httpMethod = "get")
    @RequestMapping(value = "/validateToken", method = RequestMethod.GET, produces = "application/json", headers = "token")
    @ResponseBody
    public Dto validate(HttpServletRequest request) {
        try {
            boolean lena = TokenService.validate(request.getHeader("user-agent"), request.getHeader("token"));
            System.out.println(lena + "token");
            if (lena) {
                return DtoUtil.returnSuccess("token有效");
            } else {
                return DtoUtil.returnSuccess("token无效");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);

        }


    }


    /**
     * 注销token
     *
     * @param request
     * @return
     */

   /* @ApiOperation(value = "用户注销",httpMethod = "get")
    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = "application/json", headers = "token")
    @ResponseBody
    public Dto logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        try {
            if (TokenService.validate(request.getHeader("user-agent"),token)){
                TokenService.detele(token);

            }else {
                return DtoUtil.returnFail("token无效",ErrorCode.AUTH_TOKEN_INVALID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return DtoUtil.returnFail("token无效",ErrorCode.AUTH_TOKEN_INVALID);
        }
        return null;
    }*/

    @ApiOperation(value = "置换",httpMethod = "get")
    @RequestMapping(value = "/retoken", method = RequestMethod.POST, produces = "application/json", headers = "token")
    @ResponseBody
    public Dto retoken(HttpServletRequest request) {
        String token;
        try {
            token=this.TokenService.reloadToken(request.getHeader("user-agent"),request.getHeader("token"));
            ItripTokenVO vo = new ItripTokenVO(token, Calendar.getInstance().getTimeInMillis() + 2 * 60 * 60 * 100, Calendar.getInstance().getTimeInMillis());
            return DtoUtil.returnDataSuccess(vo);
        }catch (Exception e){
            e.printStackTrace();
            return DtoUtil.returnFail(e.getLocalizedMessage(),ErrorCode.AUTH_UNKNOWN);
        }
    }
}
