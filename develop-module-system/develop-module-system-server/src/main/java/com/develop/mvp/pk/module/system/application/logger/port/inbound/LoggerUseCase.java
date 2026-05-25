package com.develop.mvp.pk.module.system.application.logger.port.inbound;

// DDD 角色：入站端口 — 定义 Logger 聚合的用例边界，供 Controller/API/跨服务调用
// Hexagonal-Lite：入站端口接口，应用服务实现此接口

import com.develop.mvp.pk.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.develop.mvp.pk.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.LoginLogDO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.OperateLogDO;
/**
 * Logger 聚合的入站用例端口。
 */
public interface LoggerUseCase {

    /**
     * 查询 get Login Log 对应的数据。
     *
     * @param id id 参数
     * @return 处理结果
     */
    LoginLogDO getLoginLog(Long id);

    /**
     * 查询 get Login Log Page 对应的数据。
     *
     * @param pageReqVO pageReqVO 参数
     * @return 处理结果
     */
    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO);

    /**
     * 创建 create Login Log 对应的数据。
     *
     * @param reqDTO reqDTO 参数
     */
    void createLoginLog(LoginLogCreateReqDTO reqDTO);

    /**
     * 创建 create Operate Log 对应的数据。
     *
     * @param createReqDTO createReqDTO 参数
     */
    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    /**
     * 查询 get Operate Log 对应的数据。
     *
     * @param id id 参数
     * @return 处理结果
     */
    OperateLogDO getOperateLog(Long id);

    /**
     * 查询 get Operate Log Page 对应的数据。
     *
     * @param pageReqVO pageReqVO 参数
     * @return 处理结果
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO);

    /**
     * 查询 get Operate Log Page 对应的数据。
     *
     * @param pageReqVO pageReqVO 参数
     * @return 处理结果
     */
    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqVO);
}
