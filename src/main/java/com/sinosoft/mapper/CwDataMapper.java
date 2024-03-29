package com.sinosoft.mapper;

import com.sinosoft.base.mapper.BaseMapper;
import com.sinosoft.model.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CwDataMapper extends BaseMapper<CwData> {
    List<CwData> selectPgaeList(@Param("page") int page, @Param("size")int size,@Param("tableName")String tableName);

    int selectAllCount(@Param("tableName")String tableName);

    int selectAllCountJyKjTj(@Param("tableName")String tableName);

    List<JykjDataTjLocal> selectPgaeListJyKjTj(@Param("page") int page, @Param("size")int size, @Param("tableName")String tableName);

    List<JykjDataJh> selectPgaeListJyKjJH(@Param("page") int page, @Param("size")int size,@Param("tableName")String tableName);

    List<JykjFdlJh> selectPgaeListJyKjFdl(@Param("page") int page, @Param("size")int size,@Param("tableName")String tableName);
}
