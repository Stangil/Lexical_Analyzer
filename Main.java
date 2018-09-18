/*
Stanley Gilstrap
CS 4308 Concepts of Programming Languages –
W01 Online Section
 */
package com.company;
import java.io.FileNotFoundException;

public class Main
{
    public static LexicalAnalyzer lexAn;

    public static void printLexTokens()//calls for tokens and lexemes simulating the parser calling the scanner
    {
        while(!lexAn.tokens.isEmpty()) {
            System.out.print("Next lexeme is: " + lexAn.getNextLex());
            System.out.print(" , its token is " + lexAn.getNextToken() + "\n");
        }
    }
    public static void main(String[] args)
    {//creates a lexical analyzer object using test file test.lus

        try {
            lexAn = new LexicalAnalyzer("testfile.lua");
            } catch (FileNotFoundException e) {
            System.out.println("File not found");
            }
           printLexTokens();

    }


}

