package com.allendowney.thinkdast;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import redis.clients.jedis.Jedis;


/**
 * Represents the results of a search query.
 *
 */
public class WikiSearch {

	// map from URLs that contain the term(s) to relevance score
	private Map<String, Integer> map;

	/**
	 * Constructor.
	 *
	 * @param map
	 */
	public WikiSearch(Map<String, Integer> map) {
		this.map = map;
	}

	/**
	 * Looks up the relevance of a given URL.
	 *
	 * @param url
	 * @return
	 */
	public Integer getRelevance(String url) {
		Integer relevance = map.get(url);
		return relevance==null ? 0: relevance;
	}

	/**
	 * Prints the contents in order of term frequency.
	 *
	 * @param
	 */
	private  void print() {
		List<Entry<String, Integer>> entries = sort();
		for (Entry<String, Integer> entry: entries) {
			System.out.println(entry);
		}
	}

	/**
	 * Combines key sets of two search results.
	 * @param that
	 * @return New key set.
	 */
	private Set<String> combineKeySets(WikiSearch that) {
		Set<String> keySet = new HashSet<>();
		keySet.addAll(this.map.keySet());
		keySet.addAll(that.map.keySet());
		return keySet;
	}

	/**
	 * Computes the union of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch or(WikiSearch that) {
		Map<String, Integer> map = new HashMap<>();

		for (String key : combineKeySets(that)) {
			Integer thisScore = this.map.getOrDefault(key, 0);
			Integer thatScore = that.map.getOrDefault(key, 0);
			map.put(key, thisScore + thatScore);
		}

		return new WikiSearch(map);
	}

	/**
	 * Computes the intersection of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch and(WikiSearch that) {
		Map<String, Integer> map = new HashMap<>();

		for (String key : combineKeySets(that)) {
			Integer thisScore = this.map.getOrDefault(key, 0);
			Integer thatScore = that.map.getOrDefault(key, 0);

			Integer computed = thisScore == 0 || thatScore == 0
					? 0 : thisScore + thatScore;
			map.put(key, computed);
		}

		return new WikiSearch(map);
	}

	/**
	 * Computes the intersection of two search results.
	 *
	 * @param that
	 * @return New WikiSearch object.
	 */
	public WikiSearch minus(WikiSearch that) {
		Map<String, Integer> map = new HashMap<>();

		for (String key : combineKeySets(that)) {
			Integer thisScore = this.map.getOrDefault(key, 0);
			Integer thatScore = that.map.getOrDefault(key, 0);

			Integer computed = Math.max(thisScore - thatScore, 0);
			map.put(key, computed);
		}

		return new WikiSearch(map);
	}

	/**
	 * Computes the relevance of a search with multiple terms.
	 *
	 * @param rel1: relevance score for the first search
	 * @param rel2: relevance score for the second search
	 * @return
	 */
	protected int totalRelevance(Integer rel1, Integer rel2) {
		// simple starting place: relevance is the sum of the term frequencies.
		return rel1 + rel2;
	}

	/**
	 * Sort the results by relevance.
	 *
	 * @return List of entries with URL and relevance.
	 */
	public List<Entry<String, Integer>> sort() {
		return map.entrySet().stream()
			.sorted(Entry.comparingByValue())
			.collect(Collectors.toList());
	}

	/**
	 * Performs a search and makes a WikiSearch object.
	 *
	 * @param term
	 * @param index
	 * @return
	 */
	public static WikiSearch search(String term, JedisIndex index) {
		Map<String, Integer> map = index.getCounts(term);
		return new WikiSearch(map);
	}

	public static void main(String[] args) throws IOException {

		// make a JedisIndex
		Jedis jedis = JedisMaker.make();
		JedisIndex index = new JedisIndex(jedis);

		// search for the first term
		String term1 = "java";
		System.out.println("Query: " + term1);
		WikiSearch search1 = search(term1, index);
		search1.print();

		// search for the second term
		String term2 = "programming";
		System.out.println("Query: " + term2);
		WikiSearch search2 = search(term2, index);
		search2.print();

		// compute the intersection of the searches
		System.out.println("Query: " + term1 + " AND " + term2);
		WikiSearch intersection = search1.and(search2);
		intersection.print();
	}
}
