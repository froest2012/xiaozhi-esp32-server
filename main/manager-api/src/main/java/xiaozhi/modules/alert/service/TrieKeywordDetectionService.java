package xiaozhi.modules.alert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import xiaozhi.modules.xin.config.XinRedisKeys;

@Service
public class TrieKeywordDetectionService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private TrieNode root;
    private long lastUpdateTime = 0;
    private static final long CACHE_DURATION = 5 * 60 * 1000;

    /**
     * Trie树节点
     */
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord = false;
        String word = null;
    }

    /**
     * 构建Trie树
     * @param keywords 关键词集合
     */
    public void buildTrie(Set<String> keywords) {
        root = new TrieNode();
        for (String keyword : keywords) {
            insertWord(keyword);
        }
    }

    /**
     * 向Trie树插入单词
     * @param word 单词
     */
    private void insertWord(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }
        current.isEndOfWord = true;
        current.word = word;
    }

    /**
     * 检测文本中的关键词
     * @param text 待检测文本
     * @return 匹配的关键词列表
     */
    public List<String> detectKeywords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> foundKeywords = new HashSet<>();
        // 从每个位置开始搜索
        for (int i = 0; i < text.length(); i++) {
            searchFromPosition(text, i, foundKeywords);
        }

        return new ArrayList<>(foundKeywords);
    }

    /**
     * 从指定位置开始搜索关键词
     * @param text 文本
     * @param startPos 起始位置
     * @param foundKeywords 找到的关键词集合
     */
    private void searchFromPosition(String text, int startPos, Set<String> foundKeywords) {
        TrieNode current = root;

        for (int i = startPos; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (!current.children.containsKey(ch)) {
                break;
            }

            current = current.children.get(ch);

            if (current.isEndOfWord) {
                foundKeywords.add(current.word);
            }
        }
    }

    /**
     * 检查单词边界
     * @param text 文本
     * @param start 单词开始位置
     * @param end 单词结束位置
     * @return 是否为完整单词
     */
    private boolean isWordBoundary(String text, int start, int end) {
        // 检查前边界
        if (start > 0 && Character.isLetterOrDigit(text.charAt(start - 1))) {
            return false;
        }

        // 检查后边界
        if (end + 1 < text.length() && Character.isLetterOrDigit(text.charAt(end + 1))) {
            return false;
        }

        return true;
    }

    /**
     * 根据需要更新Trie树
     */
    private void updateTrieIfNeeded() {
        long currentTime = System.currentTimeMillis();
        if (root == null || currentTime - lastUpdateTime > CACHE_DURATION) {
            Set<String> keywords = getKeywordsFromRedis();
            if (!keywords.isEmpty()) {
                buildTrie(keywords);
                lastUpdateTime = currentTime;
            }
        }
    }

    /**
     * 从Redis获取关键词
     * @return 关键词集合
     */
    private Set<String> getKeywordsFromRedis() {
        try {
            return redisTemplate.opsForSet().members(XinRedisKeys.getRiskKeywordsKey());
        } catch (Exception e) {
            System.err.println("从Redis获取关键词失败: " + e.getMessage());
            return new HashSet<>();
        }
    }
}