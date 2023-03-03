package com.agileboot.admin.controller.system;

import cn.hutool.core.lang.tree.Tree;
import com.agileboot.common.core.base.BaseController;
import com.agileboot.common.core.dto.ResponseDTO;
import com.agileboot.domain.common.dto.TreeSelectedDTO;
import com.agileboot.domain.system.dept.DeptApplicationService;
import com.agileboot.domain.system.dept.command.AddDeptCommand;
import com.agileboot.domain.system.dept.command.UpdateDeptCommand;
import com.agileboot.domain.system.dept.dto.DeptDTO;
import com.agileboot.domain.system.dept.query.DeptQuery;
import com.agileboot.infrastructure.annotations.AccessLog;
import com.agileboot.orm.common.enums.BusinessTypeEnum;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 部门信息
 *
 * @author valarchie
 */
@RestController
@RequestMapping("/system/dept")
@Validated
@RequiredArgsConstructor
public class SysDeptController extends BaseController {

    @NonNull
    private DeptApplicationService deptApplicationService;

    /**
     * 获取部门列表
     */
    @PreAuthorize("@permission.has('system:dept:list')")
    @GetMapping("/list")
    public ResponseDTO<List<DeptDTO>> list(DeptQuery query) {
        List<DeptDTO> deptList = deptApplicationService.getDeptList(query);
        return ResponseDTO.ok(deptList);
    }

    /**
     * 查询部门列表（排除当前部门，比如在修改部门的上级部门的时候，需要排除自身当前的部门，因为上级部门不能选自己）
     */
    @PreAuthorize("@permission.has('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public ResponseDTO<List<DeptDTO>> excludeCurrentDeptItself(@PathVariable(value = "deptId", required = false) Long deptId) {
        DeptQuery query = new DeptQuery();
        query.setDeptId(deptId);
        query.setExcludeCurrentDept(true);

        List<DeptDTO> deptList = deptApplicationService.getDeptList(query);
        return ResponseDTO.ok(deptList);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @PreAuthorize("@permission.has('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public ResponseDTO<DeptDTO> getInfo(@PathVariable Long deptId) {
        DeptDTO dept = deptApplicationService.getDeptInfo(deptId);
        return ResponseDTO.ok(dept);
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/dropdownList")
    public ResponseDTO<List<Tree<Long>>> dropdownList() {
        List<Tree<Long>> deptTree = deptApplicationService.getDeptTree();
        return ResponseDTO.ok(deptTree);
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/dropdownList/role/{roleId}")
    public ResponseDTO<TreeSelectedDTO> dropdownListForRole(@PathVariable("roleId") Long roleId) {
        TreeSelectedDTO deptTreeForRole = deptApplicationService.getDeptTreeForRole(roleId);
        return ResponseDTO.ok(deptTreeForRole);
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@permission.has('system:dept:add')")
    @AccessLog(title = "部门管理", businessType = BusinessTypeEnum.ADD)
    @PostMapping
    public ResponseDTO<Void> add(@RequestBody AddDeptCommand addCommand) {
        deptApplicationService.addDept(addCommand);
        return ResponseDTO.ok();
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@permission.has('system:dept:edit') AND @dataScope.checkDeptId(#updateCommand.deptId)")
    @AccessLog(title = "部门管理", businessType = BusinessTypeEnum.MODIFY)
    @PutMapping
    public ResponseDTO<Void> edit(@RequestBody UpdateDeptCommand updateCommand) {
        deptApplicationService.updateDept(updateCommand);
        return ResponseDTO.ok();
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@permission.has('system:dept:remove') AND @dataScope.checkDeptId(#deptId)")
    @AccessLog(title = "部门管理", businessType = BusinessTypeEnum.DELETE)
    @DeleteMapping("/{deptId}")
    public ResponseDTO<Void> remove(@PathVariable @NotNull Long deptId) {
        deptApplicationService.removeDept(deptId);
        return ResponseDTO.ok();
    }
}
