package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TMchCard;
import cn.hawy.quick.modular.api.entity.TMchCardChannel;
import cn.hawy.quick.modular.api.mapper.TMchCardChannelMapper;
import cn.hawy.quick.modular.api.mapper.TMchCardMapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商户表 服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-09
 */
@Service
public class TMchCardService extends ServiceImpl<TMchCardMapper, TMchCard> {
	
	@Autowired
	TMchCardChannelMapper mchCardChannelMapper;
	
	public TMchCard findBybankCardNo(String mchId,String bankCardNo) {
		TMchCard mchCard = new TMchCard();
		mchCard.setMchId(mchId);
		mchCard.setBankCardNo(bankCardNo);
		return this.baseMapper.selectOne(new QueryWrapper<>(mchCard));
	}
	
	@Transactional
	public void bindCard(TMchCard mchCard,TMchCardChannel mchCardChannel,TMchCardChannel successMchCardChannel) {
		if(mchCard.getId() == null) {
			this.baseMapper.insert(mchCard);
		}else {
			this.baseMapper.updateById(mchCard);
		}
		if(successMchCardChannel != null) {
			mchCardChannelMapper.updateById(successMchCardChannel);
		}
		mchCardChannel.setCardId(mchCard.getId());
		mchCardChannelMapper.insert(mchCardChannel);
	}
	
	@Transactional
	public void bindCard(TMchCard mchCard,TMchCardChannel mchCardChannel) {
		if(mchCard.getId() == null) {
			this.baseMapper.insert(mchCard);
		}else {
			this.baseMapper.updateById(mchCard);
		}
		if(mchCardChannel.getId() == null) {
			mchCardChannel.setCardId(mchCard.getId());
			mchCardChannelMapper.insert(mchCardChannel);
		}else {
			mchCardChannelMapper.updateById(mchCardChannel);
		}
		
	}

	public List<Map<String, Object>> findAll(Page page, String join, String mchId, String bankCardNo){
		return this.baseMapper.findAll(page, join, mchId, bankCardNo);
	}
}
