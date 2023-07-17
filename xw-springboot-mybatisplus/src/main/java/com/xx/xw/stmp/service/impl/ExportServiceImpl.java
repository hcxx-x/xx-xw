package com.xx.xw.stmp.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xx.xw.stmp.mapper.IExportMapper;
import com.xx.xw.stmp.mapper.ITestMapper;
import com.xx.xw.stmp.pojo.entity.ExportEntity;
import com.xx.xw.stmp.pojo.entity.TestEntity;
import com.xx.xw.stmp.service.IExportService;
import com.xx.xw.stmp.service.ITestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class ExportServiceImpl extends ServiceImpl<IExportMapper, ExportEntity> implements IExportService {



}
