# 数据库表字段信息

## 项目表（project）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 项目编号 | `project_code` | 项目名称 | `project_name` |
| 城市 | `city` | 行政区 | `district` | 片区 | `area` |
| 地址 | `address` | 登记人 | `registrar` | 登记日期 | `registration_date` |
| 委托方联系人 | `client_contact` | 委托方电话 | `client_phone` | 估价目的 | `valuation_purpose` |
| 估价时点 | `valuation_time` | 期望价格 | `expected_price` | 状态（未评估、已评估、已出报告、已结款） | `status` |
| 备注 | `remark` | 创建时间 | `created_at` | 更新时间 | `updated_at` |
| 估值单价 | `valuation_unit_price` | 估值总价 | `valuation_total_price` | 建筑面积 | `building_area` |
| 委托单位/委托人 | `client_name` | 抵押人姓名/名称 | `mortgagor_name` | 抵押人证件号 | `mortgagor_id_card` |
| 抵押人电话 | `mortgagor_phone` | 借款人姓名/名称 | `borrower_name` | 借款人证件号 | `borrower_id_card` |
| 估值类型（人工估值、自动估值） | `valuation_type` |  |  |  |  |

## 抵押物表（collateral）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 项目ID | `project_id` | 抵押物编号 | `collateral_code` |
| 抵押物类型 | `collateral_type` | 抵押物名称 | `collateral_name` | 抵押物地址 | `collateral_address` |
| 建筑面积 | `building_area` | 土地面积 | `land_area` | 小区名称 | `community_name` |
| 楼栋 | `building` | 单元 | `unit_name` | 门牌号 | `door_number` |
| 建成年份 | `build_year` | 建设用地面积 | `construction_land` | 取得土地面积 | `land_acquisition` |
| 容积率 | `floor_area_ratio` | 地上比率 | `above_ground_ratio` | 人防面积 | `civil_defense_area` |
| 地下比率 | `underground_ratio` | 绿化率 | `greening_rate` | 建筑密度 | `building_density` |
| 建筑高度 | `building_height` | 楼层数 | `floor_count` | 户数 | `household_count` |
| 车位数 | `parking_count` | 车位配比率 | `parking_ratio` | 竣工日期 | `completion_date` |
| 产权年限 | `property_rights_years` | 土地使用年限 | `land_use_years` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` | 是否主抵押物 | `is_primary` | 实际用途 | `actual_use` |
| 占用状态 | `occupancy_status` | 装修情况 | `decoration` | 朝向 | `orientation` |
| 所在层 | `current_floor` | 室内层高 | `indoor_height` | 空间布局 | `space_layout` |
| 设施设备 | `facilities_condition` | 维护养护情况及完损程度 | `maintenance_condition` | 所在宗地形状 | `parcel_shape` |
| 地形地貌 | `terrain` | 地势 | `land_level` | 土壤情况 | `soil_condition` |
| 土地开发程度 | `land_development_level` | 景观 | `landscape` | 周边环境 | `surrounding_environment` |

## 权属信息表（ownership_info）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 项目ID | `project_id` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` | 权利人 | `right_holder` | 权利证号 | `right_certificate_number` |
| 借款人姓名 | `borrower_name` | 借款人身份证 | `borrower_id_card` | 房屋结构 | `building_structure` |
| 用途 | `usage` | 所在层 | `current_floor` | 总楼层 | `total_floors` |
| 权利性质 | `right_nature` | 权利类型 | `right_type` | 共有情况 | `co_ownership` |
| 土地使用年限 | `land_use_years` | 不动产单元编号 | `property_unit_number` | 共有宗地面积 | `shared_land_area` |
| 分摊土地面积 | `allocated_land_area` | 建成年代 | `build_year` | 建成年代来源 | `build_year_source` |
| 办理网签日期 | `online_signing_date` | 合同编号 | `contract_number` | 报告出具日期 | `report_issue_date` |
| 价值时点 | `valuation_time_point` | 是否完成老旧小区改造 | `old_community_renovation` | 区域繁华度 | `area_prosperity` |
| 市场繁华度 | `market_prosperity` | 房屋所有权证 | `house_ownership_certificate` | 国有土地使用权证号 | `state_land_use_certificate_number` |
| 土地用途 | `land_use` | 丘权号 | `qiu_quan_number` | 土地使用权面积 | `land_use_area` |
| 证载坐落 | `registered_address` | 证载建筑面积 | `registered_building_area` | 权利状态 | `right_status` |
| 权利登记日期 | `right_registration_date` | 权利注销日期 | `right_cancellation_date` | 产权来源 | `property_source` |
| 土地使用权来源 | `land_use_right_source` | 土地使用开始日期 | `land_use_start_date` | 土地使用终止日期 | `land_use_end_date` |
| 抵押信息 | `mortgage_info` | 查封信息 | `seizure_info` | 租赁限制 | `lease_restriction` |
| 其他权属信息 | `other_rights_info` | 备注 | `remark` | 实际用途 | `actual_use` |
| 装修情况 | `decoration` |  |  |  |  |

