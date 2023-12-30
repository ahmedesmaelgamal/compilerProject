import java.util.Scanner;

enum TokenType {
    INTEGER,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    END_OF_FILE
}

class Token {
    TokenType type;
    int value;  

    Token(TokenType type, int value) {
        this.type = type;
        this.value = value;
    }
}

class ExpressionLexer {
    private String input;
    private int position;

    ExpressionLexer(String input) {
        this.input = input;
        this.position = 0;
    }

    Token getNextToken() throws ExpressionParseException {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }

        if (position >= input.length()) {
            return new Token(TokenType.END_OF_FILE, 0);
        }

        char currentChar = input.charAt(position);
        if (currentChar == '+') {
            position++;
            return new Token(TokenType.ADD, 0);
        } else if (currentChar == '-') {
            position++;
            return new Token(TokenType.SUBTRACT, 0);
        } else if (currentChar == '*') {
            position++;
            return new Token(TokenType.MULTIPLY, 0);
        } else if (currentChar == '/') {
            position++;
            return new Token(TokenType.DIVIDE, 0);
        } else if (Character.isDigit(currentChar)) {
            int value = 0;
            while (position < input.length() && Character.isDigit(input.charAt(position))) {
                value = value * 10 + (input.charAt(position) - '0');
                position++;
            }
            return new Token(TokenType.INTEGER, value);
        } else {
            throw new ExpressionParseException("Error: Invalid character '" + currentChar + "'");
        }
    }
}

class ExpressionParseException extends Exception {
    ExpressionParseException(String message) {
        super(message);
    }
}

class ExpressionParser {
    private ExpressionLexer lexer;
    private Token currentToken;

    ExpressionParser(ExpressionLexer lexer) throws ExpressionParseException {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
    }

    int parseExpression() throws ExpressionParseException {
        int result = parseTerm(); 

        while (currentToken.type == TokenType.ADD || currentToken.type == TokenType.SUBTRACT) {
            Token op = currentToken;
            if (op.type == TokenType.ADD) {
                consume(TokenType.ADD);
                result += parseTerm();
            } else if (op.type == TokenType.SUBTRACT) {
                consume(TokenType.SUBTRACT);
                result -= parseTerm();
            }
        }

        return result;
    }

    void parse() throws ExpressionParseException {
        parseExpression();
        if (currentToken.type != TokenType.END_OF_FILE) {
            throw new ExpressionParseException("Error: Unexpected token after expression");
        }
    }

    private void consume(TokenType expectedType) throws ExpressionParseException {
        if (currentToken.type == expectedType) {
            currentToken = lexer.getNextToken();
        } else {
            throw new ExpressionParseException("Error: Unexpected token type");
        }
    }

    private int parseFactor() throws ExpressionParseException {
        if (currentToken.type == TokenType.INTEGER) {
            int value = currentToken.value;
            consume(TokenType.INTEGER);
            return value;
        } else if (currentToken.type == TokenType.ADD) {
            consume(TokenType.ADD);
            return parseFactor();
        } else if (currentToken.type == TokenType.SUBTRACT) {
            consume(TokenType.SUBTRACT);
            return -parseFactor();
        } else {
            throw new ExpressionParseException("Error: Unexpected token type in factor");
        }
    }

    private int parseTerm() throws ExpressionParseException {
        int result = parseFactor();

        while (currentToken.type == TokenType.MULTIPLY || currentToken.type == TokenType.DIVIDE) {
            Token op = currentToken;
            if (op.type == TokenType.MULTIPLY) {
                consume(TokenType.MULTIPLY);
                result *= parseFactor();
            } else if (op.type == TokenType.DIVIDE) {
                consume(TokenType.DIVIDE);
                int divisor = parseFactor();
                if (divisor == 0) {
                    throw new ExpressionParseException("Error: Division by zero");
                }
                result /= divisor;
            }
        }

        return result;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter an arithmetic expression: ");
        String input = scanner.nextLine();
        scanner.close();

        try {
            ExpressionLexer lexer = new ExpressionLexer(input);
            ExpressionParser parser = new ExpressionParser(lexer);

            parser.parse();

            System.out.println("Parsing successful!");
        } catch (ExpressionParseException e) {
            System.err.println(e.getMessage());
        }
    }
}
