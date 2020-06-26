package com.xt.mobile.terminal.sip;

import java.util.Arrays;

/**
 * 序列参数集(俗称的sps)
 * 
 * @author luo
 * 
 */
public class SpsStructure
{
	/**
	 * 比特流所遵守的配置(当该值为100、110、122、144时，constraint_set0_flag、constraint_set1_flag、
	 * constraint_set2_flag都用该等于0)
	 */
	public int profile_idc;
	/**
	 * 如果值为1表示遵守H264标准文档中视频解码能力需求的A.2.1的基准简表,如果是0表示可以不遵守(例外：
	 * 如果constraint_set0_flag
	 * 、constraint_set1_flag、constraint_set2_flag中有一个等于1，那么该比特流必须遵守A.2下面的所有简表)
	 */
	public int constraint_set0_flag;
	/**
	 * 如果值为1表示遵守H264标准文档中视频解码能力需求的A.2.2的主要简表,如果是0表示可以不遵守
	 */
	public int constraint_set1_flag;
	/**
	 * 如果值为1表示遵守H264标准文档中视频解码能力需求的A.2.3的扩展简表,如果是0表示可以不遵守
	 */
	public int constraint_set2_flag;
	/**
	 * 如果profile_idc等于66、77或88并且level_idc等于11，constraint_set3_flag 等于1 是指该比特流遵从附
	 * 件A中对级别1b的所有规定
	 * ，constraint_set3_flag等于0是指该比特流可以遵从也可以不遵从附件A中有关1b级别的所有规定(级别有1
	 * 、1b、1.1、1.2、1.3、2、2.1)。 —
	 * 否则（profile_idc等于100、110、122或144或level_idc不等于11），constraint_set3_flag
	 * 等于1留作未来 ITU-T | ISO/IEC 使用。根据H264标准文档的建议书|
	 * 国际标准部分的规定，当profile_idc等于100、110、122或144或level_idc不
	 * 等于11时，比特流中constraint_set3_flag
	 * 应等于0。当profile_idc等于100、110、122或144或level_idc不等 于11时，遵从H264标准文档的建议书|
	 * 国际标准的部分解码器将忽略constraint_set3_flag的值。
	 */
	public int constraint_set3_flag;
	/**
	 * 备用值,应等于0。reserved_zero_4bits 的其他取值将由ITU-T | ISO/IEC 未来规定。解码器将忽略
	 * reserved_zero_4bits 的值。
	 */
	public int reserved_zero_4bits = 0;
	/**
	 * 比特流所遵守的级别
	 */
	public int level_idc;
	/**
	 * 用于识别图像参数集所指的序列参数集。seq_parameter_set_id 的值应在0-31的范围 内，包括0和31。
	 */
	public int seq_parameter_set_id;
	/**
	 * 与亮度取样对应的色度取样。chroma_format_idc的值应该在0
	 * 到3的范围内（包括0和3）。当chroma_format_idc不存在时，应推断其值为1（4：2：0的色度格式）。
	 */
	public int chroma_format_idc;
	/**
	 * 值等于1时，应用标准文档8.5节规定的残余颜色变换。 residual_colour_transform_flag
	 * 等于0时则不使用残余颜色变换。当residual_colour_transform_flag不存在时，默认其值为0。
	 */
	public int residual_colour_transform_flag = 0;
	/**
	 * 是指亮度队列样值的比特深度以及亮度量化参数范围的取值偏移QpBdOffsetY，如 下所示： BitDepthY= 8
	 * +bit_depth_luma_minus8 QpBdOffsetY= 6 * bit_depth_luma_minus8
	 * 当bit_depth_luma_minus8 不存在时，应推定其值为0。bit_depth_luma_minus8 取值范围应该在0到4之间
	 * （包括0和4）。
	 */
	public int bit_depth_luma_minus8;
	/**
	 * 是指色度队列样值的比特深度以及色度量化参数范围的取值偏移QpBdOffsetC， 如下所示： BitDepthC= 8 +
	 * bit_depth_chroma_minus8 QpBdOffsetC= 6 * ( bit_depth_chroma_minus8 +
	 * residual_colour_transform_flag )
	 * 当bit_depth_chroma_minus8不存在时，应推定其值为0。bit_depth_chroma_minus8取值范围应该在0到4之
	 * 间（包括0和4）。 变量RawMbBits 按下列公式得出： RawMbBits = 256 * BitDepthY+ 2 * MbWidthC
	 * * MbHeightC * BitDepthC
	 */
	public int bit_depth_chroma_minus8;
	/**
	 * 等于1是指当QP'Y 等于0时变换系数解码过程的变换旁路操作和图
	 * 像构建过程将会在第8.5节给出的去块效应滤波过程之前执行。qpprime_y_zero_transform_bypass_flag 等于0是指
	 * 变换系数解码过程和图像构建过程在去块效应滤波过程之 前执行而不使用变换旁路操作。当
	 * qpprime_y_zero_transform_bypass_flag 没有特别指定时，应推定其值为0。
	 */
	public int qpprime_y_zero_transform_bypass_flag;
	/**
	 * 等于1表示存在i=0..7 的标志seq_scaling_list_present_flag[i]。
	 * seq_scaling_matrix_present_flag 等于0则表示不存在这些标志并且由Flat_4x4_16
	 * 表示的序列级别的缩放比例列表
	 * 应被推断出来（对应i=0..5），由Flat_8x8_16表示的序列级别的缩放比例列表应被推断出来（对应i=6..7）。当
	 * seq_scaling_matrix_present_flag没有特别指定时，应推定其值为0。 缩放比例列表Flat_4x4_16
	 * 和Flat_8x8_16 规定如下： Flat_4x4_16[ i ] = 16, i = 0..15, (7-6) Flat_8x8_16[ i
	 * ] = 16, i = 0..63.
	 */
	public int seq_scaling_matrix_present_flag;
	/**
	 * 等于1 是指视频序列参数集中存在缩放比例列表i的语法结构。 seq_scaling_list_present_flag[i]
	 * 等于0表示视频序列参数集中不存在缩放比例列表i的语法结构并且表7-2中列出 的缩放比例序列后退规则集A应用于以i值为索引的序列级别的缩放比例列表。
	 */
	public int[] seq_scaling_list_present_flag;
	/**
	 * 按下列公式可得出与frame_num 相关的变量MaxFrameNum 的值。 MaxFrameNum = 2 (
	 * log2_max_frame_num_minus4 + 4 )
	 * log2_max_frame_num_minus4的值应在0-12范围内（包括0和12）（详细参照标准文档7-8）。
	 */
	public int log2_max_frame_num_minus4;
	/**
	 * 是指解码图像顺序的计数方法（标准文档的8.2.1节所述）。pic_order_cnt_type 的取值范围是0到 2（包括0和2）。
	 * 包含下列任何情况的一个编码视频序列中pic_order_cnt_type 都不能等于2：
	 * 1.一个包含非参考图像的访问单元紧跟在一个包含非参考帧的访问单元之后；
	 * 2.一个包含非参考图像的访问单元紧跟在分别包含一个由两个场一起组成一个互补的非参考场对的两个 访问单元之后；
	 * 3.一个包含非参考场的访问单元之后紧跟在一个包含另一个非参考图像的访问单元（该访问单元没有与 两个访问单元中的第一个组成一个互补的非参考场对）
	 */
	public int pic_order_cnt_type;
	/**
	 * 图像顺序数解码过程中的变量 MaxPicOrderCntLsb的值（标准文档的8.2.1节所述），公式如下： MaxPicOrderCntLsb
	 * = 2 ( log2_max_pic_order_cnt_lsb_minus4 + 4 )
	 * log2_max_pic_order_cnt_lsb_minus4 的取值范围应该在0-12内（包括0和12）
	 */
	public int log2_max_pic_order_cnt_lsb_minus4;
	/**
	 * 等于1表示视 频序列的条带头中没有delta_pic_order_cnt[0] 和 delta_pic_order_cnt[1]
	 * 两个字段，它们的值都默认为0。delta_pic_order_always_zero_flag 等于0表示视频序列的
	 * 条带头中包含delta_pic_order_cnt[0]字段，并可能包括delta_pic_order_cnt[1]的字段（也可以不包括）。
	 */
	public int delta_pic_order_always_zero_flag;
	/**
	 * 用于计算非参考图像的图像顺序号（标准文档8.2.1节的规定）。offset_for_non_ref_pic 的取值
	 * 范围是–2^31到2^31–1（包括-2^31和2^31–1）。
	 */
	public int offset_for_non_ref_pic;
	/**
	 * 用于计算一个帧的底场的图像顺序号（标准文档8.2.1节的规定）。offset_for_non_ref_pic 的取值
	 * 范围是–2^31到2^31–1（包括-2^31和2^31–1）。
	 */
	public int offset_for_top_to_bottom_field;
	/**
	 * 图像顺序号的解码过程（标准文档8.2.1节的规定）。offset_for_non_ref_pic 的取值
	 * 范围是–2^31到2^31–1（包括-2^31和2^31–1）。
	 */
	public int num_ref_frames_in_pic_order_cnt_cycle;
	/**
	 * 图像顺序号的解码过程中使用的一个
	 * num_ref_frames_in_pic_order_cnt_cycle值的列表中的一个元素（参照标准文档8.2
	 * .1节规定的）。offset_for_ref_frame[i] 的取值范围是–2^31到2^31–1（包括–2^31和2^31–1）。
	 */
	public int[] offset_for_ref_frame;
	/**
	 * 规定了可能在视频序列中任何图像帧间预测的解码过程中用到的短期参考帧和长期参考
	 * 帧、互补参考场对以及不成对的参考场的最大数量。num_ref_frames字段也决定了8.2.5.3节规定的滑动窗口操作
	 * 的大小。num_ref_frames 的取值范围应该在0到MaxDpbSize （参见标准文档A.3.1和A.3.2节的定义）范围内，包括0和
	 * MaxDpbSize 。
	 */
	public int num_ref_frames;
	/**
	 * frame_num的允许值（标准文档7.4.3节）以及在给出的
	 * frame_num值之间存在推测的差异的情况下进行的解码过程（标准文档8.2.5.2节）。
	 */
	public int gaps_in_frame_num_value_allowed_flag;
	/**
	 * 加1后指以宏块为单元的每个解码图像的宽度。 以宏块为单元的图像宽度变量由下列公式可得： PicWidthInMbs =
	 * pic_width_in_mbs_minus1 + 1; 亮度分量的图像宽度变量由下列公式得出： PicWidthInSamplesL=
	 * PicWidthInMbs * 16;色度分量的图像宽度变量由下列公式得出： PicWidthInSamplesC= PicWidthInMbs
	 * * MbWidthC
	 */
	public int pic_width_in_mbs_minus1;
	/**
	 * 加1表示以条带组映射为单位的一个解码帧或场的高度。 变量PicHeightInMapUnits 和PicSizeInMapUnits
	 * 由下列公式得出： PicHeightInMapUnits = pic_height_in_map_units_minus1 + 1;
	 * PicSizeInMapUnits = PicWidthInMbs * PicHeightInMapUnits
	 */
	public int pic_height_in_map_units_minus1;
	/**
	 * 等于0表示编码视频序列的编码图像可能是编码场或编码帧。frame_mbs_only_flag 等
	 * 于1表示编码视频序列的每个编码图像都是一个仅包含帧宏块的编码帧。 变量pic_width_in_mbs_minus1、
	 * pic_height_in_map_units_minus1和frame_mbs_only_flag的允许取值范围在标准文档附件A中规定。
	 * 变量pic_height_in_map_units_minus1 的语义依赖于变量frame_mbs_only_flag，规定如下： -—
	 * 如果frame_mbs_only_flag 等于0，pic_height_in_map_units_minus1加1就表示以宏块为单位的一场的高
	 * 度。 -— 否则（frame_mbs_only_flag等于1），
	 * pic_height_in_map_units_minus1加1就表示以宏块为单位的一帧的 高度。 变量FrameHeightInMbs
	 * 由下列公式得出： FrameHeightInMbs = (2–frame_mbs_only_flag) * PicHeightInMapUnits
	 */
	public int frame_mbs_only_flag;
	/**
	 * 等于0表示在一个图像的帧和场宏块之间没有交换。 mb_adaptive_frame_field_flag
	 * 等于1表示在帧和帧内的场宏块之间可能会有交换。当mb_adaptive_frame_field_flag没有特别规定时，默认其值为0。
	 */
	public int mb_adaptive_frame_field_flag = 0;
	/**
	 * B_Skip、B_Direct_16x16和B_Direct_8x8亮度运动矢量的计算过程使用的方法（标准文档8.4.1.2节）。
	 * 当frame_mbs_only_flag等于0时direct_8x8_inference_flag应等于1。
	 */
	public int direct_8x8_inference_flag;
	/**
	 * 等于1表示帧剪切偏移参数遵从视频序列参数集中的下一个值。frame_cropping_flag 等 于0表示不存在帧剪切偏移参数。
	 */
	public int frame_cropping_flag;
	/**
	 * frame_crop_left_offset, frame_crop_right_offset, frame_crop_top_offset,
	 * frame_crop_bottom_offset是指从解码过程中输出的编码图像序列中的图像样值以帧坐标中的一个矩阵区域的形式输出。
	 * 变量CropUnitX和CropUnitY由下列公式得出：如果chroma_format_idc 等于0，CropUnitX 和CropUnitY
	 * 按下列方式计算： CropUnitX = 1;CropUnitY = 2 – frame_mbs_only_flag
	 * 否则（chroma_format_idc 等于1、2 或3），CropUnitX 和CropUnitY 按下列公式计算： CropUnitX =
	 * SubWidthC (7-18) CropUnitY = SubHeightC * (2 –frame_mbs_only_flag
	 * )帧剪切矩形包括水平帧坐标从 CropUnitX * frame_crop_left_offset 到 PicWidthInSamplesL– (
	 * CropUnitX * frame_crop_right_offset + 1) 且垂直帧坐标从 CropUnitY *
	 * frame_crop_top_offset 到 (16 * FrameHeightInMbs) – (CropUnitY *
	 * frame_crop_bottom_offset + 1 )的亮度样点。frame_crop_left_offset 的值 应在0 到(
	 * PicWidthInSamplesL/CropUnitX) – (frame_crop_right_offset + 1) 范围内（包括边界值），
	 * frame_crop_top_offset的值应在0到( 16 * FrameHeightInMbs / CropUnitY ) –
	 * (frame_crop_bottom_offset + 1 )范围内 （包括边界值）。
	 * 当frame_cropping_flag等于0时，frame_crop_left_offset、frame_crop_right_offset
	 * 、frame_crop_top_offset和 frame_crop_bottom_offset的值应等于0。
	 * 当chroma_format_idc不等于0时，两个色度队列的相应的指定样点是那些帧坐标为（x / SubWidthC, y /
	 * SubHeightC）的样点，其中（x，y）是指定的亮度样点的帧坐标。 对于解码场，解码场的指定样点是那些帧坐标落在指定的矩形当中的样点。
	 */
	public int frame_crop_left_offset;
	public int frame_crop_right_offset;
	public int frame_crop_top_offset;
	public int frame_crop_bottom_offset;
	/**
	 * 等于1表示存在如附录E提到的vui_parameters( )语法结构。 vui_parameters_present_flag
	 * 等于0表示不存在如附录E提到的vui_parameters( )语法结构。
	 */
	public int vui_parameters_present_flag;

