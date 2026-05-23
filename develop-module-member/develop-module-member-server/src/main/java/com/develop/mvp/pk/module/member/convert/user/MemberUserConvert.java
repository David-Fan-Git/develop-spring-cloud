package com.develop.mvp.pk.module.member.convert.user;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.member.api.user.dto.MemberUserRespDTO;
import com.develop.mvp.pk.module.member.controller.admin.user.vo.MemberUserRespVO;
import com.develop.mvp.pk.module.member.controller.admin.user.vo.MemberUserUpdateReqVO;
import com.develop.mvp.pk.module.member.controller.app.user.vo.AppMemberUserInfoRespVO;
import com.develop.mvp.pk.module.member.convert.address.AddressConvert;
import com.develop.mvp.pk.module.member.dal.dataobject.group.MemberGroupDO;
import com.develop.mvp.pk.module.member.dal.dataobject.level.MemberLevelDO;
import com.develop.mvp.pk.module.member.dal.dataobject.tag.MemberTagDO;
import com.develop.mvp.pk.module.member.dal.dataobject.user.MemberUserDO;
import com.develop.mvp.pk.module.member.domain.user.MemberUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertList;
import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertMap;

@Mapper(uses = {AddressConvert.class})
public interface MemberUserConvert {

    MemberUserConvert INSTANCE = Mappers.getMapper(MemberUserConvert.class);

    AppMemberUserInfoRespVO convert(MemberUserDO bean);


    @Mappings({
            @Mapping(source = "level", target = "level"),
            @Mapping(source = "bean.id", target = "id"),
            @Mapping(source = "bean.experience", target = "experience")
    })
    AppMemberUserInfoRespVO convert(MemberUserDO bean, MemberLevelDO level);

    MemberUserRespDTO convert2(MemberUserDO bean);

    List<MemberUserRespDTO> convertList2(List<MemberUserDO> list);

    MemberUserDO convert(MemberUserUpdateReqVO bean);

    PageResult<MemberUserRespVO> convertPage(PageResult<MemberUserDO> page);

    @Mapping(source = "areaId", target = "areaName", qualifiedByName = "convertAreaIdToAreaName")
    MemberUserRespVO convert03(MemberUserDO bean);

    // ── Domain ↔ VO 映射 ──
    @Mapping(source = "nickname.value", target = "nickname")
    @Mapping(source = "mobile.value", target = "mobile")
    @Mapping(source = "status.code", target = "status")
    MemberUserRespVO convert(MemberUser user);

    default PageResult<MemberUserRespVO> convertPageFromDomain(PageResult<MemberUser> pageResult,
                                                                List<MemberTagDO> tags,
                                                                List<MemberLevelDO> levels,
                                                                List<MemberGroupDO> groups) {
        List<MemberUserRespVO> vos = pageResult.getList().stream().map(this::convert).toList();
        PageResult<MemberUserRespVO> result = new PageResult<>(vos, pageResult.getTotal());
        Map<Long, String> tagMap = convertMap(tags, MemberTagDO::getId, MemberTagDO::getName);
        Map<Long, String> levelMap = convertMap(levels, MemberLevelDO::getId, MemberLevelDO::getName);
        Map<Long, String> groupMap = convertMap(groups, MemberGroupDO::getId, MemberGroupDO::getName);
        result.getList().forEach(user -> {
            user.setTagNames(convertList(user.getTagIds(), tagMap::get));
            user.setLevelName(levelMap.get(user.getLevelId()));
            user.setGroupName(groupMap.get(user.getGroupId()));
        });
        return result;
    }

    default PageResult<MemberUserRespVO> convertPage(PageResult<MemberUserDO> pageResult,
                                                     List<MemberTagDO> tags,
                                                     List<MemberLevelDO> levels,
                                                     List<MemberGroupDO> groups) {
        PageResult<MemberUserRespVO> result = convertPage(pageResult);
        Map<Long, String> tagMap = convertMap(tags, MemberTagDO::getId, MemberTagDO::getName);
        Map<Long, String> levelMap = convertMap(levels, MemberLevelDO::getId, MemberLevelDO::getName);
        Map<Long, String> groupMap = convertMap(groups, MemberGroupDO::getId, MemberGroupDO::getName);
        result.getList().forEach(user -> {
            user.setTagNames(convertList(user.getTagIds(), tagMap::get));
            user.setLevelName(levelMap.get(user.getLevelId()));
            user.setGroupName(groupMap.get(user.getGroupId()));
        });
        return result;
    }

}
