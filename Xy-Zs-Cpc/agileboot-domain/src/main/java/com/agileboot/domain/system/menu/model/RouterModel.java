package com.agileboot.domain.system.menu.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.agileboot.common.constant.Constants;
import com.agileboot.domain.system.menu.dto.MetaDTO;
import com.agileboot.domain.system.menu.dto.RouterDTO;
import com.agileboot.orm.common.enums.MenuComponentEnum;
import com.agileboot.orm.common.enums.MenuTypeEnum;
import com.agileboot.orm.system.entity.SysMenuEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author valarchie
 */
public class RouterModel extends SysMenuEntity {

    public RouterDTO produceMultipleLevelMenuRouterVO(List<RouterDTO> children) {
        RouterDTO router = produceDefaultRouterVO();

        if (CollUtil.isNotEmpty(children) && Objects.equals(MenuTypeEnum.DIRECTORY.getValue(), getMenuType())) {
            router.setAlwaysShow(true);
            router.setRedirect("noRedirect");
            router.setChildren(children);
        }

        return router;
    }


    public RouterDTO produceSingleLevelMenuRouterVO() {
        RouterDTO router = produceDefaultRouterVO();

        router.setMeta(null);
        List<RouterDTO> childrenList = new ArrayList<>();
        RouterDTO children = new RouterDTO();
        children.setPath(getPath());
        children.setComponent(getComponent());
        children.setName(StrUtil.upperFirst(getPath()));
        children.setMeta(new MetaDTO(getMenuName(), getIcon(), !getIsCache(), getPath()));
        children.setQuery(getQuery());
        childrenList.add(children);
        router.setChildren(childrenList);

        return router;
    }


    public RouterDTO produceInnerLinkRouterVO() {

        RouterDTO router = produceDefaultRouterVO();

        router.setMeta(new MetaDTO(getMenuName(), getIcon()));
        router.setPath("/");
        List<RouterDTO> childrenList = new ArrayList<>();
        RouterDTO children = new RouterDTO();
        String routerPath = trimHttpPrefixForPath(getPath());
        children.setPath(routerPath);
        children.setComponent(MenuComponentEnum.INNER_LINK.description());
        children.setName(StrUtil.upperFirst(routerPath));
        children.setMeta(new MetaDTO(getMenuName(), getIcon(), getPath()));
        childrenList.add(children);
        router.setChildren(childrenList);

        return router;
    }

    public RouterDTO produceDefaultRouterVO() {
        RouterDTO router = new RouterDTO();
        router.setHidden(!getIsVisible());
        router.setName(calculateRouteName());
        router.setPath(calculateRouterPath());
        router.setComponent(calculateComponentType());
        router.setQuery(getQuery());
        router.setMeta(new MetaDTO(getMenuName(), getIcon(), !getIsCache(), getPath()));
        return router;
    }


    /**
     * ??????????????????
     * @return ????????????
     */
    public String calculateRouteName() {
        String routerName = StrUtil.upperFirst(getPath());
        // ???????????????????????????????????????????????????
        if (isSingleLevelMenu()) {
            routerName = StrUtil.EMPTY;
        }
        return routerName;
    }


    /**
     * ???????????????????????????
     *
     * @return ??????
     */
    public boolean isSingleLevelMenu() {
        return isTopLevel() && MenuTypeEnum.MENU.getValue().equals(getMenuType()) && !getIsExternal();
    }

    /**
     * ?????????????????????????????????
     *
     * @return ??????
     */
    public boolean isTopInnerLink() {
        return isTopLevel() && isInnerLink();
    }


    /**
     * ?????????????????????
     *
     * @return ??????
     */
    public boolean isMultipleLevelMenu(Tree<Long> tree) {
        return MenuTypeEnum.DIRECTORY.getValue().equals(getMenuType()) && tree.hasChild();
    }


    /**
     * ??????????????????
     * @return ????????????
     */
    public String calculateRouterPath() {
        String routerPath = getPath();
        // ????????????????????????
        if (!isTopLevel() && isInnerLink()) {
            routerPath = trimHttpPrefixForPath(routerPath);
        }
        // ???????????????????????????????????????????????????
        if (isTopLevel() && Objects.equals(MenuTypeEnum.DIRECTORY.getValue(), getMenuType()) && !getIsExternal()) {
            routerPath = "/" + getPath();
        // ???????????????????????????????????????????????????
        } else if (isSingleLevelMenu()) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * ?????????????????????
     *
     * @return ??????
     */
    public boolean isInnerLink() {
        return !getIsExternal() && (HttpUtil.isHttp(getPath()) || HttpUtil.isHttps(getPath()));
    }

    /**
     * ??????????????????????????????
     *
     * @return ??????
     */
    public boolean isTopLevel() {
        return Objects.equals(getParentId(), 0L);
    }


    /**
     * ??????????????????????????????
     */
    public String trimHttpPrefixForPath(String path) {
        if (HttpUtil.isHttp(path)) {
            return StrUtil.stripIgnoreCase(path, Constants.HTTP, "");
        }
        if (HttpUtil.isHttps(path)) {
            return StrUtil.stripIgnoreCase(path, Constants.HTTPS, "");
        }
        return path;
    }

    /**
     * ??????????????????
     *
     * @return ????????????
     */
    public String calculateComponentType() {
        String component = MenuComponentEnum.LAYOUT.description();
        if (StrUtil.isNotEmpty(getComponent()) && !isSingleLevelMenu()) {
            component = getComponent();
        } else if (isInnerLinkView()) {
            component = MenuComponentEnum.INNER_LINK.description();
        } else if (isParentView()) {
            component = MenuComponentEnum.PARENT_VIEW.description();
        }
        return component;
    }

    /**
     * ?????????inner_link_view??????
     *
     * @return ??????
     */
    public boolean isInnerLinkView() {
        return StrUtil.isEmpty(getComponent()) && !isTopLevel() && isInnerLink();
    }


    /**
     * ?????????parent_view??????
     *
     * @return ??????
     */
    public boolean isParentView() {
        return StrUtil.isEmpty(getComponent()) && !isTopLevel() &&
            MenuTypeEnum.DIRECTORY.getValue().equals(getMenuType());
    }


}
