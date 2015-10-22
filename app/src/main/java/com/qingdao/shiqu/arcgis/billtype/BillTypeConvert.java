package com.qingdao.shiqu.arcgis.billtype;


public class BillTypeConvert {
	public static String ConvertToCode(BillType value)
    {
		String code = "00";
        switch (value)
        { 
        case 登录:
           // code = "n01";
        	code = "e1";
            break;
        case 退出:
          //  code = "n02";
        	code = "e2";
            break;
        case 下单:
           // code = "n03";
        	code = "e3";
            break;
        case 提单:
           // code = "n04";
        	 code = "e4";
            break;
        case 消息下载:
        	code = "e8";
        	break;
        case 消息处理:
        	code = "e9";
        	break;
        case 获取提单数:
        	code = "e14";
        	break;
        case 获取消息数:
        	code = "e15";
        	break;
    	case 获取用户机顶盒设备信息:
    		code = "e16";
    		break;
    	case 机顶盒故障历时查询:
    		code = "e17";
    		break;
    	case 机顶盒报废:
    		code = "e18";
    		break;
    	case 机顶盒故障历史提交:
    		code = "e19";
    		break;
		case 机顶盒更换:
			code = "e20";
			break;
		case 装机单回单统计:
			code = "e21";
			break;
    	case EOC终端信息提取:
    		code = "n08";
    		break;
		case 回单:
			code = "e6";
			break;
		case 交换机更换:
			code = "n12";
			break;
		case EOC终端更换:
			code = "n09";
		    break;
		case EOC终端开通:
			code = "n10";
		    break;
		case EOC设备列表:
			code = "e10";
		    break;
		case EOC设备信息添加:
			code = "e11";
		    break;
		case EOC设备信息修改:
			code = "e12";
		    break;
		case EOC设备信息移除:
			code = "e13";
		    break;
	    case 可回单信息查询:
	    	code = "e5";
	    	break;
    	case 用户设备信息查询:
    		code = "n16";
    		break;
    	case 消息提醒请求:
        	code="m1";//m1
        	break;    
    	case 消息提醒确认:
        	code="m2";//m2
        	break;  
    	case 检查当前工单是否有激活指令:
    		code="n17";
    		break;
    	case 获取光机信息:
    		code="n18";
    		break;
    	case 获取最新版本:
    		code = "n99";
    		break;
    	case 返单:
    		code = "e7";
    		break;
		default:
			break;
        }
        return code;
    }
}
