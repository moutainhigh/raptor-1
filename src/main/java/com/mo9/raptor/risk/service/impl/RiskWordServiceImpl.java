package com.mo9.raptor.risk.service.impl;

import com.mo9.raptor.risk.service.RiskWordService;
import com.mo9.raptor.utils.log.Log;
import org.ahocorasick.trie.Token;
import org.ahocorasick.trie.Trie;
import org.ahocorasick.trie.Trie.TrieBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;

/**
 * 风险词匹配
 *
 * @author eqshen
 * @Date 2017年7月12
 */
@Service
public class RiskWordServiceImpl implements RiskWordService {
    private static Logger logger = Log.get();

    @Autowired
    private static String[] riskWords = "宜信,恒昌,民信,捷信,捷越,花呗,借呗,信而富,银谷,普惠,佰仟,锦程,利信,冠群驰聘,友信,品牛,安信,富登,快信,点融,玖富,维信,翼龙,融宜,达信,金信,金服,闪银,中腾信,亚联财,中诚信,付融宝,融金所,信安,华锐肯特,邦信,东方惠民,中安信业,普罗米斯,北银,海钜信达,52校园,阿里路亚,爱学贷,帮贷师,贝多分,贷贷红,分期乐,好贷网,金融猫,你我贷,拍拍贷,青银台,趣分期,人人分期,速溶360,投投贷,优分期,余额E贷,喵贷,稳安贷,点心贷,宜众贷,梦想贷,学富贷,牛牛贷,搜易贷,好又贷,学子易贷,团贷网,仁仁分期,道口贷,闪电借款,宜人贷,平安易贷,我来贷,诺诺镑客,学贷网,亲亲小贷,零用金,黄金指,人人贷,金宝盒,利学贷,先花花,九九分期,速可贷,安易贷,和信贷,银湖网,鑫贷网,快易贷,拍来贷,中银,夸氪金融,点筹金融,么么贷,99贷,时时贷,融道网,蚂蚁微贷,飞贷,零零花,安心贷,好贷鼠,现金贷,橘子分期,江湖救急,云分期,任分期,融360,挖财快贷,好学贷,信用钱包,e速贷,加速贷,学信宝,喵卡,51配资,祺天优贷,闪电金融,贷你飞,叮当钱包,鼎力分期,亨元金融,壹钱包,金豆分期,招联金融,信通袋,点点金融,哒分期,一麻袋,钱满仓,君融贷,富登信贷,安贷客,闪钱包,现金巴士,牛呗,积木盒子,名校贷,蚂蚁分期,小牛在线,佰仟金融,花啦花啦,58消费贷,新新贷,芒果金融,京东白条,屌丝贷,指尖贷,借钱花,易通贷,借贷宝,联盟贷,贷我飞,你我金融,爱钱进,好贷帮,我爱卡,友借友还,易贷网,投哪网,鑫利源,宝丰财富,赢瑞创投,十六铺金融,微拍贷,万代福基金,盛世通贷,一站贷,领先贷,不倒翁贷,六顺财富,融卷风,融易发财富,云回通宝,众贷汇,储信贷,快贷,微贷网,急速贷,随心贷,贷友帮,温州贷,一贷网,翼龙贷,贷贷网,贷库,易简贷,有人贷,轻松易贷,易贷通,易利贷,菜鸟贷,帮贷宝,当地贷,捷安贷,直达贷,喔喔贷,车速贷,必应贷,助贷,当当贷,介贷,小平贷,一搜贷,易车贷,潮人贷,惠卡贷,汇鑫贷,快贷帮,武汉贷,易友贷,开心e贷,同心小贷,立申贷,融易贷,快助贷,比亮贷,贷投帮帮,如意贷,讯易信贷,投哪好贷,好车e贷,ICAN贷,阳光速贷,微贷宝,金腰贷,乐融快乐贷,陇商贷,恒信易贷,创赢易贷,睿贷宝,光大富易贷,电兔优贷,双富贷,福建同城贷,互利贷,掌上易贷,易车速贷,信车贷,鼎鑫易贷,泰易贷,银信小贷,天天车贷,好时贷,好贷,轻松e贷,在线贷,马上贷,美贷网,云贷款,路路贷,摇摇贷,招商贷,好贷款,意真,众贷网,非诚勿贷,现贷网,互帮贷,立贷网,天力贷,川信贷,宜商贷,保险贷,银实贷,汇宝信贷,乐网贷,家家贷,银鑫贷,通联贷,平海金融,信网贷,铜都贷,乾坤贷,招金贷,鹏城贷,3a借贷,好想贷,徽州贷,江城贷,齐商贷,阿拉贷,帮你贷,中州易贷,沪发贷,及时雨,正大金融,天标贷,怀民贷,聚众贷,华生贷,重庆小贷,快批网,苏宁消费金融,马可金融,微粒贷,贝才网,秒借,闪钱钱包,应急钱包,钱相随,极速贷,读秒,用钱宝,卡卡贷,米么,商通贷,买单侠,小米贷款,e微贷,荣璟,苏融贷,拾财贷,有用分期,松鼠金融,发薪贷,火箭借款,人人借,微微贷,手机速贷,挖财,来分期,达飞,先花一亿元,缺钱,旭胜金融,微额速达,誉用金服,还呗,缺钱么,借点钱,99分期,分期贷,创宝分期,功夫贷,钱生花,肥肥贷,合花易贷,恒易贷,马上消费,小信用,京东金融,分期X,连资贷,财加,永旺小贷,速帮贷,中兴微贷,重信金融,温商贷,钱有路,捞财宝,还借钱,借财童子,借钱用,小葱钱包,提钱乐,啪啪钱包,催收,讨债,欠款,逾期,法律,车抵,房抵,抵押,豆豆钱,2345贷款王,快速借款,51人品贷,借吧微贷,第一贷款,神灯小贷,曹操贷,惠享分期,贷贷看,国美金融,百度有钱花,车融金融,融宜宝,捷越联合,证大,合盘,卡得万利,伊达贷,金融联,信和汇金,中赢金融,速借白条,享钱贷,简单借钱,容易贷,现金微贷,嘉银,钱马金融,用钱无忧,快好贷,简融小贷,口子贷,小赢卡贷,小花钱包,快易花,大小贷,借钱快,给你花,叮当贷,闪电贷,天神贷,星星钱袋,花无缺,美借,小胖钱包,好易借,贷款侠,神马金融,信用家,美利金融,惠借宝,速借宝,贝勒爷,钱万万,钱贝街,籽微贷,来拿钱,贷上钱,开心钱包,借了花,有贝钱袋,暖薪贷,极速钱包,魔法现金,钱急送,嗨钱,贷你嗨,原子贷,51零用钱,闪贷,现金超人,万惠及贷,急用钱,壹秒分期,全能借,幸福钱庄,九秒贷,向钱贷,麦芽贷,有贝科技,奇速贷,陆金所,爱上惠,贝壳信用,现金卡".split(",");