## 估价报告表（valuation_report）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 项目ID | `project_id` | 报告编号 | `report_code` |
| 开始时间 | `start_time` | 结束时间 | `end_time` | 单价 | `unit_price` |
| 估价结果 | `valuation_result` | 区域评估 | `area_evaluation` | 周边成交 | `surrounding_transactions` |
| 变现能力分析 | `liquidity_analysis` | 户型分析 | `floor_plan` | 土地出让金扣除 | `land_grant_deduction` |
| 装修成新率 | `decoration_new_rate` | 设备成新率 | `equipment_new_rate` | 报告文件URL | `report_url` |
| 银行建议 | `bank_suggestion` | 土地宗地图 | `land_plot` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` | 总价（万元） | `total_price` | 抵押价值（万元） | `mortgage_value` |
| 法定优先受偿款（元） | `priority_compensation_amount` | 法定优先受偿款说明 | `priority_compensation_description` | 价值时点 | `value_date` |
| 报告出具日期 | `report_issue_date` | 报告有效期开始日期 | `valid_start_date` | 报告有效期结束日期 | `valid_end_date` |
| 估价师1姓名 | `valuer1_name` | 估价师1证号 | `valuer1_cert_no` | 估价师2姓名 | `valuer2_name` |
| 估价师2证号 | `valuer2_cert_no` |  |  |  |  |

## 估价方法表（valuation_method）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 方法编号 | `method_code` | 方法名称 | `method_name` |
| 权重 | `weight` | 单价 | `unit_price` | 估价师签名 | `appraiser_signature` |
| 方法描述 | `description` | 报告ID | `report_id` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` |  |  |  |  |

## 报告盖章表（report_seal）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 报告ID | `report_id` | 项目ID | `project_id` |
| 盖章报告URL | `sealed_report_url` | 盖章人 | `sealer` | 盖章日期 | `seal_date` |
| 创建时间 | `created_at` | 更新时间 | `updated_at` |  |  |

## 报告审核表（report_review）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 项目ID | `project_id` | 报告ID | `report_id` |
| 审核人 | `reviewer` | 审核日期 | `review_date` | 审核意见 | `review_opinion` |
| 审核结果 | `review_result` | 创建时间 | `created_at` | 更新时间 | `updated_at` |

## 外勘记录表（survey）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 项目ID | `project_id` | 外勘编号 | `survey_code` |
| 勘查人 | `surveyor` | 接待人 | `receptionist` | 接待人电话 | `receptionist_phone` |
| 勘查日期 | `survey_date` | 开始时间 | `start_time` | 结束时间 | `end_time` |
| 是否验看房产证 | `property_cert_verified` | 权属争议 | `ownership_dispute` | 备注 | `remark` |
| 创建时间 | `created_at` | 更新时间 | `updated_at` | 4位外勘码 | `code` |
| 外勘状态 | `survey_status` |  |  |  |  |

## 外勘照片表（survey_photo）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 项目ID | `project_id` | 外勘ID | `survey_id` |
| 照片编号 | `photo_code` | 照片路径 | `photo_path` | 照片描述 | `photo_description` |
| 创建时间 | `created_at` | 更新时间 | `updated_at` | 照片分类 | `photo_category` |

## 估值价格表（valuation_price）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 城市 | `city` | 行政区 | `district` |
| 地址 | `address` | 单价 | `unit_price` | 总价 | `total_price` |
| 面积 | `area` | 估价时点 | `valuation_time` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` |  |  |  |  |

## 复估记录表（revaluation_record）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 复估日期 | `revaluation_date` | 复估结果（进行中、已完成） | `result` |
| 文件URL | `file_url` | 备注 | `remark` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` | 机构ID | `organization_id` |  |  |

