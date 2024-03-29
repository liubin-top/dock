package com.sinosoft.mapper;

import com.sinosoft.base.mapper.BaseMapper;
import com.sinosoft.model.SysOrg;
import com.sinosoft.vo.EchartTreeDataVO;
import com.sinosoft.vo.Node;
import com.sinosoft.vo.RiskStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysOrgMapper extends BaseMapper<SysOrg> {
    /**
     * 查询子树
     * @return
     */
    List<Node> queryOrgTreeByPid(@Param("id") Long id);
    /**
     * 查询机构项目
     * @return
     */
    List<Node> queryOrg(@Param("id") Long id);

    /**
     * 获取转换树所需要的List
     * @param orgId
     * @param withOutId
     * @return
     */
    List<Node> selectAllListForTree(@Param("orgId") String orgId, @Param("withOutId") Long withOutId);

    /**
     * 获取排序列表
     * @param pid
     * @return
     */
    List<SysOrg> getSortData(@Param("pid") Long pid, @Param("orgIdList") List<Long> orgIdList);

    /**
     * 获取pid
     * @param id
     * @return
     */
    SysOrg selectOrg(Long id);

    /**
     * 排序
     * @param idsMap
     * @return
     */
    int updateOrderby(Map<String, List<Long>> idsMap);

    /**
     * 机构名可用性校验,存在则不可用
     * @param orgName
     * @param id
     * @return
     */
    boolean verify(@Param("orgName") String orgName, @Param("id") Long id);

    /**
     * 获取当前最大排序值
     * @return
     */
    Integer getOrderBy();

    /**
     * 根据用户id获取
     * @param userId
     * @return
     */
    SysOrg getByUserId(Long userId);

    /**
     * 获取机构和机构的下属机构id列表
     * @param orgId
     * @return
     */
    List<Long> listOrgAndSonOrgId(String orgId);

    /**
     * 获取机构和机构的下属机构id列表
     * @param ids
     * @return
     */
    Long[] listSonOrgIdByIdStr(String ids);

    /**
     * 获取下级直属机构
     * @param orgId 上级id
     * @return
     */
    String listSonOrgId(String orgId);

    boolean verifyCode(@Param("orgCode")String orgCode, @Param("id")Long id);

    /**
     * 根据机构id获取上级机构
     * @param id
     * @return
     */
    List<SysOrg> getPOrgListById(@Param("id")Long id);

    /**
     * 获取需要首页统计的机构
     * @return
     */
    List<RiskStatisticsVO> selectSysOrgStatistics();

    /**
     * 获取企业图谱检索内容：机构+法人+股东
     * @return
     */
    List<SysOrg> selectOrgShareholderLegalPersonList();

    /**
     * 查询机构下级机构id串
     * @param id
     * @return
     */
    String selectChildsById(@Param("id") Long id);

    /**
     * 查询所有机构
     * @return
     */
    List<SysOrg> selectAllList();

    /**
     * 根据名称获取
     * @param orgName
     * @return
     */
    SysOrg selectByName(@Param("orgName") String orgName);

    /**
     * 根据pid查询所有子机构
     * @return
     */
    List<SysOrg> selectByPid(@Param("pid") Long pid);

    /**
     * 根据pid查询所有子机构
     * @return
     */
    List<EchartTreeDataVO> selectEachrtTreeListByPid(@Param("pid") Long pid);
    /**
     * 查询速赢重大风险的机构
     * @return
     */
    List<Node> querySuyingMajorRiskOrg();
    /**
     *@Description:
     *@Params:速赢界面的机构树
     *@Result:
     *@Date:16:43 2023/3/7
     */
    List<Node>selectAllListForSuyingTree(@Param("orgId") String orgId, @Param("withOutId") Long withOutId, @Param("level") int level);
    /**
     * 根据机构财务报表名称
     *
     * @param orgStatementName
     * @return
     */
    SysOrg selectByStatementName(@Param("orgStatementName") String orgStatementName);

    SysOrg selectByDataDockCode(String dataDockCode);

    /**
     * 根据机构简称
     *
     * @param orgSimpleName
     * @return
     */
    SysOrg selectBySimpleName(@Param("orgSimpleName")String orgSimpleName);

    SysOrg selectByOrgName(@Param("orgName")String orgStatementName);
}