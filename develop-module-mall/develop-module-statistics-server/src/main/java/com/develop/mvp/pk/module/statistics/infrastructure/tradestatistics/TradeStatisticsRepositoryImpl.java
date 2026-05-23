package com.develop.mvp.pk.module.statistics.infrastructure.tradestatistics;

import com.develop.mvp.pk.module.statistics.dal.dataobject.trade.TradeStatisticsDO;
import com.develop.mvp.pk.module.statistics.dal.mysql.trade.TradeStatisticsMapper;
import com.develop.mvp.pk.module.statistics.domain.tradestatistics.TradeStatistics;
import com.develop.mvp.pk.module.statistics.domain.tradestatistics.TradeStatisticsFactory;
import com.develop.mvp.pk.module.statistics.domain.tradestatistics.repository.TradeStatisticsRepository;
import com.develop.mvp.pk.module.statistics.domain.tradestatistics.valueobject.TradeStatisticsId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TradeStatisticsRepositoryImpl implements TradeStatisticsRepository {

    private final TradeStatisticsMapper tradeStatisticsMapper;

    public TradeStatisticsRepositoryImpl(TradeStatisticsMapper tradeStatisticsMapper) {
        this.tradeStatisticsMapper = tradeStatisticsMapper;
    }

    @Override
    @Transactional
    public TradeStatistics save(TradeStatistics stats) {
        TradeStatisticsDO statsDO = toDataObject(stats);
        if (tradeStatisticsMapper.selectById(stats.id().value()) == null) {
            tradeStatisticsMapper.insert(statsDO);
        } else {
            tradeStatisticsMapper.updateById(statsDO);
        }
        return stats;
    }

    @Override
    public TradeStatistics findById(TradeStatisticsId id) {
        TradeStatisticsDO statsDO = tradeStatisticsMapper.selectById(id.value());
        return statsDO != null ? toDomain(statsDO) : null;
    }

    @Override
    public TradeStatistics findByDate(LocalDate date) {
        TradeStatisticsDO statsDO = tradeStatisticsMapper.selectByDate(date);
        return statsDO != null ? toDomain(statsDO) : null;
    }

    @Override
    public List<TradeStatistics> findByDateBetween(LocalDate start, LocalDate end) {
        return tradeStatisticsMapper.selectListByDateBetween(start, end).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    private TradeStatisticsDO toDataObject(TradeStatistics stats) {
        TradeStatisticsDO statsDO = new TradeStatisticsDO();
        statsDO.setId(stats.id().value());
        statsDO.setDate(stats.date());
        statsDO.setOrderCount(stats.orderCount());
        statsDO.setOrderPayCount(stats.orderPayCount());
        statsDO.setOrderPayPrice(stats.orderPayPrice());
        statsDO.setRefundCount(stats.refundCount());
        statsDO.setRefundPrice(stats.refundPrice());
        statsDO.setBrokerageSettlementPrice(stats.brokerageSettlementPrice());
        return statsDO;
    }

    private TradeStatistics toDomain(TradeStatisticsDO statsDO) {
        return TradeStatisticsFactory.reconstitute(
                statsDO.getId(), statsDO.getDate(),
                statsDO.getOrderCount(), statsDO.getOrderPayCount(),
                statsDO.getOrderPayPrice(), statsDO.getRefundCount(),
                statsDO.getRefundPrice(), statsDO.getBrokerageSettlementPrice());
    }
}