## 复估项目明细表（revaluation_project）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 复估ID | `revaluation_id` | 项目编号 | `project_code` |
| 单价 | `unit_price` | 总价 | `total_price` | 备注 | `remark` |
| 创建时间 | `created_at` | 更新时间 | `updated_at` |  |  |

## 对账记录表（reconciliation_record）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 机构ID | `organization_id` | 开始时间 | `start_time` |
| 结束时间 | `end_time` | 对账日期 | `reconciliation_date` | 对账结果（进行中、已完成） | `result` |
| 文件URL | `file_url` | 备注 | `remark` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` |  |  |  |  |

## 报告模板表（report_template）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 模板名称 | `template_name` | 模板内容 | `template_content` |
| 状态 | `status` | 创建时间 | `created_at` | 更新时间 | `updated_at` |

## 机构表（sys_organization）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 机构ID | `id` | 机构类型 | `organization_type` | 机构名称 | `organization_name` |
| 机构联系人 | `contact_name` | 机构联系人电话 | `contact_phone` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` | 报告模板ID | `report_template_id` |  |  |

## 用户表（sys_user）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 用户名 | `username` | 密码 | `password` |
| 邮箱 | `email` | 手机号 | `phone` | 昵称 | `nickname` |
| 状态（1启用、0禁用） | `status` | 创建时间 | `created_at` | 更新时间 | `updated_at` |
| 所属机构ID | `organization_id` |  |  |  |  |

## 角色表（sys_role）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 角色名称 | `role_name` | 角色编码 | `role_code` |
| 角色描述 | `description` | 创建时间 | `created_at` | 更新时间 | `updated_at` |

## 权限表（sys_permission）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 权限名称 | `perm_name` | 权限编码 | `perm_code` |
| 父权限ID | `parent_id` | 类型（MENU、BUTTON） | `type` | 前端路由路径 | `path` |
| 图标 | `icon` | 排序号 | `sort_order` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` |  |  |  |  |

## 用户角色关联表（sys_user_role）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 用户ID | `user_id` | 角色ID | `role_id` |

## 角色权限关联表（sys_role_permission）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 角色ID | `role_id` | 权限ID | `permission_id` |

## 通知消息表（sys_notification）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 接收用户ID | `user_id` | 通知标题 | `title` |
| 通知内容 | `content` | 是否已读 | `is_read` | 创建时间 | `created_at` |
| 更新时间 | `updated_at` | 发送用户ID | `sender_id` |  |  |

## 文件记录表（sys_file_record）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 原始文件名 | `original_name` | 存储文件名 | `stored_name` |
| 文件路径 | `file_path` | 文件大小 | `file_size` | 文件类型 | `content_type` |
| 上传用户ID | `upload_user_id` | 创建时间 | `created_at` | 更新时间 | `updated_at` |

## 省份字典表（d_province）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 编码 | `code` | 名称 | `name` |
| 父级ID | `parent_id` |  |  |  |  |

## 城市字典表（d_city）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 省份ID | `province_id` | 编码 | `code` |
| 名称 | `name` | 父级ID | `parent_id` | 类型 | `type` |

## 行政区字典表（d_district）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 编码 | `code` | 名称 | `name` |
| 城市ID | `city_id` | 父级ID | `parent_id` |  |  |

## 街道/片区字典表（d_prestreet）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 主键ID | `id` | 编码 | `code` | 名称 | `name` |
| 城市ID | `city_id` | 类型 | `type` | 行政区ID | `district_id` |
| 父级ID | `parent_id` |  |  |  |  |

## Flyway迁移历史表（flyway_schema_history）

| 中文名 | 字段名 | 中文名 | 字段名 | 中文名 | 字段名 |
|---|---|---|---|---|---|
| 安装序号 | `installed_rank` | 版本 | `version` | 描述 | `description` |
| 类型 | `type` | 脚本 | `script` | 校验和 | `checksum` |
| 安装用户 | `installed_by` | 安装时间 | `installed_on` | 执行耗时 | `execution_time` |
| 是否成功 | `success` |  |  |  |  |
