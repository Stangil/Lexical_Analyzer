/*
Stanley Gilstrap
CS 4308 Concepts of Programming Languages –
W01 Online Section
 */
package com.company;
import com.sun.deploy.util.ArrayUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Stan Gilstrap on 2/9/2017.
 */
public class LexicalAnalyzer {
    //global variables
    String[] keywords = {"function", "end", "if", "then", "else", "while", "do", "repeat", "until", "print"};
    char nextChar;
    int charClass; //1 = letter, 2 = number, 3 = operator or paren, 4 = white space
    int lexLen=0;;
    String nextToken;
    String line;
    int index = 0;
    char[] lexeme = new char[100];
    public List<String> lexemes = new ArrayList<>();
    public List<String> tokens = new ArrayList<>();
    //scans lua file and processes it line by line
    public LexicalAnalyzer(String fileName) throws FileNotFoundException
    {
        if (fileName == null)
            throw new IllegalArgumentException("null file name");
        Scanner input = new Scanner(new File(fileName));
        while (input.hasNext())
        {
            line = input.nextLine();
            processLine ();
        }
        checkKeyWord();
        lexTokenOut();
    }
    //process line while checking for null lines
    private void processLine()
    {
        if(line == null)
        {
            throw new IllegalArgumentException("line is null");
        }
        index = 0;
        getNextChar();
    }

    void getCharClass()//checks the type of each character and sets variable charClass
    {
       if (Character.isLetter(nextChar))
       {
           charClass = 1;
       }
       else if (Character.isDigit(nextChar))
       {
           charClass = 2;
       }
       else if (Character.isWhitespace(nextChar))
       {
           charClass = 4;
       }
       else
           {
               charClass = 3;
           }
    }
    // joins characters to make lexemes
    int lex()//creating lexemes
    {
        switch (charClass)//1 = letter, 2 = number, 3 = operator or paren, 4 = white space
        {
            case 1:
                {//if character is letter and lexeme is an id add letter to lexeme
                    if(Character.isLetter(lexeme[0])||(!Character.isDigit(lexeme[0]))||(lexeme[0] == ' '))
                    {
                        addChar();
                    }
                    else
                    {
                        System.out.println("\033[31m Syntax error: Line num: "+  (index + 1)+"\033[0m");
                        System.exit(-1);
                    }

                nextToken = "id";
                break;
                }
            case 2:
                {// adds number to lexeme if lexeme is an integer
                    if(Character.isDigit(lexeme[0])||(lexeme[0]==' '))
                    {
                       addChar();
                       nextToken = "literal_integer";
                    }
                    else
                    {
                        System.out.println("\033[31m Syntax error: Line num: "+  (index + 1)+"\033[0m");
                        System.exit(-1);
                    }
                break;
                }
            case 3:
                {//creates an operator or paren () = + - * / < > <= >= == ~=
                    if(nextChar == '(' || nextChar == ')' || nextChar == '+' ||
                            nextChar == '-' ||nextChar == '*' ||nextChar == '/')
                    {
                        Arrays.fill(lexeme, ' ');
                        lexLen = 0;
                        Lookup(nextChar); //look up parans or operators
                        lexTokenOut();
                    } else
                    { //outputs a comparator or equals lexeme
                        if(((lexeme[0] == ' '||lexeme[0]=='<'||lexeme[0]=='>'||lexeme[0]=='='||lexeme[0]=='~'))
                            &&(lexeme[1]==' ')&&(!Character.isLetter(lexeme[0])&&(!Character.isDigit(lexeme[0]))))
                        {
                            addChar();
                        }
                        else   {System.out.println("\033[31m Syntax error: Line num: "+  (index + 1)+"\033[0m");
                            System.exit(-1);
                        }
                        LookupComparator();
                    }
                break;
                }
            case 4://whitespace: when whitespace is detected it checks lexeme for a keyword and then calls the lexTokenOut function
            {
                checkKeyWord();
                if (lexeme[0] == ' ')
                    break;
                else
                {
                    lexTokenOut();
                }
                break;
            }
        }
        return 0 ;
    }
    void addChar()//adds character to lexeme string
    {
        if (lexLen <= 98)
        {
            lexeme[lexLen] = nextChar;
            lexLen++;
        }
        else System.out.println("Lexeme is too long \n");
    }
    //determines the token type of operators
    int Lookup(char ch)
    {
        switch (ch)
        { // () = + - * /
            case '(' :
                addChar();
                nextToken = "left_parenthesis";
                break;
            case ')' :
                addChar();
                nextToken = "right_parenthesis";
                break;
            case '+' :
                addChar();
                nextToken = "add_operator";
                break;
            case '-' :
                addChar();
                nextToken = "sub_operator";
                break;
            case '*' :
                addChar();
                nextToken = "mul_operator";
                break;
            case '/' :
                addChar();
                nextToken = "div_operator";
                break;
                default: System.out.println("\033[31m Syntax error: Line num: "+  (index + 1)+"\033[0m"); System.exit(-1);
        }
        return 0;
    }
    //determines the token type of comparator operators
    int LookupComparator()
    {
       if(lexeme[0] == '<')
        {
            if(lexeme[1]==' ')
            {
                nextToken = "lt_operator";
            }else if(lexeme[1] == '=')
            {
                nextToken = "le_operator";
            }
        }else if(lexeme[0] == '>')
        {
           if(lexeme[1]==' ')
           {
               nextToken = "gt_operator";
           }else if(lexeme[1] == '=')
           {
               nextToken = "ge_operator";
           }
       }else if(lexeme[0] == '='&&lexeme[1] == '=')
       {
           nextToken = "eq_operator";
       }else if(lexeme[0] == '~'&&lexeme[1] == '=')
       {
           nextToken = "ne_operator";
       }
       else if(lexeme[0] == '='&&lexeme[1] == ' ')
       {
           nextToken = "assignment_operator";
       }
        return 0;
    }
    //gets next character from line
    void getNextChar()
    {
        while(index < line.length())
        {
            nextChar = line.charAt(index);
            getCharClass();
            lex();
            index++;
        }
    }
    //adds lexemes and tokens to list for the parser to read
    void lexTokenOut()
    {
        lexemes.add(new String(lexeme).trim());
        tokens.add(nextToken);
        Arrays.fill(lexeme, ' ');
        lexLen = 0;
    }
    public String getNextToken()
    {
        String token;
        if(tokens.isEmpty())
            return "No tokens left";
        else
            token = tokens.get(0);
            tokens.remove(0);
            return token;
    }
    public String getNextLex()
    {
        String lexeme;
        if(lexemes.isEmpty())
            return "No lexemes left";
        else
            lexeme = lexemes.get(0);
            lexemes.remove(0);
            return lexeme;
    }
    public void checkKeyWord()
    {
        for(int i = 0; i < keywords.length;i++)
        {
            if(Objects.equals(keywords[i],new String(lexeme).trim()))//checks lexeme to see if it is a keyword
            {
                nextToken = "keyword";
            }
        }
    }
}
