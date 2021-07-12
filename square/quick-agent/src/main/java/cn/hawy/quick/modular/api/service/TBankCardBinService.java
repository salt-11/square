package cn.hawy.quick.modular.api.service;

import cn.hawy.quick.modular.api.entity.TBankCardBin;
import cn.hawy.quick.modular.api.mapper.TBankCardBinMapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hawy
 * @since 2019-07-15
 */
@Service
public class TBankCardBinService extends ServiceImpl<TBankCardBinMapper, TBankCardBin> {
	
	public TBankCardBin findBankNameByBankCardNo(String bankCardNo) {
		TBankCardBin entity = new TBankCardBin();
		//6位bin号
		String cardbin_6 = bankCardNo.substring(0, 6);
		entity.setBankCardBin(cardbin_6);
		TBankCardBin bankCardBin = this.baseMapper.selectOne(new QueryWrapper<>(entity));
		if(bankCardBin != null) {
			return bankCardBin;
		}
		//9位bin号
		String cardbin_9 = bankCardNo.substring(0, 9);
		entity.setBankCardBin(cardbin_9);
		bankCardBin = this.baseMapper.selectOne(new QueryWrapper<>(entity));
		if(bankCardBin != null) {
			return bankCardBin;
		}
		//8位bin号
		String cardbin_8 = bankCardNo.substring(0, 8);
		entity.setBankCardBin(cardbin_8);
		bankCardBin = this.baseMapper.selectOne(new QueryWrapper<>(entity));
		if(bankCardBin != null) {
			return bankCardBin;
		}
		//5位bin号
		String cardbin_5 = bankCardNo.substring(0, 5);
		entity.setBankCardBin(cardbin_5);
		bankCardBin = this.baseMapper.selectOne(new QueryWrapper<>(entity));
		if(bankCardBin != null) {
			return bankCardBin;
		}
		//7位bin号
		String cardbin_7 = bankCardNo.substring(0, 7);
		entity.setBankCardBin(cardbin_7);
		bankCardBin = this.baseMapper.selectOne(new QueryWrapper<>(entity));
		if(bankCardBin != null) {
			return bankCardBin;
		}
		//4位bin号
		String cardbin_4 = bankCardNo.substring(0, 4);
		entity.setBankCardBin(cardbin_4);
		bankCardBin = this.baseMapper.selectOne(new QueryWrapper<>(entity));
		if(bankCardBin != null) {
			return bankCardBin;
		}
		//3位bin号
		String cardbin_3 = bankCardNo.substring(0, 3);
		entity.setBankCardBin(cardbin_3);
		bankCardBin = this.baseMapper.selectOne(new QueryWrapper<>(entity));
		if(bankCardBin != null) {
			return bankCardBin;
		}
		//10位bin号
		String cardbin_10 = bankCardNo.substring(0, 10);
		entity.setBankCardBin(cardbin_10);
		bankCardBin = this.baseMapper.selectOne(new QueryWrapper<>(entity));
		if(bankCardBin != null) {
			return bankCardBin;
		}
		return null;
	}

}
