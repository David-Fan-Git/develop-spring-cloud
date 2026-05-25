package com.develop.mvp.pk.module.system.application.logger.service;

import com.develop.mvp.pk.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.develop.mvp.pk.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.develop.mvp.pk.module.system.application.logger.port.inbound.LoggerUseCase;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.LoginLogDO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.OperateLogDO;
import com.develop.mvp.pk.module.system.domain.logger.LoginLog;
import com.develop.mvp.pk.module.system.domain.logger.OperateLog;
import com.develop.mvp.pk.module.system.domain.logger.repository.LoginLogRepository;
import com.develop.mvp.pk.module.system.domain.logger.repository.OperateLogRepository;

/**
 * Logger Application Service 应用服务。
 */
public class LoggerApplicationService implements LoggerUseCase {

	private final LoginLogRepository loginLogRepository;

	private final OperateLogRepository operateLogRepository;

	/**
	 * 创建 LoggerApplicationService 实例。
	 *
	 * @param loginLogRepository   loginLogRepository 参数
	 * @param operateLogRepository operateLogRepository 参数
	 */
	public LoggerApplicationService(LoginLogRepository loginLogRepository, OperateLogRepository operateLogRepository) {

		this.loginLogRepository = loginLogRepository;
		this.operateLogRepository = operateLogRepository;
	}

	/**
	 * 查询 get Login Log 对应的数据。
	 *
	 * @param id id 参数
	 * @return 处理结果
	 */
	public LoginLogDO getLoginLog(Long id) {

		return loginLogRepository.findDoById(id);
	}

	/**
	 * 查询 get Login Log Page 对应的数据。
	 *
	 * @param pageReqVO pageReqVO 参数
	 * @return 处理结果
	 */
	public PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO) {

		return loginLogRepository.findPage(pageReqVO);
	}

	/**
	 * 创建 create Login Log 对应的数据。
	 *
	 * @param reqDTO reqDTO 参数
	 */
	public void createLoginLog(LoginLogCreateReqDTO reqDTO) {

		loginLogRepository.save(LoginLog.builder().logType(reqDTO.getLogType()).traceId(reqDTO.getTraceId()).userId(reqDTO.getUserId()).userType(reqDTO.getUserType()).username(reqDTO.getUsername()).result(reqDTO.getResult()).userIp(reqDTO.getUserIp()).userAgent(reqDTO.getUserAgent()).build());
	}

	/**
	 * 创建 create Operate Log 对应的数据。
	 *
	 * @param createReqDTO createReqDTO 参数
	 */
	public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {

		operateLogRepository.save(OperateLog.builder().traceId(createReqDTO.getTraceId()).userId(createReqDTO.getUserId()).userType(createReqDTO.getUserType()).type(createReqDTO.getType()).subType(createReqDTO.getSubType()).bizId(createReqDTO.getBizId()).action(createReqDTO.getAction()).extra(createReqDTO.getExtra()).requestMethod(createReqDTO.getRequestMethod()).requestUrl(createReqDTO.getRequestUrl()).userIp(createReqDTO.getUserIp()).userAgent(createReqDTO.getUserAgent()).build());
	}

	/**
	 * 查询 get Operate Log 对应的数据。
	 *
	 * @param id id 参数
	 * @return 处理结果
	 */
	public OperateLogDO getOperateLog(Long id) {

		return operateLogRepository.findDoById(id);
	}

	/**
	 * 查询 get Operate Log Page 对应的数据。
	 *
	 * @param pageReqVO pageReqVO 参数
	 * @return 处理结果
	 */
	public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO) {

		return operateLogRepository.findPage(pageReqVO);
	}

	/**
	 * 查询 get Operate Log Page 对应的数据。
	 *
	 * @param pageReqVO pageReqVO 参数
	 * @return 处理结果
	 */
	public PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqVO) {

		return operateLogRepository.findPage(pageReqVO);
	}

}
