package com.xt.mobile.terminal.sip;

import java.util.Arrays;

/**
 * 图像参数集(俗称的pps)
 * 
 * @author luo
 * 
 */
public class PpsStructure
{
	/**
	 * 标识在条带头中提到的图像参数集。变量pic_parameter_set_id 的值应该在0到255的 范围内（包括0和255）。
	 */
	public int pic_parameter_set_id;
	/**
	 * 是指活动的序列参数集。变量seq_parameter_set_id 的值应该在0到31的范围内（包 括0和31）。
	 */
	public int seq_parameter_set_id;
	/**
	 * 用于选取语法元素的熵编码方式，在语法表中由两个标识符代表，具体如下： -— 如果entropy_coding_mode_flag
	 * 等于0，那么采用语法表中左边的描述符所指定的方法（Exp-Golomb编 码，见9.1节，或CAVLC，见9.2节）。 -—
	 * 否则（entropy_coding_mode_flag 等于1），就采用语法表中右边的描述符所指定的方法（CABAC，见 9.3节）。
	 */
	public int entropy_coding_mode_flag;
	/**
	 * 等于1表示与图像顺序数有关的语法元素将出现于条带头中，如第7.3.3节所规定。 pic_order_present_flag
	 * 等于0表示条带头中不会出现与图像顺序数有关的语法元素。
	 */
	public int pic_order_present_flag;
	/**
	 * 加1表示一个图像中的条带组数。当num_slice_groups_minus1 等于0时，图像中
	 * 所有的条带属于同一个条带组。num_slice_groups_minus1 的允许取值范围在附件A中给出。
	 */
	public int num_slice_groups_minus1;
	/**
	 * 表示条带组中条带组映射单元的映射是如何编码的。slice_group_map_type 的取值范围 应该在0到6内（包括0和6）。
	 * slice_group_map_type 等于0表示隔行扫描的条带组。 slice_group_map_type 等于1表示一种分散的条带组映射。
	 * slice_group_map_type 等于2表示一个或多个前景条带组和一个残余条带组。 slice_group_map_type
	 * 的值等于3、4和5表示变换的条带组。当num_slice_groups_minus1 不等于1时， slice_group_map_type
	 * 不应等于3、4或5。 slice_group_map_type 等于6表示对每个条带组映射单元清楚地分配一个条带组。 条带组映射单元规定如下：
	 * –— 如果frame_mbs_only_flag
	 * 等于0，mb_adaptive_frame_field_flag等于1，而且编码图像是一个帧，那么 条带组映射单元就是宏块对单元。 –—
	 * 否则，如果frame_mbs_only_flag等于1或者一个编码图像是一个场，条带组映射单元就是宏块的单元。 –—
	 * 否则（frame_mbs_only_flag 等于0，mb_adaptive_frame_field_flag 等于0，并且编码图像是一个帧），
	 * 条带组映射单元就是像在一个MBAFF帧中的一个帧宏块对中一样垂直相邻的两个宏块的单元。
	 */
	public int slice_group_map_type;
	/**
	 * 用来指定条带组映射单元的光栅扫描顺序中分配给第i 个条带组的连续条带组映射单 元的数目。run_length_minus1[i]
	 * 的取值范围应该在0到PicSizeInMapUnits – 1内（包括边界值）。
	 */
	public int[] run_length_minus1;
	/**
	 * top_left、bottom_right分别表示一个矩形的左上角和右下角。top_left[i] 和bottom_right[i] 是条带
	 * 组映射单元所在图像的光栅扫描中的条带组映射单元位置。对于每个矩形i，语法元素top_left[i] 和 bottom_right[i
	 * ]的值都应该遵从下面所有的规定。 -—top_left[i] 应小于或等于bottom_right[i] 并且bottom_right[i]
	 * 应小于PicSizeInMapUnits。 -— （top_left[ i ] % PicWidthInMbs ）应小于或等于
	 * （bottom_right[i] % PicWidthInMbs ）的值。
	 */
	public int[] top_left;
	public int[] bottom_right;
	/**
	 * 通常与slice_group_map_type 一起用来表示当slice_group_map_type的值为 3、4或5时精确的映射类型。
	 */
	public int slice_group_change_direction_flag;
	/**
	 * 用来指定变量SliceGroupChangeRate。SliceGroupChangeRate 表示一个条带
	 * 组的大小从一个图像到下一个的改变的倍数，以条带组映射单元为单位。slice_group_change_rate_minus1 的值应
	 * 该在0到PicSizeInMapUnits – 1的范围内（包括边界值）。变量SliceGroupChangeRate 规定如下：
	 * SliceGroupChangeRate = slice_group_change_rate_minus1 + 1
	 */
	public int slice_group_change_rate_minus1;
	/**
	 * 用来指定图像中的条带组映射单元数。pic_size_in_map_units_minus1 应该等 于PicSizeInMapUnits – 1。
	 */
	public int pic_size_in_map_units_minus1;
	/**
	 * 表示光栅扫描顺序中的第i 个条带组映射单元的一个条带组。slice_group_id[ i ] 语法元素 的大小是Ceil( Log2(
	 * num_slice_groups_minus1 + 1 ) ) 比特。slice_group_id[ i ] 的值应该在0 到
	 * num_slice_groups_minus1范围内（包括边界值）。
	 */
	public int[] slice_group_id;
	/**
	 * 表示参考图像列表0的最大参考索引号，该索引号将用来在一幅图像中
	 * num_ref_idx_active_override_flag等于0的条带使用列表0预测时，解码该图像的这些条带。当MbaffFrameFlag
	 * 等于1时，num_ref_idx_l0_active_minus1 是帧宏块解码的最大索引号值，而2 *
	 * num_ref_idx_l0_active_minus1 + 1
	 * 是场宏块解码的最大索引号值。num_ref_idx_l0_active_minus1 的值应该在0到31的范围内（包括0和31）。
	 */
	public int num_ref_idx_l0_active_minus1;
	/**
	 * 与num_ref_idx_l0_active_minus1 具有同样的定义，只是分别用11和列表1 取代10和列表0。
	 */
	public int num_ref_idx_l1_active_minus1;
	/**
	 * 等于0表示加权的预测不应用于P和SP条带。weighted_pred_flag等于1表示在P和SP 条带中应使用加权的预测。
	 */
	public int weighted_pred_flag;
	/**
	 * 等于0表示B条带应该采用默认的加权预测。weighted_bipred_idc等于1表示B条带应 该采用具
	 * 体指明的加权预测。weighted_bipred_idc等于2 表示B条带应该采用隐含的加权预测。 weighted_bipred_idc
	 * 的值应该在0到2之间（包括0和2）。
	 */
	public int weighted_bipred_idc;
	/**
	 * 表示每个条带的SliceQPY 初始值减26。当解码非0值的slice_qp_delta 时，该初始值在
	 * 条带层被修正，并且在宏块层解码非0值的mb_qp_delta 时进一步被修正。pic_init_qp_minus26 的值应该在－ (26 +
	 * QpBdOffsetY) 到+25之间（包括边界值）。
	 */
	public int pic_init_qp_minus26;
	/**
	 * 表示在SP或SI条带中的所有宏块的SliceQSY 初始值减26。当解码非0值的 slice_qs_delta
	 * 时，该初始值在条带层被修正。pic_init_qs_minus26 的值应该在-26 到+25之间（包括边界值）。
	 */
	public int pic_init_qs_minus26;
	/**
	 * 表示为在QPC 值的表格中寻找Cb色度分量而应加到参数QPY 和QSY 上的偏移。 chroma_qp_index_offset 的值应在-12
	 * 到+12范围内（包括边界值）。
	 */
	public int chroma_qp_index_offset;
	/**
	 * 于1表示控制去块效应滤波器的特征的一组语法元素将出现在条带 头中。deblocking_filter_control_present_flag
	 * 等于0表示控制去块效应滤波器的特征的一组语法元素不会出现在条 带头中，并且它们的推定值将会生效。
	 */
	public int deblocking_filter_control_present_flag;
	/**
	 * 等于0表示帧内预测允许使用残余数据，且使用帧内宏块预测模式编码的宏块
	 * 的预测可以使用帧间宏块预测模式编码的相邻宏块的解码样值。constrained_intra_pred_flag 等于1表示受限制的
	 * 帧内预测，在这种情况下，使用帧内宏块预测模式编码的宏块的预测仅使用残余数据和来自I或SI宏块类型的解 码样值。
	 */
	public int constrained_intra_pred_flag;
	/**
	 * 等于0表示redundant_pic_cnt 语法元素不会在条带头、图像参数集中指明
	 * （直接或与相应的数据分割块A关联）的数据分割块B和数据分割块C中出现。redundant_pic_cnt_present_flag等
	 * 于1表示redundant_pic_cnt 语法元素将出现在条带头、图像参数集中指明（直接或与相应的数据分割块A关联）
	 * 的数据分割块B和数据分割块C中。
	 */
	public int redundant_pic_cnt_present_flag;
	/**
	 * 等于1 表示8x8 变换解码过程可能正在使用（参见8.5节）。
	 * transform_8x8_mode_flag等于0表示未使用8x8变换解码过程。当transform_8x8_mode_flag
	 * 不存在时，默认其值 为0。
	 */
	public int transform_8x8_mode_flag = 0;
	/**
	 * 等于1表示存在用来修改在序列参数集中指定的缩放比例列表的参数。 pic_scaling_matrix_present_flag
	 * 等于0表示用于该图像中的缩放比例列表应等于那些由序列参数集规定的。当
	 * pic_scaling_matrix_present_flag不存在时，默认其值为0。
	 */
	public int pic_scaling_matrix_present_flag = 0;
	/**
	 * 等于1表示存在缩放比例列表的语法结构并用于指定序号为i 的缩放比例列 表。pic_scaling_list_present_flag[ i
	 * ]等于0表示在图像参数集中不存在缩放比例列表i 的语法结构，并且根据
	 * seq_scaling_matrix_present_flag的值，应用下列条款： –—
	 * 如果seq_scaling_matrix_present_flag 等于0，表7-2中指定的缩放比例列表后退规则集A应用于获取序号
	 * 为i的图像级缩放比例列表。 –— 否则（seq_scaling_matrix_present_flag
	 * 等于1），表7-2中指定的缩放比例列表后退规则集B应用来获取 序号为i的图像级缩放比例列表。
	 */
	public int[] pic_scaling_list_present_flag;
	/**
	 * 表示为在QPC 值的表格中寻找Cr色度分量而应加到参数QPY 和QSY 上的 偏移。second_chroma_qp_index_offset
	 * 的值应在-12 到+12范围内（包括边界值）。
	 * 当second_chroma_qp_index_offset不存在时，默认其值等于chroma_qp_index_offset。
	 */
	public int second_chroma_qp_index_offset;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "PpsStructure [pic_parameter_set_id=" + pic_parameter_set_id
				+ ", seq_parameter_set_id=" + seq_parameter_set_id
				+ ", entropy_coding_mode_flag=" + entropy_coding_mode_flag
				+ ", pic_order_present_flag=" + pic_order_present_flag
				+ ", num_slice_groups_minus1=" + num_slice_groups_minus1
				+ ", slice_group_map_type=" + slice_group_map_type
				+ ", run_length_minus1=" + Arrays.toString(run_length_minus1)
				+ ", top_left=" + Arrays.toString(top_left) + ", bottom_right="
				+ Arrays.toString(bottom_right)
				+ ", slice_group_change_direction_flag="
				+ slice_group_change_direction_flag
				+ ", slice_group_change_rate_minus1="
				+ slice_group_change_rate_minus1
				+ ", pic_size_in_map_units_minus1="
				+ pic_size_in_map_units_minus1 + ", slice_group_id="
				+ Arrays.toString(slice_group_id)
				+ ", num_ref_idx_l0_active_minus1="
				+ num_ref_idx_l0_active_minus1
				+ ", num_ref_idx_l1_active_minus1="
				+ num_ref_idx_l1_active_minus1 + ", weighted_pred_flag="
				+ weighted_pred_flag + ", weighted_bipred_idc="
				+ weighted_bipred_idc + ", pic_init_qp_minus26="
				+ pic_init_qp_minus26 + ", pic_init_qs_minus26="
				+ pic_init_qs_minus26 + ", chroma_qp_index_offset="
				+ chroma_qp_index_offset
				+ ", deblocking_filter_control_present_flag="
				+ deblocking_filter_control_present_flag
				+ ", constrained_intra_pred_flag="
				+ constrained_intra_pred_flag
				+ ", redundant_pic_cnt_present_flag="
				+ redundant_pic_cnt_present_flag + ", transform_8x8_mode_flag="
				+ transform_8x8_mode_flag
				+ ", pic_scaling_matrix_present_flag="
				+ pic_scaling_matrix_present_flag
				+ ", pic_scaling_list_present_flag="
				+ Arrays.toString(pic_scaling_list_present_flag)
				+ ", second_chroma_qp_index_offset="
				+ second_chroma_qp_index_offset + "]";
	}
}