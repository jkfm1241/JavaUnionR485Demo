package org.jkfm1241.com.r485_demo.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 * 
 * @TableName t_request_record
 */
@Table(name="t_request_record")
@Data
public class TRequestRecord implements Serializable {
    /**
     * 
     */
    @Id
    @TableId(value = "id",type = IdType.AUTO) // https://blog.csdn.net/gb4215287/article/details/126471771
    private Long id;

    /**
     * 请求报文
     */
    private String reqsno;

    /**
     * 响应报文
     */
    private String respsno;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 状态码 0初始状态；1成功
     */
    private Integer status;

    /**
     * 事件类型
     */
    private String event_type;
    /**
     * 设备类型
     */
    private String device_type;
    /**
     * 二次码
     */
    private String two_code;
    /**
     * 一次码
     */
    private String one_code;
    /**
     * 控制器码（仅适用于一次码）
     */
    private String control_no;

    /**
     * 回路号（仅适用于一次码）
     */
    private String back_no;

    private static final long serialVersionUID = 1L;


}