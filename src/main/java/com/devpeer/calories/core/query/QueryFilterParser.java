package com.devpeer.calories.core.query;

public class QueryFilterParser {

    enum ParseState {
        PARSE_FIELD,
        PARSE_OPERATOR,
        PARSE_VALUE,
        END
    }

    private ParseState parseState;

    private QueryFilter queryFilter;

    public static QueryFilter parse(String filter) {
        QueryFilterParser queryFilterParser = new QueryFilterParser(filter);
        return queryFilterParser.getQueryFilter();
    }

    QueryFilterParser(String filter) {
        queryFilter = new QueryFilter();
        parseState = ParseState.PARSE_FIELD;

        int filterLen = filter.length();
        char[] word = new char[filterLen];
        int wordCounter = 0;
        for (int i = 0; i < filterLen; ++i) {
            char c = filter.charAt(i);
            switch (c) {
                case '(':
                    break;
                case ')':
                    break;
                case ' ':
                    String wordStr = new String(word, 0, wordCounter);
                    addToFilter(wordStr);
                    wordCounter = 0;
                    break;
                default:
                    word[wordCounter++] = c;
            }
        }
        if (ParseState.PARSE_VALUE.equals(parseState)) {
            queryFilter.setValue(new String(word, 0, wordCounter));
        }
    }

    private void addToFilter(String wordStr) {
        switch (parseState) {
            case PARSE_FIELD:
                queryFilter.setKey(wordStr);
                parseState = ParseState.PARSE_OPERATOR;
                break;
            case PARSE_OPERATOR:
                QueryFilter.Operator op = QueryFilter.Operator.valueOf(wordStr.toUpperCase());
                queryFilter.setOperator(op);
                parseState = ParseState.PARSE_VALUE;
                break;
            case PARSE_VALUE:
                queryFilter.setValue(wordStr);
                parseState = ParseState.PARSE_FIELD;
                break;
            default:
                throw new IllegalStateException();
        }
    }

    QueryFilter getQueryFilter() {
        return queryFilter;
    }
}