	@Override
	public String toString()
	{
		return "SpsStructure [profile_idc=" + profile_idc
				+ ", constraint_set0_flag=" + constraint_set0_flag
				+ ", constraint_set1_flag=" + constraint_set1_flag
				+ ", constraint_set2_flag=" + constraint_set2_flag
				+ ", constraint_set3_flag=" + constraint_set3_flag
				+ ", reserved_zero_4bits=" + reserved_zero_4bits
				+ ", level_idc=" + level_idc + ", seq_parameter_set_id="
				+ seq_parameter_set_id + ", chroma_format_idc="
				+ chroma_format_idc + ", residual_colour_transform_flag="
				+ residual_colour_transform_flag + ", bit_depth_luma_minus8="
				+ bit_depth_luma_minus8 + ", bit_depth_chroma_minus8="
				+ bit_depth_chroma_minus8
				+ ", qpprime_y_zero_transform_bypass_flag="
				+ qpprime_y_zero_transform_bypass_flag
				+ ", seq_scaling_matrix_present_flag="
				+ seq_scaling_matrix_present_flag
				+ ", seq_scaling_list_present_flag="
				+ Arrays.toString(seq_scaling_list_present_flag)
				+ ", log2_max_frame_num_minus4=" + log2_max_frame_num_minus4
				+ ", pic_order_cnt_type=" + pic_order_cnt_type
				+ ", log2_max_pic_order_cnt_lsb_minus4="
				+ log2_max_pic_order_cnt_lsb_minus4
				+ ", delta_pic_order_always_zero_flag="
				+ delta_pic_order_always_zero_flag
				+ ", offset_for_non_ref_pic=" + offset_for_non_ref_pic
				+ ", offset_for_top_to_bottom_field="
				+ offset_for_top_to_bottom_field
				+ ", num_ref_frames_in_pic_order_cnt_cycle="
				+ num_ref_frames_in_pic_order_cnt_cycle
				+ ", offset_for_ref_frame="
				+ Arrays.toString(offset_for_ref_frame) + ", num_ref_frames="
				+ num_ref_frames + ", gaps_in_frame_num_value_allowed_flag="
				+ gaps_in_frame_num_value_allowed_flag
				+ ", pic_width_in_mbs_minus1=" + pic_width_in_mbs_minus1
				+ ", pic_height_in_map_units_minus1="
				+ pic_height_in_map_units_minus1 + ", frame_mbs_only_flag="
				+ frame_mbs_only_flag + ", mb_adaptive_frame_field_flag="
				+ mb_adaptive_frame_field_flag + ", direct_8x8_inference_flag="
				+ direct_8x8_inference_flag + ", frame_cropping_flag="
				+ frame_cropping_flag + ", frame_crop_left_offset="
				+ frame_crop_left_offset + ", frame_crop_right_offset="
				+ frame_crop_right_offset + ", frame_crop_top_offset="
				+ frame_crop_top_offset + ", frame_crop_bottom_offset="
				+ frame_crop_bottom_offset + ", vui_parameters_present_flag="
				+ vui_parameters_present_flag + "]";
	}
}