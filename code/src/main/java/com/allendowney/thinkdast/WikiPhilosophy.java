package com.allendowney.thinkdast;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class WikiPhilosophy {

    final static List<String> visited = new ArrayList<String>();
    final static WikiFetcher wf = new WikiFetcher();

    /**
     * Tests a conjecture about Wikipedia and Philosophy.
     *
     * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
     *
     * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String destination = "https://en.wikipedia.org/wiki/Philosophy";
        String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";

        testConjecture(destination, source, 10);
    }

    /**
     * Starts from given URL and follows first link until it finds the destination or exceeds the limit.
     *
     * @param destination
     * @param source
     * @throws IOException
     */
    public static void testConjecture(String destination, String source, int limit) throws IOException {
        URL url = new URL(source);
        String host = String.format("%s://%s", url.getProtocol(), url.getHost());

        String target = source;
        int count = 0;
        while (!url.equals(destination) && count < limit) {
            System.out.println(target);
            if (visited.contains(target)) {
                System.err.println("In loop");
                return;
            } else {
                visited.add(target);
            }

            Elements elems = wf.fetchWikipedia(target);

            for (Element elem : elems) {
                boolean found = false;
                for (Node node : new WikiNodeIterable(elem)){
                    // 1. 링크 아닌 노드 제거
                    if (node instanceof TextNode) {
                        continue;
                    }

                    if (!node.nodeName().equals("a")) {
                        continue;
                    }

                    // 2. 이탤릭체 제거
                    Node parent = node.parent();
                    String parentName = parent.nodeName();
                    if (parentName.equals("i") ||
                        parentName.equals("em")) {
                        continue;
                    }

                    // 첫번째 링크를 찾는다
                    String link = host + node.attr("href");
                    if (node.baseUri().equals(link)) {
                        continue;
                    }

                    target = link;
                    found = true;
                    break;
                }

                if (found) break;
            }
            count++;
        }
    }
}
