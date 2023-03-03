package com.agileboot.domain.system.user.dto;

import cn.hutool.core.bean.BeanUtil;
import com.agileboot.common.annotation.ExcelColumn;
import com.agileboot.common.annotation.ExcelSheet;
import com.agileboot.infrastructure.cache.CacheCenter;
import com.agileboot.orm.system.entity.SysDeptEntity;
import com.agileboot.orm.system.entity.SysUserEntity;
import com.agileboot.orm.system.result.SearchUserDO;
import java.util.Date;
import lombok.Data;

/**
 * @author valarchie
 */
@ExcelSheet(name = "用户列表")
@Data
public class UserDTO {

    public UserDTO(SysUserEntity entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity, this);

            SysDeptEntity dept = CacheCenter.deptCache.get(entity.getDeptId() + "");
            if (dept != null) {
                this.deptName = dept.getDeptName();
            }

            SysUserEntity creator = CacheCenter.userCache.getObjectById(entity.getCreatorId());
            if (creator != null) {
                this.creatorName = creator.getUsername();
            }

        }
    }

    public UserDTO(SearchUserDO entity) {
        if (entity != null) {
            BeanUtil.copyProperties(entity, this);
        }
    }


    @ExcelColumn(name = "用户ID")
    private Long userId;

    @ExcelColumn(name = "职位ID")
    private Long postId;

    @ExcelColumn(name = "角色ID")
    private Long roleId;

    @ExcelColumn(name = "部门ID")
    private Long deptId;

    @ExcelColumn(name = "部门名称")
    private String deptName;

    @ExcelColumn(name = "用户名")
    private String username;

    @ExcelColumn(name = "用户昵称")
    private String nickName;

    @ExcelColumn(name = "用户类型")
    private Integer userType;

    @ExcelColumn(name = "邮件")
    private String email;

    @ExcelColumn(name = "号码")
    private String phoneNumber;

    @ExcelColumn(name = "性别")
    private Integer sex;

    @ExcelColumn(name = "用户头像")
    private String avatar;

    @ExcelColumn(name = "状态")
    private Integer status;

    @ExcelColumn(name = "IP")
    private String loginIp;

    @ExcelColumn(name = "登录时间")
    private Date loginDate;

    @ExcelColumn(name = "创建者ID")
    private Long creatorId;

    @ExcelColumn(name = "创建者")
    private String creatorName;

    @ExcelColumn(name = "创建时间")
    private Date createTime;

    @ExcelColumn(name = "修改者ID")
    private Long updaterId;

    @ExcelColumn(name = "修改者")
    private String updaterName;

    @ExcelColumn(name = "修改时间")
    private Date updateTime;

    @ExcelColumn(name = "备注")
    private String remark;

}
