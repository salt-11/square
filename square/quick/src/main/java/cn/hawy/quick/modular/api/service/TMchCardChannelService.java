package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TMchCardChannel;
import cn.hawy.quick.modular.api.mapper.TMchCardChannelMapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 商户表 服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
@Service
public class TMchCardChannelService extends ServiceImpl<TMchCardChannelMapper, TMchCardChannel> {

	public TMchCardChannel findByCardIdAndChannel(Integer cardId,Integer status,String channel) {
		TMchCardChannel mchCardChannel = new TMchCardChannel();
		mchCardChannel.setCardId(cardId);
		mchCardChannel.setChannel(channel);
		mchCardChannel.setStatus(status);
		return this.baseMapper.selectOne(new QueryWrapper<>(mchCardChannel));
	}

	public TMchCardChannel findByCardIdAndChannel(Integer cardId,String channel) {
		TMchCardChannel mchCardChannel = new TMchCardChannel();
		mchCardChannel.setCardId(cardId);
		mchCardChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(mchCardChannel));
	}

	public void updateStatusSuccess(Integer cardId,String channel) {
		this.baseMapper.updateStatus(2, cardId, channel);
	}

	public void updateStatusFail(Integer cardId,String channel) {
		this.baseMapper.updateStatus(3, cardId, channel);
	}

	@Transactional
	public void bindCardConfirm(TMchCardChannel mchCardChannel,TMchCardChannel successMchCardChannel) {
		this.baseMapper.updateById(mchCardChannel);
		if(successMchCardChannel != null) {
			successMchCardChannel.setStatus(3);
			this.baseMapper.updateById(successMchCardChannel);
		}
	}

	@Transactional
	public void bindCardConfirm(TMchCardChannel mchCardChannel) {
		this.baseMapper.updateById(mchCardChannel);
	}

	public TMchCardChannel findBySmsNoAndChannel(String smsNo,String channel){
		TMchCardChannel mchCardChannel = new TMchCardChannel();
		mchCardChannel.setSmsNo(smsNo);
		mchCardChannel.setChannel(channel);
		return this.baseMapper.selectOne(new QueryWrapper<>(mchCardChannel));
	}

	public int updateNotifyCount(Integer id,int notifyCount,String notifyResult) {
		int count = this.baseMapper.updateNotifyCount(id, notifyCount, notifyResult);
		return count;
	}

}
