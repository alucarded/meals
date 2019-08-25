package com.devpeer.calories.core.query;

import java.util.Objects;
import java.util.Stack;

public class QueryFilterParser {

    enum ParseState {
        PARSE_FIELD,
        PARSE_OPERATOR,
        PARSE_VALUE
    }

    private ParseState parseState;

    private Stack<QueryFilter> filterStack;

    public static QueryFilter parse(String filter) {
        try {
            QueryFilterParser queryFilterParser = new QueryFilterParser(filter);
            return queryFilterParser.getFilterStack();
        } catch (Exception e) {
            throw new QueryFilterParseException(e);
        }
    }

    private QueryFilterParser(String filter) {
        filterStack = new Stack<>();
        filterStack.add(new QueryFilter());
        parseState = ParseState.PARSE_FIELD;

        int filterLen = filter.length();
        char[] word = new char[filterLen];
        int wordCounter = 0;
        for (int i = 0; i < filterLen; ++i) {
            char c = filter.charAt(i);
            switch (c) {
                case '(':
                    filterStack.push(new QueryFilter());
                    parseState = ParseState.PARSE_FIELD;
                    break;
                case ')':
                    addToFilter(word, wordCounter);
                    wordCounter = 0;
                    QueryFilter currentFilter = filterStack.pop();
                    filterStack.peek().addChainOperation(currentFilter);
                    break;
                case ' ':
                    addToFilter(word, wordCounter);
                    wordCounter = 0;
                    break;
                default:
                    word[wordCounter++] = c;
            }
        }
        if (ParseState.PARSE_VALUE.equals(parseState)) {
            filterStack.peek().setValue(new String(word, 0, wordCounter));
        }
    }

    private void addToFilter(char[] word, int wordCount) {
        String wordStr = new String(word, 0, wordCount);

        if (wordStr.isEmpty()) {
            // Running spaces or space after parenthesis
            return;
        }

        switch (parseState) {
            case PARSE_FIELD:
                filterStack.peek().setKey(wordStr);
                parseState = ParseState.PARSE_OPERATOR;
                break;
            case PARSE_OPERATOR:
                QueryFilter.Operator op = QueryFilter.Operator.valueOf(wordStr.toUpperCase());
                filterStack.peek().setOperator(op);
                parseState = ParseState.PARSE_VALUE;
                break;
            case PARSE_VALUE:
                filterStack.peek().setValue(wordStr);
                parseState = ParseState.PARSE_OPERATOR;
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private QueryFilter getFilterStack() {
        QueryFilter ret = filterStack.peek();
        while (ret.getChainOperations().size() == 1 && Objects.isNull(ret.getOperator())) {
            ret = ret.getChainOperations().get(0);
        }
        return ret;
    }
}
