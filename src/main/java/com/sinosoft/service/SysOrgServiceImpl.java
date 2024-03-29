package com.sinosoft.service;

import com.sinosoft.base.service.BaseServiceImpl;
import com.sinosoft.mapper.SysOrgMapper;
import com.sinosoft.model.SysOrg;
import com.sinosoft.vo.EchartTreeDataVO;
import com.sinosoft.vo.Node;
import com.sinosoft.vo.RiskStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: CLQ
 * @Date: 2019/12/12
 */
@Service
public class SysOrgServiceImpl extends BaseServiceImpl<SysOrg> implements SysOrgService {
    @Autowired
    private SysOrgMapper sysOrgMapper;

    @Override
    public List<Node> queryOrgTreeByPid(Long id) {
        return sysOrgMapper.queryOrgTreeByPid(id);
    }
    @Override
    public List<Node> queryOrg(Long id) {
        return sysOrgMapper.queryOrg(id);
    }

    @Override
    public List<Node> selectAllList(String orgId, Long withOutId) {
        return sysOrgMapper.selectAllListForTree(orgId, withOutId);
    }

    @Override
    public List<SysOrg> getSortData(Long pid, List<Long> orgIdList) {
        return sysOrgMapper.getSortData(pid, orgIdList);
    }

    @Override
    public SysOrg selectOrg(Long id) {
        return sysOrgMapper.selectOrg(id);
    }

    @Override
    public int updateOrderby(Map<String, List<Long>> idsMap) {
        return sysOrgMapper.updateOrderby(idsMap);
    }

    @Override
    public boolean verify(String orgName, Long id) {
        return sysOrgMapper.verify(orgName, id);
    }

    @Override
    public Integer getOrderBy() {
        return sysOrgMapper.getOrderBy();
    }

    @Override
    public SysOrg getByUserId(Long userId) {
        return sysOrgMapper.getByUserId(userId);
    }

    @Override
    public List<Long> listOrgAndSonOrgId(String orgId) {
        return sysOrgMapper.listOrgAndSonOrgId(orgId);
    }

    @Override
    public Long[] listSonOrgIdByIdStr(String ids) {
        return sysOrgMapper.listSonOrgIdByIdStr(ids);
    }

    @Override
    public String listSonOrgId(String orgId) { return sysOrgMapper.listSonOrgId(orgId); }

    @Override
    public boolean verifyCode(String orgCode, Long id) {
        return sysOrgMapper.verifyCode(orgCode, id);
    }

    @Override
    public List<RiskStatisticsVO> selectSysOrgStatistics() {
        return sysOrgMapper.selectSysOrgStatistics();
    }

    @Override
    public String selectChildsById(Long id) {
        return sysOrgMapper.selectChildsById(id);
    }

    @Override
    public List<SysOrg> selectOrgShareholderLegalPersonList() {
        return sysOrgMapper.selectOrgShareholderLegalPersonList();
    }

    @Override
    public List<SysOrg> selectAllList() {
        return sysOrgMapper.selectAllList();
    }

    @Override
    public SysOrg selectByName(String orgName) {
        return sysOrgMapper.selectByName(orgName);
    }

    @Override
    public List<SysOrg> selectByPid(Long pid) {
        return sysOrgMapper.selectByPid(pid);
    }

    @Override
    public List<EchartTreeDataVO> selectEachrtTreeListByPid(Long pid) {
        return sysOrgMapper.selectEachrtTreeListByPid(pid);
    }

    @Override
    public List<Node> querySuyingMajorRiskOrg() {
        return sysOrgMapper.querySuyingMajorRiskOrg();
    }

    @Override
    public List<Node> selectAllListForSuyingTree(String orgId, Long withOutId, int level) {
        return sysOrgMapper.selectAllListForSuyingTree(orgId,withOutId,level);
    }

    @Override
    public SysOrg selectByStatementName(String orgStatementName) {
        return sysOrgMapper.selectByStatementName(orgStatementName);
    }

    @Override
    public SysOrg selectByDataDockCode(String dataDockCode) {
        return sysOrgMapper.selectByDataDockCode(dataDockCode);
    }

    @Override
    public SysOrg selectBySimpleName(String orgSimpleName) {
        return sysOrgMapper.selectBySimpleName(orgSimpleName);
    }

    @Override
    public SysOrg selectByOrgName(String orgStatementName) {
        return sysOrgMapper.selectByOrgName(orgStatementName);
    }
}
