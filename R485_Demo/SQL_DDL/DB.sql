CREATE TABLE `t_request_record` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `reqsno` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请求报文',
                                    `respsno` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '响应报文',
                                    `createtime` datetime DEFAULT NULL COMMENT '创建时间',
                                    `updatetime` datetime DEFAULT NULL COMMENT '更新时间',
                                    `status` int DEFAULT NULL COMMENT '状态码 0初始状态；1成功',
                                    `event_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事件类型',
                                    `device_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '设备类型',
                                    `two_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '二次码',
                                    `one_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '一次码',
                                    `control_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '控制器码（仅适用于一次码）',
                                    `back_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '回路号（仅适用于一次码）',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;