    private Trie riskWordTrie;

    @PostConstruct
    public void init() {
        buildTrie();
    }

    private TrieBuilder getTrieBuilder() {
        TrieBuilder trieBuilder = Trie.builder();
        trieBuilder.removeOverlaps();
        trieBuilder.caseInsensitive();
        return trieBuilder;
    }

    private void buildTrie() {
        TrieBuilder riskTB = getTrieBuilder();

        synchronized (this) {
            int riskCNT = loadDict(riskTB);
//      logger.info("从数据字典中加载到" + riskCNT + "条风险词，开始构建AC自动机");
            riskWordTrie = riskTB.build();
//      logger.info("AC自动构建完毕");
        }
    }

    private int loadDict(TrieBuilder triebuilder) {
        int cnt = 0;
        if (riskWords != null && riskWords.length > 0) {
            cnt = riskWords.length;
            for (int i = 0; i < riskWords.length; i++) {
                triebuilder.addKeyword(riskWords[i]);
            }
        }
        return cnt;
    }

    /**
     * @param speech 待匹配文本，eg. 联系人名称拼接字符串
     * @return
     */
    public int filter(String speech) {
        int res = 0;
        if (StringUtils.isEmpty(speech)) {
            return 0;
        }
        Collection<Token> tokens = null;
        if (speech != null) {
            tokens = riskWordTrie.tokenize(speech);
        }

        for (Token token : tokens) {
            if (token.isMatch()) {
                res++;
            }
        }
        return res;
    }

}