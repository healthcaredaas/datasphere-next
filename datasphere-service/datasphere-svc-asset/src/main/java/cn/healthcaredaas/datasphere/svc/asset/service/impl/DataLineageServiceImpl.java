package cn.healthcaredaas.datasphere.svc.asset.service.impl;

import cn.healthcaredaas.datasphere.svc.asset.entity.DataLineage;
import cn.healthcaredaas.datasphere.svc.asset.mapper.DataLineageMapper;
import cn.healthcaredaas.datasphere.svc.asset.service.DataLineageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据血缘服务实现
 *
 * @author chenpan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataLineageServiceImpl extends ServiceImpl<DataLineageMapper, DataLineage>
        implements DataLineageService {

    @Override
    public IPage<DataLineage> pageQuery(IPage<DataLineage> page, DataLineage params) {
        LambdaQueryWrapper<DataLineage> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(params.getAssetId())) {
            wrapper.eq(DataLineage::getAssetId, params.getAssetId());
        }

        if (StringUtils.isNotBlank(params.getAssetType())) {
            wrapper.eq(DataLineage::getAssetType, params.getAssetType());
        }

        if (StringUtils.isNotBlank(params.getRelationType())) {
            wrapper.eq(DataLineage::getRelationType, params.getRelationType());
        }

        wrapper.orderByDesc(DataLineage::getCreateTime);

        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<DataLineage> getUpstreamLineage(String assetId) {
        return baseMapper.selectUpstreamByAssetId(assetId);
    }

    @Override
    public List<DataLineage> getDownstreamLineage(String assetId) {
        return baseMapper.selectDownstreamByAssetId(assetId);
    }

    @Override
    public Map<String, Object> getLineageGraph(String assetId) {
        Map<String, Object> graph = new HashMap<>();

        // 查询当前资产
        DataLineage current = getById(assetId);

        // 查询上游和下游
        List<DataLineage> upstream = getUpstreamLineage(assetId);
        List<DataLineage> downstream = getDownstreamLineage(assetId);

        // 构建节点列表
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();

        // 添加当前节点
        if (current != null) {
            Map<String, Object> currentNode = new HashMap<>();
            currentNode.put("id", current.getAssetId());
            currentNode.put("name", current.getAssetName());
            currentNode.put("type", current.getAssetType());
            currentNode.put("level", 0);
            nodes.add(currentNode);
        }

        // 添加上游节点和边
        for (DataLineage lineage : upstream) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", lineage.getUpstreamAssetId());
            node.put("name", lineage.getUpstreamAssetName());
            node.put("type", "UPSTREAM");
            node.put("level", -1);
            nodes.add(node);

            Map<String, Object> edge = new HashMap<>();
            edge.put("source", lineage.getUpstreamAssetId());
            edge.put("target", assetId);
            edge.put("relation", lineage.getRelationType());
            edges.add(edge);
        }

        // 添加下游节点和边
        for (DataLineage lineage : downstream) {
            Map<String, Object> node = new HashMap<>();
            node.put("id", lineage.getDownstreamAssetId());
            node.put("name", lineage.getDownstreamAssetName());
            node.put("type", "DOWNSTREAM");
            node.put("level", 1);
            nodes.add(node);

            Map<String, Object> edge = new HashMap<>();
            edge.put("source", assetId);
            edge.put("target", lineage.getDownstreamAssetId());
            edge.put("relation", lineage.getRelationType());
            edges.add(edge);
        }

        graph.put("nodes", nodes);
        graph.put("edges", edges);

        return graph;
    }
}
