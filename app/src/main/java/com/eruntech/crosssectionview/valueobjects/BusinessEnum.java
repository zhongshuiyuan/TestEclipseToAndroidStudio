package com.eruntech.crosssectionview.valueobjects;

/**
 * 设备等相关类型的枚举值集合
 * @author 覃远逸
 */
public class BusinessEnum
{

	// 设备类型 //属于大类项
	// enum EEquType
	public static final int EQU_TYPE_RONGJIE = 1;// 光纤熔接设备 //光连接设备
	public static final int EQU_TYPE_JIETOU = 2;// 电缆接头设备 //电连接设备
	public static final int EQU_TYPE_ODF = 3;// ODF设备 //光配线设备
	public static final int EQU_TYPE_DDF = 4;// DDF设备 //电配线设备
	public static final int EQU_TYPE_MDFH = 5;// MDF横排设备 //电配线设备
	public static final int EQU_TYPE_MDFV = 6;// MDF直列设备 //电配线设备
	public static final int EQU_TYPE_CAB = 7;// CAB设备 //电配线设备
	public static final int EQU_TYPE_DP = 8;// DP设备 //电配线设备
	public static final int EQU_TYPE_FASHE = 9;// 发射设备
	// public static final int EQU_TYPE_GLIANTONG = 10;//光连通设备 //光连接设备
	public static final int EQU_TYPE_DLIANTONG = 11;// 电连通设备 //电连接设备
	public static final int EQU_TYPE_GCHAPAN = 12;// 光插盘 //光插盘
	public static final int EQU_TYPE_DCHAPAN = 13;// 电插盘 //电插盘
	public static final int EQU_TYPE_VRONGJIE = 14;// 虚拟熔接器 //光连接设备
	public static final int EQU_TYPE_NOPORTPAN = 15;// 没有端口插盘
	public static final int EQU_TYPE_OTRANSMIT = 16;// 光发机
	public static final int EQU_TYPE_ORECEIVE = 17;// 光收机
	public static final int EQU_TYPE_OBRANCE = 18;// 分光器
	public static final int EQU_TYPE_AMPLIFIER = 19;// 放大器
	public static final int EQU_TYPE_DISTR = 20;// 分配器
	public static final int EQU_TYPE_RAMIFY = 21;// 分支器
	public static final int EQU_TYPE_OPTIALSTATIONEQU = 22;// 光站
	public static final int EQU_TYPE_USERBOX = 23;// 用户盒
	public static final int EQU_TYPE_FILTER = 24;// 滤波器
	public static final int EQU_TYPE_POWERSUPPLY = 25;// 供电器
	public static final int EQU_TYPE_SUBEQU = 26;// 附属设备
	public static final int EQU_TYPE_NARU = 27;// 纳入器
	public static final int EQU_TYPE_OMODEM = 28;// 光调制解调器
	public static final int EQU_TYPE_OTRANSCEIVER = 29;// 光纤收发器
	public static final int EQU_TYPE_SWITCH = 30;// 交换机
	public static final int EQU_TYPE_CMTS = 31;// CMTS
	public static final int EQU_TYPE_JOIN = 32;// 合路器
	public static final int EQU_TYPE_AMPLIFIER_Module = 33;// 放大模块
	public static final int EQU_TYPE_MatchDZ = 34;// 匹配电阻
	public static final int EQU_TYPE_Attenuator = 35; // 衰减器
	public static final int EQU_TYPE_RFSwitch = 36; // 射频信号切换器
	public static final int EQU_TYPE_CableTie = 37; // 同轴电缆接头
	public static final int EQU_TYPE_GDExtend = 38;// 广电扩展设备
	public static final int EQU_TYPE_CABLEMODEM = 39; // Cable Modem
	public static final int EQU_TYPE_OLTEQU = 40; // OLT设备
	public static final int EQU_TYPE_COMBINER = 41; // 合波器(Combiner)
	/** 熔接桶 **/
	public static final int EQU_TYPE_RJT = 42;

	// 端口列表的输入/输入端口
	// enum EInOrOut
	public static final int EInOrOut_In = 1;// 输入口
	public static final int EInOrOut_Out = 2;// 输出口
	public static final int EInOrOut_Switch = 3;// 分支口
	public static final int EInOrOut_InOut = 4;// 输入输出口(双向端口,两个端子)
	public static final int EInOrOut_Else = 5;// 其它端口(无方向的或端口里的信号是双向的)

	// 管孔状态
	// enum EHoleState
	public static final int NO_HOLE = 0;// 代表无管孔
	public static final int EMPTY_HOLE = 1;// 代表空管孔
	public static final int OTHER_IMPROPRIATE_HOLE = 2;// 代表它缆占用管孔
	public static final int FIBER_IMPROPRIATE_HOLE = 3;// 代表光缆占用管孔
	public static final int CABLE_IMPROPRIATE_HOLE = 4;// 代表电缆占用管孔
	public static final int BAD_HOLE = 5;// 代表损坏管孔
	public static final int ZUYONG_HOLE = 6;// 租用管孔
	public static final int YULIU_HOLE = 7;// 预留管孔
	public static final int HAVE_CHILD = 8;// 有子孔

	// 管孔类型
	// enum EPadType
	public static final int PAD_HOLE = 1;// 代表块孔
	public static final int PAD_TAO = 2;// 代表管套
	public static final int TAO_HOLE = 3;// 代表套孔
	public static final int PAD_SUBTAO = 4;// 代表子管套 2007-3-2添加
	public static final int TAO_VHOLE = 5;// 代表虚套孔

	// 设备模板
	// enum deviceTemplate
	public static final int RJT_IN_1_OUT_1 = 1;// 熔接桶一进一出
	public static final int RJT_IN_1_OUT_2 = 2;// 熔接桶一进二出
	public static final int RJT_IN_1_OUT_3 = 3;// 熔接桶一进三出
	public static final int RJT_IN_1_OUT_4 = 4;// 熔接桶一进四出
	public static final int RJT_IN_1_OUT_5 = 5;// 熔接桶一进五出
	public static final int RJT_IN_1_OUT_6 = 6;// 熔接桶一进六出
	public static final int RJT_IN_1_OUT_15 = 7;// 熔接桶一进十五出 4*4
	public static final int RJT_IN_1_OUT_16 = 8;// 熔接桶一进十六出
	public static final int RJT_IN_1_OUT_17 = 9;// 熔接桶一进十七出 2*9
	public static final int GFLQ_DEFAULT = 11;// 光分路器
	public static final int GFLQ_IN_1_OUT_2 = 12;// 光分路器一进二出
	public static final int GFLQ_IN_1_OUT_4 = 13;// 光分路器一进四出
	public static final int GFLQ_IN_1_OUT_5 = 14;// 光分路器一进五出
	public static final int GFLQ_IN_1_OUT_6 = 15;// 光分路器一进六出

	// 熔接器模板
	// enum rjqTemplate
	public static final int RJQ_2 = 0;// 二芯
	public static final int RJQ_4 = 1;// 四芯
	public static final int RJQ_6 = 2;// 六芯
	public static final int RJQ_8 = 3;// 八芯
	public static final int RJQ_12 = 4;// 十二芯
	public static final int RJQ_16 = 5;// 十六芯
}
