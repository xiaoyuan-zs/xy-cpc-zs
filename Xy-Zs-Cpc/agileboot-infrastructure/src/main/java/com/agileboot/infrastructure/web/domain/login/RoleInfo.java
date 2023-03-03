package com.agileboot.infrastructure.web.domain.login;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.agileboot.orm.common.enums.DataScopeEnum;
import com.agileboot.orm.common.util.BasicEnumUtil;
import com.agileboot.orm.system.entity.SysRoleEntity;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.SetUtils;

/**
 * @author valarchie
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleInfo {

    public static final RoleInfo EMPTY_ROLE = new RoleInfo();
    public static final long ADMIN_ROLE_ID = -1;
    public static final String ADMIN_ROLE_KEY = "admin";
    public static final String ALL_PERMISSIONS = "*:*:*";

    public static final Set<String> ADMIN_PERMISSIONS = SetUtils.hashSet(ALL_PERMISSIONS);


    public RoleInfo(Long roleId, String roleKey, DataScopeEnum dataScope, Set<Long> deptIdSet,
        Set<String> menuPermissions, Set<Long> menuIds) {
        this.roleId = roleId;
        this.roleKey = roleKey;
        this.dataScope = dataScope;
        this.deptIdSet = deptIdSet;
        this.menuPermissions = menuPermissions != null ? menuPermissions : SetUtils.emptySet();
        this.menuIds = menuIds != null ? menuIds : SetUtils.emptySet();
    }


    @Deprecated
    public RoleInfo(SysRoleEntity entity, String roleKey, Set<String> menuPermissions, Set<Long> menuIds) {
        if (entity != null) {
            this.roleId = entity.getRoleId();
            this.roleName = entity.getRoleName();
            this.dataScope = BasicEnumUtil.fromValue(DataScopeEnum.class, entity.getDataScope());

            if(StrUtil.isNotEmpty(entity.getDeptIdSet())) {
                this.deptIdSet = StrUtil.split(entity.getDeptIdSet(), ",").stream()
                    .map(Convert::toLong).collect( Collectors.toSet());
            }

            this.roleKey = roleKey;
            this.menuPermissions = menuPermissions != null ? menuPermissions : SetUtils.emptySet();
            this.menuIds = menuIds != null ? menuIds : SetUtils.emptySet();
        }
    }

    private Long roleId;
    private String roleName;
    private DataScopeEnum dataScope;
    private Set<Long> deptIdSet;
    private String roleKey;
    private Set<String> menuPermissions;
    private Set<Long> menuIds;

}
