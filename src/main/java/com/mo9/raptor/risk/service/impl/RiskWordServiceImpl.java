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
    private static String[] riskWords = "催收,崔收,摧收,黑户,网黑".split(",");

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